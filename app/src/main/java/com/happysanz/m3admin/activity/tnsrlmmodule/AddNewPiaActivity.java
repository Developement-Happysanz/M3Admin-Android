package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class AddNewPiaActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = PiaActivity.class.getName();

    Button addNewPia;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pia);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        addNewPia = (Button) findViewById(R.id.save_pia);
        addNewPia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(M3AdminConstants.PARAMS_USER_ID, "1");
                    jsonObject.put(M3AdminConstants.PARAMS_UNIQUE_NUMBER, "1122334455667");
                    jsonObject.put(M3AdminConstants.PARAMS_NAME, "Test 1");
                    jsonObject.put(M3AdminConstants.PARAMS_ADDRESS, "Sample Address 1122331");
                    jsonObject.put(M3AdminConstants.PARAMS_EMAIL, "random@rmail.com");
                    jsonObject.put(M3AdminConstants.PARAMS_PHONE, "1234567890");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                String url = M3AdminConstants.BUILD_URL + M3AdminConstants.CREATE_PIA;
                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
            }
        });

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
//                JSONObject piaProfile = response.getJSONObject("piaProfile");
////                JSONObject dashBoardData = response.getJSONObject("dashboardData");
//
////                saveUserData(userData);
////                savePIAProfile(piaProfile);
//
//
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}