package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClubPositionRequest {

    @NotBlank
    @Size(max = 50)
    private String positionName;

    private Long parentPositionId;

    @NotNull
    @Min(1)
    private Integer levelNo;

    @Min(0)
    private Integer sortNo;
}
