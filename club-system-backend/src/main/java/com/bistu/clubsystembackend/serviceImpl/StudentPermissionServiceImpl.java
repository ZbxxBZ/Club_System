package com.bistu.clubsystembackend.serviceImpl;

import com.bistu.clubsystembackend.entity.AuthUser;
import com.bistu.clubsystembackend.entity.ClubJoinEligibilityMeta;
import com.bistu.clubsystembackend.entity.EventMeta;
import com.bistu.clubsystembackend.entity.EventSignupMeta;
import com.bistu.clubsystembackend.entity.request.ClubApplySubmitRequest;
import com.bistu.clubsystembackend.entity.request.ClubJoinApplySubmitRequest;
import com.bistu.clubsystembackend.entity.request.StudentCheckinRequest;
import com.bistu.clubsystembackend.entity.request.UpdateProfileRequest;
import com.bistu.clubsystembackend.entity.response.ClubApplyMineItem;
import com.bistu.clubsystembackend.entity.response.ClubApplySubmitData;
import com.bistu.clubsystembackend.entity.response.ClubJoinApplyMineItem;
import com.bistu.clubsystembackend.entity.response.ClubJoinApplySubmitData;
import com.bistu.clubsystembackend.entity.response.ClubPublicItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventOpenItem;
import com.bistu.clubsystembackend.entity.response.FileUploadData;
import com.bistu.clubsystembackend.entity.response.MyJoinedClubItem;
import com.bistu.clubsystembackend.entity.response.PageResponseData;
import com.bistu.clubsystembackend.entity.response.UserProfileData;
import com.bistu.clubsystembackend.mapper.AuthMapper;
import com.bistu.clubsystembackend.mapper.UserPermissionMapper;
import com.bistu.clubsystembackend.service.CacheService;
import com.bistu.clubsystembackend.service.StudentPermissionService;
import com.bistu.clubsystembackend.util.AccessChecker;
import com.bistu.clubsystembackend.util.BizCode;
import com.bistu.clubsystembackend.util.BusinessException;
import com.bistu.clubsystembackend.util.CosUtil;
import com.bistu.clubsystembackend.util.CurrentUser;
import com.bistu.clubsystembackend.util.IdGenerator;
import com.bistu.clubsystembackend.util.RoleCode;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class StudentPermissionServiceImpl implements StudentPermissionService {

    private static final int CLUB_STATUS_ACTIVE = 2;
    private static final int CLUB_APPLY_APPROVED = 4;
    private static final int JOIN_STATUS_PENDING = 1;
    private static final int JOIN_STATUS_JOINED = 2;
    private static final int JOIN_STATUS_REJECTED = 3;

    private final UserPermissionMapper mapper;
    private final AuthMapper authMapper;
    private final IdGenerator idGenerator;
    private final CosUtil cosUtil;
    private final PasswordEncoder passwordEncoder;
    private final CacheService cacheService;

    private static final java.util.regex.Pattern PASSWORD_PATTERN =
            java.util.regex.Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,64}$");

    public StudentPermissionServiceImpl(UserPermissionMapper mapper,
                                        AuthMapper authMapper,
                                        IdGenerator idGenerator,
                                        CosUtil cosUtil,
                                        PasswordEncoder passwordEncoder,
                                        CacheService cacheService) {
        this.mapper = mapper;
        this.authMapper = authMapper;
        this.idGenerator = idGenerator;
        this.cosUtil = cosUtil;
        this.passwordEncoder = passwordEncoder;
        this.cacheService = cacheService;
    }

    public PageResponseData<ClubPublicItem> listPublicClubs(int pageNum,
                                                            int pageSize,
                                                            String keyword,
                                                            String category,
                                                            Boolean isRecruiting,
                                                            Boolean onlyApproved,
                                                            Integer applyStatus,
                                                            String status) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        String safeKeyword = normalizeText(keyword);
        String safeCategory = normalizeText(category);

        boolean queryApproved = true;
        int queryApplyStatus = CLUB_APPLY_APPROVED;
        int queryClubStatus = CLUB_STATUS_ACTIVE;

        List<ClubPublicItem> records = mapper.listPublicClubs(
                safeKeyword,
                safeCategory,
                isRecruiting,
                queryApproved,
                queryApplyStatus,
                queryClubStatus,
                user.getUserId(),
                offset,
                safePageSize
        );
        long total = mapper.countPublicClubs(
                safeKeyword,
                safeCategory,
                isRecruiting,
                queryApproved,
                queryApplyStatus,
                queryClubStatus,
                user.getUserId()
        );
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public FileUploadData uploadClubApplyFile(MultipartFile file, String bizType) {
        AccessChecker.requireRole(RoleCode.STUDENT, RoleCode.CLUB_ADMIN);
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "file is empty");
        }
        if (!"CLUB_APPLY_CHARTER".equals(bizType)
                && !"CLUB_APPLY_INSTRUCTOR_PROOF".equals(bizType)
                && !"CLUB_CANCEL_ASSET_SETTLEMENT".equals(bizType)
                && !"EVENT_SAFETY_PLAN".equals(bizType)
                && !"EVENT_SUMMARY_ATTACHMENT".equals(bizType)
                && !"EVENT_SUMMARY_IMAGE".equals(bizType)
                && !"FINANCE_PROOF".equals(bizType)
                && !"EXPENSE_INVOICE".equals(bizType)) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(),
                    "bizType is not supported");
        }
        validateFileType(file.getOriginalFilename(), bizType);
        try {
            String key = cosUtil.upload(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), file.getSize());
            return new FileUploadData(cosUtil.getUrl(key));
        } catch (IOException e) {
            throw new BusinessException(BizCode.SYSTEM_ERROR.getCode(), "file upload failed");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubApplySubmitData submitClubApply(ClubApplySubmitRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        Integer memberCount = mapper.countActiveClubMemberships(user.getUserId());
        if (memberCount != null && memberCount > 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "您已加入社团，不能再申请创办新社团");
        }
        Integer exists = mapper.existsClubName(request.getClubName());
        if (exists != null && exists > 0) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "该社团名称已存在，请更换名称");
        }
        LocalDateTime now = LocalDateTime.now();
        Long clubId = idGenerator.nextId();
        Long applyId = idGenerator.nextId();
        mapper.insertClub(
                clubId,
                buildClubCode(clubId),
                request.getClubName(),
                request.getCategory(),
                request.getPurpose(),
                request.getInstructorName(),
                1,
                user.getUserId(),
                user.getUserId(),
                now
        );
        mapper.insertClubApply(
                applyId,
                clubId,
                user.getUserId(),
                request.getCharterUrl(),
                request.getInstructorName(),
                request.getInstructorProofUrl(),
                1,
                "MATERIAL_REVIEW",
                request.getRemark(),
                user.getUserId(),
                user.getUserId(),
                now
        );
        return new ClubApplySubmitData(applyId, clubId, 1, "MATERIAL_REVIEW");
    }

    public PageResponseData<ClubApplyMineItem> listMyClubApplyRecords(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubApplyMineItem> records = mapper.listMyClubApplyRecords(user.getUserId(), offset, safePageSize);
        long total = mapper.countMyClubApplyRecords(user.getUserId());
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClubJoinApplySubmitData joinClub(Long clubId, ClubJoinApplySubmitRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        ClubJoinEligibilityMeta meta = mapper.findClubJoinEligibilityMeta(clubId, CLUB_STATUS_ACTIVE, CLUB_APPLY_APPROVED);
        if (meta == null || meta.getApproved() == null || meta.getApproved() == 0) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "club not found");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!"OPEN".equalsIgnoreCase(meta.getRecruitStatus())) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "club recruit is not open");
        }
        if (meta.getRecruitStartAt() == null || meta.getRecruitEndAt() == null) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "club recruit window is invalid");
        }
        if (now.isBefore(meta.getRecruitStartAt()) || now.isAfter(meta.getRecruitEndAt())) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "club is not in recruit window");
        }
        int activeMemberCount = meta.getActiveMemberCount() == null ? 0 : meta.getActiveMemberCount();
        int pendingApplyCount = meta.getPendingApplyCount() == null ? 0 : meta.getPendingApplyCount();
        if (meta.getRecruitLimit() != null && activeMemberCount + pendingApplyCount >= meta.getRecruitLimit()) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "club recruit quota is full");
        }

        Integer memberStatus = mapper.findClubMemberStatus(clubId, user.getUserId());
        if (memberStatus != null && memberStatus == 1) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "already joined this club");
        }
        Integer latestApplyStatus = mapper.findLatestJoinApplyStatus(clubId, user.getUserId());
        if (latestApplyStatus != null && latestApplyStatus == JOIN_STATUS_PENDING) {
            throw new BusinessException(BizCode.DATA_SCOPE_DENIED.getCode(), "已有待审批的申请，不能重复提交");
        }

        String selfIntro = normalizeText(request == null ? null : request.getSelfIntro());
        String applyReason = normalizeText(request == null ? null : request.getApplyReason());

        Long applyId = idGenerator.nextId();
        mapper.insertClubJoinApply(
                applyId,
                clubId,
                user.getUserId(),
                selfIntro,
                applyReason,
                JOIN_STATUS_PENDING,
                user.getUserId(),
                user.getUserId(),
                now
        );
        return new ClubJoinApplySubmitData(applyId, clubId, "PENDING", now);
    }

    public PageResponseData<ClubJoinApplyMineItem> listMyJoinApplies(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<ClubJoinApplyMineItem> records = mapper.listMyJoinApplies(user.getUserId(), offset, safePageSize);
        long total = mapper.countMyJoinApplies(user.getUserId());
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public PageResponseData<MyJoinedClubItem> listMyJoinedClubs(int pageNum, int pageSize) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        List<MyJoinedClubItem> records = mapper.listMyJoinedClubs(user.getUserId(), offset, safePageSize);
        long total = mapper.countMyJoinedClubs(user.getUserId());
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    public PageResponseData<EventOpenItem> listOpenEvents(int pageNum, int pageSize, String keyword, Boolean onlyMyClubs) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT, RoleCode.CLUB_ADMIN, RoleCode.SCHOOL_ADMIN);
        int safePageNum = Math.max(1, pageNum);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        int offset = (safePageNum - 1) * safePageSize;
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<EventOpenItem> records = mapper.listOpenEvents(user.getUserId(), safeKeyword, onlyMyClubs, offset, safePageSize);
        for (EventOpenItem item : records) {
            item.setCanSignup(item.getEventStatus() != null && item.getEventStatus() == 3);
            if (item.getLimitCount() != null && item.getId() != null) {
                Integer cached = cacheService.getEventSlots(item.getId());
                if (cached != null) {
                    item.setRemainingSlots(cached);
                } else {
                    int signedCount = mapper.countActiveSignups(item.getId());
                    int remaining = Math.max(0, item.getLimitCount() - signedCount);
                    cacheService.cacheEventSlots(item.getId(), remaining);
                    item.setRemainingSlots(remaining);
                }
            }
        }
        long total = mapper.countOpenEvents(user.getUserId(), safeKeyword, onlyMyClubs);
        return new PageResponseData<>(records, total, safePageNum, safePageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public void signupEvent(Long eventId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);

        boolean locked = cacheService.trySignupLock(eventId, user.getUserId(), 5000);
        if (!locked) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "请勿重复提交");
        }

        boolean quotaDecremented = false;
        try {
            EventSignupMeta meta = mapper.selectEventForSignup(eventId);
            if (meta == null || meta.getEventStatus() == null || meta.getEventStatus() != 3) {
                throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "活动未找到或未开始报名");
            }

            LocalDateTime now = LocalDateTime.now();
            if (meta.getSignupStartAt() != null && now.isBefore(meta.getSignupStartAt())) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "报名未开始");
            }
            if (meta.getSignupEndAt() != null && now.isAfter(meta.getSignupEndAt())) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "报名已终止");
            }
            if (meta.getOnlyMember() != null && meta.getOnlyMember() == 1) {
                Integer isMember = mapper.isActiveClubMember(meta.getClubId(), user.getUserId());
                if (isMember == null || isMember == 0) {
                    throw new BusinessException(BizCode.NO_PERMISSION.getCode(), "只有社团成员可报名");
                }
            }
            Integer hasSigned = mapper.hasSigned(eventId, user.getUserId());
            if (hasSigned != null && hasSigned > 0) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "无法重复报名");
            }

            // Atomic quota check via Redis DECR
            if (meta.getLimitCount() != null) {
                try {
                    Long quota = cacheService.getEventQuota(eventId);
                    if (quota == null) {
                        int currentCount = mapper.countActiveSignups(eventId);
                        cacheService.initEventQuota(eventId, meta.getLimitCount() - currentCount);
                    }
                    long afterDecr = cacheService.decrementEventQuota(eventId);
                    quotaDecremented = true;
                    if (afterDecr < 0) {
                        cacheService.incrementEventQuota(eventId);
                        quotaDecremented = false;
                        throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "名额已满");
                    }
                } catch (BusinessException e) {
                    throw e;
                } catch (Exception e) {
                    // Redis unavailable, fall back to DB check
                    quotaDecremented = false;
                    Integer signedCount = mapper.countActiveSignups(eventId);
                    if (signedCount != null && signedCount >= meta.getLimitCount()) {
                        throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "名额已满");
                    }
                }
            }

            Integer hasCanceled = mapper.hasCanceledSignup(eventId, user.getUserId());
            if (hasCanceled != null && hasCanceled > 0) {
                mapper.updateEventSignupStatus(eventId, user.getUserId(), 1, now);
            } else {
                try {
                    mapper.insertEventSignup(idGenerator.nextId(), eventId, meta.getClubId(), user.getUserId(), now);
                } catch (DuplicateKeyException ex) {
                    if (quotaDecremented) {
                        cacheService.incrementEventQuota(eventId);
                    }
                    throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "无法重复报名");
                }
            }

            cacheService.delete("event:slots:" + eventId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            if (quotaDecremented) {
                cacheService.incrementEventQuota(eventId);
            }
            throw e;
        } finally {
            cacheService.releaseSignupLock(eventId, user.getUserId());
        }
    }

    @Override
    public EventDetailData getEventDetail(Long eventId) {
        AccessChecker.requireRole(RoleCode.STUDENT, RoleCode.CLUB_ADMIN, RoleCode.SCHOOL_ADMIN);
        EventDetailData detail = mapper.findEventDetailById(eventId);
        if (detail == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "event not found");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelEventSignup(Long eventId) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null) {
            throw new BusinessException(BizCode.RESOURCE_NOT_FOUND.getCode(), "活动不存在");
        }
        if (meta.getEventStatus() == 6 || meta.getEventStatus() == 4) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "活动已开始，无法取消报名");
        }
        Integer hasSigned = mapper.hasSigned(eventId, user.getUserId());
        if (hasSigned == null || hasSigned == 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "not signed up");
        }
        mapper.updateEventSignupStatus(eventId, user.getUserId(), 2, LocalDateTime.now());
        cacheService.incrementEventQuota(eventId);
        cacheService.delete("event:slots:" + eventId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkinByCode(StudentCheckinRequest request) {
        CurrentUser user = AccessChecker.requireRole(RoleCode.STUDENT);
        String checkinCode = request.getCheckinCode() == null ? null : request.getCheckinCode().trim();
        if (checkinCode == null || checkinCode.isEmpty()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "签到码不能为空");
        }
        Long eventId;
        if (request.getEventId() != null) {
            EventMeta target = mapper.findEventMetaById(request.getEventId());
            if (target == null) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "活动不存在");
            }
            Long matched = mapper.findEventIdByCheckinCode(checkinCode);
            if (matched == null || !matched.equals(request.getEventId())) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "签到码错误");
            }
            eventId = request.getEventId();
        } else {
            eventId = mapper.findEventIdByCheckinCode(checkinCode);
            if (eventId == null) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "签到码无效");
            }
        }
        EventMeta meta = mapper.findEventMetaById(eventId);
        if (meta == null || (meta.getEventStatus() != 3 && meta.getEventStatus() != 6)) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "活动未开始或已结束");
        }
        LocalDateTime now = LocalDateTime.now();
        if (meta.getStartAt() != null && now.isBefore(meta.getStartAt())) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "活动尚未开始，无法签到");
        }
        if (meta.getEndAt() != null && now.isAfter(meta.getEndAt())) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "活动已结束，无法签到");
        }
        Integer signed = mapper.hasSigned(eventId, user.getUserId());
        if (signed == null || signed == 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "您未报名此活动");
        }
        Integer checked = mapper.hasCheckedIn(eventId, user.getUserId());
        if (checked != null && checked > 0) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "您已签到");
        }
        int restored = mapper.restoreEventCheckin(eventId, user.getUserId(),
                request.getCheckinType(), now, request.getDeviceNo(),
                user.getUserId(), now);
        if (restored == 0) {
            mapper.insertEventCheckin(idGenerator.nextId(), eventId, user.getUserId(),
                    request.getCheckinType(), now, request.getDeviceNo(),
                    user.getUserId(), user.getUserId(), now);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UpdateProfileRequest request) {
        CurrentUser currentUser = AccessChecker.requireLogin();
        Long userId = currentUser.getUserId();
        LocalDateTime now = LocalDateTime.now();

        String realName = request.getRealName() == null ? null : request.getRealName().trim();
        if (realName == null || realName.isEmpty()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "真实姓名不能为空");
        }
        authMapper.updateUserRealName(userId, realName, now);

        String newPassword = request.getNewPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            String oldPassword = request.getOldPassword();
            if (oldPassword == null || oldPassword.isEmpty()) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "请输入旧密码");
            }
            AuthUser user = authMapper.findUserById(userId);
            if (user == null) {
                throw new BusinessException(BizCode.ACCOUNT_NOT_FOUND);
            }
            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "旧密码不正确");
            }
            if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                throw new BusinessException(BizCode.PASSWORD_WEAK);
            }
            authMapper.updateUserPassword(userId, passwordEncoder.encode(newPassword), now);
        }
    }

    @Override
    public UserProfileData getUserProfile() {
        CurrentUser currentUser = AccessChecker.requireLogin();
        AuthUser user = authMapper.findFullUserById(currentUser.getUserId());
        if (user == null) {
            throw new BusinessException(BizCode.ACCOUNT_NOT_FOUND);
        }
        UserProfileData data = new UserProfileData();
        data.setUsername(user.getUsername());
        data.setRealName(user.getRealName());
        data.setStudentNo(user.getStudentNo());
        data.setPhone(user.getPhone());
        data.setEmail(user.getEmail());
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBasicInfo(UpdateProfileRequest request) {
        CurrentUser currentUser = AccessChecker.requireLogin();
        Long userId = currentUser.getUserId();
        String realName = request.getRealName() == null ? null : request.getRealName().trim();
        if (realName == null || realName.isEmpty()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "真实姓名不能为空");
        }
        String studentNo = request.getStudentNo() == null ? null : request.getStudentNo().trim();
        if (studentNo != null && studentNo.isEmpty()) studentNo = null;
        authMapper.updateUserBasicInfo(userId, realName, studentNo, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContactInfo(UpdateProfileRequest request) {
        CurrentUser currentUser = AccessChecker.requireLogin();
        Long userId = currentUser.getUserId();
        String phone = request.getPhone() == null ? null : request.getPhone().trim();
        if (phone != null && phone.isEmpty()) phone = null;
        String email = request.getEmail() == null ? null : request.getEmail().trim();
        if (email != null && email.isEmpty()) email = null;
        if (email != null && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "邮箱格式不正确");
        }
        authMapper.updateUserContact(userId, phone, email, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdateProfileRequest request) {
        CurrentUser currentUser = AccessChecker.requireLogin();
        Long userId = currentUser.getUserId();
        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "新密码不能为空");
        }
        String oldPassword = request.getOldPassword();
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "请输入旧密码");
        }
        AuthUser user = authMapper.findUserById(userId);
        if (user == null) {
            throw new BusinessException(BizCode.ACCOUNT_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "旧密码不正确");
        }
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new BusinessException(BizCode.PASSWORD_WEAK);
        }
        authMapper.updateUserPassword(userId, passwordEncoder.encode(newPassword), LocalDateTime.now());
    }

    private String buildClubCode(Long clubId) {
        return "CLUB" + clubId;
    }

    private void validateFileType(String originalFilename, String bizType) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "unsupported file type");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);

        if ("EVENT_SUMMARY_IMAGE".equals(bizType)) {
            if (!("jpg".equals(suffix) || "jpeg".equals(suffix) || "png".equals(suffix)
                    || "gif".equals(suffix) || "bmp".equals(suffix) || "webp".equals(suffix))) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "unsupported image type");
            }
        } else {
            if (!("pdf".equals(suffix) || "doc".equals(suffix) || "docx".equals(suffix))) {
                throw new BusinessException(BizCode.PARAM_INVALID.getCode(), "unsupported file type");
            }
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    // ===== Hot clubs =====

    @Override
    public void trackClubView(Long clubId) {
        AccessChecker.requireLogin();
        cacheService.incrementClubView(clubId);
    }

    @Override
    public List<ClubPublicItem> listHotClubs() {
        AccessChecker.requireLogin();
        List<ClubPublicItem> cached = cacheService.getHotClubs(
                new com.fasterxml.jackson.core.type.TypeReference<List<ClubPublicItem>>() {});
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        List<Long> topIds = cacheService.getTopViewedClubIds(20);
        if (topIds == null || topIds.isEmpty()) {
            return List.of();
        }
        List<ClubPublicItem> clubs = mapper.listClubsByIds(topIds);
        if (clubs != null && !clubs.isEmpty()) {
            cacheService.cacheHotClubs(clubs);
        }
        return clubs != null ? clubs : List.of();
    }
}
