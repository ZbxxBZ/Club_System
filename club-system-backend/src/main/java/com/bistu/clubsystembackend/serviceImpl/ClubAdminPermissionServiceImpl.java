package com.bistu.clubsystembackend.serviceImpl;

import com.bistu.clubsystembackend.entity.ClubJoinApplyRecord;
import com.bistu.clubsystembackend.entity.EventMeta;
import com.bistu.clubsystembackend.entity.request.AddClubMemberRequest;
import com.bistu.clubsystembackend.entity.request.CreateExpenseRequest;
import com.bistu.clubsystembackend.entity.request.CreateEventRequest;
import com.bistu.clubsystembackend.entity.request.CreateIncomeRequest;
import com.bistu.clubsystembackend.entity.request.EventCheckinRequest;
import com.bistu.clubsystembackend.entity.request.SubmitEventSummaryRequest;
import com.bistu.clubsystembackend.entity.request.ClubCancelSubmitRequest;
import com.bistu.clubsystembackend.entity.request.CreateClubPositionRequest;
import com.bistu.clubsystembackend.entity.request.JoinApplyDecisionRequest;
import com.bistu.clubsystembackend.entity.request.UpdateRecruitConfigRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubPositionRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubRequest;
import com.bistu.clubsystembackend.entity.request.UpdateMemberRoleRequest;
import com.bistu.clubsystembackend.entity.response.ClubAdminEventItem;
import com.bistu.clubsystembackend.entity.response.ClubAdminJoinApplyItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventSignupItem;
import com.bistu.clubsystembackend.entity.response.EventSummaryData;
import com.bistu.clubsystembackend.entity.response.ClubBalanceData;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyData;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyItem;
import com.bistu.clubsystembackend.entity.response.ClubInfoData;
import com.bistu.clubsystembackend.entity.response.ClubMemberItem;
import com.bistu.clubsystembackend.entity.response.ClubPositionItem;
import com.bistu.clubsystembackend.entity.response.ClubRecruitConfigData;
import com.bistu.clubsystembackend.entity.response.ExpenseDetailData;
import com.bistu.clubsystembackend.entity.response.FinanceRecordItem;
import com.bistu.clubsystembackend.entity.response.IncomeDetailData;
import com.bistu.clubsystembackend.entity.response.JoinApplyDecisionData;
import com.bistu.clubsystembackend.entity.response.LedgerItem;
import com.bistu.clubsystembackend.entity.response.PageResponseData;
import com.bistu.clubsystembackend.entity.response.PositionDeleteData;
import com.bistu.clubsystembackend.mapper.UserPermissionMapper;
import com.bistu.clubsystembackend.service.CacheService;
import com.bistu.clubsystembackend.service.ClubAdminPermissionService;
import com.bistu.clubsystembackend.util.AccessChecker;
import com.bistu.clubsystembackend.util.BizCode;
import com.bistu.clubsystembackend.util.BusinessException;
import com.bistu.clubsystembackend.util.CurrentUser;
import com.bistu.clubsystembackend.util.IdGenerator;
import com.bistu.clubsystembackend.util.RoleCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class ClubAdminPermissionServiceImpl implements ClubAdminPermissionService {

    private static final int JOIN_STATUS_PENDING = 1;
    private static final int JOIN_STATUS_JOINED = 2;
    private static final int JOIN_STATUS_REJECTED = 3;

    private final UserPermissionMapper mapper;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;
    private final CacheService cacheService;

    public ClubAdminPermissionServiceImpl(UserPermissionMapper mapper, IdGenerator idGenerator,
                                          ObjectMapper objectMapper, CacheService cacheService) {
        this.mapper = mapper;
        this.idGenerator = idGenerator;
        this.objectMapper = objectMapper;
        this.cacheService = cacheService;
    }

    public ClubInfoData getMyClub() {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        ClubInfoData club = mapper.findClubInfoById(clubId);
        if (club == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND);
        }
        return club;
    }

    public void updateMyClub(UpdateClubRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int rows = mapper.updateClubInfo(
                clubId,
                request.getClubName(),
                request.getCategory(),
                request.getIntroduction(),
                request.getPurpose(),
                request.getInstructorName(),
                user.getUserId(),
                LocalDateTime.now()
        );
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND);
        }
    }

    public PageResponseData<ClubMemberItem> listMyClubMembers(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubMemberItem> records = mapper.listClubMembers(clubId, offset, safePageSize);
        long total = mapper.countClubMembers(clubId);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public void updateMyMemberRole(Long memberId, UpdateMemberRoleRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        Long positionId = resolvePositionId(clubId, request.getPositionId(), request.getPositionName(), true);
        int rows = mapper.updateClubMemberPosition(clubId, memberId, positionId, user.getUserId(), LocalDateTime.now());
        if (rows == 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "member not found in this club");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addMyClubMember(AddClubMemberRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);

        Integer userExists = mapper.existsUserById(request.getUserId());
        if (userExists == null || userExists == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "user not found");
        }

        Long positionId = resolvePositionId(clubId, request.getPositionId(), request.getPositionName(), true);
        Integer memberStatus = mapper.findClubMemberStatus(clubId, request.getUserId());
        LocalDateTime now = LocalDateTime.now();
        if (memberStatus == null) {
            mapper.insertClubMemberWithPosition(
                    idGenerator.nextId(),
                    clubId,
                    request.getUserId(),
                    positionId,
                    user.getUserId(),
                    user.getUserId(),
                    now
            );
            return;
        }
        if (memberStatus == 1) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "member already exists in this club");
        }
        mapper.reactivateClubMemberWithPosition(clubId, request.getUserId(), positionId, user.getUserId(), now);
    }

    public void removeMyMember(Long memberId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int rows = mapper.removeClubMember(clubId, memberId, user.getUserId(), LocalDateTime.now());
        if (rows == 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "member not found in this club");
        }
    }

    public PageResponseData<ClubAdminJoinApplyItem> listMyClubJoinApplies(int pageNum, int pageSize, String status) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 200));
        int offset = (safePageNum - 1) * safePageSize;
        Integer joinStatus = parseJoinStatus(status);

        List<ClubAdminJoinApplyItem> records = mapper.listClubJoinApplies(clubId, joinStatus, offset, safePageSize);
        long total = mapper.countClubJoinApplies(clubId, joinStatus);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public JoinApplyDecisionData decideJoinApply(Long applyId, JoinApplyDecisionRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        ClubJoinApplyRecord record = mapper.findClubJoinApplyByIdForUpdate(applyId);
        if (record == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "join apply not found");
        }
        if (!Objects.equals(clubId, record.getClubId())) {
            throw new BusinessException(BizCode.NO_PERMISSION.getCode(), "cannot operate out-of-scope apply");
        }
        if (record.getJoinStatus() == null || record.getJoinStatus() != JOIN_STATUS_PENDING) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "apply is already reviewed");
        }

        String decision = request.getDecision().trim().toUpperCase(Locale.ROOT);
        LocalDateTime now = LocalDateTime.now();
        if ("APPROVE".equals(decision)) {
            mapper.updateClubJoinApplyDecision(
                    applyId,
                    JOIN_STATUS_JOINED,
                    user.getUserId(),
                    now,
                    null,
                    user.getUserId(),
                    now
            );
            Integer memberStatus = mapper.findClubMemberStatus(record.getClubId(), record.getUserId());
            if (memberStatus == null) {
                mapper.insertClubMemberWithPosition(
                        idGenerator.nextId(),
                        record.getClubId(),
                        record.getUserId(),
                        null,
                        user.getUserId(),
                        user.getUserId(),
                        now
                );
            } else if (memberStatus != 1) {
                mapper.reactivateClubMemberWithPosition(
                        record.getClubId(),
                        record.getUserId(),
                        null,
                        user.getUserId(),
                        now
                );
            }
            mapper.insertAuditLog(
                    idGenerator.nextId(),
                    "CLUB_JOIN_APPLY",
                    applyId,
                    "APPROVE",
                    user.getUserId(),
                    user.getRoleCode(),
                    now,
                    null,
                    null,
                    null,
                    null,
                    "join apply approved"
            );
            return new JoinApplyDecisionData(applyId, "JOINED", now);
        }

        String rejectReason = normalizeText(request.getRejectReason());
        if ("REJECT".equals(decision) && rejectReason == null) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "rejectReason is required when decision is REJECT");
        }
        mapper.updateClubJoinApplyDecision(
                applyId,
                JOIN_STATUS_REJECTED,
                user.getUserId(),
                now,
                rejectReason,
                user.getUserId(),
                now
        );
        mapper.insertAuditLog(
                idGenerator.nextId(),
                "CLUB_JOIN_APPLY",
                applyId,
                "REJECT",
                user.getUserId(),
                user.getRoleCode(),
                now,
                null,
                null,
                null,
                null,
                rejectReason
        );
        return new JoinApplyDecisionData(applyId, "REJECTED", now);
    }

    public PageResponseData<FinanceRecordItem> listMyFinances(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<FinanceRecordItem> records = mapper.listClubFinances(clubId, offset, safePageSize);
        long total = mapper.countClubFinances(clubId);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubCancelApplyData submitMyClubCancel(ClubCancelSubmitRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        Integer clubStatus = mapper.findClubStatusById(clubId);
        if (clubStatus == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        if (clubStatus == 5) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "canceled club cannot submit cancel apply");
        }
        ClubCancelApplyData latest = mapper.findLatestClubCancelByClubId(clubId);
        if (latest != null && latest.getCancelStatus() != null
                && latest.getCancelStatus() >= 1 && latest.getCancelStatus() <= 4) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "there is an in-progress cancel apply");
        }

        LocalDateTime now = LocalDateTime.now();
        Long cancelId = idGenerator.nextId();
        mapper.insertClubCancel(
                cancelId,
                clubId,
                request.getApplyType(),
                request.getApplyReason(),
                request.getAssetSettlementUrl(),
                1,
                user.getUserId(),
                user.getUserId(),
                now
        );
        if (clubStatus != 4) {
            mapper.updateClubStatus(clubId, 4, user.getUserId(), now);
        }

        ClubCancelApplyData data = new ClubCancelApplyData();
        data.setId(cancelId);
        data.setClubId(clubId);
        data.setApplyType(request.getApplyType());
        data.setApplyReason(request.getApplyReason());
        data.setAssetSettlementUrl(request.getAssetSettlementUrl());
        data.setCancelStatus(1);
        data.setCreatedAt(now);
        return data;
    }

    public PageResponseData<ClubCancelApplyItem> listMyClubCancelApplies(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubCancelApplyItem> records = mapper.listMyClubCancelApplies(clubId, offset, safePageSize);
        long total = mapper.countMyClubCancelApplies(clubId);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public ClubRecruitConfigData getMyClubRecruitConfig() {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        ClubRecruitConfigData data = mapper.findClubRecruitConfigById(clubId);
        if (data == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubRecruitConfigData updateMyClubRecruitConfig(UpdateRecruitConfigRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);

        if (!request.getRecruitEndAt().isAfter(request.getRecruitStartAt())) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "recruitEndAt must be later than recruitStartAt");
        }
        if (request.getRecruitLimit() == null || request.getRecruitLimit() <= 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "recruitLimit must be positive");
        }

        Integer clubStatus = mapper.findClubStatusById(clubId);
        if (clubStatus == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        if ("OPEN".equals(request.getRecruitStatus()) && clubStatus != 2) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "current club status does not allow OPEN recruit");
        }

        LocalDateTime now = LocalDateTime.now();
        int rows = mapper.updateClubRecruitConfig(
                clubId,
                request.getRecruitStartAt(),
                request.getRecruitEndAt(),
                request.getRecruitLimit(),
                request.getRecruitStatus(),
                user.getUserId(),
                now
        );
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        ClubRecruitConfigData data = mapper.findClubRecruitConfigById(clubId);
        if (data == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }
        return data;
    }

    public PageResponseData<ClubPositionItem> listMyClubPositions(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 200));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubPositionItem> records = mapper.listClubPositions(clubId, offset, safePageSize);
        long total = mapper.countClubPositions(clubId);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubPositionItem createMyClubPosition(CreateClubPositionRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        Long parentId = request.getParentPositionId();
        Integer levelNo = calculateLevelNo(clubId, parentId);
        Integer sortNo = nextSortNo(clubId, parentId);

        Long id = idGenerator.nextId();
        LocalDateTime now = LocalDateTime.now();
        mapper.insertClubPosition(
                id,
                clubId,
                request.getPositionName().trim(),
                parentId,
                levelNo,
                sortNo,
                user.getUserId(),
                user.getUserId(),
                now
        );
        ClubPositionItem item = mapper.findClubPositionById(clubId, id);
        if (item == null) {
            throw new BusinessException(BizCode.SYSTEM_ERROR.getCode(), "position create failed");
        }
        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubPositionItem updateMyClubPosition(Long positionId, UpdateClubPositionRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        ClubPositionItem current = requirePosition(clubId, positionId);
        Long parentId = request.getParentPositionId();

        if (parentId != null) {
            if (Objects.equals(parentId, positionId)) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "parentPositionId cannot be self");
            }
            ensureNoCycle(clubId, positionId, parentId);
        }

        int levelNo = calculateLevelNo(clubId, parentId);
        Integer sortNo = request.getSortNo() != null ? request.getSortNo() : current.getSortNo();
        int rows = mapper.updateClubPosition(
                clubId,
                positionId,
                request.getPositionName().trim(),
                parentId,
                levelNo,
                sortNo,
                user.getUserId(),
                LocalDateTime.now()
        );
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "position not found");
        }
        ClubPositionItem item = mapper.findClubPositionById(clubId, positionId);
        if (item == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "position not found");
        }
        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    public PositionDeleteData deleteMyClubPosition(Long positionId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        requirePosition(clubId, positionId);

        long childCount = mapper.countChildPositions(clubId, positionId);
        if (childCount > 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "position has child positions");
        }
        long memberCount = mapper.countMembersByPosition(clubId, positionId);
        if (memberCount > 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "position is occupied by members");
        }
        int rows = mapper.deleteClubPosition(clubId, positionId, user.getUserId(), LocalDateTime.now());
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "position not found");
        }
        return new PositionDeleteData(positionId);
    }

    private Long resolveMyClubId(CurrentUser user) {
        Long clubId = user.getClubId();
        if (clubId != null) {
            return clubId;
        }
        clubId = mapper.findManagedClubIdByUserId(user.getUserId());
        if (clubId == null) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "club admin scope is not bound");
        }
        return clubId;
    }

    private Long resolvePositionId(Long clubId, Long positionId, String positionName, boolean allowNullResult) {
        Long byId = positionId;
        String normalizedName = normalizeText(positionName);
        Long byName = normalizedName == null ? null : mapper.findPositionIdByName(clubId, normalizedName);

        if (byId != null) {
            ClubPositionItem item = mapper.findClubPositionById(clubId, byId);
            if (item == null) {
                throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "position not found");
            }
            if (byName != null && !Objects.equals(byName, byId)) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "positionId and positionName are inconsistent");
            }
            return byId;
        }

        if (byName != null) {
            return byName;
        }

        if (!allowNullResult) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "position is required");
        }
        return null;
    }

    private ClubPositionItem requirePosition(Long clubId, Long positionId) {
        ClubPositionItem item = mapper.findClubPositionById(clubId, positionId);
        if (item == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "position not found");
        }
        return item;
    }

    private int calculateLevelNo(Long clubId, Long parentId) {
        if (parentId == null) {
            return 1;
        }
        ClubPositionItem parent = requirePosition(clubId, parentId);
        if (parent.getLevelNo() == null) {
            return 1;
        }
        return parent.getLevelNo() + 1;
    }

    private void ensureNoCycle(Long clubId, Long sourcePositionId, Long newParentId) {
        Long current = newParentId;
        while (current != null) {
            if (Objects.equals(current, sourcePositionId)) {
                throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "position hierarchy cannot form a cycle");
            }
            ClubPositionItem item = mapper.findClubPositionById(clubId, current);
            if (item == null) {
                throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "parent position not found");
            }
            current = item.getParentPositionId();
        }
    }

    private Integer nextSortNo(Long clubId, Long parentId) {
        Integer maxSortNo = mapper.findMaxSortNoByParent(clubId, parentId);
        if (maxSortNo == null) {
            return 10;
        }
        return maxSortNo + 10;
    }

    private Integer parseJoinStatus(String status) {
        String normalized = normalizeText(status);
        if (normalized == null) {
            return null;
        }
        String upper = normalized.toUpperCase(Locale.ROOT);
        if ("PENDING".equals(upper)) {
            return JOIN_STATUS_PENDING;
        }
        if ("JOINED".equals(upper)) {
            return JOIN_STATUS_JOINED;
        }
        if ("REJECTED".equals(upper)) {
            return JOIN_STATUS_REJECTED;
        }
        throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "status must be PENDING/JOINED/REJECTED");
    }

    // ===== Event management =====

    @Override
    public PageResponseData<ClubAdminEventItem> listMyEvents(int pageNum, int pageSize, Integer eventStatus) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubAdminEventItem> records = mapper.listClubEvents(clubId, eventStatus, offset, safePageSize);
        long total = mapper.countClubEvents(clubId, eventStatus);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDetailData createMyEvent(CreateEventRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        validateEventTimes(request);
        Long eventId = idGenerator.nextId();
        LocalDateTime now = LocalDateTime.now();
        mapper.insertEvent(eventId, clubId,
                normalizeText(request.getTitle()), request.getContent(),
                normalizeText(request.getLocation()),
                request.getStartAt(), request.getEndAt(),
                request.getSignupStartAt(), request.getSignupEndAt(),
                request.getLimitCount(),
                Boolean.TRUE.equals(request.getOnlyMember()) ? 1 : 0,
                2, normalizeText(request.getSafetyPlanUrl()),
                null,
                user.getUserId(), user.getUserId(), now);
        return mapper.findEventDetailById(eventId);
    }

    @Override
    public EventDetailData getMyEventDetail(Long eventId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventDetailData detail = mapper.findEventDetailById(eventId);
        if (detail == null || !Objects.equals(detail.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDetailData resubmitMyEvent(Long eventId, CreateEventRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        if (meta.getEventStatus() != 5) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "only rejected events can be resubmitted");
        }
        validateEventTimes(request);
        LocalDateTime now = LocalDateTime.now();
        mapper.updateEventForResubmit(eventId, clubId,
                normalizeText(request.getTitle()), request.getContent(),
                normalizeText(request.getLocation()),
                request.getStartAt(), request.getEndAt(),
                request.getSignupStartAt(), request.getSignupEndAt(),
                request.getLimitCount(),
                Boolean.TRUE.equals(request.getOnlyMember()) ? 1 : 0,
                normalizeText(request.getSafetyPlanUrl()),
                null,
                user.getUserId(), now);
        return mapper.findEventDetailById(eventId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMyEventCheckinCode(Long eventId, String checkinCode) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        if (meta.getEventStatus() != 3 && meta.getEventStatus() != 6 && meta.getEventStatus() != 4) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "can only set checkin code for active or ended events");
        }
        String normalized = normalizeText(checkinCode);
        if (normalized != null && normalized.length() > 20) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "checkinCode length must be <= 20");
        }
        int rows = mapper.updateEventCheckinCode(eventId, normalized, LocalDateTime.now());
        if (rows == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
    }

    @Override
    public PageResponseData<EventSignupItem> listMyEventSignups(Long eventId, int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 200));
        int offset = (safePageNum - 1) * safePageSize;
        List<EventSignupItem> records = mapper.listEventSignups(eventId, offset, safePageSize);
        long total = mapper.countEventSignups(eventId);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkinMyEvent(Long eventId, EventCheckinRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        if (meta.getEventStatus() != 3 && meta.getEventStatus() != 6 && meta.getEventStatus() != 4) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "event is not active");
        }
        Integer signed = mapper.hasSigned(eventId, request.getUserId());
        if (signed == null || signed == 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "user has not signed up");
        }
        Integer checked = mapper.hasCheckedIn(eventId, request.getUserId());
        if (checked != null && checked > 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "user already checked in");
        }
        LocalDateTime now = LocalDateTime.now();
        int restored = mapper.restoreEventCheckin(eventId, request.getUserId(),
                request.getCheckinType(), now, request.getDeviceNo(),
                user.getUserId(), now);
        if (restored == 0) {
            mapper.insertEventCheckin(idGenerator.nextId(), eventId, request.getUserId(),
                    request.getCheckinType(), now, request.getDeviceNo(),
                    user.getUserId(), user.getUserId(), now);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCheckinMyEvent(Long eventId, Long userId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        Integer checked = mapper.hasCheckedIn(eventId, userId);
        if (checked == null || checked == 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "user has not checked in");
        }
        mapper.cancelEventCheckin(eventId, userId, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventSummaryData submitMyEventSummary(Long eventId, SubmitEventSummaryRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        if (meta.getEventStatus() != 4) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "event has not ended yet");
        }
        LocalDateTime now = LocalDateTime.now();
        String summaryImagesJson = serializeImageList(request.getSummaryImages());
        EventSummaryData existing = mapper.findEventSummaryByEventId(eventId);
        if (existing != null) {
            mapper.updateEventSummary(eventId, request.getSummaryText(), summaryImagesJson,
                    request.getFeedbackScore(),
                    request.getIssueReflection(), normalizeText(request.getAttachmentUrl()),
                    user.getUserId(), now);
        } else {
            mapper.insertEventSummary(idGenerator.nextId(), eventId, request.getSummaryText(),
                    summaryImagesJson, request.getFeedbackScore(), request.getIssueReflection(),
                    normalizeText(request.getAttachmentUrl()),
                    user.getUserId(), user.getUserId(), now);
        }
        EventSummaryData result = mapper.findEventSummaryByEventId(eventId);
        if (result != null) {
            result.setSummaryImages(deserializeImageList(result.getSummaryImages()));
        }
        return result;
    }

    @Override
    public EventSummaryData getMyEventSummary(Long eventId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || !Objects.equals(meta.getClubId(), clubId)) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        EventSummaryData result = mapper.findEventSummaryByEventId(eventId);
        if (result != null) {
            result.setSummaryImages(deserializeImageList(result.getSummaryImagesJson()));
        }
        return result;
    }

    private void validateEventTimes(CreateEventRequest request) {
        if (request.getLimitCount() == null || request.getLimitCount() < 1) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "limitCount must be >= 1");
        }
        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "endAt must be after startAt");
        }
        if (request.getSignupStartAt() != null && request.getSignupEndAt() != null) {
            if (request.getSignupEndAt().isBefore(request.getSignupStartAt())) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "signupEndAt must be after signupStartAt");
            }
        }
        if (request.getSignupEndAt() != null && request.getStartAt().isBefore(request.getSignupEndAt())) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "活动开始时间不能早于报名截止时间");
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String serializeImageList(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(images);
        } catch (JsonProcessingException e) {
            throw new BusinessException(BizCode.SYSTEM_ERROR.getCode(), "Failed to serialize image list");
        }
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

    private static final BigDecimal DEFAULT_AUTO_APPROVE_THRESHOLD = new BigDecimal("500");

    private BigDecimal getAutoApproveThreshold() {
        String configValue = cacheService.getConfig("config:approval:expense-threshold");
        if (configValue != null) {
            try {
                return new BigDecimal(configValue);
            } catch (NumberFormatException e) {
                // fall through to default
            }
        }
        return DEFAULT_AUTO_APPROVE_THRESHOLD;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IncomeDetailData createIncome(CreateIncomeRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);

        if (request.getIncomeType() == null || request.getIncomeType() < 1 || request.getIncomeType() > 3) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "incomeType must be 1, 2 or 3");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "amount must be > 0");
        }

        Long incomeId = idGenerator.nextId();
        LocalDateTime now = LocalDateTime.now();
        mapper.insertIncome(incomeId, clubId, request.getIncomeType(), request.getAmount(),
                request.getOccurAt(), request.getProofUrl(), request.getSourceDesc(),
                user.getUserId(), user.getUserId(), now);

        // 写入台账
        BigDecimal prevBalance = mapper.findLatestClubBalance(clubId);
        if (prevBalance == null) prevBalance = BigDecimal.ZERO;
        BigDecimal newBalance = prevBalance.add(request.getAmount());
        mapper.insertLedger(idGenerator.nextId(), clubId, 1, incomeId,
                request.getAmount(), newBalance, request.getOccurAt(),
                user.getUserId(), user.getUserId(), now);

        // 审计日志
        mapper.insertAuditLog(idGenerator.nextId(), "INCOME", incomeId, "CREATE",
                user.getUserId(), user.getRoleCode(), now, null, null, null, null,
                "income created");

        IncomeDetailData data = new IncomeDetailData();
        data.setId(incomeId);
        data.setClubId(clubId);
        data.setIncomeType(request.getIncomeType());
        data.setIncomeTypeName(incomeTypeName(request.getIncomeType()));
        data.setAmount(request.getAmount());
        data.setOccurAt(request.getOccurAt());
        data.setProofUrl(request.getProofUrl());
        data.setSourceDesc(request.getSourceDesc());
        data.setCreatedAt(now);
        return data;
    }

    @Override
    public PageResponseData<IncomeDetailData> listMyIncomes(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<IncomeDetailData> records = mapper.listClubIncomes(clubId, offset, safePageSize);
        long total = mapper.countClubIncomes(clubId);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseDetailData createExpense(CreateExpenseRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "amount must be > 0");
        }

        // 余额校验：可用余额 = 当前余额 - 待审核支出总额
        BigDecimal currentBalance = mapper.findLatestClubBalance(clubId);
        if (currentBalance == null) currentBalance = BigDecimal.ZERO;
        BigDecimal pendingSum = mapper.sumPendingExpensesByClub(clubId, null);
        BigDecimal availableBalance = currentBalance.subtract(pendingSum);
        if (request.getAmount().compareTo(availableBalance) > 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(),
                    "余额不足，当前可用余额为 " + availableBalance.setScale(2) + " 元（余额 "
                            + currentBalance.setScale(2) + " - 待审核 " + pendingSum.setScale(2) + "）");
        }

        int approveLevel = request.getAmount().compareTo(getAutoApproveThreshold()) <= 0 ? 1 : 2;
        int expenseStatus = (approveLevel == 1) ? 2 : 1; // 1=社团自审自动通过, 2=待学校审核

        Long expenseId = idGenerator.nextId();
        LocalDateTime now = LocalDateTime.now();
        mapper.insertExpense(expenseId, clubId, request.getEventId(), request.getCategory(),
                request.getAmount(), request.getPayeeName(), request.getPayeeAccount(),
                request.getInvoiceUrl(), request.getExpenseDesc(),
                approveLevel, expenseStatus,
                user.getUserId(), user.getUserId(), now);

        // <=500 自动通过, 直接写入台账
        if (approveLevel == 1) {
            BigDecimal prevBalance = mapper.findLatestClubBalance(clubId);
            if (prevBalance == null) prevBalance = BigDecimal.ZERO;
            BigDecimal newBalance = prevBalance.subtract(request.getAmount());
            mapper.insertLedger(idGenerator.nextId(), clubId, 2, expenseId,
                    request.getAmount().negate(), newBalance, now,
                    user.getUserId(), user.getUserId(), now);
        }

        // 审计日志
        mapper.insertAuditLog(idGenerator.nextId(), "EXPENSE", expenseId, "CREATE",
                user.getUserId(), user.getRoleCode(), now, null, null, null, null,
                approveLevel == 1 ? "expense auto-approved (<=500)" : "expense pending school review (>500)");

        ExpenseDetailData data = new ExpenseDetailData();
        data.setId(expenseId);
        data.setClubId(clubId);
        data.setEventId(request.getEventId());
        data.setCategory(request.getCategory());
        data.setAmount(request.getAmount());
        data.setPayeeName(request.getPayeeName());
        data.setPayeeAccount(request.getPayeeAccount());
        data.setInvoiceUrl(request.getInvoiceUrl());
        data.setExpenseDesc(request.getExpenseDesc());
        data.setApproveLevel(approveLevel);
        data.setExpenseStatus(expenseStatus);
        data.setCreatedAt(now);
        data.setUpdatedAt(now);
        return data;
    }

    @Override
    public PageResponseData<ExpenseDetailData> listMyExpenses(int pageNum, int pageSize, Integer expenseStatus) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ExpenseDetailData> records = mapper.listClubExpenses(clubId, expenseStatus, offset, safePageSize);
        long total = mapper.countClubExpenses(clubId, expenseStatus);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseDetailData resubmitExpense(Long expenseId, CreateExpenseRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);

        ExpenseDetailData existing = mapper.findExpenseDetailById(expenseId);
        if (existing == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "expense not found");
        }
        if (!existing.getClubId().equals(clubId)) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "无权操作此支出");
        }
        if (existing.getExpenseStatus() != 3) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "只有被驳回的支出才能重新提交");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "amount must be > 0");
        }

        // 余额校验：排除当前这笔（已驳回不占额度）的待审核支出
        BigDecimal currentBalance = mapper.findLatestClubBalance(clubId);
        if (currentBalance == null) currentBalance = BigDecimal.ZERO;
        BigDecimal pendingSum = mapper.sumPendingExpensesByClub(clubId, expenseId);
        BigDecimal availableBalance = currentBalance.subtract(pendingSum);
        if (request.getAmount().compareTo(availableBalance) > 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(),
                    "余额不足，当前可用余额为 " + availableBalance.setScale(2) + " 元（余额 "
                            + currentBalance.setScale(2) + " - 待审核 " + pendingSum.setScale(2) + "）");
        }

        int approveLevel = request.getAmount().compareTo(getAutoApproveThreshold()) <= 0 ? 1 : 2;
        int expenseStatus = (approveLevel == 1) ? 2 : 1;

        LocalDateTime now = LocalDateTime.now();
        int rows = mapper.resubmitExpense(expenseId, request.getCategory(), request.getAmount(),
                request.getPayeeName(), request.getPayeeAccount(), request.getInvoiceUrl(),
                request.getExpenseDesc(), approveLevel, expenseStatus, user.getUserId(), now);
        if (rows == 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "重新提交失败，状态已变更");
        }

        if (approveLevel == 1) {
            BigDecimal prevBalance = mapper.findLatestClubBalance(clubId);
            if (prevBalance == null) prevBalance = BigDecimal.ZERO;
            BigDecimal newBalance = prevBalance.subtract(request.getAmount());
            mapper.insertLedger(idGenerator.nextId(), clubId, 2, expenseId,
                    request.getAmount().negate(), newBalance, now,
                    user.getUserId(), user.getUserId(), now);
        }

        mapper.insertAuditLog(idGenerator.nextId(), "EXPENSE", expenseId, "RESUBMIT",
                user.getUserId(), user.getRoleCode(), now, null, null, null, null,
                approveLevel == 1 ? "expense resubmitted, auto-approved (<=500)" : "expense resubmitted, pending school review (>500)");

        ExpenseDetailData data = new ExpenseDetailData();
        data.setId(expenseId);
        data.setClubId(clubId);
        data.setCategory(request.getCategory());
        data.setAmount(request.getAmount());
        data.setPayeeName(request.getPayeeName());
        data.setPayeeAccount(request.getPayeeAccount());
        data.setInvoiceUrl(request.getInvoiceUrl());
        data.setExpenseDesc(request.getExpenseDesc());
        data.setApproveLevel(approveLevel);
        data.setExpenseStatus(expenseStatus);
        data.setUpdatedAt(now);
        return data;
    }

    @Override
    public PageResponseData<LedgerItem> listMyLedger(int pageNum, int pageSize, Integer bizType,
                                                      LocalDateTime startTime, LocalDateTime endTime) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<LedgerItem> records = mapper.listClubLedger(clubId, bizType, startTime, endTime, offset, safePageSize);
        long total = mapper.countClubLedger(clubId, bizType, startTime, endTime);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Override
    public ClubBalanceData getMyBalance() {
        CurrentUser user = AccessChecker.requireRole(RoleCode.CLUB_ADMIN);
        Long clubId = resolveMyClubId(user);
        BigDecimal balance = mapper.findLatestClubBalance(clubId);
        if (balance == null) balance = BigDecimal.ZERO;
        BigDecimal pendingSum = mapper.sumPendingExpensesByClub(clubId, null);
        return new ClubBalanceData(clubId, balance, pendingSum);
    }

    private String incomeTypeName(Integer type) {
        if (type == null) return "其他";
        switch (type) {
            case 1: return "学校拨款";
            case 2: return "赞助收入";
            case 3: return "成员会费";
            default: return "其他";
        }
    }
}
