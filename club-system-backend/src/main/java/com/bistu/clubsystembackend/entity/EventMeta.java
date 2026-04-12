package com.bistu.clubsystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventMeta {
    private Long id;
    private Long clubId;
    private Integer eventStatus;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
