package com.bistu.clubsystembackend.util;

import java.util.Arrays;

public final class AccessChecker {

    private AccessChecker() {
    }

    public static CurrentUser requireLogin() {
        CurrentUser currentUser = AuthContextHolder.get();
        if (currentUser == null) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
        return currentUser;
    }

    public static CurrentUser requireRole(String... roles) {
        CurrentUser currentUser = requireLogin();
        String role = currentUser.getRoleCode();
        if (role == null || Arrays.stream(roles).noneMatch(role::equals)) {
            throw new BusinessException(BizCode.NO_PERMISSION);
        }
        return currentUser;
    }
}
