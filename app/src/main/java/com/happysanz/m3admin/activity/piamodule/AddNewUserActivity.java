package com.happysanz.m3admin.activity.piamodule;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.StoreMobilizer;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AndroidMultiPartEntity;
import com.happysanz.m3admin.utils.AppValidator;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class AddNewUserActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = AddTaskActivity.class.getName();

    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    EditText spnMobilizer;
    String storeMobilizerId = "", res;
    Button save;
    EditText txtName, txtGender, txtDob, txtNationality, txtReligion, txtStatus, txtClass, txtCommunity, txtAddress, txtMail, txtSecMail, txtPhone, txtSecPhone, txtQualification;
    private List<String> mGenderList = new ArrayList<String>();
    private ArrayAdapter<String> mGenderAdapter = null;
    private List<String> mRoleList = new ArrayList<String>();
    private ArrayAdapter<String> mRoleAdapter = null;
    private List<String> mStatusList = new ArrayList<String>();
    private ArrayAdapter<String> mStatusAdapter = null;
    String page = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        page = getIntent().getStringExtra("page");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page.equalsIgnoreCase("mob")) {
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
        spnMobilizer = findViewById(R.id.spn_role);
        spnMobilizer.setFocusable(false);
        txtName = findViewById(R.id.user_name);
        txtGender = findViewById(R.id.gender);
        txtStatus = findViewById(R.id.status);
        txtStatus.setOnClickListener(this);
        txtStatus.setFocusable(false);


        txtDob = findViewById(R.id.dob);
        txtNationality = findViewById(R.id.nationality);
        txtReligion = findViewById(R.id.religion);
        txtClass = findViewById(R.id.community_class);
        txtCommunity = findViewById(R.id.community);
        txtAddress = findViewById(R.id.address);
        txtMail = findViewById(R.id.email);
        txtSecMail = findViewById(R.id.secondary_mail);
        txtPhone = findViewById(R.id.phone);
        txtSecPhone = findViewById(R.id.seconday_phone);
        txtQualification = findViewById(R.id.qualification);
        txtDob.setOnClickListener(this);
        txtDob.setFocusable(false);
        spnMobilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoleList();
            }
        });
        txtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderList();
            }
        });
        txtGender.setFocusable(false);
        save = findViewById(R.id.save_user);
        save.setOnClickListener(this);
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        mGenderList.add("Male");
        mGenderList.add("Female");
        mGenderList.add("Other");
        mGenderList.add("Rather not say");

        mGenderAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mGenderList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mGenderList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        mRoleList.add("Trainer");
        mRoleList.add("Mobiliser");

        mRoleAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mRoleList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mRoleList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

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

    private void showGenderList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Gender");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Gender");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mGenderAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mGenderList.get(which);
                        txtGender.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showRoleList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Role");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Role");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mRoleAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mRoleList.get(which);
                        spnMobilizer.setText(strName);
                    }
                });
        builderSingle.show();
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
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        txtDob.setText(mDateFormatter.format(newDate.getTime()));
    }

    @Override
    public void onClick(View view) {
        if (view == save) {
            if (validateFields()) {
                sendValues();
            }
        } else if (view == txtDob) {
            showBirthdayDate();
        } else if (view == txtStatus) {
            showStatusList();
        }
    }

    private void sendValues() {
        res = "";

        String name = txtName.getText().toString();
        String roleName = spnMobilizer.getText().toString();
        String role = "";
        if (roleName.equalsIgnoreCase("Trainer")) {
            role = "4";
        } else if (roleName.equalsIgnoreCase("Mobiliser")) {
            role = "5";
        }
        String gender = txtGender.getText().toString();
        String religion = txtReligion.getText().toString();
        String nationality = txtNationality.getText().toString();
        String communityClass = txtClass.getText().toString();
        String community = txtCommunity.getText().toString();
        String dob = txtDob.getText().toString();
        String qualification = txtQualification.getText().toString();
        String phone = txtPhone.getText().toString();
        String secphone = txtSecPhone.getText().toString();
        String mail = txtMail.getText().toString();
        String seccmail = txtSecMail.getText().toString();
        String status = txtStatus.getText().toString();
        String serverFormatDate = "";

        if (dob != null && dob != "") {

            String date = dob;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            serverFormatDate = formatter.format(testDate);
            System.out.println(".....Date..." + serverFormatDate);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(M3AdminConstants.KEY_USER_MASTER_ID, PreferenceStorage.getPIAProfileId(getApplicationContext()));
            jsonObject.put(M3AdminConstants.PARAMS_NAME, name);
            jsonObject.put(M3AdminConstants.PARAMS_ROLE, role);
            jsonObject.put(M3AdminConstants.PARAMS_GENDER, gender);
            jsonObject.put(M3AdminConstants.PARAMS_RELIGION, religion);
            jsonObject.put(M3AdminConstants.PARAMS_NATIONALITY, nationality);
            jsonObject.put(M3AdminConstants.PARAMS_COMMUNITY_CLASS, communityClass);
            jsonObject.put(M3AdminConstants.PARAMS_COMMUNITY, community);
            jsonObject.put(M3AdminConstants.PARAMS_DOB, serverFormatDate);
            jsonObject.put(M3AdminConstants.PARAMS_PHONE, phone);
            jsonObject.put(M3AdminConstants.PARAMS_SEC_PHONE, secphone);
            jsonObject.put(M3AdminConstants.PARAMS_EMAIL, mail);
            jsonObject.put(M3AdminConstants.PARAMS_SEC_EMAIL, seccmail);
            jsonObject.put(M3AdminConstants.PARAMS_QUALIFICATION, qualification);
            jsonObject.put(M3AdminConstants.PARAMS_STATUS, status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.CREATE_USER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = txtDob.getText().toString();
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
                DatePicker dP = mDatePicker.getDatePicker();
                Calendar cal = Calendar.getInstance();
                cal.set(2000,12,31);
                Date result = cal.getTime();
                dP.setMaxDate(result.getTime());
                mDatePicker.show();
            }
        } else {
            Log.d(TAG, "show default date");

            mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
            DatePicker dP = mDatePicker.getDatePicker();
            Calendar cal = Calendar.getInstance();
            cal.set(2000,12,31);
            Date result = cal.getTime();
            dP.setMaxDate(result.getTime());
            mDatePicker.show();
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

            Toast.makeText(this, "You have just created a profile for your staff!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
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

    private boolean validateFields() {

        if (!AppValidator.checkNullString(this.txtName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter staff name");
            return false;
        } else if (!AppValidator.checkNullString(this.txtGender.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select gender");
            return false;
        } else if (!AppValidator.checkNullString(this.txtDob.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select date of birth");
            return false;
        } else if (!AppValidator.checkNullString(this.txtNationality.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter nationality");
            return false;
        } else if (!AppValidator.checkNullString(this.txtReligion.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter religion");
            return false;
        } else if (!AppValidator.checkNullString(this.txtClass.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter community class");
            return false;
        } else if (!AppValidator.checkNullString(this.txtCommunity.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter community");
            return false;
        } else if (!AppValidator.checkNullString(this.txtAddress.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select address");
            return false;
        } else if (!AppValidator.checkNullString(this.txtPhone.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter phone number");
            return false;
        } else if (!AppValidator.checkStringMinLength(10, this.txtPhone.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Invalid phone number");
            return false;
        } else if (!AppValidator.checkNullString(this.txtQualification.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter qualification");
            return false;
        } else if (!AppValidator.checkNullString(this.txtMail.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter Email ID");
            return false;
        } else if (!AppValidator.checkNullString(this.txtStatus.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Set a status");
            return false;
        } else {
            return true;
        }
    }

}