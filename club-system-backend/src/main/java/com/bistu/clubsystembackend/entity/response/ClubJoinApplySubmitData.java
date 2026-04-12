package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubJoinApplySubmitData {
    private Long applyId;
    private Long clubId;
    private String joinStatus;
    private LocalDateTime createdAt;
}
