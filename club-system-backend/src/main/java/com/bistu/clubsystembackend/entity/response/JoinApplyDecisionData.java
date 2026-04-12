package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinApplyDecisionData {
    private Long applyId;
    private String joinStatus;
    private LocalDateTime reviewedAt;
}
