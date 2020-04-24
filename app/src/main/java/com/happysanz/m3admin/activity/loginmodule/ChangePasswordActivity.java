package com.happysanz.m3admin.activity.loginmodule;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ChangePasswordActivity  extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = ChangePasswordActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText oldPass, newPass, confirmPass;
    private Button btSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        oldPass = findViewById(R.id.old_password);
        newPass = findViewById(R.id.new_password);
        confirmPass = findViewById(R.id.confirm_password);
        btSubmit = findViewById(R.id.btnSubmit);
        btSubmit.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public void onClick(View v) {
            if (v == btSubmit) {
                if (validateFields()) {
                    String oldPa = oldPass.getText().toString();
                    String newPa = newPass.getText().toString();
                    String id = PreferenceStorage.getUserId(this);

                    if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
                            jsonObject.put(M3AdminConstants.PARAMS_OLD_PASSWORD, oldPa);
                            jsonObject.put(M3AdminConstants.PARAMS_NEW_PASSWORD, newPa);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.USER_CHANGE_PASSWORD;
                        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                    } else {
                        AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
                    }
                }
            }
    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.oldPass.getText().toString().trim())) {
            oldPass.setError("Enter current password.");
            requestFocus(oldPass);
            return false;
        } else if (!M3Validator.checkNullString(this.newPass.getText().toString().trim())) {
            newPass.setError("Enter New password.");
            requestFocus(newPass);
            return false;
        } else if (!M3Validator.checkNullString(this.confirmPass.getText().toString().trim())) {
            confirmPass.setError("Please confirm the new password by re-tying it.");
            requestFocus(confirmPass);
            return false;
        } else if (!(this.confirmPass.getText().toString().trim().equalsIgnoreCase(this.newPass.getText().toString().trim()))) {
            confirmPass.setError("This doesn't match with your new password.");
            requestFocus(confirmPass);
            return false;
        } else if (!M3Validator.checkStringMinLength(6, this.newPass.getText().toString().trim())) {
            newPass.setError(getString(R.string.err_min_pass_length));
            requestFocus(newPass);
            return false;
        } else if (!M3Validator.checkStringMinLength(6, this.confirmPass.getText().toString().trim())) {
            confirmPass.setError(getString(R.string.err_min_pass_length));
            requestFocus(confirmPass);
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
            Toast.makeText(ChangePasswordActivity.this, "Your password has been reset.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onError(String error) {

    }
}