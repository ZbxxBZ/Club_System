package com.bistu.clubsystembackend.controller;

import com.bistu.clubsystembackend.entity.request.CreateExpenseRequest;
import com.bistu.clubsystembackend.entity.request.CreateEventRequest;
import com.bistu.clubsystembackend.entity.request.CreateIncomeRequest;
import com.bistu.clubsystembackend.entity.request.EventCheckinRequest;
import com.bistu.clubsystembackend.entity.request.SubmitEventSummaryRequest;
import com.bistu.clubsystembackend.entity.request.SubmitClubReviewRequest;
import com.bistu.clubsystembackend.entity.request.AddClubMemberRequest;
import com.bistu.clubsystembackend.entity.request.ClubCancelSubmitRequest;
import com.bistu.clubsystembackend.entity.request.CreateClubPositionRequest;
import com.bistu.clubsystembackend.entity.request.JoinApplyDecisionRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubPositionRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubRequest;
import com.bistu.clubsystembackend.entity.request.UpdateMemberRoleRequest;
import com.bistu.clubsystembackend.entity.request.UpdateRecruitConfigRequest;
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
import com.bistu.clubsystembackend.entity.response.ClubReviewDetailData;
import com.bistu.clubsystembackend.entity.response.ClubReviewItem;
import com.bistu.clubsystembackend.entity.response.ExpenseDetailData;
import com.bistu.clubsystembackend.entity.response.FinanceRecordItem;
import com.bistu.clubsystembackend.entity.response.IncomeDetailData;
import com.bistu.clubsystembackend.entity.response.JoinApplyDecisionData;
import com.bistu.clubsystembackend.entity.response.LedgerItem;
import com.bistu.clubsystembackend.entity.response.PageResponseData;
import com.bistu.clubsystembackend.entity.response.PositionDeleteData;
import com.bistu.clubsystembackend.service.ClubAdminPermissionService;
import com.bistu.clubsystembackend.util.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/club-admin/me")
public class ClubAdminController {

    private final ClubAdminPermissionService userPermissionService;

    public ClubAdminController(ClubAdminPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @GetMapping("/club")
    public ApiResponse<ClubInfoData> getMyClub() {
        return ApiResponse.success(userPermissionService.getMyClub());
    }

    @PutMapping("/club")
    public ApiResponse<Void> updateMyClub(@Valid @RequestBody UpdateClubRequest request) {
        userPermissionService.updateMyClub(request);
        return ApiResponse.success("updated", null);
    }

    @GetMapping("/club/recruit-config")
    public ApiResponse<ClubRecruitConfigData> getMyClubRecruitConfig() {
        return ApiResponse.success(userPermissionService.getMyClubRecruitConfig());
    }

    @PutMapping("/club/recruit-config")
    public ApiResponse<ClubRecruitConfigData> updateMyClubRecruitConfig(@Valid @RequestBody UpdateRecruitConfigRequest request) {
        return ApiResponse.success("recruit config updated", userPermissionService.updateMyClubRecruitConfig(request));
    }

    @GetMapping("/positions")
    public ApiResponse<PageResponseData<ClubPositionItem>> listPositions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "200") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyClubPositions(pageNum, pageSize));
    }

    @PostMapping("/positions")
    public ApiResponse<ClubPositionItem> createPosition(@Valid @RequestBody CreateClubPositionRequest request) {
        return ApiResponse.success("position created", userPermissionService.createMyClubPosition(request));
    }

    @PatchMapping("/positions/{positionId}")
    public ApiResponse<ClubPositionItem> updatePosition(@PathVariable Long positionId,
                                                        @Valid @RequestBody UpdateClubPositionRequest request) {
        return ApiResponse.success("position updated", userPermissionService.updateMyClubPosition(positionId, request));
    }

    @DeleteMapping("/positions/{positionId}")
    public ApiResponse<PositionDeleteData> deletePosition(@PathVariable Long positionId) {
        return ApiResponse.success("position deleted", userPermissionService.deleteMyClubPosition(positionId));
    }

    @GetMapping("/members")
    public ApiResponse<PageResponseData<ClubMemberItem>> listMembers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyClubMembers(pageNum, pageSize));
    }

    @PostMapping("/members")
    public ApiResponse<Void> addMember(@Valid @RequestBody AddClubMemberRequest request) {
        userPermissionService.addMyClubMember(request);
        return ApiResponse.success("member added", null);
    }

    @PatchMapping("/members/{memberId}/role")
    public ApiResponse<Void> updateMemberRole(@PathVariable Long memberId, @Valid @RequestBody UpdateMemberRoleRequest request) {
        userPermissionService.updateMyMemberRole(memberId, request);
        return ApiResponse.success("member role updated", null);
    }

    @DeleteMapping("/members/{memberId}")
    public ApiResponse<Void> removeMember(@PathVariable Long memberId) {
        userPermissionService.removeMyMember(memberId);
        return ApiResponse.success("member removed", null);
    }

    @GetMapping("/join-applies")
    public ApiResponse<PageResponseData<ClubAdminJoinApplyItem>> listJoinApplies(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "PENDING") String status) {
        return ApiResponse.success(userPermissionService.listMyClubJoinApplies(pageNum, pageSize, status));
    }

    @PostMapping("/join-applies/{applyId}/decision")
    public ApiResponse<JoinApplyDecisionData> decideJoinApply(@PathVariable Long applyId,
                                                              @Valid @RequestBody JoinApplyDecisionRequest request) {
        return ApiResponse.success("审批成功", userPermissionService.decideJoinApply(applyId, request));
    }

    @GetMapping("/finances")
    public ApiResponse<PageResponseData<FinanceRecordItem>> listFinances(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyFinances(pageNum, pageSize));
    }

    @PostMapping("/club/cancel")
    public ApiResponse<ClubCancelApplyData> submitClubCancel(@Valid @RequestBody ClubCancelSubmitRequest request) {
        return ApiResponse.success(userPermissionService.submitMyClubCancel(request));
    }

    @GetMapping("/club/cancel-applies")
    public ApiResponse<PageResponseData<ClubCancelApplyItem>> listMyClubCancelApplies(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyClubCancelApplies(pageNum, pageSize));
    }

    // ===== Event management =====

    @GetMapping("/events")
    public ApiResponse<PageResponseData<ClubAdminEventItem>> listMyEvents(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer eventStatus) {
        return ApiResponse.success(userPermissionService.listMyEvents(pageNum, pageSize, eventStatus));
    }

    @PostMapping("/events")
    public ApiResponse<EventDetailData> createMyEvent(@Valid @RequestBody CreateEventRequest request) {
        return ApiResponse.success("活动已提交审核", userPermissionService.createMyEvent(request));
    }

    @GetMapping("/events/{eventId}")
    public ApiResponse<EventDetailData> getMyEventDetail(@PathVariable Long eventId) {
        return ApiResponse.success(userPermissionService.getMyEventDetail(eventId));
    }

    @PutMapping("/events/{eventId}")
    public ApiResponse<EventDetailData> resubmitMyEvent(@PathVariable Long eventId,
                                                         @Valid @RequestBody CreateEventRequest request) {
        return ApiResponse.success("活动已重新提交审核", userPermissionService.resubmitMyEvent(eventId, request));
    }

    @GetMapping("/events/{eventId}/signups")
    public ApiResponse<PageResponseData<EventSignupItem>> listMyEventSignups(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyEventSignups(eventId, pageNum, pageSize));
    }

    @PostMapping("/events/{eventId}/checkin")
    public ApiResponse<Void> checkinMyEvent(@PathVariable Long eventId,
                                             @Valid @RequestBody EventCheckinRequest request) {
        userPermissionService.checkinMyEvent(eventId, request);
        return ApiResponse.success("签到成功", null);
    }

    @DeleteMapping("/events/{eventId}/checkin/{userId}")
    public ApiResponse<Void> cancelCheckinMyEvent(@PathVariable Long eventId,
                                                   @PathVariable Long userId) {
        userPermissionService.cancelCheckinMyEvent(eventId, userId);
        return ApiResponse.success("取消签到成功", null);
    }

    @PatchMapping("/events/{eventId}/checkin-code")
    public ApiResponse<Void> updateMyEventCheckinCode(@PathVariable Long eventId,
                                                       @RequestBody Map<String, String> payload) {
        String checkinCode = payload.get("checkinCode");
        userPermissionService.updateMyEventCheckinCode(eventId, checkinCode);
        return ApiResponse.success("签到码已更新", null);
    }

    @PostMapping("/events/{eventId}/summary")
    public ApiResponse<EventSummaryData> submitMyEventSummary(@PathVariable Long eventId,
                                                               @Valid @RequestBody SubmitEventSummaryRequest request) {
        return ApiResponse.success("活动总结已提交", userPermissionService.submitMyEventSummary(eventId, request));
    }

    @GetMapping("/events/{eventId}/summary")
    public ApiResponse<EventSummaryData> getMyEventSummary(@PathVariable Long eventId) {
        return ApiResponse.success(userPermissionService.getMyEventSummary(eventId));
    }

    // ===== Finance management =====

    @PostMapping("/incomes")
    public ApiResponse<IncomeDetailData> createIncome(@Valid @RequestBody CreateIncomeRequest request) {
        return ApiResponse.success("收入已录入", userPermissionService.createIncome(request));
    }

    @GetMapping("/incomes")
    public ApiResponse<PageResponseData<IncomeDetailData>> listIncomes(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyIncomes(pageNum, pageSize));
    }

    @PostMapping("/expenses")
    public ApiResponse<ExpenseDetailData> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        return ApiResponse.success("支出申请已提交", userPermissionService.createExpense(request));
    }

    @PutMapping("/expenses/{expenseId}/resubmit")
    public ApiResponse<ExpenseDetailData> resubmitExpense(@PathVariable Long expenseId,
                                                           @Valid @RequestBody CreateExpenseRequest request) {
        return ApiResponse.success("重新提交成功", userPermissionService.resubmitExpense(expenseId, request));
    }

    @GetMapping("/expenses")
    public ApiResponse<PageResponseData<ExpenseDetailData>> listExpenses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer expenseStatus) {
        return ApiResponse.success(userPermissionService.listMyExpenses(pageNum, pageSize, expenseStatus));
    }

    @GetMapping("/ledger")
    public ApiResponse<PageResponseData<LedgerItem>> listLedger(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer bizType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LocalDateTime start = startTime != null && !startTime.isBlank() ? LocalDate.parse(startTime).atStartOfDay() : null;
        LocalDateTime end = endTime != null && !endTime.isBlank() ? LocalDate.parse(endTime).atTime(23, 59, 59) : null;
        return ApiResponse.success(userPermissionService.listMyLedger(pageNum, pageSize, bizType, start, end));
    }

    @GetMapping("/balance")
    public ApiResponse<ClubBalanceData> getBalance() {
        return ApiResponse.success(userPermissionService.getMyBalance());
    }

    // ===== Club Review (年审) =====

    @GetMapping("/review")
    public ApiResponse<ClubReviewDetailData> getMyCurrentReview() {
        return ApiResponse.success(userPermissionService.getMyCurrentReview());
    }

    @PostMapping("/review")
    public ApiResponse<Void> submitMyReview(@Valid @RequestBody SubmitClubReviewRequest request) {
        userPermissionService.submitMyReview(request);
        return ApiResponse.success("年审报告提交成功", null);
    }

    @GetMapping("/reviews")
    public ApiResponse<PageResponseData<ClubReviewItem>> listMyReviews(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userPermissionService.listMyReviews(pageNum, pageSize));
    }
}
