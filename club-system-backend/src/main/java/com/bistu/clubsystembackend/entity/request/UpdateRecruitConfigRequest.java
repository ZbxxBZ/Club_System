package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateRecruitConfigRequest {

    @NotNull(message = "recruitStartAt is required")
    private LocalDateTime recruitStartAt;

    @NotNull(message = "recruitEndAt is required")
    private LocalDateTime recruitEndAt;

    @NotNull(message = "recruitLimit is required")
    @Min(value = 1, message = "recruitLimit must be at least 1")
    @Max(value = 10000, message = "recruitLimit must be at most 10000")
    private Integer recruitLimit;

    @NotBlank(message = "recruitStatus is required")
    @Pattern(regexp = "^(OPEN|CLOSED)$", message = "recruitStatus must be OPEN or CLOSED")
    private String recruitStatus;
}
