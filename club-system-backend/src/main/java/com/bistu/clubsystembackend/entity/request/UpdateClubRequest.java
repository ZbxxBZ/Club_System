package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateClubRequest {
    @NotBlank
    private String clubName;
    @NotBlank
    private String category;
    @Size(max = 300, message = "introduction length must be <= 300")
    private String introduction;
    private String purpose;
    @NotBlank
    private String instructorName;
}
