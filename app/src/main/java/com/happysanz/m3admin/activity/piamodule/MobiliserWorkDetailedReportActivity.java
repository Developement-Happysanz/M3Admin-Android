package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.WorkDetailsListAdapter;
import com.happysanz.m3admin.bean.pia.WorkDetails;
import com.happysanz.m3admin.bean.pia.WorkDetailsList;
import com.happysanz.m3admin.bean.pia.WorkMonth;
import com.happysanz.m3admin.fragments.DynamicWorkTypeFragment;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class MobiliserWorkDetailedReportActivity extends AppCompatActivity implements IServiceListener, AdapterView.OnItemClickListener, DialogClickListener {

    private View view;
    private static ArrayList<WorkMonth> WorkMonthArrayList;
    private ArrayList<WorkDetails> serviceArrayList;
    private int val;
    private WorkDetailsListAdapter serviceListAdapter;
    private static final String TAG = DynamicWorkTypeFragment.class.getName();
    private String subCatId = "";
    private String YEARId = "";
    private ServiceHelper serviceHelper;
    private int totalCount = 0, checkrun = 0;
    private  boolean isLoadingForFirstTime = true;
    private ProgressDialogHelper progressDialogHelper;
    private ListView loadMoreListView;
    private Boolean msgErr = false;
    private Boolean noService = false;
    private String res = "";
    private String id = "";
    private TextView monthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiliser_detailed_work_report);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadMoreListView = findViewById(R.id.detailed_report_list);
        loadMoreListView.setOnItemClickListener(this);

        String name = "";
        subCatId = getIntent().getStringExtra("month");
        YEARId = getIntent().getStringExtra("year");
        name = getIntent().getStringExtra("month_name");

        monthYear = findViewById(R.id.month_year);
        monthYear.setText(name + " - " + YEARId);

        loadDayMonth();
    }

    private void loadDayMonth() {
        res = "day";
        JSONObject jsonObject = new JSONObject();
//        String id = "";
        String mobid = "";
        id = PreferenceStorage.getUserId(this);
        mobid = PreferenceStorage.getMobId(this);
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, mobid);
            jsonObject.put(M3AdminConstants.PARAMS_MONTH_ID, subCatId);
            jsonObject.put(M3AdminConstants.PARAMS_YEAR_ID, YEARId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_WORK;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<WorkDetails> serviceArrayList) {
        if (msgErr) {
//            AlertDialogHelper.showSimpleAlertDialog(this, "No Services found");
        } else {
            msgErr = false;
            this.serviceArrayList.clear();
            this.serviceArrayList.addAll(serviceArrayList);
            if (serviceListAdapter == null) {
                serviceListAdapter = new WorkDetailsListAdapter(this, this.serviceArrayList);
                loadMoreListView.setAdapter(serviceListAdapter);
            } else {
                serviceListAdapter.notifyDataSetChanged();
            }
        }

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
                        if (msg.equalsIgnoreCase("Services not found")){
                            msgErr = true;
                            noService = true;
                        }
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
                if (res.equalsIgnoreCase("day")) {
                    JSONArray getData = response.getJSONArray("result");
//                loadMembersList(getData.length());
                    Gson gson = new Gson();
                    WorkDetailsList serviceList = gson.fromJson(response.toString(), WorkDetailsList.class);
                    serviceArrayList = new ArrayList<>();
                    if (serviceList.getCategoryArrayList() != null && serviceList.getCategoryArrayList().size() > 0) {
                        totalCount = serviceList.getCount();
//                    this.categoryArrayList.addAll(WorkMonthList.getCategoryArrayList());
                        isLoadingForFirstTime = false;
                        updateListAdapter(serviceList.getCategoryArrayList());
                    } else {
                        if (serviceArrayList != null) {
                            serviceArrayList.clear();
                            updateListAdapter(serviceList.getCategoryArrayList());
//                            serviceListAdapter = new MainServiceListAdapter(this, this.serviceArrayList);
//                            loadMoreListView.setAdapter(serviceListAdapter);
                        }
                    }
                } else if (res.equalsIgnoreCase("clear")) {
//                    PreferenceStorage.saveServiceCount(this, "");
//                    PreferenceStorage.saveRate(this, "");
//                    PreferenceStorage.savePurchaseStatus(this, false);
//                    loadCat();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {

    }
}
