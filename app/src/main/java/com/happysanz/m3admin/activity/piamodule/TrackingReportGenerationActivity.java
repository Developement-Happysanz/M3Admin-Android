package com.happysanz.m3admin.activity.piamodule;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.RecordListAdapter;
import com.happysanz.m3admin.bean.pia.MobilizerRecord;
import com.happysanz.m3admin.bean.pia.MobilizerRecordList;
import com.happysanz.m3admin.bean.pia.StoreMobilizer;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.util.Log.d;

public class TrackingReportGenerationActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, DatePickerDialog.OnDateSetListener {


    private static final String TAG =  TrackingReportGenerationActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    TextView startDate, endDate;
    String storeMobilizerId = "", res;
    EditText spnMobilizer;
    private String click = "";
    ArrayList<StoreMobilizer> classesList;
    ArrayAdapter<StoreMobilizer> adapter;
    RecordListAdapter recordListAdapter;
    Button generate;

    private SimpleDateFormat mDateFormatter;
    private DatePickerDialog mDatePicker;
    ListView list;
    ArrayList<MobilizerRecord> tradeDataArrayList = new ArrayList<>();

    protected boolean isLoadingForFirstTime = true;
    int pageNumber = 0, totalCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generation);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        spnMobilizer = findViewById(R.id.spn_user_mob);
        spnMobilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrades();
            }
        });

        startDate = findViewById(R.id.start_date);
        startDate.setOnClickListener(this);
        endDate = findViewById(R.id.end_date);
        endDate.setOnClickListener(this);

        generate = findViewById(R.id.gen);
        generate.setOnClickListener(this);
        
        list = findViewById(R.id.list_dates);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadMob();
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = "";
        if (click.equalsIgnoreCase("start")) {
            currentdate = startDate.getText().toString();
        } else {
            currentdate = endDate.getText().toString();
        }
        Log.d(TAG, "current date is" + currentdate);
        int month = newCalendar.get(Calendar.MONTH);
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        if ((currentdate != null) && !(currentdate.isEmpty())) {
            //extract the date/month and year
            try {
                Date startDate = mDateFormatter.parse(currentdate);
                Calendar newDate = Calendar.getInstance();

                newDate.setTime(startDate);
                month = newDate.get(Calendar.MONTH);
                day = newDate.get(Calendar.DAY_OF_MONTH);
                year = newDate.get(Calendar.YEAR);
                Log.d(TAG, "month" + month + "day" + day + "year" + year);

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
                mDatePicker.show();
            }
        } else {
            Log.d(TAG, "show default date");

            mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
            DatePicker dP = mDatePicker.getDatePicker();
            Calendar cal = Calendar.getInstance();
            cal.set(2017,1,1);
            Date result = cal.getTime();
            dP.setMinDate(result.getTime());
            dP.setMaxDate(System.currentTimeMillis() - 1000);
            mDatePicker.show();
        }
    }

    private void showTrades() {
        Log.d(TAG, "Show trade lists");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select user");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StoreMobilizer classList = classesList.get(which);
                        spnMobilizer.setText(classList.getMobilizerName());
                        storeMobilizerId = classList.getMobilizerId();
//
//                        StoreTrade trade = tradeList.get(which);
//                        etCandidatesPreferredTrade.setText(trade.getTradeName());
//                        tradeId = trade.getTradeId();
//                        GetTradeTimings();
                    }
                });
        builderSingle.show();
    }

    private void loadMob() {
        res = "spnMobilizer";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_PIA_ID, PreferenceStorage.getUserId(getApplicationContext()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_MOBILIZER_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getReportData() {
        res = "genera";
        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = mDateFormatter.parse(startDate.getText().toString());
            String formattedStartDate = tf.format(date);
            Date date1 = mDateFormatter.parse(endDate.getText().toString());
            String formattedEndDate = tf.format(date1);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(M3AdminConstants.PARAMS_TRACK_START_DATE, formattedStartDate);
                jsonObject.put(M3AdminConstants.PARAMS_TRACK_END_DATE, formattedEndDate);
                jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, storeMobilizerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_REPORT;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        }catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, dayOfMonth);
        if (click.equalsIgnoreCase("start")) {
            startDate.setText(mDateFormatter.format(newDate.getTime()));
        } else {
            endDate.setText(mDateFormatter.format(newDate.getTime()));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == startDate) {
            click = "start";
            showBirthdayDate();
        }if (v == endDate) {
            click = "end";
            showBirthdayDate();
        }if (v == generate) {
            click = "gene";
            tradeDataArrayList.clear();
//            recordListAdapter.notifyDataSetChanged();
            getReportData();
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
            try {
                if (res.equalsIgnoreCase("spnMobilizer")) {
                    JSONArray getData = response.getJSONArray("userList");
                    int getLength = getData.length();
                    String subjectName = null;
                    Log.d(TAG, "userData dictionary" + getData.toString());

                    String classId = "";
                    String className = "";
                    classesList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        classId = getData.getJSONObject(i).getString("user_id");
                        className = getData.getJSONObject(i).getString("name");

                        classesList.add(new StoreMobilizer(classId, className));
                    }

                    //fill data in spinner
                    adapter = new ArrayAdapter<StoreMobilizer>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, classesList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(classesList.get(position).getMobilizerName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };
                }
                if (res.equalsIgnoreCase("genera")) {
                    Gson gson = new Gson();
                    MobilizerRecordList tradeDataList = gson.fromJson(response.toString(), MobilizerRecordList.class);
                    if (tradeDataList.getMobilizerRecord() != null && tradeDataList.getMobilizerRecord().size() > 0) {
                        totalCount = tradeDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(tradeDataList.getMobilizerRecord());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<MobilizerRecord> tradeDataArrayList) {
        this.tradeDataArrayList.addAll(tradeDataArrayList);
        if (recordListAdapter == null) {
            recordListAdapter = new RecordListAdapter(this, this.tradeDataArrayList);
            list.setAdapter(recordListAdapter);
        } else {
            recordListAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onError(String error) {

    }
}
