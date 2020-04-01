package com.happysanz.m3admin.activity.piamodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.MobilizerListAdapter;
import com.happysanz.m3admin.bean.pia.StoreTimings;
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

public class MobiliserWorkReportActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = MobiliserWorkReportActivity.class.getName();

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
    private String checkS = "";
    private String monthId = "";
    //    TabLayout tab;
//    ViewPager viewPager;
    private String storeClassId = "";

    private EditText year, month;
    TextView txtFieldCount, txtOfficeCount, txtDistanceCount, txtLeaveCount, txtHolsCount, monthYear;
    Button viewDetailed;

    ArrayAdapter<StoreTimings> mTimingsAdapter = null;
    ArrayList<StoreTimings> timingsList;

    Boolean monthyearLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiliser_work_report);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        year = findViewById(R.id.year_select);
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYears();
            }
        });
        month = findViewById(R.id.month_select);
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimings();
            }
        });
        monthYear = findViewById(R.id.month_year);
        txtFieldCount = findViewById(R.id.field_count);
        txtOfficeCount = findViewById(R.id.office_count);
        txtDistanceCount = findViewById(R.id.distance_count);
        txtLeaveCount = findViewById(R.id.leave_count);
        txtHolsCount = findViewById(R.id.hols_count);

        txtFieldCount.setClickable(false);
        txtFieldCount.setFocusable(false);

        txtOfficeCount.setClickable(false);
        txtOfficeCount.setFocusable(false);

        txtDistanceCount.setClickable(false);
        txtDistanceCount.setFocusable(false);

        txtLeaveCount.setClickable(false);
        txtLeaveCount.setFocusable(false);

        txtHolsCount.setClickable(false);
        txtHolsCount.setFocusable(false);

        viewDetailed = findViewById(R.id.detailed_report);
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

    private void showYears() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Year");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Year");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(dataAdapter3
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = yearList.get(which).toString();
                        year.setText(strName);
                        storeClassId = strName;
                        loadMonth();
                    }
                });
        builderSingle.show();
    }

    private void showTimings() {
        Log.d(TAG, "Show month lists");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select month");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mTimingsAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreTimings timings = timingsList.get(which);
                        month.setText(timings.getTimingsName());
                        monthId = timings.getTimingsId();
                        loadDayMonth();
                    }
                });
        builderSingle.show();
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
        id = PreferenceStorage.getUserId(getApplicationContext());
        mobid = PreferenceStorage.getMobId(getApplicationContext());
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, mobid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_YEAR_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadMonth() {
        checkS = "month";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String mobid = "";
        id = PreferenceStorage.getUserId(getApplicationContext());
        mobid = PreferenceStorage.getMobId(getApplicationContext());
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, mobid);
            jsonObject.put(M3AdminConstants.PARAMS_YEAR_ID, storeClassId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_MONTH_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadDayMonth() {
        checkS = "day";
        viewDetailed.setOnClickListener(this);
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String mobid = "";
        id = PreferenceStorage.getUserId(getApplicationContext());
        mobid = PreferenceStorage.getMobId(getApplicationContext());
        PreferenceStorage.saveMobId(this, mobid);
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, mobid);
            jsonObject.put(M3AdminConstants.PARAMS_MONTH_ID, monthId);
            jsonObject.put(M3AdminConstants.PARAMS_YEAR_ID, storeClassId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_WORK_MONTH_REPORT;
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
                if (checkS.equalsIgnoreCase("year")) {
                    JSONArray getData = response.getJSONArray("result");
                    String year = "";
                    yearList.clear();
                    for (int i = 0; i < getData.length(); i++) {
                        year = getData.getJSONObject(i).getString("year_id");
                        yearList.add(year);
                    }
                    dataAdapter3 = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, yearList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(yearList.get(position).toString());

                            // ... Fill in other views ...
                            return view;
                        }
                    };
                } else if (checkS.equalsIgnoreCase("month")) {
                    JSONArray getData = response.getJSONArray("result");
                    int getLength = getData.length();
                    String timingsId = "";
                    String timingsName = "";
                    timingsList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {
                        timingsId = getData.getJSONObject(i).getString("month_id");
                        timingsName = getData.getJSONObject(i).getString("month_name");
                        timingsList.add(new StoreTimings(timingsId, timingsName));
                    }

                    //fill data in spinner
                    mTimingsAdapter = new ArrayAdapter<StoreTimings>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, timingsList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(timingsList.get(position).getTimingsName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };

                } else {
                    JSONArray getData = response.getJSONArray("result");
                    int getLength = getData.length();
                    for (int i = 0; i < getLength; i++) {
                        JSONObject dat = getData.getJSONObject(i);
                        if (dat.getString("work_type").equalsIgnoreCase("Office work")) {
                            txtOfficeCount.setText("" + dat.getString("count") + " Days");
                        } else if (dat.getString("work_type").equalsIgnoreCase("Field work")) {
                            txtFieldCount.setText("" + dat.getString("count") + " Days");
                        } else if (dat.getString("work_type").equalsIgnoreCase("Hoilday")) {
                            txtHolsCount.setText("" + dat.getString("count") + " Days");
                        } else if (dat.getString("work_type").equalsIgnoreCase("Leave")) {
                            txtLeaveCount.setText("" + dat.getString("count") + " Days");
                        }
                    }
                    txtDistanceCount.setText("" + response.getString("km_result") + " Kms");

                }

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
        if (v == viewDetailed) {
            Intent ine = new Intent(this, MobiliserWorkDetailedReportActivity.class);
            ine.putExtra("month", monthId);
            ine.putExtra("year", storeClassId);
            ine.putExtra("month_name", month.getText().toString());
            startActivity(ine);
        }
    }
}