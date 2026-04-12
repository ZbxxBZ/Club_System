package com.bistu.clubsystembackend.service;

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
import com.bistu.clubsystembackend.entity.response.ApprovalStatusUpdateData;
import com.bistu.clubsystembackend.entity.response.AnomalyExpenseItem;
import com.bistu.clubsystembackend.entity.response.ClubStatisticsData;
import com.bistu.clubsystembackend.entity.response.EventApprovalItem;
import com.bistu.clubsystembackend.entity.response.EventStatisticsData;
import com.bistu.clubsystembackend.entity.response.FinanceStatisticsData;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventSummaryData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalDetailData;
import com.bistu.clubsystembackend.entity.response.ClubApprovalItem;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyItem;
import com.bistu.clubsystembackend.entity.response.ClubCancelDetailData;
import com.bistu.clubsystembackend.entity.response.ClubCancelStatusUpdateData;
import com.bistu.clubsystembackend.entity.response.ClubStatusChangeData;
import com.bistu.clubsystembackend.entity.response.ExpenseApprovalItem;
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

import java.time.LocalDateTime;
import java.util.Map;

public interface SchoolAdminPermissionService {

    PageResponseData<SchoolUserItem> listUsers(int pageNum, int pageSize, String roleCode, String status, String keyword);

    UserStatusChangeData updateUserStatus(Long userId, UpdateUserStatusRequest request);

    ClubStatusChangeData updateClubStatus(Long clubId, UpdateClubStatusRequest request);

    UserRoleChangeData updateUserRole(Long userId, UpdateUserRoleRequest request);

    PageResponseData<ClubApprovalItem> listClubApprovals(int pageNum, int pageSize, String keyword, Integer applyStatus);

    ClubApprovalDetailData getClubApprovalDetail(Long approvalId);

    ApprovalStatusUpdateData updateClubApprovalStatus(Long approvalId, ApprovalStatusUpdateRequest request);

    PageResponseData<ClubCancelApplyItem> listClubCancelApprovals(int pageNum, int pageSize, String keyword, Integer cancelStatus);

    ClubCancelDetailData getClubCancelApprovalDetail(Long cancelId);

    ClubCancelStatusUpdateData updateClubCancelApprovalStatus(Long cancelId, ClubCancelStatusUpdateRequest request);

    PageResponseData<SchoolAdminClubListItem> listSchoolAdminClubManagePage(int pageNum, int pageSize, String keyword, String category);

    SchoolAdminClubDetailData getSchoolAdminClubManageDetail(Long clubId);

    void updateSchoolAdminClub(Long clubId, SchoolAdminUpdateClubRequest request);

    void decisionClubApproval(Long approvalId, ApprovalDecisionRequest request);

    ProcessedCountData exitGraduationUsers(GraduationExitRequest request);

    FrozenCountData freezeAccounts(String clubId, FreezeAccountsRequest request);

    StatisticsOverviewData statisticsOverview();

    long autoExitGraduates();

    long autoFreezeCanceledClubAdmins();

    long autoStartEvents();

    long autoEndEvents();

    // Event approval
    PageResponseData<EventApprovalItem> listEventApprovals(int pageNum, int pageSize, String keyword, Integer eventStatus);
    EventDetailData getEventApprovalDetail(Long eventId);
    void decisionEventApproval(Long eventId, EventApprovalDecisionRequest request);
    PageResponseData<EventSummaryData> listEventSummaries(int pageNum, int pageSize, String keyword);
    EventSummaryData getEventSummaryByEventId(Long eventId);

    // Finance management
    PageResponseData<ExpenseApprovalItem> listPendingExpenseApprovals(int pageNum, int pageSize, String keyword);
    ExpenseDetailData getExpenseApprovalDetail(Long expenseId);
    void decisionExpenseApproval(Long expenseId, ExpenseApprovalDecisionRequest request);
    PageResponseData<LedgerItem> listClubLedger(Long clubId, int pageNum, int pageSize, Integer bizType,
                                                 LocalDateTime startTime, LocalDateTime endTime);
    FinanceAuditReportData getFinanceAuditReport(Long clubId, Integer year);

    // Anomaly expense management
    PageResponseData<AnomalyExpenseItem> listAnomalyExpenses(int pageNum, int pageSize, String keyword);

    // Statistics
    ClubStatisticsData getClubStatistics(int year);
    EventStatisticsData getEventStatistics(int year);
    FinanceStatisticsData getFinanceStatistics(int year);

    // Config management
    Map<String, String> getExpenseThreshold();
    void updateExpenseThreshold(String value);
    Map<String, Object> getClubApprovalStages();
    void updateClubApprovalStages(String stagesJson);
}
