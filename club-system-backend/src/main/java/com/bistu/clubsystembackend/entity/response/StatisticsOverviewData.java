package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsOverviewData {
    private long activeClubCount;
    private long totalClubCount;
    private long activeUserCount;
    private long totalUserCount;
    private long pendingApprovalCount;
    private long suspiciousExpenseCount;
    private long monthEventCount;
    private long registeredMemberCount;
}
