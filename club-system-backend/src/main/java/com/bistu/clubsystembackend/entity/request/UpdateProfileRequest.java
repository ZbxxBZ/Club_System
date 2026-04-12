package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 50, message = "真实姓名不能超过50字")
    private String realName;

    @Size(max = 30, message = "学号不能超过30字")
    private String studentNo;

    /** 修改密码时必填：旧密码 */
    private String oldPassword;

    /** 修改密码时必填：新密码（至少8位，含字母和数字） */
    private String newPassword;

    @Size(max = 20, message = "电话号码不能超过20字")
    private String phone;

    @Size(max = 100, message = "邮箱不能超过100字")
    private String email;
}
