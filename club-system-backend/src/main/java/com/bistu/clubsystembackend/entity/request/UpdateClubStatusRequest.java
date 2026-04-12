package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClubStatusRequest {
    @NotBlank
    @Pattern(regexp = "^(ACTIVE|FROZEN)$", message = "status must be ACTIVE or FROZEN")
    private String status;

    @Size(max = 500, message = "reason length must be <= 500")
    private String reason;
}
