package com.happysanz.m3admin.activity.piamodule;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.StoreMobilizer;
import com.happysanz.m3admin.bean.pia.TradeData;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTradeActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener{

    private static final String TAG = AddTaskActivity.class.getName();

    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Spinner spnMobilizer;
    String storeMobilizerId ="", res;
    Button save;
    EditText txtTitle, txtStatus;
    private List<String> mStatusList = new ArrayList<String>();
    private ArrayAdapter<String> mStatusAdapter = null;
    TradeData tradeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trade);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tradeData = (TradeData) getIntent().getSerializableExtra("eventObj") ;

        txtTitle = findViewById(R.id.trade_title);
        txtStatus = findViewById(R.id.status);
        txtStatus.setOnClickListener(this);
        save = findViewById(R.id.save_trade);
        save.setOnClickListener(this);

        if (tradeData != null){
            txtTitle.setText(tradeData.getTradeName());
            txtStatus.setText(tradeData.getStatus());
        }

        mStatusList.add("Active");
        mStatusList.add("Inactive");

        mStatusAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mStatusList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mStatusList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

    }

    private void showStatusList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Status");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Status");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mStatusAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mStatusList.get(which);
                        txtStatus.setText(strName);
                    }
                });
        builderSingle.show();
    }
    @Override
    public void onClick(View view) {
        if (view == save) {
            if (validateFields()) {
                if (tradeData != null) {
                    updateTaskValues();
                } else {
                    sendTaskValues();
                }
            }
        } else if (view == txtStatus) {
            showStatusList();
        }
    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.txtTitle.getText().toString().trim())) {
            txtTitle.setError(getString(R.string.empty_entry));
            requestFocus(txtTitle);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void sendTaskValues() {
        res = "add";

        String title = txtTitle.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(M3AdminConstants.PARAMS_TRADE_TITLE, title);
            jsonObject.put(M3AdminConstants.PARAMS_STATUS, txtStatus.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.CREATE_TRADE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void updateTaskValues() {
        res = "update";

        String title = txtTitle.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(M3AdminConstants.PARAMS_TRADE_ID, tradeData.getId());
            jsonObject.put(M3AdminConstants.PARAMS_TRADE_TITLE, title);
            jsonObject.put(M3AdminConstants.PARAMS_STATUS, txtStatus.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.UPDATE_TRADE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
            if (tradeData != null) {
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(M3AdminConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
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
    public void onError(String error) {

    }
}