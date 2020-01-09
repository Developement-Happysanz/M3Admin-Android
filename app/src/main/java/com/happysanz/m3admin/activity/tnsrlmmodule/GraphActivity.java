package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.happysanz.m3admin.R;
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

import java.util.ArrayList;

import static android.util.Log.d;

public class GraphActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = GraphActivity.class.getName();
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries = new ArrayList<>();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        barChart = findViewById(R.id.BarChart);
        getDataEntries();

//        getEntries();
//        barDataSet = new BarDataSet(barEntries, "ab");
//        barData = new BarData(barDataSet);
//        barChart.setData(barData);
//        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(18f);
    }

    private void getEntries() {
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 1));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 3));
    }

    private void getDataEntries() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.PARAMS_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
//            jsonObject.put(M3AdminConstants.PARAMS_PIA_ID, "21");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GRAPH_PIA;
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GRAPH_TNSRLM;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                JSONArray data = response.getJSONArray("graph_data");
//                barEntries.add(data.getJSONObject(0).getString("stu_count"));

                ArrayList NoOfEmp = new ArrayList();
                ArrayList year = new ArrayList();

                for (int i = 0; i < data.length(); i++) {
                    float ff = Float.valueOf(data.getJSONObject(i).getString("stu_count"));
                    NoOfEmp.add(new BarEntry(ff, i));
                    year.add(data.getJSONObject(i).getString("month"));
                }


//                barDataSet = new BarDataSet(NoOfEmp, "No of students");
//                barData = new BarData(year, barDataSet);
//                barChart.setData(barData);
//                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//                barDataSet.setValueTextColor(Color.BLACK);
//                barDataSet.setValueTextSize(18f);

                BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Students");
                barChart.animateY(1000);
                BarData dataa = new BarData(year, bardataset);
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                barChart.setData(dataa);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Intent intent = new Intent(this, PiaActivity.class);
//            Toast.makeText(AddNewPiaActivity.this, "Pia created successfully", Toast.LENGTH_SHORT).show();
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
        }
    }

    @Override
    public void onError(String error) {
        AlertDialogHelper.showSimpleAlertDialog(this, error);

    }
}