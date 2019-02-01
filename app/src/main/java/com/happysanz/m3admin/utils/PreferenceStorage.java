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
        editor.putString(M3AdminConstants.KEY_USER_TYPE, userType);
        editor.apply();
    }

    public static String getPIAProfileId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userType;
        userType = sharedPreferences.getString(M3AdminConstants.KEY_USER_TYPE, "");
        return userType;
    }
    /*End*/


}
