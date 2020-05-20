package com.happysanz.m3admin.activity.piamodule;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.Mobilizer;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AppValidator;
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
import java.util.List;
import java.util.Locale;

public class UserDetailsActivity  extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = AddTaskActivity.class.getName();

    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    EditText spnMobilizer;
    String storeMobilizerId = "", res;
    Button save;
    EditText txtName, txtGender, txtDob, txtNationality, txtReligion, txtClass, txtStatus, txtCommunity, txtAddress, txtMail, txtSecMail, txtPhone, txtSecPhone, txtQualification;
    private List<String> mGenderList = new ArrayList<String>();
    private ArrayAdapter<String> mGenderAdapter = null;
    private List<String> mRoleList = new ArrayList<String>();
    private ArrayAdapter<String> mRoleAdapter = null;
    private List<String> mStatusList = new ArrayList<String>();
    private ArrayAdapter<String> mStatusAdapter = null;
    Mobilizer user;
    String mob = "";
    ImageView edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_details);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        mob = getIntent().getStringExtra("page");

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mob.equalsIgnoreCase("mob")){
                    Intent intent = new Intent(getApplicationContext(), MobilizerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        user = (Mobilizer) getIntent().getSerializableExtra("userObj");
        TextView text1 = findViewById(R.id.title);
        text1.setText("User Detail - " + user.getName());
        PreferenceStorage.saveMobName(this, user.getName());

//        spnMobilizer.setVisibility(View.GONE);

        save = findViewById(R.id.mobliser_work);
        save.setOnClickListener(this);

        txtName = findViewById(R.id.user_name);
        txtName.setClickable(false);
        txtName.setFocusable(false);
        txtGender = findViewById(R.id.gender);
        txtGender.setClickable(false);
        txtGender.setFocusable(false);
        txtStatus = findViewById(R.id.status);

        txtStatus = findViewById(R.id.status);
        txtStatus.setClickable(false);
        txtStatus.setFocusable(false);

        txtDob = findViewById(R.id.dob);
        txtDob.setClickable(false);
        txtDob.setFocusable(false);
        txtNationality = findViewById(R.id.nationality);
        txtNationality.setClickable(false);
        txtNationality.setFocusable(false);
        txtReligion = findViewById(R.id.religion);
        txtReligion.setClickable(false);
        txtReligion.setFocusable(false);
        txtClass = findViewById(R.id.community_class);
        txtClass.setClickable(false);
        txtClass.setFocusable(false);
        txtCommunity = findViewById(R.id.community);
        txtCommunity.setClickable(false);
        txtCommunity.setFocusable(false);
        txtAddress = findViewById(R.id.address);
        txtAddress.setClickable(false);
        txtAddress.setFocusable(false);
        txtMail = findViewById(R.id.email);
        txtMail.setClickable(false);
        txtMail.setFocusable(false);
        txtSecMail = findViewById(R.id.secondary_mail);
        txtSecMail.setClickable(false);
        txtSecMail.setFocusable(false);
        txtPhone = findViewById(R.id.phone);
        txtPhone.setClickable(false);
        txtPhone.setFocusable(false);
        txtSecPhone = findViewById(R.id.seconday_phone);
        txtSecPhone.setClickable(false);
        txtSecPhone.setFocusable(false);
        txtQualification = findViewById(R.id.qualification);
        txtQualification.setClickable(false);
        txtQualification.setFocusable(false);

        edit = findViewById(R.id.edit_user);
        edit.setOnClickListener(this);

        getUserData();

    }

    private void getUserData() {
        res = "data";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(M3AdminConstants.KEY_USER_MASTER_ID, user.getUser_master_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_USER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        txtDob.setText(mDateFormatter.format(newDate.getTime()));
    }

    @Override
    public void onClick(View view) {
        if (view == save) {
            Intent intent = new Intent(this, MobiliserWorkTypeActivity.class);
            intent.putExtra("userObj", user);
            intent.putExtra("page", "mob");
            startActivity(intent);
            finish();
        } else if (view == edit) {
            Intent intent = new Intent(this, UpdateUserActivity.class);
            intent.putExtra("userObj", user);
            intent.putExtra("page", "mob");
            startActivity(intent);
            finish();
        }
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

            if (res.equalsIgnoreCase("send")) {


            } else if (res.equalsIgnoreCase("data")) {
                try {
                    JSONArray getData = response.getJSONArray("userList");

                    txtName.setText(getData.getJSONObject(0).getString("name"));
                    txtGender.setText(getData.getJSONObject(0).getString("sex"));
                    txtDob.setText(getData.getJSONObject(0).getString("dob"));
                    txtNationality.setText(getData.getJSONObject(0).getString("nationality"));
                    txtReligion.setText(getData.getJSONObject(0).getString("religion"));
                    txtClass.setText(getData.getJSONObject(0).getString("community_class"));
                    txtCommunity.setText(getData.getJSONObject(0).getString("community"));
                    txtQualification.setText(getData.getJSONObject(0).getString("qualification"));
                    txtPhone.setText(getData.getJSONObject(0).getString("phone"));
                    txtSecPhone.setText(getData.getJSONObject(0).getString("sec_phone"));
                    txtMail.setText(getData.getJSONObject(0).getString("email"));
                    txtSecMail.setText(getData.getJSONObject(0).getString("sec_email"));
                    txtAddress.setText(getData.getJSONObject(0).getString("address"));
//                    if (getData.getJSONObject(0).getString("role_type").equalsIgnoreCase("5")) {
//                        spnMobilizer.setText("Mobiliser");
//                    } else if (getData.getJSONObject(0).getString("role_type").equalsIgnoreCase("4")) {
//                        spnMobilizer.setText("Trainer");
//                    }
                    txtStatus.setText(getData.getJSONObject(0).getString("status"));
                    String serverFormatDate = "";
                    String dob = txtDob.getText().toString();

                    if (dob != null && dob != "") {

                        String date = dob;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date testDate = null;
                        try {
                            testDate = sdf.parse(date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        serverFormatDate = formatter.format(testDate);
                        System.out.println(".....Date..." + serverFormatDate);
                        txtDob.setText(serverFormatDate);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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