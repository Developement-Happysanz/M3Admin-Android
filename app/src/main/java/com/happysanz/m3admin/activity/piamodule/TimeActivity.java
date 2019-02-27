package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.CenterPhotosList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class TimeActivity extends AppCompatActivity implements IServiceListener , DialogClickListener {
    private static final String TAG = TimeActivity.class.getName();

    DatePicker dateStart, dateEnd;
    String start = "";
    String end = "";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        dateStart = findViewById(R.id.start_time);
        dateEnd = findViewById(R.id.end_time);
        upload = findViewById(R.id.upload_time);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day_start = dateStart.getDayOfMonth();
                int month_start = dateStart.getMonth() + 1;
                int year_start = dateStart.getYear();
                start = ""+year_start+"-"+month_start+"-"+day_start;
                int day_end = dateEnd.getDayOfMonth();
                int month_end = dateEnd.getMonth() + 1;
                int year_end = dateEnd.getYear();
                end = ""+year_end+"-"+month_end+"-"+day_end;
                uploadTime();
            }
        });

    }

    private void uploadTime() {
        JSONObject jsonObject = new JSONObject();
        String userId;
        if(PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")){
            userId = PreferenceStorage.getPIAProfileId(getApplicationContext());
        } else {
            userId = PreferenceStorage.getUserId(getApplicationContext());
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
            jsonObject.put(M3AdminConstants.PARAMS_START_DATE, start);
            jsonObject.put(M3AdminConstants.PARAMS_END_DATE, end);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.PROJECT_PERIOD;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
            AlertDialogHelper.showSimpleAlertDialog(this, "Project period update");

            finish();
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}