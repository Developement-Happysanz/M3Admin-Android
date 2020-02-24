package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.TaskPictureListAdapter;
import com.happysanz.m3admin.bean.pia.TaskData;
import com.happysanz.m3admin.bean.pia.TaskPicture;
import com.happysanz.m3admin.bean.pia.TaskPictureList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class ViewTaskPhotosActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = UpdateTaskActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private TaskData taskData;
    private ImageView ivBack;
    protected ListView loadMoreListView;
    protected TaskPictureListAdapter taskPictureListAdapter;
    protected ArrayList<TaskPicture> taskPictureArrayList;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    protected boolean isLoadingForFirstTime = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_photos);

        taskData = (TaskData) getIntent().getSerializableExtra("eventObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);

        loadMoreListView = (ListView) findViewById(R.id.listView_events);
        taskPictureArrayList = new ArrayList<>();

        viewTaskPhotos();
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
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
//            try {


            Gson gson = new Gson();
            TaskPictureList taskPictureList = gson.fromJson(response.toString(), TaskPictureList.class);
            if (taskPictureList.getTaskPicture() != null && taskPictureList.getTaskPicture().size() > 0) {
                totalCount = taskPictureList.getCount();
                isLoadingForFirstTime = false;
                updateListAdapter(taskPictureList.getTaskPicture());
            }


//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onError(final String error) {

        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(ViewTaskPhotosActivity.this, error);

    }

    private void viewTaskPhotos() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.PARAMS_TASK_ID, taskData.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.TASK_IMAGE_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<TaskPicture> taskPictureArrayList) {
        this.taskPictureArrayList.addAll(taskPictureArrayList);
//        if (taskDataListAdapter == null) {
        taskPictureListAdapter = new TaskPictureListAdapter(ViewTaskPhotosActivity.this, this.taskPictureArrayList);
        loadMoreListView.setAdapter(taskPictureListAdapter);
//        } else {
        taskPictureListAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}