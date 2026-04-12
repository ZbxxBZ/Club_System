package com.bistu.clubsystembackend.serviceImpl;

import com.bistu.clubsystembackend.entity.request.EventApprovalDecisionRequest;
import com.bistu.clubsystembackend.entity.request.ExpenseApprovalDecisionRequest;
import com.bistu.clubsystembackend.entity.request.ApprovalDecisionRequest;
import com.bistu.clubsystembackend.entity.request.ApprovalStatusUpdateRequest;
import com.bistu.clubsystembackend.entity.request.ClubCancelStatusUpdateRequest;
import com.bistu.clubsystembackend.entity.request.FreezeAccountsRequest;
import com.bistu.clubsystembackend.entity.request.GraduationExitRequest;
import com.bistu.clubsystembackend.entity.request.SchoolAdminUpdateClubRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubStatusRequest;
import com.bistu.clubsystembackend.entity.request.UpdateUserRoleRequest;
import com.bistu.clubsystembackend.entity.request.UpdateUserStatusRequest;
import com.bistu.clubsystembackend.entity.EventMeta;
import com.bistu.clubsystembackend.entity.response.EventApprovalItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventSummaryData;
import com.bistu.clubsystembackend.entity.response.ApprovalStatusUpdateData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalDetailData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalItem;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyData;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyItem;
import com.bistu.clubsystembackend.entity.response.ClubCancelDetailData;
import com.bistu.clubsystembackend.entity.response.ClubCancelStatusUpdateData;
import com.bistu.clubsystembackend.entity.response.ClubStatusChangeData;
import com.bistu.clubsystembackend.entity.response.AnomalyExpenseItem;
import com.bistu.clubsystembackend.entity.response.ClubStatisticsData;
import com.bistu.clubsystembackend.entity.response.EventStatisticsData;
import com.bistu.clubsystembackend.entity.response.ExpenseApprovalItem;
import com.bistu.clubsystembackend.entity.response.FinanceStatisticsData;
import com.bistu.clubsystembackend.entity.response.ExpenseDetailData;
import com.bistu.clubsystembackend.entity.response.FinanceAuditReportData;
import com.bistu.clubsystembackend.entity.response.FrozenCountData;
import com.bistu.clubsystembackend.entity.response.LedgerItem;
import com.bistu.clubsystembackend.entity.response.PageResponseData;
import com.bistu.clubsystembackend.entity.response.ProcessedCountData;
import com.bistu.clubsystembackend.entity.response.SchoolAdminClubDetailData;
import com.bistu.clubsystembackend.entity.response.SchoolAdminClubListItem;
import com.bistu.clubsystembackend.entity.response.SchoolUserItem;
import com.bistu.clubsystembackend.entity.response.StatisticsOverviewData;
import com.bistu.clubsystembackend.entity.response.UserRoleChangeData;
import com.bistu.clubsystembackend.entity.response.UserStatusChangeData;
import com.bistu.clubsystembackend.mapper.UserPermissionMapper;
import com.bistu.clubsystembackend.service.CacheService;
import com.bistu.clubsystembackend.service.SchoolAdminPermissionService;
import com.bistu.clubsystembackend.util.AccessChecker;
import com.bistu.clubsystembackend.util.BizCode;
import com.bistu.clubsystembackend.util.BusinessException;
import com.bistu.clubsystembackend.util.CurrentUser;
import com.bistu.clubsystembackend.util.IdGenerator;
import com.bistu.clubsystembackend.util.RoleCode;
import com.bistu.clubsystembackend.util.UserStatusUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchoolAdminPermissionServiceImpl implements SchoolAdminPermissionService {

    private final UserPermissionMapper mapper;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;
    private final CacheService cacheService;

    public SchoolAdminPermissionServiceImpl(UserPermissionMapper mapper, IdGenerator idGenerator,
                                            ObjectMapper objectMapper, CacheService cacheService) {
        this.mapper = mapper;
        this.idGenerator = idGenerator;
        this.objectMapper = objectMapper;
        this.cacheService = cacheService;
    }

    public PageResponseData<SchoolUserItem> listUsers(int pageNum, int pageSize, String roleCode, String status, String keyword) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        Integer dbStatus = UserStatusUtil.toDbStatus(status);
        Boolean graduated = "GRADUATED".equals(status) ? Boolean.TRUE : null;
        List<SchoolUserItem> records = mapper.listUsers(roleCode, keyword, dbStatus, graduated, offset, safePageSize);
        long total = mapper.countUsers(roleCode, keyword, dbStatus, graduated);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserStatusChangeData updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        Integer beforeStatus = mapper.findUserStatusById(userId);
        if (beforeStatus == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "user not found");
        }
        if (beforeStatus != 1 && beforeStatus != 2) {
            throw new BusinessException(BizCode.ACCOUNT_STATUS_INVALID.getCode(), "current status does not allow this operation");
        }
        Integer dbStatus = UserStatusUtil.toDbStatus(request.getStatus());
        if (dbStatus == null || (dbStatus != 1 && dbStatus != 2)) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "target status is invalid");
        }
        if (beforeStatus.equals(dbStatus)) {
            return new UserStatusChangeData(userId, UserStatusUtil.toStatusText(dbStatus, null));
        }
        int rows = mapper.updateUserStatus(userId, dbStatus, LocalDateTime.now());
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "user not found");
        }
        cacheService.evictSessionInfo(userId);
        String afterText = UserStatusUtil.toStatusText(dbStatus, null);
        return new UserStatusChangeData(userId, afterText);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubStatusChangeData updateClubStatus(Long clubId, UpdateClubStatusRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        Integer beforeStatus = mapper.findClubStatusById(clubId);
        if (beforeStatus == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        if (beforeStatus != 2 && beforeStatus != 3) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "only clubs in status 2 or 3 can switch");
        }
        Integer dbStatus = switch (request.getStatus()) {
            case "ACTIVE" -> 2;
            case "FROZEN" -> 3;
            default -> null;
        };
        if (dbStatus == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "status value is invalid");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!beforeStatus.equals(dbStatus)) {
            int rows = mapper.updateClubStatus(clubId, dbStatus, user.getUserId(), now);
            if (rows == 0) {
                throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
            }
        }
        String opType = dbStatus == 3 ? "FREEZE" : "UNFREEZE";
        writeAudit("CLUB_STATUS", clubId, opType, user, String.valueOf(beforeStatus), String.valueOf(dbStatus), limitLength(request.getReason(), 500));
        return new ClubStatusChangeData(clubId, request.getStatus(), now);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserRoleChangeData updateUserRole(Long userId, UpdateUserRoleRequest request) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        Integer userExists = mapper.existsUserById(userId);
        if (userExists == null || userExists == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "user not found");
        }
        Long roleId = mapper.findRoleIdByCode(request.getRoleCode());
        if (roleId == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "role not found");
        }
        LocalDateTime now = LocalDateTime.now();
        Long clubId = RoleCode.CLUB_ADMIN.equals(request.getRoleCode()) ? mapper.findAnyClubIdByUser(userId) : null;
        Long roleRecordId = mapper.findActiveUserRoleRecordId(userId);
        if (roleRecordId == null) {
            roleRecordId = mapper.findLatestUserRoleRecordId(userId);
        }
        if (roleRecordId == null) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "user_role record not found");
        }
        int updated = mapper.updateUserRoleRecordById(roleRecordId, roleId, clubId, now);
        if (updated == 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "failed to update user_role");
        }
        mapper.disableOtherActiveUserRoles(userId, roleRecordId, now);
        cacheService.evictSessionInfo(userId);
        String effectiveRoleCode = mapper.findEffectiveRoleCodeByUserId(userId);
        return new UserRoleChangeData(userId, request.getRoleCode(), effectiveRoleCode);
    }

    public PageResponseData<ClubApprovalItem> listClubApprovals(int pageNum, int pageSize, String keyword, Integer applyStatus) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubApprovalItem> records = mapper.listClubApprovals(applyStatus, keyword, offset, safePageSize);
        long total = mapper.countClubApprovals(applyStatus, keyword);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public ClubApprovalDetailData getClubApprovalDetail(Long approvalId) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ClubApprovalDetailData detail = mapper.findClubApprovalDetailById(approvalId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "approval record not found");
        }
        return detail;
    }

    public PageResponseData<ClubCancelApplyItem> listClubCancelApprovals(int pageNum, int pageSize, String keyword, Integer cancelStatus) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubCancelApplyItem> records = mapper.listClubCancelApprovals(cancelStatus, keyword, offset, safePageSize);
        long total = mapper.countClubCancelApprovals(cancelStatus, keyword);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public ClubCancelDetailData getClubCancelApprovalDetail(Long cancelId) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ClubCancelDetailData detail = mapper.findClubCancelDetailById(cancelId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "cancel apply not found");
        }
        return detail;
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubCancelStatusUpdateData updateClubCancelApprovalStatus(Long cancelId, ClubCancelStatusUpdateRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ClubCancelApplyData cancelApply = mapper.findClubCancelById(cancelId);
        if (cancelApply == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "cancel apply not found");
        }

        Integer fromStatus = cancelApply.getCancelStatus();
        Integer targetStatus = request.getCancelStatus();
        validateCancelTransition(fromStatus, targetStatus);

        LocalDateTime now = LocalDateTime.now();
        int rows = mapper.updateClubCancelStatus(cancelId, targetStatus, user.getUserId(), now);
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "cancel apply not found");
        }
        if (targetStatus == 4) {
            mapper.updateClubStatus(cancelApply.getClubId(), 5, user.getUserId(), now);
            downgradeClubInitiatorToStudent(cancelApply.getClubId(), now);
        } else if (targetStatus == 5) {
            mapper.updateClubStatus(cancelApply.getClubId(), 2, user.getUserId(), now);
        }
        String opType = targetStatus == 5 ? "REJECT" : "APPROVE";
        writeAudit("CLUB_CANCEL", cancelId, opType, user, String.valueOf(fromStatus), String.valueOf(targetStatus), limitLength(request.getOpinion(), 500));
        return new ClubCancelStatusUpdateData(cancelId, targetStatus);
    }

    public PageResponseData<SchoolAdminClubListItem> listSchoolAdminClubManagePage(int pageNum, int pageSize, String keyword, String category) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<SchoolAdminClubListItem> records = mapper.listSchoolAdminClubManagePage(keyword, category, offset, safePageSize);
        for (SchoolAdminClubListItem item : records) {
            applyManagePermission(item);
        }
        long total = mapper.countSchoolAdminClubManagePage(keyword, category);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public SchoolAdminClubDetailData getSchoolAdminClubManageDetail(Long clubId) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        SchoolAdminClubDetailData detail = mapper.findSchoolAdminClubManageDetailByClubId(clubId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        return detail;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSchoolAdminClub(Long clubId, SchoolAdminUpdateClubRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        SchoolAdminClubDetailData before = requireManageClub(clubId);
        if (!canEdit(before.getStatus(), before.getApplyStatus())) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), editDisabledReason(before.getStatus(), before.getApplyStatus()));
        }

        String clubName = normalizeText(request.getClubName());
        String category = normalizeText(request.getCategory());
        String purpose = normalizeText(request.getPurpose());
        String instructorName = normalizeText(request.getInstructorName());
        if (clubName == null && category == null && purpose == null && instructorName == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "at least one updatable field is required");
        }

        int rows = mapper.updateSchoolAdminClub(clubId, clubName, category, purpose, instructorName, user.getUserId(), LocalDateTime.now());
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        SchoolAdminClubDetailData after = requireManageClub(clubId);
        writeAudit("CLUB_MANAGE", clubId, "UPDATE", user, buildClubManageSnapshot(before), buildClubManageSnapshot(after), "update club basic info");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApprovalStatusUpdateData updateClubApprovalStatus(Long approvalId, ApprovalStatusUpdateRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ClubApprovalItem approval = mapper.findClubApprovalById(approvalId);
        if (approval == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND);
        }
        Integer fromStatus = approval.getApplyStatus();
        Integer targetStatus = request.getApplyStatus();
        validateApprovalTransition(fromStatus, targetStatus);
        String currentStep = stepByStatus(targetStatus);
        LocalDateTime now = LocalDateTime.now();
        int rows = mapper.updateClubApprovalDecision(approvalId, targetStatus, currentStep, request.getOpinion(), user.getUserId(), now);
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND);
        }
        if (targetStatus == 4) {
            mapper.updateClubStatus(approval.getClubId(), 2, user.getUserId(), now);
            mapper.updateClubEstablishDateToToday(approval.getClubId(), user.getUserId(), now);
            grantClubAdminRoleForApproval(approval, now);
        }
        String opType = targetStatus == 5 ? "REJECT" : "APPROVE";
        writeAudit("CLUB_APPLY", approvalId, opType, user, String.valueOf(fromStatus), String.valueOf(targetStatus), limitLength(request.getOpinion(), 500));
        return new ApprovalStatusUpdateData(approvalId, targetStatus, currentStep);
    }

    public void decisionClubApproval(Long approvalId, ApprovalDecisionRequest request) {
        Integer targetStatus = switch (request.getAction()) {
            case "APPROVE" -> 4;
            case "REJECT" -> 5;
            case "RETURN" -> 5;
            default -> throw new BusinessException(BizCode.PARAM_INVALID);
        };
        ApprovalStatusUpdateRequest updateRequest = new ApprovalStatusUpdateRequest();
        updateRequest.setApplyStatus(targetStatus);
        updateRequest.setOpinion(request.getOpinion());
        updateClubApprovalStatus(approvalId, updateRequest);
    }

    public ProcessedCountData exitGraduationUsers(GraduationExitRequest request) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        long count = autoExitGraduates();
        return new ProcessedCountData(count);
    }

    public FrozenCountData freezeAccounts(String clubId, FreezeAccountsRequest request) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        long count;
        LocalDateTime now = LocalDateTime.now();
        if ("batch".equalsIgnoreCase(clubId)) {
            count = mapper.freezeAccountsByCanceledClubs(now);
        } else {
            Long parsedClubId;
            try {
                parsedClubId = Long.parseLong(clubId);
            } catch (NumberFormatException ex) {
                throw new BusinessException(BizCode.PARAM_INVALID);
            }
            count = mapper.freezeAccountsByClubId(parsedClubId, now);
        }
        return new FrozenCountData(count);
    }

    public StatisticsOverviewData statisticsOverview() {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        return new StatisticsOverviewData(
                mapper.countActiveClubs(),
                mapper.countClubs(),
                mapper.countActiveUsers(),
                mapper.countTotalUsers(),
                mapper.countPendingApprovals(),
                mapper.countSuspiciousExpenses()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public long autoExitGraduates() {
        return mapper.exitGraduatedMembers(LocalDateTime.now());
    }

    @Transactional(rollbackFor = Exception.class)
    public long autoFreezeCanceledClubAdmins() {
        return mapper.freezeAccountsByCanceledClubs(LocalDateTime.now());
    }

    @Transactional(rollbackFor = Exception.class)
    public long autoStartEvents() {
        return mapper.autoStartEvents(LocalDateTime.now());
    }

    @Transactional(rollbackFor = Exception.class)
    public long autoEndEvents() {
        return mapper.autoEndEvents(LocalDateTime.now());
    }

    private void writeAudit(String bizType, Long bizId, String opType, CurrentUser user, String beforeJson, String afterJson, String remark) {
        String ip = null;
        String userAgent = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteAddr();
            userAgent = request.getHeader("User-Agent");
        }
        mapper.insertAuditLog(
                idGenerator.nextId(),
                bizType,
                bizId,
                opType,
                user.getUserId(),
                user.getRoleCode(),
                LocalDateTime.now(),
                beforeJson,
                afterJson,
                ip,
                userAgent,
                remark
        );
    }

    private String limitLength(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private void validateApprovalTransition(Integer fromStatus, Integer targetStatus) {
        if (fromStatus == null || targetStatus == null) {
            throw new BusinessException(BizCode.PARAM_INVALID);
        }
        if (fromStatus == 4 || fromStatus == 5) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "terminal status cannot transition");
        }
        if (fromStatus.equals(targetStatus)) {
            return;
        }

        // Try configurable stages from Redis
        String stagesJson = cacheService.getConfig("config:approval:club-stages");
        if (stagesJson != null) {
            try {
                List<Map<String, Integer>> transitions = objectMapper.readValue(stagesJson,
                        new TypeReference<List<Map<String, Integer>>>() {});
                boolean allowed = false;
                for (Map<String, Integer> t : transitions) {
                    if (fromStatus.equals(t.get("from")) && targetStatus.equals(t.get("to"))) {
                        allowed = true;
                        break;
                    }
                }
                // Always allow rejection to status 5 from non-terminal
                boolean reject = (fromStatus >= 1 && fromStatus <= 3) && targetStatus == 5;
                if (!allowed && !reject) {
                    throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "status transition is not allowed");
                }
                return;
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                // Fall through to hardcoded logic
            }
        }

        // Fallback: hardcoded 3-stage approval
        boolean next = (fromStatus == 1 && targetStatus == 2)
                || (fromStatus == 2 && targetStatus == 3)
                || (fromStatus == 3 && targetStatus == 4);
        boolean reject = (fromStatus == 1 || fromStatus == 2 || fromStatus == 3) && targetStatus == 5;
        if (!next && !reject) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "status transition is not allowed");
        }
    }

    private void validateCancelTransition(Integer fromStatus, Integer targetStatus) {
        if (fromStatus == null || targetStatus == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "cancel status is invalid");
        }
        if (fromStatus == 4 || fromStatus == 5) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "terminal cancel status cannot transition");
        }
        if (fromStatus.equals(targetStatus)) {
            return;
        }
        boolean next = (fromStatus == 1 && targetStatus == 2)
                || (fromStatus == 2 && targetStatus == 3)
                || (fromStatus == 3 && targetStatus == 4);
        boolean reject = (fromStatus == 1 || fromStatus == 2 || fromStatus == 3) && targetStatus == 5;
        if (!next && !reject) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "cancel status transition is not allowed");
        }
    }

    private String stepByStatus(Integer applyStatus) {
        return switch (applyStatus) {
            case 1 -> "MATERIAL_REVIEW";
            case 2 -> "DEFENSE_REVIEW";
            case 3 -> "PUBLIC_REVIEW";
            case 4 -> "APPROVED";
            case 5 -> "REJECTED";
            default -> "MATERIAL_REVIEW";
        };
    }

    private SchoolAdminClubDetailData requireManageClub(Long clubId) {
        SchoolAdminClubDetailData detail = mapper.findSchoolAdminClubManageDetailByClubId(clubId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        return detail;
    }

    private String buildClubManageSnapshot(SchoolAdminClubDetailData detail) {
        if (detail == null) {
            return null;
        }
        return "{\"clubId\":" + detail.getClubId()
                + ",\"clubName\":\"" + safeJson(detail.getClubName())
                + "\",\"category\":\"" + safeJson(detail.getCategory())
                + "\",\"status\":" + detail.getStatus()
                + ",\"applyStatus\":" + detail.getApplyStatus()
                + ",\"instructorName\":\"" + safeJson(detail.getInstructorName())
                + "\",\"purpose\":\"" + safeJson(detail.getPurpose())
                + "\"}";
    }

    private String safeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void applyManagePermission(SchoolAdminClubListItem item) {
        boolean canEdit = canEdit(item.getStatus(), item.getApplyStatus());
        item.setCanEdit(canEdit);
        item.setEditDisabledReason(canEdit ? "" : editDisabledReason(item.getStatus(), item.getApplyStatus()));
    }

    private boolean canEdit(Integer status, Integer applyStatus) {
        return status != null && status != 5
                && (applyStatus == null || (applyStatus != 1 && applyStatus != 2 && applyStatus != 3));
    }

    private String editDisabledReason(Integer status, Integer applyStatus) {
        if (status != null && status == 5) {
            return "Canceled clubs cannot be edited";
        }
        if (applyStatus != null && (applyStatus == 1 || applyStatus == 2 || applyStatus == 3)) {
            return "Approval is in progress, editing is not allowed";
        }
        return "Current status does not allow editing";
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void downgradeClubInitiatorToStudent(Long clubId, LocalDateTime now) {
        Long initiatorUserId = mapper.findClubInitiatorUserId(clubId);
        if (initiatorUserId == null) {
            return;
        }
        Long studentRoleId = mapper.findRoleIdByCode(RoleCode.STUDENT);
        if (studentRoleId == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "STUDENT role not found");
        }
        Long roleRecordId = mapper.findActiveUserRoleRecordId(initiatorUserId);
        if (roleRecordId == null) {
            roleRecordId = mapper.findLatestUserRoleRecordId(initiatorUserId);
        }
        if (roleRecordId == null) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "user_role record not found");
        }
        int updated = mapper.updateUserRoleRecordById(roleRecordId, studentRoleId, null, now);
        if (updated == 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "failed to downgrade initiator role");
        }
        mapper.disableOtherActiveUserRoles(initiatorUserId, roleRecordId, now);
        cacheService.evictSessionInfo(initiatorUserId);
    }

    private void grantClubAdminRoleForApproval(ClubApprovalItem approval, LocalDateTime now) {
        Long initiatorUserId = approval.getInitiatorUserId();
        if (initiatorUserId == null) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "approval initiator is missing");
        }
        Long clubAdminRoleId = mapper.findRoleIdByCode(RoleCode.CLUB_ADMIN);
        if (clubAdminRoleId == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "CLUB_ADMIN role not found");
        }
        Long roleRecordId = mapper.findActiveUserRoleRecordId(initiatorUserId);
        if (roleRecordId == null) {
            roleRecordId = mapper.findLatestUserRoleRecordId(initiatorUserId);
        }
        if (roleRecordId == null) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "user_role record not found");
        }
        int updated = mapper.updateUserRoleRecordById(roleRecordId, clubAdminRoleId, approval.getClubId(), now);
        if (updated == 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "failed to update user_role");
        }
        mapper.disableOtherActiveUserRoles(initiatorUserId, roleRecordId, now);
        cacheService.evictSessionInfo(initiatorUserId);
    }

    // ===== Event approval =====

    @Override
    public PageResponseData<EventApprovalItem> listEventApprovals(int pageNum, int pageSize, String keyword, Integer eventStatus) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<EventApprovalItem> records = mapper.listEventApprovals(eventStatus, safeKeyword, offset, safePageSize);
        long total = mapper.countEventApprovals(eventStatus, safeKeyword);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    public EventDetailData getEventApprovalDetail(Long eventId) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        EventDetailData detail = mapper.findEventDetailById(eventId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decisionEventApproval(Long eventId, EventApprovalDecisionRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        if (meta.getEventStatus() != 2) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "event is not pending approval");
        }
        LocalDateTime now = LocalDateTime.now();
        String action = request.getAction().toUpperCase();
        int newStatus;
        String rejectReason = null;
        if ("APPROVE".equals(action)) {
            newStatus = 3;
        } else {
            newStatus = 5;
            rejectReason = request.getRejectReason() != null ? request.getRejectReason().trim() : null;
        }
        mapper.updateEventStatus(eventId, newStatus, rejectReason, user.getUserId(), now);
        // Initialize Redis quota when event enters signup state
        if (newStatus == 3) {
            Integer limitCount = mapper.findEventLimitCount(eventId);
            if (limitCount != null && limitCount > 0) {
                int currentSignups = mapper.countActiveSignups(eventId);
                cacheService.initEventQuota(eventId, limitCount - currentSignups);
            }
        }
        writeAudit("EVENT", eventId, action, user, String.valueOf(2), String.valueOf(newStatus),
                limitLength(rejectReason, 500));
    }

    @Override
    public PageResponseData<EventSummaryData> listEventSummaries(int pageNum, int pageSize, String keyword) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<EventSummaryData> records = mapper.listEventSummaries(safeKeyword, offset, safePageSize);
        for (EventSummaryData record : records) {
            record.setSummaryImages(deserializeImageList(record.getSummaryImagesJson()));
        }
        long total = mapper.countEventSummaries(safeKeyword);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    public EventSummaryData getEventSummaryByEventId(Long eventId) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        EventSummaryData summary = mapper.findEventSummaryByEventId(eventId);
        if (summary == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event summary not found");
        }
        summary.setSummaryImages(deserializeImageList(summary.getSummaryImagesJson()));
        return summary;
    }

    private List<String> deserializeImageList(Object images) {
        if (images == null) {
            return new ArrayList<>();
        }

        // 如果已经是 List，直接返回
        if (images instanceof List) {
            List<?> list = (List<?>) images;
            if (list.isEmpty()) {
                return new ArrayList<>();
            }
            // 检查第一个元素是否是 JSON 字符串
            Object first = list.get(0);
            if (first instanceof String && ((String) first).startsWith("[")) {
                try {
                    return objectMapper.readValue((String) first, new TypeReference<List<String>>() {});
                } catch (JsonProcessingException e) {
                    return new ArrayList<>();
                }
            }
            // 否则假设是字符串列表
            List<String> result = new ArrayList<>();
            for (Object item : list) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
            return result;
        }

        // 如果是字符串，尝试解析 JSON
        if (images instanceof String) {
            String str = (String) images;
            if (str.trim().isEmpty()) {
                return new ArrayList<>();
            }
            if (str.startsWith("[")) {
                try {
                    return objectMapper.readValue(str, new TypeReference<List<String>>() {});
                } catch (JsonProcessingException e) {
                    return new ArrayList<>();
                }
            }
        }

        return new ArrayList<>();
    }

    // ===== Finance management =====

    @Override
    public PageResponseData<ExpenseApprovalItem> listPendingExpenseApprovals(int pageNum, int pageSize, String keyword) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<ExpenseApprovalItem> records = mapper.listPendingExpenseApprovals(safeKeyword, offset, safePageSize);
        long total = mapper.countPendingExpenseApprovals(safeKeyword);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    public ExpenseDetailData getExpenseApprovalDetail(Long expenseId) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ExpenseDetailData detail = mapper.findExpenseDetailById(expenseId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "expense not found");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decisionExpenseApproval(Long expenseId, ExpenseApprovalDecisionRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ExpenseDetailData expense = mapper.findExpenseDetailById(expenseId);
        if (expense == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "expense not found");
        }
        if (expense.getApproveLevel() != 2) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "this expense does not require school approval");
        }
        if (expense.getExpenseStatus() != 1) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "expense is not pending approval");
        }

        LocalDateTime now = LocalDateTime.now();
        String action = request.getAction().toUpperCase();

        if ("APPROVE".equals(action)) {
            mapper.updateExpenseStatus(expenseId, 2, null, user.getUserId(), now);
            // 写入台账
            BigDecimal prevBalance = mapper.findLatestClubBalance(expense.getClubId());
            if (prevBalance == null) prevBalance = BigDecimal.ZERO;
            BigDecimal newBalance = prevBalance.subtract(expense.getAmount());
            mapper.insertLedger(idGenerator.nextId(), expense.getClubId(), 2, expenseId,
                    expense.getAmount().negate(), newBalance, now,
                    user.getUserId(), user.getUserId(), now);
        } else {
            String rejectReason = request.getRejectReason() != null ? request.getRejectReason().trim() : null;
            mapper.updateExpenseStatus(expenseId, 3, rejectReason, user.getUserId(), now);
        }

        writeAudit("EXPENSE", expenseId, action, user,
                String.valueOf(1), String.valueOf("APPROVE".equals(action) ? 2 : 3),
                "APPROVE".equals(action) ? "school admin approved expense" : limitLength(request.getRejectReason(), 500));
    }

    @Override
    public PageResponseData<LedgerItem> listClubLedger(Long clubId, int pageNum, int pageSize,
                                                        Integer bizType, LocalDateTime startTime, LocalDateTime endTime) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<LedgerItem> records = mapper.listClubLedger(clubId, bizType, startTime, endTime, offset, safePageSize);
        long total = mapper.countClubLedger(clubId, bizType, startTime, endTime);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    public FinanceAuditReportData getFinanceAuditReport(Long clubId, Integer year) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        String clubName = mapper.findClubNameById(clubId);
        if (clubName == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }

        BigDecimal totalIncome = mapper.sumClubIncomeByYear(clubId, year);
        BigDecimal totalExpense = mapper.sumClubExpenseByYear(clubId, year);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        // 合并月度统计
        List<FinanceAuditReportData.MonthlyFinanceStat> incomeStats = mapper.monthlyIncomeStats(clubId, year);
        List<FinanceAuditReportData.MonthlyFinanceStat> expenseStats = mapper.monthlyExpenseStats(clubId, year);
        Map<Integer, FinanceAuditReportData.MonthlyFinanceStat> monthMap = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            monthMap.put(m, new FinanceAuditReportData.MonthlyFinanceStat(m, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        for (FinanceAuditReportData.MonthlyFinanceStat s : incomeStats) {
            monthMap.get(s.getMonth()).setIncome(s.getIncome());
        }
        for (FinanceAuditReportData.MonthlyFinanceStat s : expenseStats) {
            monthMap.get(s.getMonth()).setExpense(s.getExpense());
        }
        List<FinanceAuditReportData.MonthlyFinanceStat> monthlyStats = new ArrayList<>(monthMap.values());
        monthlyStats.sort((a, b) -> a.getMonth() - b.getMonth());

        List<FinanceAuditReportData.CategoryExpenseStat> categoryStats = mapper.categoryExpenseStats(clubId, year);
        long anomalyCount = mapper.countAnomalyExpensesByClubAndYear(clubId, year);

        FinanceAuditReportData report = new FinanceAuditReportData();
        report.setClubId(clubId);
        report.setClubName(clubName);
        report.setYear(year);
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setBalance(balance);
        report.setMonthlyStats(monthlyStats);
        report.setCategoryStats(categoryStats);
        report.setAnomalyCount(anomalyCount);
        return report;
    }

    @Override
    public PageResponseData<AnomalyExpenseItem> listAnomalyExpenses(int pageNum, int pageSize, String keyword) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<AnomalyExpenseItem> records = mapper.listAnomalyExpenses(safeKeyword, offset, safePageSize);
        long total = mapper.countAnomalyExpenses(safeKeyword);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    public ClubStatisticsData getClubStatistics(int year) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        ClubStatisticsData data = new ClubStatisticsData();
        data.setCategoryDistribution(mapper.countClubsByCategory());
        data.setStatusDistribution(mapper.countClubsByStatus());
        data.setYearlyNewCount((int) mapper.countNewClubsByYear(year));
        data.setYearlyCancelCount((int) mapper.countCancelledClubsByYear(year));
        long totalStudents = mapper.countTotalStudents();
        long joinedStudents = mapper.countJoinedStudents();
        data.setTotalStudentCount(totalStudents);
        data.setJoinedStudentCount(joinedStudents);
        data.setCoverageRate(totalStudents > 0 ? Math.round(joinedStudents * 10000.0 / totalStudents) / 100.0 : 0);
        data.setTopActiveClubs(mapper.listTopActiveClubs(year, 10));

        // Member size distribution
        List<ClubStatisticsData.ActiveClub> allClubs = mapper.listTopActiveClubs(year, 9999);
        int s1 = 0, s2 = 0, s3 = 0, s4 = 0;
        for (ClubStatisticsData.ActiveClub c : allClubs) {
            int m = c.getMemberCount();
            if (m <= 10) s1++;
            else if (m <= 30) s2++;
            else if (m <= 50) s3++;
            else s4++;
        }
        data.setMemberSizeDistribution(List.of(
                new ClubStatisticsData.MemberSizeRange("0-10人", s1),
                new ClubStatisticsData.MemberSizeRange("11-30人", s2),
                new ClubStatisticsData.MemberSizeRange("31-50人", s3),
                new ClubStatisticsData.MemberSizeRange("50人以上", s4)
        ));
        return data;
    }

    @Override
    public EventStatisticsData getEventStatistics(int year) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        EventStatisticsData data = new EventStatisticsData();
        data.setTotalEventCount((int) mapper.countEventsByYear(year));
        data.setTotalSignupCount(mapper.countTotalSignupsByYear(year));
        data.setMonthlyEventStats(mapper.monthlyEventStatsByYear(year));
        data.setTopEventClubs(mapper.listTopEventClubs(year, 10));
        data.setStatusDistribution(mapper.countEventsByStatusAndYear(year));
        return data;
    }

    @Override
    public FinanceStatisticsData getFinanceStatistics(int year) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        FinanceStatisticsData data = new FinanceStatisticsData();
        List<FinanceStatisticsData.IncomeByType> incomeByType = mapper.sumIncomeByTypeAndYear(year);
        data.setIncomeByType(incomeByType);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal schoolFunding = BigDecimal.ZERO;
        for (FinanceStatisticsData.IncomeByType item : incomeByType) {
            totalIncome = totalIncome.add(item.getAmount());
            if ("学校拨款".equals(item.getTypeName())) schoolFunding = item.getAmount();
        }
        data.setTotalSchoolFunding(schoolFunding);
        data.setTotalIncome(totalIncome);

        BigDecimal totalExpense = mapper.sumTotalExpenseByYear(year);
        data.setTotalExpense(totalExpense);
        data.setTotalBalance(totalIncome.subtract(totalExpense));
        data.setClubFinanceList(mapper.listClubFinanceSummary(year));
        data.setMonthlyFinanceStats(mapper.monthlyFinanceStatsByYear(year));
        return data;
    }

    // ===== Config management =====

    @Override
    public Map<String, String> getExpenseThreshold() {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        String value = cacheService.getConfig("config:approval:expense-threshold");
        return Map.of("value", value != null ? value : "500");
    }

    @Override
    public void updateExpenseThreshold(String value) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        if (value == null || value.isBlank()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "阈值不能为空");
        }
        try {
            BigDecimal bd = new BigDecimal(value);
            if (bd.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "阈值不能为负数");
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "阈值格式不正确");
        }
        cacheService.setConfig("config:approval:expense-threshold", value);
    }

    @Override
    public Map<String, Object> getClubApprovalStages() {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        String json = cacheService.getConfig("config:approval:club-stages");
        String defaultStages = "[{\"from\":1,\"to\":2},{\"from\":2,\"to\":3},{\"from\":3,\"to\":4}]";
        return Map.of("stages", json != null ? json : defaultStages);
    }

    @Override
    public void updateClubApprovalStages(String stagesJson) {
        AccessChecker.requireRole(RoleCode.SCHOOL_ADMIN);
        if (stagesJson == null || stagesJson.isBlank()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "审批阶段配置不能为空");
        }
        try {
            objectMapper.readValue(stagesJson, new TypeReference<List<Map<String, Integer>>>() {});
        } catch (Exception e) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "审批阶段JSON格式不正确");
        }
        cacheService.setConfig("config:approval:club-stages", stagesJson);
    }
}
