package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClubApplySubmitData {
    private Long applyId;
    private Long clubId;
    private Integer applyStatus;
    private String currentStep;
}
