package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class CenterDetailActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = CenterDetailActivity.class.getName();
    Centers centers;
    ImageView centerBanner, editDetails;
    TextView centerTitle, CenterInfo;
    Button galleryLayout, videoLayout;
    String res;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_detail);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        centers = (Centers) getIntent().getSerializableExtra("cent");
        centerBanner = findViewById(R.id.center_logo12331);
        editDetails = findViewById(R.id.ic_edit_details);
        editDetails.setOnClickListener(this);
        if (PreferenceStorage.getTnsrlmCheck(this)) {
            editDetails.setVisibility(View.GONE);
        }

        CenterInfo = findViewById(R.id.center_detail);
        galleryLayout = findViewById(R.id.gallery_layout);
        galleryLayout.setOnClickListener(this);
        videoLayout = findViewById(R.id.video_gallery_layout);
        videoLayout.setOnClickListener(this);
        centerTitle = findViewById(R.id.center_title);
//        centerTitle.setText(centers.getCenter_name());
//        CenterInfo.setText(centers.getcenter_info());
//        String url = "";
//        url = M3AdminConstants.BUILD_URL + M3AdminConstants.ASSETS_URL_LOGO + centers.getcenter_banner();
//        if (((url != null) && !(url.isEmpty()))) {
//            Picasso.get().load(url).into(centerBanner);
//        } else {
//            centerBanner.setImageResource(R.drawable.ic_profile);
//        }
        loadCentersDetails();
    }

    private void loadCentersDetails() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("1")) {
            id = PreferenceStorage.getPIAProfileId(this);
        } else {
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, centers.getid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_CENTER_DETAILS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }


    @Override
    public void onClick(View view) {
        if (view == galleryLayout) {
            Intent intent = new Intent(this, PhotoGalleryActivity.class);
            intent.putExtra("cent", centers);
            startActivity(intent);
        } else if (view == videoLayout) {
            Intent intent = new Intent(this, VideoGalleryActivity.class);
            PreferenceStorage.saveCenterId(this, centers.getid());
            startActivity(intent);
        } else if (view == editDetails) {
            if (!PreferenceStorage.getTnsrlmCheck(this)) {
                Intent intent = new Intent(this, AddCenterDetailActivity.class);
                PreferenceStorage.saveCenterId(this, centers.getid());
                intent.putExtra("page", "update");
                intent.putExtra("center", centers);
                startActivity(intent);
                finish();
            }
        }
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
                String url = response.getJSONArray("cenerDetails").getJSONObject(0).getString("center_logo");
                String centerName = response.getJSONArray("cenerDetails").getJSONObject(0).getString("center_name");
                String centerInfo = response.getJSONArray("cenerDetails").getJSONObject(0).getString("center_info");
                String centerAddress = response.getJSONArray("cenerDetails").getJSONObject(0).getString("center_address");

                if (((url != null) && !(url.isEmpty()))) {
                    Picasso.get().load(url).into(centerBanner);
                } else {
                    centerBanner.setImageResource(R.drawable.ic_profile);
                }
                centerTitle.setText(centerName);
                CenterInfo.setText(centerInfo);
                centerTitle.setText(centerName);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onError(final String error) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}