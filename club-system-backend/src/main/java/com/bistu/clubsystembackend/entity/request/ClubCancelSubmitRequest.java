package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClubCancelSubmitRequest {

    @NotNull
    @Min(1)
    @Max(2)
    private Integer applyType;

    @NotBlank
    @Size(max = 500)
    private String applyReason;

    @NotBlank
    @Size(max = 255)
    private String assetSettlementUrl;
}
