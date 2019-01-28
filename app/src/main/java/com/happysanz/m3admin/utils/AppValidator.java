package com.happysanz.m3admin.utils;

/**
 * Created by Admin on 12-04-2017.
 */

public class AppValidator {

    public static boolean checkNullString(String value) {
        if (value == null)
            return false;
        else
            return value.trim().length() > 0;
    }

    public static boolean checkStringMinLength(int minValue, String value) {
        if (value == null)
            return false;
        return value.trim().length() >= minValue;
    }

    public static String checkEditTextValid100AndA(String value, int totalMark) {
        boolean check = value.matches("\\d+");
        String EditTextValid;
        if (check) {
            int validMark = totalMark;
            int mark = Integer.parseInt(value);
            if (mark <= -1 || mark >= validMark + 1) {
                EditTextValid = "NotValidMark";
            } else {
                EditTextValid = "valid";
            }
        } else {
            if (!value.contentEquals("AB")) {
                EditTextValid = "NotValidAbsent";
            } else {
                EditTextValid = "valid";
            }
        }
        return EditTextValid;
    }

    public static String checkEditTextValidInternalAndA(String value, int internalMark) {
        boolean check = value.matches("\\d+");
        String EditTextValid;
        if (check) {
            int validMark = internalMark;
            int mark = Integer.parseInt(value);
            if (mark <= -1 || mark >= validMark + 1) {
                EditTextValid = "NotValidMark";
            } else {
                EditTextValid = "valid";
            }
        } else {
            if (!value.contentEquals("AB")) {
                EditTextValid = "NotValidAbsent";
            } else {
                EditTextValid = "valid";
            }
        }
        return EditTextValid;
    }

    public static String checkEditTextValidExternalAndA(String value, int externalMark) {
        boolean check = value.matches("\\d+");
        String EditTextValid;
        if (check) {
            int validMark = externalMark;
            int mark = Integer.parseInt(value);
            if (mark <= -1 || mark >= validMark + 1) {
                EditTextValid = "NotValidMark";
            } else {
                EditTextValid = "valid";
            }
        } else {
            if (!value.contentEquals("AB")) {
                EditTextValid = "NotValidAbsent";
            } else {
                EditTextValid = "valid";
            }
        }
        return EditTextValid;
    }
}
