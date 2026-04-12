package com.bistu.clubsystembackend.util;

import java.time.LocalDateTime;

public final class UserStatusUtil {

    private UserStatusUtil() {
    }

    public static String toStatusText(Integer dbStatus, LocalDateTime graduateAt) {
        if (graduateAt != null && !graduateAt.isAfter(LocalDateTime.now())) {
            return "GRADUATED";
        }
        if (dbStatus == null) {
            return "CANCELED";
        }
        return switch (dbStatus) {
            case 1 -> "ACTIVE";
            case 2 -> "FROZEN";
            case 3 -> "CANCELED";
            default -> "CANCELED";
        };
    }

    public static Integer toDbStatus(String status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case "ACTIVE" -> 1;
            case "FROZEN" -> 2;
            case "CANCELED" -> 3;
            default -> null;
        };
    }
}
