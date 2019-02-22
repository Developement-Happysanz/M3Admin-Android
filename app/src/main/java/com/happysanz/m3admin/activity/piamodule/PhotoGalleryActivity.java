package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.CenterPhotosListAdapter;
import com.happysanz.m3admin.bean.pia.CenterPhotosData;
import com.happysanz.m3admin.bean.pia.CenterPhotosList;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class PhotoGalleryActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = PhotoGalleryActivity.class.getName();
    Centers centers;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView ivBack;
    //    private CenterPhotosData centerPhotosData;
    protected ListView loadMoreListView;
    protected CenterPhotosListAdapter centerPhotosListAdapter;
    protected ArrayList<CenterPhotosData> centerPhotosDataArrayList;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    protected boolean isLoadingForFirstTime = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        centers = (Centers) getIntent().getSerializableExtra("cent");

//        taskData = (TaskData) getIntent().getSerializableExtra("eventObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadMoreListView = (ListView) findViewById(R.id.photo_list);
        loadMoreListView.setOnItemClickListener(this);

        centerPhotosDataArrayList = new ArrayList<>();

        viewCenterPhotos();
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
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

            Gson gson = new Gson();
            CenterPhotosList taskPictureList = gson.fromJson(response.toString(), CenterPhotosList.class);
            if (taskPictureList.getCenterPhotosData() != null && taskPictureList.getCenterPhotosData().size() > 0) {
                totalCount = taskPictureList.getCount();
                isLoadingForFirstTime = false;
                updateListAdapter(taskPictureList.getCenterPhotosData());
            }

        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(PhotoGalleryActivity.this, error);
    }

    private void viewCenterPhotos() {
        JSONObject jsonObject = new JSONObject();
        String userId;
        if(PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")){
            userId = PreferenceStorage.getPIAProfileId(getApplicationContext());
        } else {
            userId = PreferenceStorage.getUserId(getApplicationContext());
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
            jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, centers.getid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.VIEW_GALLERY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<CenterPhotosData> centerPhotosDataArrayList) {
        this.centerPhotosDataArrayList.addAll(centerPhotosDataArrayList);
//        if (taskDataListAdapter == null) {
        centerPhotosListAdapter = new CenterPhotosListAdapter(PhotoGalleryActivity.this, this.centerPhotosDataArrayList);
        loadMoreListView.setAdapter(centerPhotosListAdapter);
//        } else {
        centerPhotosListAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        CenterPhotosData taskData = null;
        if ((centerPhotosListAdapter != null) && (centerPhotosListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = centerPhotosListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            taskData = centerPhotosDataArrayList.get(actualindex);
        } else {
            taskData = centerPhotosDataArrayList.get(position);
        }
        Intent intent = new Intent(getApplicationContext(), ZoomImageActivity.class);
        intent.putExtra("eventObj", taskData);
        startActivity(intent);
    }
}