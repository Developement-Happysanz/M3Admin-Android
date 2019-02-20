package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

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

public class AddCenterDetailActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "TradeFragment";

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    EditText centerName, centerDetail, centerAddress;
    Button savedetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_center_detail);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        centerName = findViewById(R.id.center_name);
        centerDetail = findViewById(R.id.center_detail);
        centerAddress = findViewById(R.id.center_address);
        savedetails = findViewById(R.id.save_center);
        savedetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == savedetails) {
            sendCenterDetails();
        }
    }

    private void sendCenterDetails() {
        String cName = centerName.getText().toString();
        String cDetail = centerDetail.getText().toString();
        String cAddress = centerAddress.getText().toString();
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            if (validateFields()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
                    jsonObject.put(M3AdminConstants.PARAMS_CENTER_NAME, cName);
                    jsonObject.put(M3AdminConstants.PARAMS_CENTER_ADDRESS, cAddress);
                    jsonObject.put(M3AdminConstants.PARAMS_CENTER_INFO, cDetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                String url = M3AdminConstants.BUILD_URL + M3AdminConstants.CREATE_CENTER;
                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }

    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.centerName.getText().toString().trim())) {
            centerName.setError(getString(R.string.empty_entry));
            requestFocus(centerName);
            return false;
        } else if (!M3Validator.checkNullString(this.centerDetail.getText().toString().trim())) {
            centerDetail.setError(getString(R.string.empty_entry));
            requestFocus(centerDetail);
            return false;
        } else if (!M3Validator.checkNullString(this.centerAddress.getText().toString().trim())) {
            centerAddress.setError(getString(R.string.empty_entry));
            requestFocus(centerAddress);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
        if (validateSignInResponse(response)){
            AlertDialogHelper.showSimpleAlertDialog(this, "Center Created Successfully");

            finish();
        }
    }

    @Override
    public void onError(String error) {

    }
}