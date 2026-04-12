package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SsoLoginRequest {
    @NotBlank
    private String ticket;
}
