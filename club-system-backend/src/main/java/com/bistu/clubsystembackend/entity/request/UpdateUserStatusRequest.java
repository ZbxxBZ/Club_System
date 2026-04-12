package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotBlank
    @Pattern(regexp = "^(ACTIVE|FROZEN)$", message = "status must be ACTIVE or FROZEN")
    private String status;
    private String reason;
}
