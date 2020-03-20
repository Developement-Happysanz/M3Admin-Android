package com.happysanz.m3admin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceStorage {

    /*To save FCM key locally*/
    public static void saveGCM(Context context, String gcmId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_FCM_ID, gcmId);
        editor.apply();
    }

    public static String getGCM(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(M3AdminConstants.KEY_FCM_ID, "");
    }
    /*End*/

    // UserId
    public static void saveUserId(Context context, String userId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_USER_ID, userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String  userId;
        userId = sharedPreferences.getString(M3AdminConstants.KEY_USER_ID, "");
        return userId;
    }
    /*End*/

    // Name
    public static void saveName(Context context, String name) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_NAME, name);
        editor.apply();
    }

    public static String getName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String name;
        name = sharedPreferences.getString(M3AdminConstants.KEY_NAME, "");
        return name;
    }
    /*End*/

    // User Name
    public static void saveUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userName;
        userName = sharedPreferences.getString(M3AdminConstants.KEY_USER_NAME, "");
        return userName;
    }
    /*End*/

    // User Image
    public static void saveUserPicture(Context context, String userPicture) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_USER_IMAGE, userPicture);
        editor.apply();
    }

    public static String getUserPicture(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userPicture;
        userPicture = sharedPreferences.getString(M3AdminConstants.KEY_USER_IMAGE, "");
        return userPicture;
    }
    /*End*/

    // User Type Name
    public static void saveUserTypeName(Context context, String userTypeName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_USER_TYPE_NAME, userTypeName);
        editor.apply();
    }

    public static String getUserTypeName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userTypeName;
        userTypeName = sharedPreferences.getString(M3AdminConstants.KEY_USER_TYPE_NAME, "");
        return userTypeName;
    }
    /*End*/

    // User Type
    public static void saveUserType(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_USER_TYPE, userType);
        editor.apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_USER_TYPE, "");
        return userType;
    }
    /*End*/

    // PIA Profile Id
    public static void savePIAProfileId(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_PIA_PROFILE_ID, userType);
        editor.apply();
    }

    public static String getPIAProfileId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_PIA_PROFILE_ID, "");
        return userType;
    }
    /*End*/

    // PIA Profile Id
    public static void saveSchemeId(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.SCHEME_ID, userType);
        editor.apply();
    }

    public static String getSchemeId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.SCHEME_ID, "");
        return userType;
    }
    /*End*/

    // PIA Profile Id
    public static void saveCenterId(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.PARAMS_CENTER_ID, userType);
        editor.apply();
    }

    public static String getCenterId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.PARAMS_CENTER_ID, "");
        return userType;
    }
    /*End*/

    // PIA PRN Number
    public static void savePIAPRNNumber(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_PIA_PRN_NUMBER, userType);
        editor.apply();
    }

    public static String getPIAPRNNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_PIA_PRN_NUMBER, "");
        return userType;
    }
    /*End*/

    // PIA Name
    public static void savePIAName(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_PIA_NAME, userType);
        editor.apply();
    }

    public static String getPIAName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_PIA_NAME, "");
        return userType;
    }
    /*End*/

    // PIA Address
    public static void savePIAAddress(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_PIA_ADDRESS, userType);
        editor.apply();
    }

    public static String getPIAAddress(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_PIA_ADDRESS, "");
        return userType;
    }
    /*End*/

    // PIA Phone
    public static void savePIAPhone(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_PIA_PHONE, userType);
        editor.apply();
    }

    public static String getPIAPhone(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_PIA_PHONE, "");
        return userType;
    }
    /*End*/

    // PIA Email
    public static void savePIAEmail(Context context, String userType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_PIA_EMAIL, userType);
        editor.apply();
    }

    public static String getPIAEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_PIA_EMAIL, "");
        return userType;
    }
    /*End*/

    // TNSRLM
    public static void saveTnsrlmCheck(Context context, Boolean check) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tnsrl_check", check);
        editor.apply();
    }

    public static Boolean getTnsrlmCheck(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Boolean check = false;
        check = sharedPreferences.getBoolean("tnsrl_check",check);
        return check;
    }
    /*End*/

    public static void saveAadhaarAction(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.KEY_AADHAAR, staffQualification);
        editor.apply();
    }

    public static String getAadhaarAction(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(M3AdminConstants.KEY_AADHAAR, "");
        return staffQualification;
    }

    public static void savePIACount(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.PIA_COUNT, staffQualification);
        editor.apply();
    }

    public static String getPIACount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(M3AdminConstants.PIA_COUNT, "");
        return staffQualification;
    }

    public static void savemobCount(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.MOB_COUNT, staffQualification);
        editor.apply();
    }

    public static String getmobCount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(M3AdminConstants.MOB_COUNT, "");
        return staffQualification;
    }

    public static void savecenterCount(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.CENTER_COUNT, staffQualification);
        editor.apply();
    }

    public static String getcenterCount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(M3AdminConstants.CENTER_COUNT, "");
        return staffQualification;
    }

    public static void savestudentCount(Context context, String staffQualification) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.PROSPECT_COUNT, staffQualification);
        editor.apply();
    }

    public static String getstudentCount(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String staffQualification;
        staffQualification = sharedPreferences.getString(M3AdminConstants.PROSPECT_COUNT, "");
        return staffQualification;
    }


    // Admission Id
    public static void saveAdmissionId(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.PARAMS_ADMISSION_ID, staffId);
        editor.apply();
    }

    public static String getAdmissionId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.PARAMS_ADMISSION_ID, "");
        return userType;
    }
    /*End*/

    // Admission Id
    public static void saveCaste(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.PARAMS_COMMUNITY_CLASS, staffId);
        editor.apply();
    }

    public static String getCaste(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.PARAMS_COMMUNITY_CLASS, "");
        return userType;
    }
    /*End*/

    // Admission Id
    public static void saveDisability(Context context, String staffId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(M3AdminConstants.PARAMS_DISABILITY, staffId);
        editor.apply();
    }

    public static String getDisability(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.PARAMS_DISABILITY, "");
        return userType;
    }
    /*End*/
    
}
