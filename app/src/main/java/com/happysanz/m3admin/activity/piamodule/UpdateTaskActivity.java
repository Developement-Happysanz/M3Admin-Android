package com.happysanz.m3admin.activity.piamodule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.TaskData;
import com.happysanz.m3admin.bean.pia.WorkDetails;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AppValidator;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.M3AdminConstants;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

/**
 * Created by Admin on 09-01-2018.
 */

public class UpdateTaskActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = UpdateTaskActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText edtTitle, edtTaskDetails, edtTaskDate, txtStatus, txtType;
    private Button btnUploadPhotos, btnViewPhotos, btnDone;
    private ImageView ivBack;
    private SimpleDateFormat mDateFormatter;
    private DatePickerDialog mDatePicker;

    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;

    private WorkDetails taskData;

    private List<String> mStatusList = new ArrayList<String>();
    private ArrayAdapter<String> mStatusAdapter = null;

    private Button viewPhotos;

    private List<String> mTypeList = new ArrayList<String>();
    private ArrayAdapter<String> mTypeAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        taskData = (WorkDetails) getIntent().getSerializableExtra("taskObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);

        edtTitle = findViewById(R.id.task_title);
        edtTitle.setText(taskData.gettask_title());

        edtTaskDetails = findViewById(R.id.task_link);
        edtTaskDetails.setText(taskData.getcomments());

        edtTaskDate = findViewById(R.id.task_date);
        edtTaskDate.setOnClickListener(this);
        edtTaskDate.setFocusable(false);

        txtStatus = findViewById(R.id.status);
        txtStatus.setOnClickListener(this);
        txtStatus.setFocusable(false);
        txtStatus.setText(taskData.getstatus());

        txtType = findViewById(R.id.task_type);
        txtType.setOnClickListener(this);
        txtType.setFocusable(false);
        txtType.setText(taskData.getwork_type());

        String taskDate = "";
        if (taskData.getattendance_date() != null && !taskData.getattendance_date().isEmpty()) {

            String date = taskData.getattendance_date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
            taskDate = formatter.format(testDate);
            System.out.println(".....Date..." + taskDate);
        }

        edtTaskDate.setText(taskDate);

        btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);

//        viewPhotos = findViewById(R.id.btn_view_photos);
//        viewPhotos.setOnClickListener(this);

        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        setupUI(findViewById(R.id.scrollID));
        mStatusList.add("Assigned");
        mStatusList.add("Ongoing");
        mStatusList.add("Completed");

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

        mTypeList.add("Field Work");
        mTypeList.add("Office Work");

        mTypeAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mTypeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mTypeList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(UpdateTaskActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        edtTaskDate.setText(mDateFormatter.format(newDate.getTime()));
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
//            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
//            startActivity(intent);
            finish();
        }
        if (v == edtTaskDate) {
            showBirthdayDate();
        }
        if (v == btnDone) {
            if (validateFields()) {
                saveTask();
            }
        }
        if (v == btnUploadPhotos) {
        }
        if (v == txtStatus) {
            showGenderList();
        }
        if (v == viewPhotos) {
            Intent intent = new Intent(getApplicationContext(), ViewTaskPhotosActivity.class);
            intent.putExtra("eventObj", taskData);
            startActivity(intent);
        }
        if (v == txtType) {
            showTypeList();
        }
    }

    private void showTypeList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Type");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Type");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mTypeAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mTypeList.get(which);
                        txtType.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private boolean validateFields() {
        if (!AppValidator.checkNullString(this.edtTitle.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Give your task a title");
            return false;
        } else if (!AppValidator.checkNullString(this.edtTaskDetails.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Write your task");
            return false;
        } else if (!AppValidator.checkNullString(this.edtTaskDate.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Choose the date");
            return false;
        } else if (!AppValidator.checkNullString(this.txtStatus.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Set a status");
            return false;
        } else {
            return true;
        }
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
                        AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), msg);

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
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
//            setResult(RESULT_OK);
            Toast.makeText(this, "Changes made to the task are saved.", Toast.LENGTH_SHORT).show();
//            finish();
//            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
//            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void saveTask() {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {

            String title = edtTitle.getText().toString();
            String details = edtTaskDetails.getText().toString();
            String serverFormatDate = "";
            String Task = "";

            if (edtTaskDate.getText().toString() != null && edtTaskDate.getText().toString() != "") {

                String date = edtTaskDate.getText().toString();
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
            }if (txtType.getText().toString() != null && txtType.getText().toString() != "") {
                if (txtType.getText().toString().equalsIgnoreCase("Field Work")) {
                    Task = "2";
                } else if (txtType.getText().toString().equalsIgnoreCase("Office Work")) {
                    Task = "1";
                }
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
                jsonObject.put(M3AdminConstants.PARAMS_ATTEND_ID, taskData.getid());
                jsonObject.put(M3AdminConstants.PARAMS_MOBILIZER_ID, taskData.getmobilizer_id());
                jsonObject.put(M3AdminConstants.PARAMS_TASK_TITLE, title);
                jsonObject.put(M3AdminConstants.PARAMS_TASK_COMMENTS, details);
                jsonObject.put(M3AdminConstants.PARAMS_TASK_DATE, serverFormatDate);
                jsonObject.put(M3AdminConstants.PARAMS_TASK_ID, taskData.gettask_id());
                jsonObject.put(M3AdminConstants.PARAMS_TASK_TYPE, Task);
                jsonObject.put(M3AdminConstants.PARAMS_STATUS, txtStatus.getText().toString());
                jsonObject.put(M3AdminConstants.PARAMS_CREATED_AT, "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = M3AdminConstants.BUILD_URL + M3AdminConstants.TASK_UPDATE;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = edtTaskDate.getText().toString();
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

    private void showGenderList() {
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

}
