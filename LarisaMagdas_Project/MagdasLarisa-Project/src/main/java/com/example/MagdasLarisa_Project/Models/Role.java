package com.example.MagdasLarisa_Project.Models;

public enum Role {
    ROLE_USER,
    ROLE_AUTHOR;

    public static boolean isValidRole(String role) {
        for (Role r : values()) {
            if (r.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}