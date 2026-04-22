package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitClubReviewRequest {
    @NotBlank(message = "年度工作总结不能为空")
    @Size(max = 5000, message = "年度工作总结不能超过5000字")
    private String summaryText;

    @Size(max = 255)
    private String attachmentUrl;
}
