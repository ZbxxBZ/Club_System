package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotBlank
    @Pattern(regexp = "^(STUDENT|CLUB_ADMIN|SCHOOL_ADMIN)$", message = "roleCode must be STUDENT, CLUB_ADMIN or SCHOOL_ADMIN")
    private String roleCode;
}
