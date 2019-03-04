package com.happysanz.m3admin.utils;

public class M3AdminConstants {

    //URL'S
    //BASE URL
    private static final String BASE_URL = "http://happysanz.net/";

    //BUILD URL
//    public static final String BUILD_URL = BASE_URL + "M3TNSRLM/";
    public static final String BUILD_URL = BASE_URL + "m3test/";

    //GENERAL URL
    //Users URL
    public static final String USER_LOGIN_API = "apimain/login/";

    //Forgot password URL
    public static final String USER_FORGOT_PASSWORD = "apimain/forgot_password/";


    //    PIA
//    MOBILIZER LIST
    public static final String GET_MOBILIZER_LIST = "apimain/pia_mob_list";


    //    CENTER LIST
    public static final String GET_CENTER_LIST = "apipia/center_list";

    //    CREATE CENTER
    public static final String CREATE_CENTER = "apipia/create_center";


    //    TASK LIST
    public static final String TASK_LIST = "apipia/list_task";

    //    SCHEME LIST
    public static final String SCHEME_LIST = "apipia/list_scheme";


    //    TRADE LIST
    public static final String TRADE_LIST = "apipia/list_trade";


    public static final String PROJECT_PERIOD = "apipia/project_period";

    //    CENTER GALLERY LIST
    public static final String VIEW_GALLERY = "apipia/center_gallery";

    public static final String ADD_PHOTO = "apipia/add_center_photos/";

    public static final String ADD_LOGO = "apipia/update_center_banner/";

    public static final String ADD_VIDEO = "apipia/add_center_videos/";

    public static final String ASSETS_URL_LOGO = "assets/center/logo/";


    public static final String VIEW_VIDEO_GALLERY = "apipia/center_videos";

    //    TRADE LIST
    public static final String CREATE_TRADE= "apipia/create_trade";


    //    PROSPECT LIST
    public static final String ALL_STUDENTS = "apipia/list_students";

    //    PROSPECT LIST
    public static final String STUDENTS_LIST_STATUS = "apipia/list_students_status";


    //    USER LIST
    public static final String USER_LIST = "apipia/user_list";

    //    Staff LIST
    public static final String TNSRLM_STAFF_LIST = "apimain/user_list";

    //    TASK ADD
    public static final String TASK_ADD = "apipia/add_task";

    //    TASK UPDATE
    public static final String TASK_UPDATE = "apipia/update_task";

    //    TASK UPDATE
    public static final String PROJECT_TIME = "apipia/project_period";

    //    TASK UPDATE
    public static final String ADD_CANDIDATE = "apipia/add_student";


    //    Task add params
    public static final String PARAMS_TASK_TITLE = "task_title";
    public static final String PARAMS_TASK_ID = "task_title";
    public static final String PARAMS_TASK_DESCRIPTION = "task_description";
    public static final String PARAMS_TASK_DATE = "task_date";
    public static final String PARAMS_TASK_STATUS = "status";

    //    Task add params
    public static final String PARAMS_TRADE_TITLE = "trade_name";

    public static final String UPLOAD_CANDIDATE_PIC = "student_picupload/";


    //    TNSRLM

//    PIA Creation URL
    public static final String CREATE_PIA = "apimain/create_pia";

//    PIA Creation URL
    public static final String PIA_LIST = "apimain/pia_list";


    //    Service Params
    public static String PARAM_MESSAGE = "msg";

    // User Login Preferences
    // User data
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER_IMAGE = "user_pic";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_TYPE_NAME = "user_type_name";

    // PIA Profile
    public static final String KEY_PIA_PROFILE_ID = "pia_profile_id";
    public static final String KEY_PIA_ID = "pia_id";
    public static final String KEY_PIA_PRN_NUMBER = "pia_prn_number";
    public static final String KEY_PIA_NAME = "pia_name";
    public static final String KEY_PIA_ADDRESS = "pia_address";
    public static final String KEY_PIA_PHONE = "pia_phone";
    public static final String KEY_PIA_EMAIL = "pia_email";

    //    Shared FCM ID
    public static final String KEY_FCM_ID = "fcm_id";


    public static final String KEY_AADHAAR = "aadhaar_action";


    public static final String PARAMS_CENTER_ID = "center_id";
    public static final String PARAMS_CENTER_PHOTO = "center_photo";


    public static final String PARAMS_START_DATE = "start_date";
    public static final String PARAMS_END_DATE = "end_date";


    // Alert Dialog Constants
    public static String ALERT_DIALOG_TITLE = "alertDialogTitle";
    public static String ALERT_DIALOG_MESSAGE = "alertDialogMessage";
    public static String ALERT_DIALOG_TAG = "alertDialogTag";
    public static String ALERT_DIALOG_POS_BUTTON = "alert_dialog_pos_button";
    public static String ALERT_DIALOG_NEG_BUTTON = "alert_dialog_neg_button";

    //    SignIn params
    public static final String PARAMS_USERNAME = "user_name";
    public static final String PARAMS_PASSWORD = "password";
    public static final String PARAMS_GCM_KEY = "device_id";
    public static final String PARAMS_MOBILE_TYPE = "mobile_type";

    //    Create Pia params
    public static final String PARAMS_USER_ID = "user_id";
    public static final String PARAMS_UNIQUE_NUMBER = "unique_number";
    public static final String PARAMS_NAME = "name";
    public static final String PARAMS_ADDRESS = "address";
    public static final String PARAMS_PHONE = "phone";
    public static final String PARAMS_EMAIL = "email";

    //    Create Pia params
    public static final String PARAMS_MOB_ID = "mob_id";

    //    Create Center params
    public static final String PARAMS_CENTER_NAME = "center_name";
    public static final String PARAMS_CENTER_ADDRESS = "center_address";
    public static final String PARAMS_CENTER_INFO = "center_info";


    //    Add candidate params
    public static final String PARAMS_HAVE_AADHAAR_CARD = "have_aadhaar_card";
    public static final String PARAMS_AADHAAR_CARD_NUMBER = "aadhaar_card_number";
    public static final String PARAMS_SEX = "sex";
    public static final String PARAMS_DOB = "dob";
    public static final String PARAMS_AGE = "age";
    public static final String PARAMS_NATIONALITY = "nationality";
    public static final String PARAMS_RELIGION = "religion";
    public static final String PARAMS_COMMUNITY_CLASS = "community_class";
    public static final String PARAMS_COMMUNITY = "community";
    public static final String PARAMS_FATHER_NAME = "father_name";
    public static final String PARAMS_MOTHER_NAME = "mother_name";
    public static final String PARAMS_MOBILE = "mobile";
    public static final String PARAMS_SEC_MOBILE = "sec_mobile";
    public static final String PARAMS_STATE = "state";
    public static final String PARAMS_CITY = "city";
    public static final String PARAMS_MOTHER_TONGUE = "mother_tongue";
    public static final String PARAMS_DISABILITY = "disability";
    public static final String PARAMS_BLOOD_GROUP = "blood_group";
    public static final String PARAMS_ADMISSION_DATE = "admission_date";
    public static final String PARAMS_ADMISSION_LOCATION = "admission_location";
    public static final String PARAMS_ADMISSION_LATITUDE = "admission_latitude ";
    public static final String PARAMS_ADMISSION_LONGITUDE = "admission_longitude";
    public static final String PARAMS_PREFERRED_TRADE = "preferred_trade";
    public static final String PARAMS_PREFERRED_TIMING = "preferred_timing";
    public static final String PARAMS_LAST_INSTITUTE = "last_institute";
    public static final String PARAMS_LAST_STUDIED = "last_studied";
    public static final String PARAMS_QUALIFIED_PROMOTION = "qualified_promotion";
    public static final String PARAMS_TRANSFER_CERTIFICATE = "transfer_certificate";
    public static final String PARAMS_STATUS = "status";
    public static final String PARAMS_CREATED_BY = "created_by";
    public static final String PARAMS_CREATED_AT = "created_at";
    public static final String PARAMS_PIA_ID = "pia_id";


}
