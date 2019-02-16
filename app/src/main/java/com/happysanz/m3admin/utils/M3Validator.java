package com.happysanz.m3admin.utils;

public class M3Validator {
    public static boolean checkNullString(String value) {
        if (value == null)
            return false;
        else
            return value.trim().length() > 0;
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkStringMinLength(int minValue, String value) {
        if (value == null)
            return false;
        return value.trim().length() >= minValue;
    }

    public static boolean checkMobileNumLength(String mobile) {
        if (mobile.length() < 10 || mobile.length() > 13) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkUniqueNumLength(String mobile) {
        if (mobile.length() == 13) {
            return true;
        } else {
            return false;
        }
    }
}
