package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddClubMemberRequest {

    @NotNull
    private Long userId;

    private Long positionId;

    @Size(max = 50)
    private String positionName;
}
