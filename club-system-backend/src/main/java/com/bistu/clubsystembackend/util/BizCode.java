package com.bistu.clubsystembackend.util;

import lombok.Getter;

@Getter
public enum BizCode {
    SUCCESS(0, "success"),
    PARAM_INVALID(1001, "输入信息有误"),
    ACCOUNT_NOT_FOUND(1002, "账号或密码错误"),
    PASSWORD_INCORRECT(1003, "账号或密码错误"),
    ACCOUNT_DISABLED(1004, "账号不可用，请联系管理员"),
    ACCOUNT_EXISTS(1005, "该账号已注册"),
    PASSWORD_WEAK(1006, "密码至少8位，且包含字母和数字"),
    LOGIN_EXPIRED(1007, "登录已失效，请重新登录"),
    NO_PERMISSION(1003, "无权限访问"),
    RESOURCE_NOT_FOUND(1005, "资源不存在"),
    DATA_SCOPE_DENIED(1006, "数据范围越权"),
    ACCOUNT_STATUS_INVALID(1004, "账号状态不可用"),
    SYSTEM_ERROR(1099, "系统繁忙，请稍后再试");

    private final int code;
    private final String message;

    BizCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
