package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    @NotBlank(message = "title is required")
    @Size(max = 200, message = "title length must be <= 200")
    private String title;

    private String content;

    @NotBlank(message = "location is required")
    @Size(max = 200, message = "location length must be <= 200")
    private String location;

    @NotNull(message = "startAt is required")
    private LocalDateTime startAt;

    @NotNull(message = "endAt is required")
    private LocalDateTime endAt;

    private LocalDateTime signupStartAt;

    private LocalDateTime signupEndAt;

    @NotNull(message = "limitCount is required")
    private Integer limitCount;

    private Boolean onlyMember;

    @Size(max = 255, message = "safetyPlanUrl length must be <= 255")
    private String safetyPlanUrl;
}
