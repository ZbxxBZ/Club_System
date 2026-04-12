package com.bistu.clubsystembackend.serviceImpl;

import com.bistu.clubsystembackend.entity.request.ScheduledTaskCommandRequest;
import com.bistu.clubsystembackend.entity.response.ScheduledTaskCommandData;
import com.bistu.clubsystembackend.entity.response.ScheduledTaskStatusData;
import com.bistu.clubsystembackend.mapper.UserPermissionMapper;
import com.bistu.clubsystembackend.service.ScheduledTaskService;
import com.bistu.clubsystembackend.service.SchoolAdminPermissionService;
import com.bistu.clubsystembackend.util.AccessChecker;
import com.bistu.clubsystembackend.util.BusinessException;
import com.bistu.clubsystembackend.util.CurrentUser;
import com.bistu.clubsystembackend.util.IdGenerator;
import com.bistu.clubsystembackend.util.RoleCode;
import com.bistu.clubsystembackend.util.ScheduledTaskCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskServiceImpl.class);

    private static final String STATUS_STOPPED = "STOPPED";
    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_FAILED = "FAILED";

    private static final DateTimeFormatter JOB_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final SchoolAdminPermissionService schoolAdminPermissionService;
    private final IdGenerator idGenerator;
    private final UserPermissionMapper userPermissionMapper;
    private final Map<String, TaskState> stateMap = new LinkedHashMap<>();

    public ScheduledTaskServiceImpl(SchoolAdminPermissionService schoolAdminPermissionService,
                                    IdGenerator idGenerator,
                                    UserPermissionMapper userPermissionMapper) {
        this.schoolAdminPermissionService = schoolAdminPermissionService;
        this.idGenerator = idGenerator;
        this.userPermissionMapper = userPermissionMapper;
        initState();
    }

    @Override
    public ScheduledTaskCommandData commandTask(String taskCode, ScheduledTaskCommandRequest request) {
        CurrentUser operator = AccessChecker.requireLogin();
        if (!RoleCode.SCHOOL_ADMIN.equals(operator.getRoleCode())) {
            throw new BusinessException(2004, "no permission");
        }

        String normalizedTaskCode = normalizeTaskCode(taskCode);
        String action = normalizeAction(request.getAction());
        TaskState state = getStateOrThrow(normalizedTaskCode);
        String beforeStatus;
        String afterStatus;
        String jobId;
        long processedCount;
        LocalDateTime now;

        synchronized (state) {
            beforeStatus = state.taskStatus;
            if ("START".equals(action)) {
                if (STATUS_RUNNING.equals(state.taskStatus)) {
                    throw new BusinessException(2002, "task is running, duplicate start is not allowed");
                }
                if (STATUS_STOPPED.equals(state.taskStatus) || STATUS_FAILED.equals(state.taskStatus)) {
                    state.taskStatus = STATUS_STARTED;
                    state.updatedAt = LocalDateTime.now();
                    state.jobId = buildJobId();
                    log.info("Task START requested: taskCode={}, operatorId={}, beforeStatus={}, afterStatus={}, jobId={}",
                            normalizedTaskCode, operator.getUserId(), beforeStatus, state.taskStatus, state.jobId);
                }
            } else if ("STOP".equals(action)) {
                if (STATUS_RUNNING.equals(state.taskStatus)) {
                    throw new BusinessException(2003, "task is running, stop is not allowed");
                }
                if (!STATUS_STOPPED.equals(state.taskStatus)) {
                    state.taskStatus = STATUS_STOPPED;
                    state.updatedAt = LocalDateTime.now();
                    log.info("Task STOP requested: taskCode={}, operatorId={}, beforeStatus={}, afterStatus={}",
                            normalizedTaskCode, operator.getUserId(), beforeStatus, state.taskStatus);
                } else {
                    log.info("Task STOP requested but already stopped: taskCode={}, operatorId={}, status={}",
                            normalizedTaskCode, operator.getUserId(), state.taskStatus);
                }
            } else {
                throw new BusinessException(1001, "invalid action");
            }
            afterStatus = state.taskStatus;
            now = state.updatedAt;
            jobId = state.jobId;
            processedCount = state.processedCount;
        }

        ScheduledTaskCommandData data = new ScheduledTaskCommandData(
                normalizedTaskCode,
                action,
                afterStatus,
                jobId,
                processedCount,
                now
        );
        writeSchoolAdminAudit(
                operator,
                "SCHEDULED_TASK_COMMAND",
                normalizedTaskCode + ":" + action,
                "{\"taskCode\":\"" + normalizedTaskCode + "\",\"status\":\"" + afterStatus + "\"}"
        );
        return data;
    }

    @Override
    public List<ScheduledTaskStatusData> queryStatus(List<String> taskCodes) {
        CurrentUser operator = AccessChecker.requireLogin();
        if (!RoleCode.SCHOOL_ADMIN.equals(operator.getRoleCode())) {
            throw new BusinessException(2004, "no permission");
        }

        List<String> targets;
        if (taskCodes == null || taskCodes.isEmpty()) {
            targets = new ArrayList<>(stateMap.keySet());
        } else {
            targets = taskCodes.stream()
                    .filter(Objects::nonNull)
                    .map(this::normalizeTaskCode)
                    .collect(Collectors.toList());
        }

        List<ScheduledTaskStatusData> result = new ArrayList<>();
        for (String code : targets) {
            TaskState state = getStateOrThrow(code);
            synchronized (state) {
                result.add(new ScheduledTaskStatusData(code, state.taskStatus, state.updatedAt));
            }
        }
        return result;
    }

    @Override
    public void executeBySystem(String taskCode) {
        String normalizedTaskCode = normalizeTaskCode(taskCode);
        TaskState state = getStateOrThrow(normalizedTaskCode);
        synchronized (state) {
            if (!STATUS_STARTED.equals(state.taskStatus)) {
                return;
            }
            state.taskStatus = STATUS_RUNNING;
            state.updatedAt = LocalDateTime.now();
            state.jobId = buildJobId();
        }

        try {
            long processedCount = runTask(normalizedTaskCode);
            synchronized (state) {
                state.taskStatus = STATUS_STARTED;
                state.processedCount = processedCount;
                state.updatedAt = LocalDateTime.now();
            }
        } catch (Exception ex) {
            synchronized (state) {
                state.taskStatus = STATUS_FAILED;
                state.updatedAt = LocalDateTime.now();
            }
        }
    }

    private long runTask(String taskCode) {
        return switch (taskCode) {
            case ScheduledTaskCode.GRADUATION_EXIT_CLUB -> schoolAdminPermissionService.autoExitGraduates();
            case ScheduledTaskCode.CLUB_CANCEL_FREEZE_ACCOUNT -> schoolAdminPermissionService.autoFreezeCanceledClubAdmins();
            case ScheduledTaskCode.AUTO_END_EVENTS -> schoolAdminPermissionService.autoEndEvents();
            default -> throw new BusinessException(2001, "task code not found");
        };
    }

    private String normalizeTaskCode(String taskCode) {
        return taskCode == null ? "" : taskCode.trim().toUpperCase();
    }

    private String normalizeAction(String action) {
        return action == null ? "" : action.trim().toUpperCase();
    }

    private TaskState getStateOrThrow(String taskCode) {
        TaskState state = stateMap.get(taskCode);
        if (state == null) {
            throw new BusinessException(2001, "task code not found");
        }
        return state;
    }

    private String buildJobId() {
        return "job-" + LocalDateTime.now().format(JOB_TIME) + "-" + idGenerator.nextId();
    }

    private void initState() {
        stateMap.put(ScheduledTaskCode.GRADUATION_EXIT_CLUB, new TaskState(STATUS_STOPPED, LocalDateTime.now(), null, 0L));
        stateMap.put(ScheduledTaskCode.CLUB_CANCEL_FREEZE_ACCOUNT, new TaskState(STATUS_STOPPED, LocalDateTime.now(), null, 0L));
        stateMap.put(ScheduledTaskCode.AUTO_END_EVENTS, new TaskState(STATUS_STOPPED, LocalDateTime.now(), null, 0L));
    }

    private void writeSchoolAdminAudit(CurrentUser user, String opType, String remark, String afterJson) {
        String ip = null;
        String userAgent = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteAddr();
            userAgent = request.getHeader("User-Agent");
        }
        userPermissionMapper.insertAuditLog(
                idGenerator.nextId(),
                "SCHOOL_ADMIN_OP",
                0L,
                opType,
                user.getUserId(),
                user.getRoleCode(),
                LocalDateTime.now(),
                null,
                normalizeJson(afterJson),
                ip,
                userAgent,
                remark
        );
    }

    private String normalizeJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return trimmed;
        }
        return "\"" + trimmed.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static class TaskState {
        private String taskStatus;
        private LocalDateTime updatedAt;
        private String jobId;
        private long processedCount;

        private TaskState(String taskStatus, LocalDateTime updatedAt, String jobId, long processedCount) {
            this.taskStatus = taskStatus;
            this.updatedAt = updatedAt;
            this.jobId = jobId;
            this.processedCount = processedCount;
        }
    }
}
