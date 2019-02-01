package com.happysanz.m3admin.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.happysanz.m3admin.R;
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

    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
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
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
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
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
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
        } else if (!M3Validator.checkStringMinLength(5, this.edtPassword.getText().toString().trim())) {
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
                JSONObject piaProfile = response.getJSONObject("piaProfile");
//                JSONObject dashBoardData = response.getJSONObject("dashboardData");

                saveUserData(userData);
                savePIAProfile(piaProfile);


                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

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
        String piEmail = "";
        String piaPhone = "";

        try {

            if (piaProfile != null) {

                // User Preference - Staff Id
                piaProfileId = piaProfile.getString("pia_profile_id");
                if ((piaProfileId != null) && !(piaProfileId.isEmpty()) && !piaProfileId.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAProfileId(this, piaProfileId);
                }

                /*// User Preference - Staff Id
                piaPRNNumber = piaProfile.getString("pia_unique_number");
                if ((piaPRNNumber != null) && !(piaPRNNumber.isEmpty()) && !piaPRNNumber.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePRNNumber(this, piaPRNNumber);
                }*/

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
