package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SchoolAdminUpdateClubRequest {

    @Size(max = 100, message = "clubName length must be <= 100")
    private String clubName;

    @Size(max = 50, message = "category length must be <= 50")
    private String category;

    @Size(max = 1000, message = "purpose length must be <= 1000")
    private String purpose;

    @Size(max = 50, message = "instructorName length must be <= 50")
    private String instructorName;
}
