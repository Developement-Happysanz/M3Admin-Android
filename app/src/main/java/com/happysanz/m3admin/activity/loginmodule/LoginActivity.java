package com.happysanz.m3admin.activity.loginmodule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.MainActivity;
import com.happysanz.m3admin.activity.piamodule.PiaDashboard;
import com.happysanz.m3admin.activity.tnsrlmmodule.TnsrlmDashboard;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.FirstTimePreference;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PermissionUtil;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText edtUsername, edtPassword;
    private Button signIn;
    private TextView txtForgotPsw;

    private static String[] PERMISSIONS_ALL = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION_All = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.login);
        signIn.setOnClickListener(this);
        txtForgotPsw = findViewById(R.id.forgot);
        txtForgotPsw.setOnClickListener(this);

        FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

        if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestAllPermissions();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
        return true;
    }

    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission == true) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == signIn) {
                if (validateFields()) {

                    String username = edtUsername.getText().toString();
                    String password = edtPassword.getText().toString();

                    String GCMKey = PreferenceStorage.getGCM(getApplicationContext());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(M3AdminConstants.PARAMS_USERNAME, username);
                        jsonObject.put(M3AdminConstants.PARAMS_PASSWORD, password);
                        jsonObject.put(M3AdminConstants.PARAMS_GCM_KEY, GCMKey);
                        jsonObject.put(M3AdminConstants.PARAMS_MOBILE_TYPE, "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = M3AdminConstants.BUILD_URL + M3AdminConstants.USER_LOGIN_API;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            } else if (v == txtForgotPsw) {
                Intent i = new Intent(LoginActivity.this,
                        ForgotPasswordActivity.class);
                startActivity(i);
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.edtUsername.getText().toString().trim())) {
            edtUsername.setError(getString(R.string.err_username));
            requestFocus(edtUsername);
            return false;
        } else if (!M3Validator.checkNullString(this.edtPassword.getText().toString().trim())) {
            edtPassword.setError(getString(R.string.err_empty_password));
            requestFocus(edtPassword);
            return false;
        } else if (!M3Validator.checkStringMinLength(6, this.edtPassword.getText().toString().trim())) {
            edtPassword.setError(getString(R.string.err_min_pass_length));
            requestFocus(edtPassword);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(M3AdminConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            try {

//                startService(new Intent(LoginActivity.this, LocationService.class));
//                startService(new Intent(LoginActivity.this, GPSTracker.class));

                JSONObject userData = response.getJSONObject("userData");

                String findUser = userData.getString("user_type");

                if (findUser.equalsIgnoreCase("3")) {
                    JSONObject piaProfile = response.getJSONObject("piaProfile");
//                JSONObject dashBoardData = response.getJSONObject("dashboardData");

                    saveUserData(userData);
                    savePIAProfile(piaProfile);


                    Intent intent = new Intent(this, PiaDashboard.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else if (findUser.equalsIgnoreCase("1")) {

                    JSONObject dashBoardData = response.getJSONObject("dashboardData");
                    saveUserData(userData);
                    saveTNSRLMProfile(dashBoardData);

                    Intent intent = new Intent(this, TnsrlmDashboard.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialogHelper.showSimpleAlertDialog(this, "Login as valid user");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void saveUserData(JSONObject userData) {

        Log.d(TAG, "userData dictionary" + userData.toString());

        String userId = "";
        String fullName = "";
        String userName = "";
        String userPicture = "";
        String userTypeName = "";
        String userType = "";
        String passwordStatus = "";

        try {

            if (userData != null) {

                // User Preference - User Id
                userId = userData.getString("user_id");
                if ((userId != null) && !(userId.isEmpty()) && !userId.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserId(this, userId);
                }

                // User Preference - User Full Name
                fullName = userData.getString("name");
                if ((fullName != null) && !(fullName.isEmpty()) && !fullName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveName(this, fullName);
                }

                // User Preference - User Name
                userName = userData.getString("user_name");
                if ((userName != null) && !(userName.isEmpty()) && !userName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserName(this, userName);
                }

                // User Preference - User Picture
                userPicture = userData.getString("user_pic");
                if ((userPicture != null) && !(userPicture.isEmpty()) && !userPicture.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserPicture(this, userPicture);
                }

                // User Preference - User Type Name
                userTypeName = userData.getString("user_type_name");
                if ((userTypeName != null) && !(userTypeName.isEmpty()) && !userTypeName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserTypeName(this, userTypeName);
                }

                // User Preference - User Type
                userType = userData.getString("user_type");
                if ((userType != null) && !(userType.isEmpty()) && !userType.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserType(this, userType);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void savePIAProfile(JSONObject piaProfile) {

        Log.d(TAG, "piaProfile dictionary" + piaProfile.toString());

        String piaProfileId = "";
        String piaPRNNumber = "";
        String piaName = "";
        String piaAddress = "";
        String piaPhone = "";
        String piaEmail = "";
        String piaProfilePic = "";
        String schemeId = "";

        try {

            if (piaProfile != null) {

                // PIA Preference - PIA profile Id
                piaProfileId = piaProfile.getString("pia_profile_id");
                if ((piaProfileId != null) && !(piaProfileId.isEmpty()) && !piaProfileId.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAProfileId(this, piaProfileId);
                }

                // PIA Preference - PIA PRN Number
                piaPRNNumber = piaProfile.getString("pia_unique_number");
                if ((piaPRNNumber != null) && !(piaPRNNumber.isEmpty()) && !piaPRNNumber.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAPRNNumber(this, piaPRNNumber);
                }

                // PIA Preference - PIA Name
                piaName = piaProfile.getString("pia_name");
                if ((piaName != null) && !(piaName.isEmpty()) && !piaName.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAName(this, piaName);
                }

                // PIA Preference - PIA Address
                piaAddress = piaProfile.getString("pia_address");
                if ((piaAddress != null) && !(piaAddress.isEmpty()) && !piaAddress.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAAddress(this, piaAddress);
                }

                // PIA Preference - PIA Phone
                piaPhone = piaProfile.getString("pia_phone");
                if ((piaPhone != null) && !(piaPhone.isEmpty()) && !piaPhone.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAPhone(this, piaPhone);
                }

                // PIA Preference - PIA Email
                piaEmail = piaProfile.getString("pia_email");
                if ((piaEmail != null) && !(piaEmail.isEmpty()) && !piaEmail.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAEmail(this, piaEmail);
                }

                // PIA Preference - PIA Scheme
                schemeId = piaProfile.getString("scheme_id");
                if ((schemeId != null) && !(schemeId.isEmpty()) && !schemeId.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveSchemeId(this, schemeId);
                }

                // PIA Preference - PIA Pic
//                piaProfilePic = piaProfile.getString("profile_pic");
//                if ((piaProfilePic != null) && !(piaProfilePic.isEmpty()) && !piaProfilePic.equalsIgnoreCase("null")) {
//                    PreferenceStorage.saveUserPicture(this, piaProfilePic);
//                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void saveTNSRLMProfile(JSONObject TNSRLMProfile) {

        Log.d(TAG, "TNSRLMProfile dictionary" + TNSRLMProfile.toString());

        String piaCount = "";
        String mobCount = "";
        String centerCount = "";
        String studentCount = "";

        try {

            if (TNSRLMProfile != null) {

//                // PIA Preference - PIA profile Id
                piaCount = TNSRLMProfile.getString("pia_count");
                if ((piaCount != null) && !(piaCount.isEmpty()) && !piaCount.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIACount(this, piaCount);
                }

                // PIA Preference - PIA profile Id
                mobCount = TNSRLMProfile.getString("mobilizer_count");
                if ((mobCount != null) && !(mobCount.isEmpty()) && !mobCount.equalsIgnoreCase("null")) {
                    PreferenceStorage.savemobCount(this, mobCount);
                }

                // PIA Preference - PIA PRN Number
                centerCount = TNSRLMProfile.getString("center_count");
                if ((centerCount != null) && !(centerCount.isEmpty()) && !centerCount.equalsIgnoreCase("null")) {
                    PreferenceStorage.savecenterCount(this, centerCount);
                }

                // PIA Preference - PIA Name
                studentCount = TNSRLMProfile.getString("student_count");
                if ((studentCount != null) && !(studentCount.isEmpty()) && !studentCount.equalsIgnoreCase("null")) {
                    PreferenceStorage.savestudentCount(this, studentCount);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
