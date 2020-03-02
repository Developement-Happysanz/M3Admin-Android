package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.SchemePhotoListAdapter;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.bean.pia.SchemePhoto;
import com.happysanz.m3admin.bean.pia.SchemePhotoList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.util.Log.d;

public class SchemePhotoGallery extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "ImageScalling";
    private static final String TAG = SchemePhotoGallery.class.getName();
    Centers centers;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView addPhoto;
    //    private CenterPhotosData centerPhotosData;
    protected ListView loadMoreListView;
    protected SchemePhotoListAdapter centerPhotosListAdapter;
    protected ArrayList<SchemePhoto> centerPhotosDataArrayList;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    protected boolean isLoadingForFirstTime = true;
    String res;
    ImageView imageview;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        centerPhotosDataArrayList = new ArrayList<>();
        imageview = findViewById(R.id.sample);

//        taskData = (TaskData) getIntent().getSerializableExtra("eventObj");
        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.add_photo).setVisibility(View.GONE);

        if (PreferenceStorage.getTnsrlmCheck(this)){
            findViewById(R.id.add_photo).setVisibility(View.GONE);
        }

        loadMoreListView = (ListView) findViewById(R.id.photo_list);

        viewCenterPhotos();

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
    

    protected void updateListAdapter(ArrayList<SchemePhoto> centerPhotosDataArrayList) {
        this.centerPhotosDataArrayList.addAll(centerPhotosDataArrayList);
//        if (taskDataListAdapter == null) {
        centerPhotosListAdapter = new SchemePhotoListAdapter(SchemePhotoGallery.this, this.centerPhotosDataArrayList);
        loadMoreListView.setAdapter(centerPhotosListAdapter);
//        } else {
        centerPhotosListAdapter.notifyDataSetChanged();
//        }
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
//                JSONObject schemeData = response.getJSONObject("scheme_details");
                JSONObject schemeDetails = response.getJSONObject("scheme_photo");
                if (schemeDetails.getString("status").equalsIgnoreCase("success")) {
                    Gson gson = new Gson();
                    SchemePhotoList taskPictureList = gson.fromJson(schemeDetails.toString(), SchemePhotoList.class);
                    if (taskPictureList.getCenterPhotosData() != null && taskPictureList.getCenterPhotosData().size() > 0) {
                        totalCount = taskPictureList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(taskPictureList.getCenterPhotosData());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(SchemePhotoGallery.this, error);
    }

    private void viewCenterPhotos() {
        res = "photo";
        JSONObject jsonObject = new JSONObject();
        String userId;
        userId = PreferenceStorage.getUserId(getApplicationContext());

        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
            jsonObject.put(M3AdminConstants.SCHEME_ID, PreferenceStorage.getSchemeId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.SCHEME_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}