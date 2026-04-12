package com.bistu.clubsystembackend.mapper;

import com.bistu.clubsystembackend.entity.EventMeta;
import com.bistu.clubsystembackend.entity.EventSignupMeta;
import com.bistu.clubsystembackend.entity.ClubJoinApplyRecord;
import com.bistu.clubsystembackend.entity.ClubJoinEligibilityMeta;
import com.bistu.clubsystembackend.entity.response.AnomalyExpenseItem;
import com.bistu.clubsystembackend.entity.response.ClubAdminJoinApplyItem;
import com.bistu.clubsystembackend.entity.response.ClubStatisticsData;
import com.bistu.clubsystembackend.entity.response.EventStatisticsData;
import com.bistu.clubsystembackend.entity.response.FinanceStatisticsData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalItem;
import com.bistu.clubsystembackend.entity.response.ClubApprovalDetailData;
import com.bistu.clubsystembackend.entity.response.ClubApplyMineItem;
import com.bistu.clubsystembackend.entity.response.ClubJoinApplyMineItem;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyData;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyItem;
import com.bistu.clubsystembackend.entity.response.ClubCancelDetailData;
import com.bistu.clubsystembackend.entity.response.ClubInfoData;
import com.bistu.clubsystembackend.entity.response.ClubMemberItem;
import com.bistu.clubsystembackend.entity.response.ClubPositionItem;
import com.bistu.clubsystembackend.entity.response.ClubPublicItem;
import com.bistu.clubsystembackend.entity.response.ClubRecruitConfigData;
import com.bistu.clubsystembackend.entity.response.ClubAdminEventItem;
import com.bistu.clubsystembackend.entity.response.EventApprovalItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventOpenItem;
import com.bistu.clubsystembackend.entity.response.EventSignupItem;
import com.bistu.clubsystembackend.entity.response.EventSummaryData;
import com.bistu.clubsystembackend.entity.response.ExpenseApprovalItem;
import com.bistu.clubsystembackend.entity.response.ExpenseDetailData;
import com.bistu.clubsystembackend.entity.response.FinanceAuditReportData;
import com.bistu.clubsystembackend.entity.response.FinanceRecordItem;
import com.bistu.clubsystembackend.entity.response.IncomeDetailData;
import com.bistu.clubsystembackend.entity.response.LedgerItem;
import com.bistu.clubsystembackend.entity.response.MyJoinedClubItem;
import com.bistu.clubsystembackend.entity.response.SchoolAdminClubDetailData;
import com.bistu.clubsystembackend.entity.response.SchoolAdminClubListItem;
import com.bistu.clubsystembackend.entity.response.SchoolUserItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserPermissionMapper {

    List<ClubPublicItem> listPublicClubs(@Param("keyword") String keyword,
                                         @Param("category") String category,
                                         @Param("isRecruiting") Boolean isRecruiting,
                                         @Param("onlyApproved") Boolean onlyApproved,
                                         @Param("applyStatus") Integer applyStatus,
                                         @Param("clubStatus") Integer clubStatus,
                                         @Param("userId") Long userId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    long countPublicClubs(@Param("keyword") String keyword,
                          @Param("category") String category,
                          @Param("isRecruiting") Boolean isRecruiting,
                          @Param("onlyApproved") Boolean onlyApproved,
                          @Param("applyStatus") Integer applyStatus,
                          @Param("clubStatus") Integer clubStatus,
                          @Param("userId") Long userId);

    List<ClubPublicItem> listClubsByIds(@Param("clubIds") List<Long> clubIds);

    Integer existsClubName(@Param("clubName") String clubName);

    int insertClub(@Param("id") Long id,
                   @Param("clubCode") String clubCode,
                   @Param("clubName") String clubName,
                   @Param("category") String category,
                   @Param("purpose") String purpose,
                   @Param("instructorName") String instructorName,
                   @Param("status") Integer status,
                   @Param("createdBy") Long createdBy,
                   @Param("updatedBy") Long updatedBy,
                   @Param("now") LocalDateTime now);

    int insertClubApply(@Param("id") Long id,
                        @Param("clubId") Long clubId,
                        @Param("initiatorUserId") Long initiatorUserId,
                        @Param("charterUrl") String charterUrl,
                        @Param("instructorName") String instructorName,
                        @Param("instructorProofUrl") String instructorProofUrl,
                        @Param("applyStatus") Integer applyStatus,
                        @Param("currentStep") String currentStep,
                        @Param("remark") String remark,
                        @Param("createdBy") Long createdBy,
                        @Param("updatedBy") Long updatedBy,
                        @Param("now") LocalDateTime now);

    int insertClubCancel(@Param("id") Long id,
                         @Param("clubId") Long clubId,
                         @Param("applyType") Integer applyType,
                         @Param("applyReason") String applyReason,
                         @Param("assetSettlementUrl") String assetSettlementUrl,
                         @Param("cancelStatus") Integer cancelStatus,
                         @Param("createdBy") Long createdBy,
                         @Param("updatedBy") Long updatedBy,
                         @Param("now") LocalDateTime now);

    List<ClubApplyMineItem> listMyClubApplyRecords(@Param("userId") Long userId,
                                                   @Param("offset") int offset,
                                                   @Param("pageSize") int pageSize);

    long countMyClubApplyRecords(@Param("userId") Long userId);

    ClubCancelApplyData findLatestClubCancelByClubId(@Param("clubId") Long clubId);

    List<ClubCancelApplyItem> listMyClubCancelApplies(@Param("clubId") Long clubId,
                                                      @Param("offset") int offset,
                                                      @Param("pageSize") int pageSize);

    long countMyClubCancelApplies(@Param("clubId") Long clubId);

    Integer findClubStatusById(@Param("clubId") Long clubId);

    int updateClubStatus(@Param("clubId") Long clubId,
                         @Param("status") Integer status,
                         @Param("updatedBy") Long updatedBy,
                         @Param("updatedAt") LocalDateTime updatedAt);

    int updateClubEstablishDateToToday(@Param("clubId") Long clubId,
                                       @Param("updatedBy") Long updatedBy,
                                       @Param("updatedAt") LocalDateTime updatedAt);

    Integer findClubMemberStatus(@Param("clubId") Long clubId, @Param("userId") Long userId);

    ClubJoinEligibilityMeta findClubJoinEligibilityMeta(@Param("clubId") Long clubId,
                                                        @Param("clubStatus") Integer clubStatus,
                                                        @Param("approvedStatus") Integer approvedStatus);

    Integer findLatestJoinApplyStatus(@Param("clubId") Long clubId, @Param("userId") Long userId);

    Long findLatestJoinApplyId(@Param("clubId") Long clubId, @Param("userId") Long userId);

    int resetJoinApplyToPending(@Param("applyId") Long applyId,
                                @Param("selfIntro") String selfIntro,
                                @Param("applyReason") String applyReason,
                                @Param("updatedBy") Long updatedBy,
                                @Param("now") LocalDateTime now);

    int insertClubJoinApply(@Param("id") Long id,
                            @Param("clubId") Long clubId,
                            @Param("userId") Long userId,
                            @Param("selfIntro") String selfIntro,
                            @Param("applyReason") String applyReason,
                            @Param("joinStatus") Integer joinStatus,
                            @Param("createdBy") Long createdBy,
                            @Param("updatedBy") Long updatedBy,
                            @Param("now") LocalDateTime now);

    List<ClubJoinApplyMineItem> listMyJoinApplies(@Param("userId") Long userId,
                                                  @Param("offset") int offset,
                                                  @Param("pageSize") int pageSize);

    long countMyJoinApplies(@Param("userId") Long userId);

    List<ClubAdminJoinApplyItem> listClubJoinApplies(@Param("clubId") Long clubId,
                                                     @Param("joinStatus") Integer joinStatus,
                                                     @Param("offset") int offset,
                                                     @Param("pageSize") int pageSize);

    long countClubJoinApplies(@Param("clubId") Long clubId, @Param("joinStatus") Integer joinStatus);

    ClubJoinApplyRecord findClubJoinApplyByIdForUpdate(@Param("applyId") Long applyId);

    int updateClubJoinApplyDecision(@Param("applyId") Long applyId,
                                    @Param("joinStatus") Integer joinStatus,
                                    @Param("reviewUserId") Long reviewUserId,
                                    @Param("reviewedAt") LocalDateTime reviewedAt,
                                    @Param("rejectReason") String rejectReason,
                                    @Param("updatedBy") Long updatedBy,
                                    @Param("updatedAt") LocalDateTime updatedAt);

    int insertClubMember(@Param("id") Long id,
                         @Param("clubId") Long clubId,
                         @Param("userId") Long userId,
                         @Param("now") LocalDateTime now);

    int reactivateClubMember(@Param("clubId") Long clubId, @Param("userId") Long userId, @Param("now") LocalDateTime now);

    int reactivateClubMemberWithPosition(@Param("clubId") Long clubId,
                                         @Param("userId") Long userId,
                                         @Param("positionId") Long positionId,
                                         @Param("updatedBy") Long updatedBy,
                                         @Param("now") LocalDateTime now);

    int insertClubMemberWithPosition(@Param("id") Long id,
                                     @Param("clubId") Long clubId,
                                     @Param("userId") Long userId,
                                     @Param("positionId") Long positionId,
                                     @Param("createdBy") Long createdBy,
                                     @Param("updatedBy") Long updatedBy,
                                     @Param("now") LocalDateTime now);

    List<EventOpenItem> listOpenEvents(@Param("userId") Long userId,
                                       @Param("keyword") String keyword,
                                       @Param("onlyMyClubs") Boolean onlyMyClubs,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);

    long countOpenEvents(@Param("userId") Long userId,
                         @Param("keyword") String keyword,
                         @Param("onlyMyClubs") Boolean onlyMyClubs);

    EventSignupMeta selectEventForSignup(@Param("eventId") Long eventId);

    Integer countActiveSignups(@Param("eventId") Long eventId);

    Integer findEventLimitCount(@Param("eventId") Long eventId);

    Integer hasSigned(@Param("eventId") Long eventId, @Param("userId") Long userId);

    Integer hasCanceledSignup(@Param("eventId") Long eventId, @Param("userId") Long userId);

    int insertEventSignup(@Param("id") Long id,
                          @Param("eventId") Long eventId,
                          @Param("clubId") Long clubId,
                          @Param("userId") Long userId,
                          @Param("now") LocalDateTime now);

    Long findManagedClubIdByUserId(@Param("userId") Long userId);

    ClubInfoData findClubInfoById(@Param("clubId") Long clubId);

    int updateClubInfo(@Param("clubId") Long clubId,
                       @Param("clubName") String clubName,
                       @Param("category") String category,
                       @Param("introduction") String introduction,
                       @Param("purpose") String purpose,
                       @Param("instructorName") String instructorName,
                       @Param("updatedBy") Long updatedBy,
                       @Param("updatedAt") LocalDateTime updatedAt);

    ClubRecruitConfigData findClubRecruitConfigById(@Param("clubId") Long clubId);

    int updateClubRecruitConfig(@Param("clubId") Long clubId,
                                @Param("recruitStartAt") LocalDateTime recruitStartAt,
                                @Param("recruitEndAt") LocalDateTime recruitEndAt,
                                @Param("recruitLimit") Integer recruitLimit,
                                @Param("recruitStatus") String recruitStatus,
                                @Param("updatedBy") Long updatedBy,
                                @Param("updatedAt") LocalDateTime updatedAt);

    List<ClubPositionItem> listClubPositions(@Param("clubId") Long clubId,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    long countClubPositions(@Param("clubId") Long clubId);

    ClubPositionItem findClubPositionById(@Param("clubId") Long clubId, @Param("positionId") Long positionId);

    int insertClubPosition(@Param("id") Long id,
                           @Param("clubId") Long clubId,
                           @Param("positionName") String positionName,
                           @Param("parentPositionId") Long parentPositionId,
                           @Param("levelNo") Integer levelNo,
                           @Param("sortNo") Integer sortNo,
                           @Param("createdBy") Long createdBy,
                           @Param("updatedBy") Long updatedBy,
                           @Param("now") LocalDateTime now);

    int updateClubPosition(@Param("clubId") Long clubId,
                           @Param("positionId") Long positionId,
                           @Param("positionName") String positionName,
                           @Param("parentPositionId") Long parentPositionId,
                           @Param("levelNo") Integer levelNo,
                           @Param("sortNo") Integer sortNo,
                           @Param("updatedBy") Long updatedBy,
                           @Param("updatedAt") LocalDateTime updatedAt);

    int deleteClubPosition(@Param("clubId") Long clubId,
                           @Param("positionId") Long positionId,
                           @Param("updatedBy") Long updatedBy,
                           @Param("updatedAt") LocalDateTime updatedAt);

    long countChildPositions(@Param("clubId") Long clubId, @Param("parentPositionId") Long parentPositionId);

    long countMembersByPosition(@Param("clubId") Long clubId, @Param("positionId") Long positionId);

    Integer findMaxSortNoByParent(@Param("clubId") Long clubId, @Param("parentPositionId") Long parentPositionId);

    List<ClubMemberItem> listClubMembers(@Param("clubId") Long clubId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    long countClubMembers(@Param("clubId") Long clubId);

    Long findPositionIdByName(@Param("clubId") Long clubId, @Param("positionName") String positionName);

    int updateClubMemberPosition(@Param("clubId") Long clubId,
                                 @Param("memberId") Long memberId,
                                 @Param("positionId") Long positionId,
                                 @Param("updatedBy") Long updatedBy,
                                 @Param("updatedAt") LocalDateTime updatedAt);

    int removeClubMember(@Param("clubId") Long clubId,
                         @Param("memberId") Long memberId,
                         @Param("updatedBy") Long updatedBy,
                         @Param("now") LocalDateTime now);

    List<FinanceRecordItem> listClubFinances(@Param("clubId") Long clubId,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    long countClubFinances(@Param("clubId") Long clubId);

    List<SchoolUserItem> listUsers(@Param("roleCode") String roleCode,
                                   @Param("keyword") String keyword,
                                   @Param("dbStatus") Integer dbStatus,
                                   @Param("graduated") Boolean graduated,
                                   @Param("offset") int offset,
                                   @Param("pageSize") int pageSize);

    long countUsers(@Param("roleCode") String roleCode,
                    @Param("keyword") String keyword,
                    @Param("dbStatus") Integer dbStatus,
                    @Param("graduated") Boolean graduated);

    Integer findUserStatusById(@Param("userId") Long userId);

    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status, @Param("updatedAt") LocalDateTime updatedAt);

    Long findRoleIdByCode(@Param("roleCode") String roleCode);

    Long findUserRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    String findEffectiveRoleCodeByUserId(@Param("userId") Long userId);

    Integer existsUserById(@Param("userId") Long userId);

    int disableUserRolesByUserId(@Param("userId") Long userId, @Param("updatedAt") LocalDateTime updatedAt);

    int activateUserRole(@Param("userId") Long userId,
                         @Param("roleId") Long roleId,
                         @Param("clubId") Long clubId,
                         @Param("updatedAt") LocalDateTime updatedAt);

    Long findActiveUserRoleRecordId(@Param("userId") Long userId);

    Long findLatestUserRoleRecordId(@Param("userId") Long userId);

    int updateUserRoleRecordById(@Param("id") Long id,
                                 @Param("roleId") Long roleId,
                                 @Param("clubId") Long clubId,
                                 @Param("updatedAt") LocalDateTime updatedAt);

    int disableOtherActiveUserRoles(@Param("userId") Long userId,
                                    @Param("keepId") Long keepId,
                                    @Param("updatedAt") LocalDateTime updatedAt);

    Long findAnyClubIdByUser(@Param("userId") Long userId);

    int insertUserRole(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("roleId") Long roleId,
                       @Param("clubId") Long clubId,
                       @Param("now") LocalDateTime now);

    List<ClubApprovalItem> listClubApprovals(@Param("applyStatus") Integer applyStatus,
                                             @Param("keyword") String keyword,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    long countClubApprovals(@Param("applyStatus") Integer applyStatus,
                            @Param("keyword") String keyword);

    ClubApprovalItem findClubApprovalById(@Param("approvalId") Long approvalId);

    ClubApprovalDetailData findClubApprovalDetailById(@Param("approvalId") Long approvalId);

    List<ClubCancelApplyItem> listClubCancelApprovals(@Param("cancelStatus") Integer cancelStatus,
                                                      @Param("keyword") String keyword,
                                                      @Param("offset") int offset,
                                                      @Param("pageSize") int pageSize);

    long countClubCancelApprovals(@Param("cancelStatus") Integer cancelStatus,
                                  @Param("keyword") String keyword);

    ClubCancelDetailData findClubCancelDetailById(@Param("cancelId") Long cancelId);

    ClubCancelApplyData findClubCancelById(@Param("cancelId") Long cancelId);

    int updateClubCancelStatus(@Param("cancelId") Long cancelId,
                               @Param("cancelStatus") Integer cancelStatus,
                               @Param("updatedBy") Long updatedBy,
                               @Param("updatedAt") LocalDateTime updatedAt);

    Long findClubInitiatorUserId(@Param("clubId") Long clubId);

    int updateClubApprovalDecision(@Param("approvalId") Long approvalId,
                                   @Param("applyStatus") Integer applyStatus,
                                   @Param("currentStep") String currentStep,
                                   @Param("remark") String remark,
                                   @Param("updatedBy") Long updatedBy,
                                   @Param("updatedAt") LocalDateTime updatedAt);

    List<SchoolAdminClubListItem> listSchoolAdminClubManagePage(@Param("keyword") String keyword,
                                                                 @Param("category") String category,
                                                                 @Param("offset") int offset,
                                                                 @Param("pageSize") int pageSize);

    long countSchoolAdminClubManagePage(@Param("keyword") String keyword,
                                        @Param("category") String category);

    SchoolAdminClubDetailData findSchoolAdminClubManageDetailByClubId(@Param("clubId") Long clubId);

    int updateSchoolAdminClub(@Param("clubId") Long clubId,
                              @Param("clubName") String clubName,
                              @Param("category") String category,
                              @Param("purpose") String purpose,
                              @Param("instructorName") String instructorName,
                              @Param("updatedBy") Long updatedBy,
                              @Param("updatedAt") LocalDateTime updatedAt);

    int hardDeleteClubApplyByClubId(@Param("clubId") Long clubId);

    int hardDeleteClubCancelByClubId(@Param("clubId") Long clubId);

    int hardDeleteClubById(@Param("clubId") Long clubId);

    int exitGraduatedMembers(@Param("now") LocalDateTime now);

    int freezeAccountsByClubId(@Param("clubId") Long clubId, @Param("updatedAt") LocalDateTime updatedAt);

    int freezeAccountsByCanceledClubs(@Param("updatedAt") LocalDateTime updatedAt);

    int autoStartEvents(@Param("now") LocalDateTime now);

    int autoEndEvents(@Param("now") LocalDateTime now);

    long countActiveClubs();

    long countClubs();

    long countActiveUsers();

    long countTotalUsers();

    long countPendingApprovals();

    long countSuspiciousExpenses();

    int insertAuditLog(@Param("id") Long id,
                       @Param("bizType") String bizType,
                       @Param("bizId") Long bizId,
                       @Param("opType") String opType,
                       @Param("opUserId") Long opUserId,
                       @Param("opUserRole") String opUserRole,
                       @Param("opAt") LocalDateTime opAt,
                       @Param("beforeJson") String beforeJson,
                       @Param("afterJson") String afterJson,
                       @Param("ip") String ip,
                       @Param("userAgent") String userAgent,
                       @Param("remark") String remark);

    Integer isActiveClubMember(@Param("clubId") Long clubId, @Param("userId") Long userId);

    Integer countActiveClubMemberships(@Param("userId") Long userId);

    BigDecimal findExpenseAmountById(@Param("expenseId") Long expenseId);

    // ===== Event management =====

    int insertEvent(@Param("id") Long id, @Param("clubId") Long clubId,
                    @Param("title") String title, @Param("content") String content,
                    @Param("location") String location,
                    @Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt,
                    @Param("signupStartAt") LocalDateTime signupStartAt, @Param("signupEndAt") LocalDateTime signupEndAt,
                    @Param("limitCount") Integer limitCount, @Param("onlyMember") Integer onlyMember,
                    @Param("eventStatus") Integer eventStatus, @Param("safetyPlanUrl") String safetyPlanUrl,
                    @Param("checkinCode") String checkinCode,
                    @Param("createdBy") Long createdBy, @Param("updatedBy") Long updatedBy,
                    @Param("now") LocalDateTime now);

    int updateEventForResubmit(@Param("eventId") Long eventId, @Param("clubId") Long clubId,
                               @Param("title") String title, @Param("content") String content,
                               @Param("location") String location,
                               @Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt,
                               @Param("signupStartAt") LocalDateTime signupStartAt, @Param("signupEndAt") LocalDateTime signupEndAt,
                               @Param("limitCount") Integer limitCount, @Param("onlyMember") Integer onlyMember,
                               @Param("safetyPlanUrl") String safetyPlanUrl,
                               @Param("checkinCode") String checkinCode,
                               @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);

    List<ClubAdminEventItem> listClubEvents(@Param("clubId") Long clubId,
                                            @Param("eventStatus") Integer eventStatus,
                                            @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countClubEvents(@Param("clubId") Long clubId, @Param("eventStatus") Integer eventStatus);

    EventDetailData findEventDetailById(@Param("eventId") Long eventId);

    EventMeta findEventMetaById(@Param("eventId") Long eventId);

    Long findEventIdByCheckinCode(@Param("checkinCode") String checkinCode);

    int updateEventStatus(@Param("eventId") Long eventId, @Param("eventStatus") Integer eventStatus,
                          @Param("rejectReason") String rejectReason,
                          @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);

    int updateEventCheckinCode(@Param("eventId") Long eventId,
                                @Param("checkinCode") String checkinCode,
                                @Param("updatedAt") LocalDateTime updatedAt);

    List<EventApprovalItem> listEventApprovals(@Param("eventStatus") Integer eventStatus,
                                               @Param("keyword") String keyword,
                                               @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countEventApprovals(@Param("eventStatus") Integer eventStatus, @Param("keyword") String keyword);

    List<EventSignupItem> listEventSignups(@Param("eventId") Long eventId,
                                           @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countEventSignups(@Param("eventId") Long eventId);

    int insertEventCheckin(@Param("id") Long id, @Param("eventId") Long eventId,
                           @Param("userId") Long userId, @Param("checkinType") Integer checkinType,
                           @Param("checkinAt") LocalDateTime checkinAt, @Param("deviceNo") String deviceNo,
                           @Param("createdBy") Long createdBy, @Param("updatedBy") Long updatedBy,
                           @Param("now") LocalDateTime now);

    Integer hasCheckedIn(@Param("eventId") Long eventId, @Param("userId") Long userId);

    int cancelEventCheckin(@Param("eventId") Long eventId, @Param("userId") Long userId,
                           @Param("updatedAt") LocalDateTime updatedAt);

    int restoreEventCheckin(@Param("eventId") Long eventId, @Param("userId") Long userId,
                            @Param("checkinType") Integer checkinType, @Param("checkinAt") LocalDateTime checkinAt,
                            @Param("deviceNo") String deviceNo, @Param("updatedBy") Long updatedBy,
                            @Param("updatedAt") LocalDateTime updatedAt);

    EventSummaryData findEventSummaryByEventId(@Param("eventId") Long eventId);

    int insertEventSummary(@Param("id") Long id, @Param("eventId") Long eventId,
                           @Param("summaryText") String summaryText, @Param("summaryImages") String summaryImages,
                           @Param("feedbackScore") BigDecimal feedbackScore,
                           @Param("issueReflection") String issueReflection, @Param("attachmentUrl") String attachmentUrl,
                           @Param("createdBy") Long createdBy, @Param("updatedBy") Long updatedBy,
                           @Param("now") LocalDateTime now);

    int updateEventSummary(@Param("eventId") Long eventId,
                           @Param("summaryText") String summaryText, @Param("summaryImages") String summaryImages,
                           @Param("feedbackScore") BigDecimal feedbackScore,
                           @Param("issueReflection") String issueReflection, @Param("attachmentUrl") String attachmentUrl,
                           @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);

    List<EventSummaryData> listEventSummaries(@Param("keyword") String keyword,
                                              @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countEventSummaries(@Param("keyword") String keyword);

    int updateEventSignupStatus(@Param("eventId") Long eventId, @Param("userId") Long userId,
                                @Param("signupStatus") Integer signupStatus,
                                @Param("updatedAt") LocalDateTime updatedAt);

    int autoEndPastEvents(@Param("now") LocalDateTime now);

    List<MyJoinedClubItem> listMyJoinedClubs(@Param("userId") Long userId,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);

    long countMyJoinedClubs(@Param("userId") Long userId);

    // ===== Finance management =====

    int insertIncome(@Param("id") Long id, @Param("clubId") Long clubId,
                     @Param("incomeType") Integer incomeType, @Param("amount") BigDecimal amount,
                     @Param("occurAt") LocalDateTime occurAt, @Param("proofUrl") String proofUrl,
                     @Param("sourceDesc") String sourceDesc,
                     @Param("createdBy") Long createdBy, @Param("updatedBy") Long updatedBy,
                     @Param("now") LocalDateTime now);

    List<IncomeDetailData> listClubIncomes(@Param("clubId") Long clubId,
                                            @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countClubIncomes(@Param("clubId") Long clubId);

    int insertExpense(@Param("id") Long id, @Param("clubId") Long clubId,
                      @Param("eventId") Long eventId, @Param("category") String category,
                      @Param("amount") BigDecimal amount,
                      @Param("payeeName") String payeeName, @Param("payeeAccount") String payeeAccount,
                      @Param("invoiceUrl") String invoiceUrl, @Param("expenseDesc") String expenseDesc,
                      @Param("approveLevel") Integer approveLevel, @Param("expenseStatus") Integer expenseStatus,
                      @Param("createdBy") Long createdBy, @Param("updatedBy") Long updatedBy,
                      @Param("now") LocalDateTime now);

    List<ExpenseDetailData> listClubExpenses(@Param("clubId") Long clubId,
                                              @Param("expenseStatus") Integer expenseStatus,
                                              @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countClubExpenses(@Param("clubId") Long clubId, @Param("expenseStatus") Integer expenseStatus);

    ExpenseDetailData findExpenseDetailById(@Param("expenseId") Long expenseId);

    int updateExpenseStatus(@Param("expenseId") Long expenseId, @Param("expenseStatus") Integer expenseStatus,
                            @Param("rejectReason") String rejectReason,
                            @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);

    int resubmitExpense(@Param("expenseId") Long expenseId, @Param("category") String category,
                        @Param("amount") BigDecimal amount, @Param("payeeName") String payeeName,
                        @Param("payeeAccount") String payeeAccount, @Param("invoiceUrl") String invoiceUrl,
                        @Param("expenseDesc") String expenseDesc, @Param("approveLevel") int approveLevel,
                        @Param("expenseStatus") int expenseStatus,
                        @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);

    BigDecimal sumPendingExpensesByClub(@Param("clubId") Long clubId, @Param("excludeExpenseId") Long excludeExpenseId);

    List<ExpenseApprovalItem> listPendingExpenseApprovals(@Param("keyword") String keyword,
                                                           @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countPendingExpenseApprovals(@Param("keyword") String keyword);

    int insertLedger(@Param("id") Long id, @Param("clubId") Long clubId,
                     @Param("bizType") Integer bizType, @Param("bizId") Long bizId,
                     @Param("changeAmount") BigDecimal changeAmount, @Param("balanceAfter") BigDecimal balanceAfter,
                     @Param("occurAt") LocalDateTime occurAt,
                     @Param("createdBy") Long createdBy, @Param("updatedBy") Long updatedBy,
                     @Param("now") LocalDateTime now);

    List<LedgerItem> listClubLedger(@Param("clubId") Long clubId,
                                     @Param("bizType") Integer bizType,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countClubLedger(@Param("clubId") Long clubId, @Param("bizType") Integer bizType,
                         @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    BigDecimal findLatestClubBalance(@Param("clubId") Long clubId);

    // Finance audit report
    BigDecimal sumClubIncomeByYear(@Param("clubId") Long clubId, @Param("year") int year);

    BigDecimal sumClubExpenseByYear(@Param("clubId") Long clubId, @Param("year") int year);

    List<FinanceAuditReportData.MonthlyFinanceStat> monthlyIncomeStats(@Param("clubId") Long clubId, @Param("year") int year);

    List<FinanceAuditReportData.MonthlyFinanceStat> monthlyExpenseStats(@Param("clubId") Long clubId, @Param("year") int year);

    List<FinanceAuditReportData.CategoryExpenseStat> categoryExpenseStats(@Param("clubId") Long clubId, @Param("year") int year);

    long countAnomalyExpensesByClubAndYear(@Param("clubId") Long clubId, @Param("year") int year);

    List<AnomalyExpenseItem> listAnomalyExpenses(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    long countAnomalyExpenses(@Param("keyword") String keyword);

    String findClubNameById(@Param("clubId") Long clubId);

    // ========== Statistics ==========
    // Club statistics
    List<ClubStatisticsData.CategoryCount> countClubsByCategory();
    List<ClubStatisticsData.StatusCount> countClubsByStatus();
    long countNewClubsByYear(@Param("year") int year);
    long countCancelledClubsByYear(@Param("year") int year);
    long countTotalStudents();
    long countJoinedStudents();
    List<ClubStatisticsData.ActiveClub> listTopActiveClubs(@Param("year") int year, @Param("limit") int limit);

    // Event statistics
    long countEventsByYear(@Param("year") int year);
    long countTotalSignupsByYear(@Param("year") int year);
    List<EventStatisticsData.MonthlyEventStat> monthlyEventStatsByYear(@Param("year") int year);
    List<EventStatisticsData.TopEventClub> listTopEventClubs(@Param("year") int year, @Param("limit") int limit);
    List<EventStatisticsData.StatusCount> countEventsByStatusAndYear(@Param("year") int year);

    // Finance statistics
    List<FinanceStatisticsData.IncomeByType> sumIncomeByTypeAndYear(@Param("year") int year);
    BigDecimal sumTotalExpenseByYear(@Param("year") int year);
    List<FinanceStatisticsData.ClubFinanceItem> listClubFinanceSummary(@Param("year") int year);
    List<FinanceStatisticsData.MonthlyFinance> monthlyFinanceStatsByYear(@Param("year") int year);
}
