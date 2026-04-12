package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClubApplySubmitRequest {
    @NotBlank
    @Size(max = 100)
    private String clubName;

    @NotBlank
    @Size(max = 30)
    private String category;

    @Size(max = 500)
    private String purpose;

    @NotBlank
    @Size(max = 50)
    private String instructorName;

    @NotBlank
    @Size(max = 255)
    private String charterUrl;

    @NotBlank
    @Size(max = 255)
    private String instructorProofUrl;

    @Size(max = 1000)
    private String remark;
}
