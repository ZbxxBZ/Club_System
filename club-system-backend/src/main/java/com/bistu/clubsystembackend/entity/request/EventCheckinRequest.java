package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventCheckinRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "checkinType is required")
    private Integer checkinType;

    @Size(max = 64, message = "deviceNo length must be <= 64")
    private String deviceNo;
}
