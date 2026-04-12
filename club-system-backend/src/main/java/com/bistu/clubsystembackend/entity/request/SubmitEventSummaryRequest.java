package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubmitEventSummaryRequest {

    @NotBlank(message = "summaryText is required")
    private String summaryText;

    private List<String> summaryImages;

    private BigDecimal feedbackScore;

    private String issueReflection;

    @Size(max = 255, message = "attachmentUrl length must be <= 255")
    private String attachmentUrl;
}
