package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClubJoinApplySubmitRequest {

    @Size(max = 500, message = "selfIntro length must be <= 500")
    private String selfIntro;

    @Size(max = 500, message = "applyReason length must be <= 500")
    private String applyReason;
}
