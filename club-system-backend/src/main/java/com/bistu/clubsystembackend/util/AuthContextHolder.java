package com.bistu.clubsystembackend.util;

public final class AuthContextHolder {

    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void set(CurrentUser currentUser) {
        CONTEXT.set(currentUser);
    }

    public static CurrentUser get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
