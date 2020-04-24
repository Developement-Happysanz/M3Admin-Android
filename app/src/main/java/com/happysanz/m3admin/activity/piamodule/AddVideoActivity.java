package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

public class AddVideoActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener {
    private static final String TAG = AddVideoActivity.class.getName();

    EditText title, link;
    Button upload;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Centers centers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        centers = (Centers) getIntent().getSerializableExtra("cent");

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        title = findViewById(R.id.video_title);
        link = findViewById(R.id.video_link);
        upload = findViewById(R.id.upload_link);
        upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == upload) {
            uploadLink();
        }
    }

    private void uploadLink() {
        if (validateFields()) {
            JSONObject jsonObject = new JSONObject();
            String userId;
            if (PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")) {
                userId = PreferenceStorage.getPIAProfileId(getApplicationContext());
            } else {
                userId = PreferenceStorage.getUserId(getApplicationContext());
            }
            try {
                jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
                jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, PreferenceStorage.getCenterId(this));
                jsonObject.put(M3AdminConstants.PARAMS_VIDEO_TITLE, title.getText().toString());
                jsonObject.put(M3AdminConstants.PARAMS_VIDEO_LINK, link.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = M3AdminConstants.BUILD_URL + M3AdminConstants.ADD_VIDEO;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }

    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.title.getText().toString().trim())) {
            title.setError("Give your video a title");
            requestFocus(title);
            return false;
        }
        if (!M3Validator.checkNullString(this.link.getText().toString().trim())) {
            link.setError("Paste the URL of the video here");
            requestFocus(link);
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
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            Toast.makeText(this, "Video uploaded for your training center.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, VideoGalleryActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(M3AdminConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
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
    public void onError(String error) {

    }
}
