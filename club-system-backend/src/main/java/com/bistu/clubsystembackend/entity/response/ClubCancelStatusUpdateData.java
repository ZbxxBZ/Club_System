package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClubCancelStatusUpdateData {
    private Long id;
    private Integer cancelStatus;
}
