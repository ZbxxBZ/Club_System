package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {
    private Long positionId;

    @Size(max = 50)
    private String positionName;
}
