package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

@Data
public class SchoolAdminClubListItem {
    private Long clubId;
    private String clubName;
    private String category;
    private Integer status;
    private Integer applyStatus;
    private String initiatorRealName;
    private String instructorName;
    private Boolean canEdit;
    private Boolean canDelete;
    private String editDisabledReason;
    private String deleteDisabledReason;
}
