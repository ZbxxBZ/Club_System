package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentCheckinRequest {

    private Long eventId;

    @NotBlank(message = "checkinCode is required")
    @Size(max = 20, message = "checkinCode length must be <= 20")
    private String checkinCode;

    @NotNull(message = "checkinType is required")
    private Integer checkinType;

    @Size(max = 64, message = "deviceNo length must be <= 64")
    private String deviceNo;
}
