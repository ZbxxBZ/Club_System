package com.bistu.clubsystembackend.service;

import com.bistu.clubsystembackend.entity.request.CreateExpenseRequest;
import com.bistu.clubsystembackend.entity.request.CreateEventRequest;
import com.bistu.clubsystembackend.entity.request.CreateIncomeRequest;
import com.bistu.clubsystembackend.entity.request.EventCheckinRequest;
import com.bistu.clubsystembackend.entity.request.SubmitEventSummaryRequest;
import com.bistu.clubsystembackend.entity.request.AddClubMemberRequest;
import com.bistu.clubsystembackend.entity.request.ClubCancelSubmitRequest;
import com.bistu.clubsystembackend.entity.request.CreateClubPositionRequest;
import com.bistu.clubsystembackend.entity.request.JoinApplyDecisionRequest;
import com.bistu.clubsystembackend.entity.request.UpdateRecruitConfigRequest;
import com.bistu.clubsystembackend.entity.request.UpdateClubPositionRequest;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyData;
import com.bistu.clubsystembackend.entity.response.ClubCancelApplyItem;
import com.bistu.clubsystembackend.entity.request.UpdateClubRequest;
import com.bistu.clubsystembackend.entity.request.UpdateMemberRoleRequest;
import com.bistu.clubsystembackend.entity.response.ClubAdminJoinApplyItem;
import com.bistu.clubsystembackend.entity.response.ClubAdminEventItem;
import com.bistu.clubsystembackend.entity.response.EventDetailData;
import com.bistu.clubsystembackend.entity.response.EventSignupItem;
import com.bistu.clubsystembackend.entity.response.EventSummaryData;
import com.bistu.clubsystembackend.entity.response.ClubInfoData;
import com.bistu.clubsystembackend.entity.response.ClubMemberItem;
import com.bistu.clubsystembackend.entity.response.ClubPositionItem;
import com.bistu.clubsystembackend.entity.response.ClubRecruitConfigData;
import com.bistu.clubsystembackend.entity.response.ClubBalanceData;
import com.bistu.clubsystembackend.entity.response.ExpenseDetailData;
import com.bistu.clubsystembackend.entity.response.FinanceRecordItem;
import com.bistu.clubsystembackend.entity.response.IncomeDetailData;
import com.bistu.clubsystembackend.entity.response.JoinApplyDecisionData;
import com.bistu.clubsystembackend.entity.response.LedgerItem;
import com.bistu.clubsystembackend.entity.response.PageResponseData;
import com.bistu.clubsystembackend.entity.response.PositionDeleteData;

import java.time.LocalDateTime;

public interface ClubAdminPermissionService {

    ClubInfoData getMyClub();

    void updateMyClub(UpdateClubRequest request);

    PageResponseData<ClubMemberItem> listMyClubMembers(int pageNum, int pageSize);

    void updateMyMemberRole(Long memberId, UpdateMemberRoleRequest request);

    void removeMyMember(Long memberId);

    PageResponseData<FinanceRecordItem> listMyFinances(int pageNum, int pageSize);

    ClubCancelApplyData submitMyClubCancel(ClubCancelSubmitRequest request);

    PageResponseData<ClubCancelApplyItem> listMyClubCancelApplies(int pageNum, int pageSize);

    ClubRecruitConfigData getMyClubRecruitConfig();

    ClubRecruitConfigData updateMyClubRecruitConfig(UpdateRecruitConfigRequest request);

    PageResponseData<ClubPositionItem> listMyClubPositions(int pageNum, int pageSize);

    ClubPositionItem createMyClubPosition(CreateClubPositionRequest request);

    ClubPositionItem updateMyClubPosition(Long positionId, UpdateClubPositionRequest request);

    PositionDeleteData deleteMyClubPosition(Long positionId);

    void addMyClubMember(AddClubMemberRequest request);

    PageResponseData<ClubAdminJoinApplyItem> listMyClubJoinApplies(int pageNum, int pageSize, String status);

    JoinApplyDecisionData decideJoinApply(Long applyId, JoinApplyDecisionRequest request);

    // Event management
    PageResponseData<ClubAdminEventItem> listMyEvents(int pageNum, int pageSize, Integer eventStatus);
    EventDetailData createMyEvent(CreateEventRequest request);
    EventDetailData getMyEventDetail(Long eventId);
    EventDetailData resubmitMyEvent(Long eventId, CreateEventRequest request);
    void updateMyEventCheckinCode(Long eventId, String checkinCode);
    PageResponseData<EventSignupItem> listMyEventSignups(Long eventId, int pageNum, int pageSize);
    void checkinMyEvent(Long eventId, EventCheckinRequest request);
    void cancelCheckinMyEvent(Long eventId, Long userId);
    EventSummaryData submitMyEventSummary(Long eventId, SubmitEventSummaryRequest request);
    EventSummaryData getMyEventSummary(Long eventId);

    // Finance management
    IncomeDetailData createIncome(CreateIncomeRequest request);
    PageResponseData<IncomeDetailData> listMyIncomes(int pageNum, int pageSize);
    ExpenseDetailData createExpense(CreateExpenseRequest request);
    PageResponseData<ExpenseDetailData> listMyExpenses(int pageNum, int pageSize, Integer expenseStatus);
    ExpenseDetailData resubmitExpense(Long expenseId, CreateExpenseRequest request);
    PageResponseData<LedgerItem> listMyLedger(int pageNum, int pageSize, Integer bizType,
                                               LocalDateTime startTime, LocalDateTime endTime);
    ClubBalanceData getMyBalance();
}
