package com.bistu.clubsystembackend.controller;

import com.bistu.clubsystembackend.entity.request.EventApprovalDecisionRequest;
import com.bistu.clubsystembackend.entity.request.ExpenseApprovalDecisionRequest;
import com.bistu.clubsystembackend.entity.request.ApprovalDecisionRequest;
import com.bistu.clubsystembackend.entity.request.ApprovalStatusUpdateRequest;
import com.bistu.clubsystembackend.entity.request.ClubCancelStatusUpdateRequest;
import com.bistu.clubsystembackend.entity.request.FreezeAccountsRequest;
import com.bistu.clubsystembackend.entity.request.GraduationExitRequest;
import com.bistu.clubsystembackend.entity.request.ScheduledTaskCommandRequest;
import com.bistu.clubsystembackend.entity.request.SchoolAdminUpdateClubRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubStatusRequest;
import com.bistu.clubsystembackend.entity.request.UpdateUserRoleRequest;
import com.bistu.clubsystembackend.entity.request.UpdateUserStatusRequest;
import com.bistu.clubsystembackend.entity.response.EventApprovalItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventSummaryData;
import com.bistu.clubsystembackend.entity.response.ApprovalStatusUpdateData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalDetailData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalItem;
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
import com.bistu.clubsystembackend.entity.response.ScheduledTaskCommandData;
import com.bistu.clubsystembackend.entity.response.ScheduledTaskStatusData;
import com.bistu.clubsystembackend.entity.response.StatisticsOverviewData;
import com.bistu.clubsystembackend.entity.response.UserRoleChangeData;
import com.bistu.clubsystembackend.entity.response.UserStatusChangeData;
import com.bistu.clubsystembackend.service.ScheduledTaskService;
import com.bistu.clubsystembackend.service.SchoolAdminPermissionService;
import com.bistu.clubsystembackend.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/school-admin")
public class SchoolAdminController {

    private final SchoolAdminPermissionService userPermissionService;
    private final ScheduledTaskService scheduledTaskService;

    public SchoolAdminController(SchoolAdminPermissionService userPermissionService,
                                 ScheduledTaskService scheduledTaskService) {
        this.userPermissionService = userPermissionService;
        this.scheduledTaskService = scheduledTaskService;
    }

    @GetMapping("/users")
    public ApiResponse<PageResponseData<SchoolUserItem>> listUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(userPermissionService.listUsers(pageNum, pageSize, roleCode, status, keyword));
    }

    @PatchMapping("/users/{userId}/status")
    public ApiResponse<UserStatusChangeData> updateUserStatus(@PathVariable Long userId,
                                                               @Valid @RequestBody UpdateUserStatusRequest request) {
        UserStatusChangeData data = userPermissionService.updateUserStatus(userId, request);
        String message = "ACTIVE".equals(data.getStatus()) ? "已取消账号冻结" : "账号已冻结";
        return ApiResponse.success(message, data);
    }

    @PatchMapping("/clubs/{clubId}/status")
    public ApiResponse<ClubStatusChangeData> updateClubStatus(@PathVariable Long clubId,
                                                               @Valid @RequestBody UpdateClubStatusRequest request) {
        ClubStatusChangeData data = userPermissionService.updateClubStatus(clubId, request);
        return ApiResponse.success("社团状态更新成功", data);
    }

    @PatchMapping("/users/{userId}/role")
    public ApiResponse<UserRoleChangeData> updateUserRole(@PathVariable Long userId,
                                                           @Valid @RequestBody UpdateUserRoleRequest request) {
        UserRoleChangeData data = userPermissionService.updateUserRole(userId, request);
        String message = "STUDENT".equals(data.getEffectiveRoleCode()) ? "已取消社团管理员" : "角色调整成功";
        return ApiResponse.success(message, data);
    }

    @GetMapping("/approvals/clubs")
    public ApiResponse<PageResponseData<ClubApprovalItem>> listApprovals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer applyStatus) {
        return ApiResponse.success(userPermissionService.listClubApprovals(pageNum, pageSize, keyword, applyStatus));
    }

    @GetMapping("/approvals/clubs/{approvalId}")
    public ApiResponse<ClubApprovalDetailData> getApprovalDetail(@PathVariable Long approvalId) {
        return ApiResponse.success(userPermissionService.getClubApprovalDetail(approvalId));
    }

    @GetMapping("/approvals/club-cancels")
    public ApiResponse<PageResponseData<ClubCancelApplyItem>> listClubCancelApprovals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer cancelStatus) {
        return ApiResponse.success(userPermissionService.listClubCancelApprovals(pageNum, pageSize, keyword, cancelStatus));
    }

    @GetMapping("/approvals/club-cancels/{cancelId}")
    public ApiResponse<ClubCancelDetailData> getClubCancelApprovalDetail(@PathVariable Long cancelId) {
        return ApiResponse.success(userPermissionService.getClubCancelApprovalDetail(cancelId));
    }

    @PatchMapping("/approvals/club-cancels/{cancelId}/status")
    public ApiResponse<ClubCancelStatusUpdateData> updateClubCancelApprovalStatus(@PathVariable Long cancelId,
                                                                                   @Valid @RequestBody ClubCancelStatusUpdateRequest request) {
        return ApiResponse.success(userPermissionService.updateClubCancelApprovalStatus(cancelId, request));
    }

    @GetMapping("/clubs/manage")
    public ApiResponse<PageResponseData<SchoolAdminClubListItem>> listManageClubs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return ApiResponse.success(userPermissionService.listSchoolAdminClubManagePage(pageNum, pageSize, keyword, category));
    }

    @GetMapping("/clubs/{clubId}/manage-detail")
    public ApiResponse<SchoolAdminClubDetailData> getManageClubDetail(@PathVariable Long clubId) {
        return ApiResponse.success(userPermissionService.getSchoolAdminClubManageDetail(clubId));
    }

    @PatchMapping("/clubs/{clubId}")
    public ApiResponse<Void> updateManageClub(@PathVariable Long clubId,
                                              @Valid @RequestBody SchoolAdminUpdateClubRequest request) {
        userPermissionService.updateSchoolAdminClub(clubId, request);
        return ApiResponse.success("updated", null);
    }

    @PatchMapping("/approvals/clubs/{approvalId}/status")
    public ApiResponse<ApprovalStatusUpdateData> updateApprovalStatus(@PathVariable Long approvalId,
                                                                       @Valid @RequestBody ApprovalStatusUpdateRequest request) {
        ApprovalStatusUpdateData data = userPermissionService.updateClubApprovalStatus(approvalId, request);
        return ApiResponse.success("状态更新成功", data);
    }

    @PostMapping("/approvals/clubs/{approvalId}/decision")
    public ApiResponse<Void> decision(@PathVariable Long approvalId,
                                      @Valid @RequestBody ApprovalDecisionRequest request) {
        userPermissionService.decisionClubApproval(approvalId, request);
        return ApiResponse.success("审批成功", null);
    }

    @PostMapping("/users/graduation/exit-clubs")
    public ApiResponse<ProcessedCountData> exitGraduation(@Valid @RequestBody GraduationExitRequest request) {
        return ApiResponse.success(userPermissionService.exitGraduationUsers(request));
    }

    @PostMapping("/clubs/{clubId}/freeze-accounts")
    public ApiResponse<FrozenCountData> freezeAccounts(@PathVariable String clubId,
                                                        @RequestBody(required = false) FreezeAccountsRequest request) {
        return ApiResponse.success(userPermissionService.freezeAccounts(clubId, request));
    }

    @GetMapping("/statistics/overview")
    public ApiResponse<StatisticsOverviewData> statisticsOverview() {
        return ApiResponse.success(userPermissionService.statisticsOverview());
    }

    @PostMapping("/scheduled-tasks/{taskCode}/command")
    public ApiResponse<ScheduledTaskCommandData> commandScheduledTask(@PathVariable String taskCode,
                                                                      @Valid @RequestBody ScheduledTaskCommandRequest request) {
        return ApiResponse.success(scheduledTaskService.commandTask(taskCode, request));
    }

    @GetMapping("/scheduled-tasks/status")
    public ApiResponse<List<ScheduledTaskStatusData>> getScheduledTaskStatus(@RequestParam(required = false) String taskCodes) {
        List<String> codes = null;
        if (taskCodes != null && !taskCodes.isBlank()) {
            codes = Arrays.stream(taskCodes.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());
        }
        return ApiResponse.success(scheduledTaskService.queryStatus(codes));
    }

    // ===== Event approval =====

    @GetMapping("/approvals/events")
    public ApiResponse<PageResponseData<EventApprovalItem>> listEventApprovals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer eventStatus) {
        return ApiResponse.success(userPermissionService.listEventApprovals(pageNum, pageSize, keyword, eventStatus));
    }

    @GetMapping("/approvals/events/{eventId}")
    public ApiResponse<EventDetailData> getEventApprovalDetail(@PathVariable Long eventId) {
        return ApiResponse.success(userPermissionService.getEventApprovalDetail(eventId));
    }

    @PostMapping("/approvals/events/{eventId}/decision")
    public ApiResponse<Void> decisionEventApproval(@PathVariable Long eventId,
                                                    @Valid @RequestBody EventApprovalDecisionRequest request) {
        userPermissionService.decisionEventApproval(eventId, request);
        return ApiResponse.success("审批成功", null);
    }

    @GetMapping("/events/{eventId}/summary")
    public ApiResponse<EventSummaryData> getEventSummaryByEventId(@PathVariable Long eventId) {
        return ApiResponse.success(userPermissionService.getEventSummaryByEventId(eventId));
    }

    @GetMapping("/event-summaries")
    public ApiResponse<PageResponseData<EventSummaryData>> listEventSummaries(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(userPermissionService.listEventSummaries(pageNum, pageSize, keyword));
    }

    // ===== Finance management =====

    @GetMapping("/approvals/expenses")
    public ApiResponse<PageResponseData<ExpenseApprovalItem>> listExpenseApprovals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(userPermissionService.listPendingExpenseApprovals(pageNum, pageSize, keyword));
    }

    @GetMapping("/approvals/expenses/{expenseId}")
    public ApiResponse<ExpenseDetailData> getExpenseApprovalDetail(@PathVariable Long expenseId) {
        return ApiResponse.success(userPermissionService.getExpenseApprovalDetail(expenseId));
    }

    @PostMapping("/approvals/expenses/{expenseId}/decision")
    public ApiResponse<Void> decisionExpenseApproval(@PathVariable Long expenseId,
                                                      @Valid @RequestBody ExpenseApprovalDecisionRequest request) {
        userPermissionService.decisionExpenseApproval(expenseId, request);
        return ApiResponse.success("审批成功", null);
    }

    @GetMapping("/clubs/{clubId}/ledger")
    public ApiResponse<PageResponseData<LedgerItem>> listClubLedger(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer bizType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LocalDateTime start = startTime != null && !startTime.isBlank() ? LocalDate.parse(startTime).atStartOfDay() : null;
        LocalDateTime end = endTime != null && !endTime.isBlank() ? LocalDate.parse(endTime).atTime(23, 59, 59) : null;
        return ApiResponse.success(userPermissionService.listClubLedger(clubId, pageNum, pageSize, bizType, start, end));
    }

    @GetMapping("/clubs/{clubId}/finance-report")
    public ApiResponse<FinanceAuditReportData> getFinanceAuditReport(
            @PathVariable Long clubId,
            @RequestParam Integer year) {
        return ApiResponse.success(userPermissionService.getFinanceAuditReport(clubId, year));
    }

    @GetMapping("/anomaly-expenses")
    public ApiResponse<PageResponseData<AnomalyExpenseItem>> listAnomalyExpenses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(userPermissionService.listAnomalyExpenses(pageNum, pageSize, keyword));
    }

    @GetMapping("/statistics/clubs")
    public ApiResponse<ClubStatisticsData> getClubStatistics(@RequestParam(defaultValue = "2026") int year) {
        return ApiResponse.success(userPermissionService.getClubStatistics(year));
    }

    @GetMapping("/statistics/events")
    public ApiResponse<EventStatisticsData> getEventStatistics(@RequestParam(defaultValue = "2026") int year) {
        return ApiResponse.success(userPermissionService.getEventStatistics(year));
    }

    @GetMapping("/statistics/finance")
    public ApiResponse<FinanceStatisticsData> getFinanceStatistics(@RequestParam(defaultValue = "2026") int year) {
        return ApiResponse.success(userPermissionService.getFinanceStatistics(year));
    }

    // ===== Config management =====

    @GetMapping("/config/expense-threshold")
    public ApiResponse<java.util.Map<String, String>> getExpenseThreshold() {
        return ApiResponse.success(userPermissionService.getExpenseThreshold());
    }

    @PutMapping("/config/expense-threshold")
    public ApiResponse<Void> updateExpenseThreshold(@RequestBody java.util.Map<String, String> body) {
        userPermissionService.updateExpenseThreshold(body.get("value"));
        return ApiResponse.success("配置已更新", null);
    }

    @GetMapping("/config/club-approval-stages")
    public ApiResponse<java.util.Map<String, Object>> getClubApprovalStages() {
        return ApiResponse.success(userPermissionService.getClubApprovalStages());
    }

    @PutMapping("/config/club-approval-stages")
    public ApiResponse<Void> updateClubApprovalStages(@RequestBody java.util.Map<String, String> body) {
        userPermissionService.updateClubApprovalStages(body.get("stages"));
        return ApiResponse.success("配置已更新", null);
    }
}
