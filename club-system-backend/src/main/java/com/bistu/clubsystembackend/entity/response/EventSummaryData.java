package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventSummaryData {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String clubName;
    private String summaryText;
    private String summaryImagesJson;  // 用于接收数据库的原始 JSON 字符串
    private List<String> summaryImages;  // 用于返回给前端的列表
    private BigDecimal feedbackScore;
    private String issueReflection;
    private String attachmentUrl;
    private LocalDateTime createdAt;
}
