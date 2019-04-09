package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFormatException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.ProjectPeriodListAdapter;
import com.happysanz.m3admin.adapter.TaskDataListAdapter;
import com.happysanz.m3admin.bean.pia.CenterPhotosList;
import com.happysanz.m3admin.bean.pia.ProjectPeriod;
import com.happysanz.m3admin.bean.pia.ProjectPeriodList;
import com.happysanz.m3admin.bean.pia.TaskData;
import com.happysanz.m3admin.bean.pia.TaskDataList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.util.Log.d;

public class ProjectPeriodActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "TaskFragment";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    ProjectPeriodListAdapter taskDataListAdapter;
    ArrayList<ProjectPeriod> taskDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    ImageView addNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.listView_task);
        loadMoreListView.setOnItemClickListener(this);
        taskDataArrayList = new ArrayList<>();
        addNewTask = findViewById(R.id.add_task);
        addNewTask.setOnClickListener(this);

        loadTask();
    }

    private void loadTask() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("1")) {
            id = PreferenceStorage.getPIAProfileId(this);
        } else {
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.PROJECT_PERIOD_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == addNewTask) {
            Intent intent = new Intent(getApplicationContext(), AddProjectPeriodActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (taskDataArrayList != null) {
                taskDataArrayList.clear();
                taskDataListAdapter = new ProjectPeriodListAdapter(this, this.taskDataArrayList);
                loadMoreListView.setAdapter(taskDataListAdapter);
                loadTask();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        ProjectPeriod taskData = null;
        if ((taskDataListAdapter != null) && (taskDataListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = taskDataListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            taskData = taskDataArrayList.get(actualindex);
        } else {
            taskData = taskDataArrayList.get(position);
        }
//        Intent intent = new Intent(this, UpdateTaskActivity.class);
//        intent.putExtra("taskObj", taskData);
//        startActivity(intent);
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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    progressDialogHelper.hideProgressDialog();

                    Gson gson = new Gson();
                    ProjectPeriodList taskDataList = gson.fromJson(response.toString(), ProjectPeriodList.class);
                    if (taskDataList.getTaskData() != null && taskDataList.getTaskData().size() > 0) {
                        totalCount = taskDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(taskDataList.getTaskData());
                    }
                }
            });
        }
    }

    @Override
    public void onError(final String error) {

    }

    protected void updateListAdapter(ArrayList<ProjectPeriod> taskDataArrayList) {
        this.taskDataArrayList.addAll(taskDataArrayList);
//        if (taskDataListAdapter == null) {
        taskDataListAdapter = new ProjectPeriodListAdapter(this, this.taskDataArrayList);
        loadMoreListView.setAdapter(taskDataListAdapter);
//        } else {
        taskDataListAdapter.notifyDataSetChanged();
//        }
    }
}