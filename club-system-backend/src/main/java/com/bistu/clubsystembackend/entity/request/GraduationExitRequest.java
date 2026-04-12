package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GraduationExitRequest {
    @NotBlank
    private String mode;
}
