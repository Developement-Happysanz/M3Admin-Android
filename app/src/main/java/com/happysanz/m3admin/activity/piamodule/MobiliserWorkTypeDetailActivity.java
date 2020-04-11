package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.MobilizerListAdapter;
import com.happysanz.m3admin.adapter.WorkTypeTabAdapter;
import com.happysanz.m3admin.bean.pia.Mobilizer;
import com.happysanz.m3admin.bean.pia.WorkDetails;
import com.happysanz.m3admin.bean.pia.WorkMonth;
import com.happysanz.m3admin.bean.pia.WorkMonthList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class MobiliserWorkTypeDetailActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = MobiliserWorkTypeActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ArrayList<WorkMonth> mobilizerArrayList;
    private MobilizerListAdapter mobilizerListAdapter;
    private ListView loadMoreListView;
    private WorkDetails pia;
    private ArrayList yearList = new ArrayList();
    private ArrayAdapter<String> dataAdapter3;
    private Spinner yearSelect;
    private String checkS = "";
//    TabLayout tab;
//    ViewPager viewPager;
    private String storeClassId = "";

    EditText txtTitle, txtDetails, txtDate, txtStatus, txtType, txtMobComt;
    Button viewPhotos;
    ImageView EditTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiliser_work_type_detail);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtTitle = findViewById(R.id.task_title);
        txtDetails = findViewById(R.id.task_link);
        txtDate = findViewById(R.id.task_date);
        txtStatus = findViewById(R.id.status);
        txtType = findViewById(R.id.task_type);
        txtMobComt = findViewById(R.id.mobiliser_comment);

        txtTitle.setClickable(false);
        txtTitle.setFocusable(false);

        txtDetails.setClickable(false);
        txtDetails.setFocusable(false);

        txtDate.setClickable(false);
        txtDate.setFocusable(false);

        txtStatus.setClickable(false);
        txtStatus.setFocusable(false);

        txtType.setClickable(false);
        txtType.setFocusable(false);

        txtMobComt.setClickable(false);
        txtMobComt.setFocusable(false);

        viewPhotos = findViewById(R.id.btn_view_photos);
        viewPhotos.setOnClickListener(this);


        EditTask = findViewById(R.id.edit_task);
        EditTask.setOnClickListener(this);


        mobilizerArrayList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("serviceObj")) {
            pia = (WorkDetails) intent.getSerializableExtra("serviceObj");
//            PreferenceStorage.savePIAProfileId(this, pia.getUser_id());
            callGetClassTestService();
        } else {
            callGetClassTestService();
        }
        if (PreferenceStorage.getTnsrlmCheck(this)) {
            findViewById(R.id.add_user).setVisibility(View.GONE);
        }
    }

    public void callGetClassTestService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadYear();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void loadYear() {
        checkS = "year";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String mobid = "";
        String yearid = "";
        String monthid = "";
        String attendId = "";
        id = PreferenceStorage.getUserId(this);
        mobid = pia.getmobilizer_id();
        yearid = PreferenceStorage.getYearId(this);
        monthid = PreferenceStorage.getMonthId(this);
        attendId = pia.getid();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, mobid);
            jsonObject.put(M3AdminConstants.PARAMS_YEAR_ID, yearid);
            jsonObject.put(M3AdminConstants.PARAMS_MONTH_ID, monthid);
            jsonObject.put(M3AdminConstants.PARAMS_ATTEND_ID, attendId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_WORK_DETAIL;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONObject getData = response.getJSONObject("attedance_details");
                JSONObject getDataResult = getData.getJSONObject("result");
                txtTitle.setText(getDataResult.getString("title"));
                txtDate.setText(getDataResult.getString("attendance_date"));
                txtStatus.setText(getDataResult.getString("status"));
                txtDetails.setText(getDataResult.getString("comments"));
                txtType.setText(getDataResult.getString("work_type"));
                txtMobComt.setText(getDataResult.getString("mobilizer_comments"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {
        if (v == viewPhotos) {
//            Intent intent = new Intent(getApplicationContext(), ViewTaskPhotosActivity.class);
//            intent.putExtra("eventObj", taskData);
//            startActivity(intent);
        }else if (v == EditTask) {
            Intent intent = new Intent(this, UpdateTaskActivity.class);
            intent.putExtra("taskObj", pia);
            startActivity(intent);
            finish();
        }
    }
}