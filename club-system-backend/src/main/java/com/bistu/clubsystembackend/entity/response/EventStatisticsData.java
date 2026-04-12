package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class EventStatisticsData {
    private int totalEventCount;
    private long totalSignupCount;
    private List<MonthlyEventStat> monthlyEventStats;
    private List<TopEventClub> topEventClubs;
    private List<StatusCount> statusDistribution;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class MonthlyEventStat {
        private int month;
        private int eventCount;
        private long signupCount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TopEventClub {
        private String clubName;
        private int eventCount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class StatusCount {
        private String statusName;
        private int count;
    }
}
