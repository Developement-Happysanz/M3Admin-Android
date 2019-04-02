package com.happysanz.m3admin.activity.piamodule;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.TaskDataListAdapter;
import com.happysanz.m3admin.bean.pia.StoreMobilizer;
import com.happysanz.m3admin.bean.pia.TaskData;
import com.happysanz.m3admin.bean.pia.TaskDataList;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class TrackingUserSelectionActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG =  TrackingUserSelectionActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    TaskDataListAdapter taskDataListAdapter;
    ArrayList<TaskData> taskDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Spinner spnMobilizer;
    String storeMobilizerId = "", res;
    Button live, distance;
    TextView spnDate;
    private SimpleDateFormat mDateFormatter;
    private DatePickerDialog mDatePicker;
    String d;
    List<LatLng> list = new ArrayList<>();
    LatLng livLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_user_selection);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        live = findViewById(R.id.live_track);
        live.setOnClickListener(this);
        distance = findViewById(R.id.distance_track);
        distance.setOnClickListener(this);
        spnDate = findViewById(R.id.spn_date);
        spnDate.setOnClickListener(this);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        spnMobilizer = findViewById(R.id.spn_user_mob);
        spnMobilizer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StoreMobilizer classList = (StoreMobilizer) parent.getSelectedItem();

                storeMobilizerId = classList.getMobilizerId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadMob();
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    }

    private void getLiveTrackData() {
        res = "LIvdata";
        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = Calendar.getInstance().getTime();
        String formattedDate = tf.format(date);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.PARAMS_MOB_ID, storeMobilizerId);
            jsonObject.put(M3AdminConstants.PARAMS_TRACK_DATE, formattedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.LIVE_TRACKING;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getTrackData() {
        res = "data";
        JSONObject jsonObject = new JSONObject();
        String serverFormatDate = "";

        if (spnDate.getText().toString() != null && spnDate.getText().toString() != "") {

            String date = spnDate.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            serverFormatDate = formatter.format(testDate);
            System.out.println(".....Date..." + serverFormatDate);
        }
        try {
            jsonObject.put(M3AdminConstants.PARAMS_MOB_ID, storeMobilizerId);
            jsonObject.put(M3AdminConstants.PARAMS_TRACK_DATE, serverFormatDate);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.DISTANCE_TRACKING;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        spnDate.setText(mDateFormatter.format(newDate.getTime()));
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = spnDate.getText().toString();
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
            mDatePicker.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == live) {
            if ((!storeMobilizerId.isEmpty())) {
                getLiveTrackData();

            } else {
                AlertDialogHelper.showSimpleAlertDialog(this, "User cannot be empty");
            }

        }
        if (v == distance) {
            if ((!storeMobilizerId.isEmpty()) && (!spnDate.getText().toString().isEmpty())) {
                getTrackData();
            } else {
                AlertDialogHelper.showSimpleAlertDialog(this, "User and Date cannot be empty");
            }
        }
        if (v == spnDate) {
            showBirthdayDate();
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
    public void onResponse(final JSONObject response) {
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
                    ArrayList<StoreMobilizer> classesList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        classId = getData.getJSONObject(i).getString("user_id");
                        className = getData.getJSONObject(i).getString("name");

                        classesList.add(new StoreMobilizer(classId, className));
                    }

                    //fill data in spinner
                    ArrayAdapter<StoreMobilizer> adapter = new ArrayAdapter<StoreMobilizer>(getApplicationContext(), R.layout.spinner_item_ns, classesList);
                    spnMobilizer.setAdapter(adapter);
                } else if (res.equalsIgnoreCase("data")) {
                    String lat, lng;
                    Double latitude, longitude;
                    JSONArray trackingDetails = response.getJSONArray("trackingDetails");
                    JSONArray distance = response.getJSONArray("Distance");
                    d = distance.getJSONObject(0).getString("km");
                    DecimalFormat df = new DecimalFormat("#.##");
                    String d1 = df.format(Double.valueOf(d));
//                    dis.setText("Distance Travelled : " + d1 + " Km");
                    for (int a = 0 ; a < trackingDetails.length(); a++) {
                        lat = trackingDetails.getJSONObject(a).getString("Latitude");
                        lng = trackingDetails.getJSONObject(a).getString("Longitude");
                        latitude = Double.valueOf(lat);
                        longitude = Double.valueOf(lng);
                        LatLng location = new LatLng(latitude, longitude);
                        list.add(location);
                    }
                    Intent intent = new Intent(this, TrackingDistanceActivity.class);
//                    intent.putParcelableArrayListExtra("latlng", list);
                    intent.putParcelableArrayListExtra("latlng", (ArrayList<? extends Parcelable>) list);
                    intent.putExtra("dist",  d1);
                    startActivity(intent);
                } else if (res.equalsIgnoreCase("LIvdata")) {
                    String lat, lng;
                    Double latitude, longitude;
                    JSONArray trackingDetails = response.getJSONArray("trackingDetails");
                    lat = trackingDetails.getJSONObject(0).getString("Latitude");
                    lng = trackingDetails.getJSONObject(0).getString("Longitude");
                    latitude = Double.valueOf(lat);
                    longitude = Double.valueOf(lng);
                    livLoc = new LatLng(latitude, longitude);
                    Intent intent = new Intent(this, TrackingLiveActivity.class);
                    intent.putExtra("dist",  livLoc);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final String error) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}