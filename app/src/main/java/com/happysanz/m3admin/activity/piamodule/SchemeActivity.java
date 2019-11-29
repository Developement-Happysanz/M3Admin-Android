package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class SchemeActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "TaskFragment";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private TextView schemeTitle, sDetail;

    String schemeName = "";
    String schemeInfo = "";
    String schemeVideo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        schemeTitle = findViewById(R.id.scheme_title);
        sDetail = findViewById(R.id.scheme_detail);
        loadScheme();
    }

    private void loadScheme() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.USER_LOGIN_API, PreferenceStorage.getUserId(this));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.SCHEME_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View view) {

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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            try {
                JSONArray schemeDetails = response.getJSONArray("schemeDetails");

                schemeName = schemeDetails.getJSONObject(0).getString("scheme_name");
                schemeInfo = schemeDetails.getJSONObject(0).getString("scheme_info");
                schemeVideo = schemeDetails.getJSONObject(0).getString("scheme_video");

                schemeTitle.setText(schemeName);
                sDetail.setText(schemeInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

}
