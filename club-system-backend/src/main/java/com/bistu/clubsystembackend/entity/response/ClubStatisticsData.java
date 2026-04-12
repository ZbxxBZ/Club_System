package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ClubStatisticsData {
    private List<CategoryCount> categoryDistribution;
    private List<StatusCount> statusDistribution;
    private List<MemberSizeRange> memberSizeDistribution;
    private int yearlyNewCount;
    private int yearlyCancelCount;
    private long totalStudentCount;
    private long joinedStudentCount;
    private double coverageRate;
    private List<ActiveClub> topActiveClubs;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class CategoryCount {
        private String category;
        private int count;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class StatusCount {
        private String statusName;
        private int count;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class MemberSizeRange {
        private String range;
        private int count;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class ActiveClub {
        private String clubName;
        private int memberCount;
        private int eventCount;
    }
}
