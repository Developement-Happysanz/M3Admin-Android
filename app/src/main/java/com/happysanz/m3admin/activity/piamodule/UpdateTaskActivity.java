package com.happysanz.m3admin.activity.piamodule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
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
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.TaskData;
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
    private EditText edtTitle, edtTaskDetails, edtTaskDate;
    private Button btnUploadPhotos, btnViewPhotos, btnDone;
    private ImageView ivBack;
    private SimpleDateFormat mDateFormatter;
    private DatePickerDialog mDatePicker;

    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;

    private TaskData taskData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        taskData = (TaskData) getIntent().getSerializableExtra("taskObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);

        edtTitle = findViewById(R.id.et_task_title);
        edtTitle.setText(taskData.getTaskTitle());

        edtTaskDetails = findViewById(R.id.et_task_detail);
        edtTaskDetails.setText(taskData.getTaskDescription());

        edtTaskDate = findViewById(R.id.et_task_date);
        edtTaskDate.setOnClickListener(this);
        edtTaskDate.setFocusable(false);

        String taskDate = "";
        if (taskData.getTaskDate() != null && taskData.getTaskDate() != "") {

            String date = taskData.getTaskDate();
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

        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        setupUI(findViewById(R.id.scrollID));

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
            openImageIntent();
        }
    }

    private boolean validateFields() {
        if (!AppValidator.checkNullString(this.edtTitle.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid title");
            return false;
        } else if (!AppValidator.checkNullString(this.edtTaskDetails.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid task details");
            return false;
        } else if (!AppValidator.checkNullString(this.edtTaskDate.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid date");
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
            Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show();
//            finish();
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
            String taskDetails = edtTaskDetails.getText().toString();
            String taskDate = edtTaskDate.getText().toString();

            String serverFormatDate = "";

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
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(M3AdminConstants.PARAMS_TASK_TITLE, title);
                jsonObject.put(M3AdminConstants.PARAMS_TASK_ID, taskData.getId());
                jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
                jsonObject.put(M3AdminConstants.PARAMS_TASK_DESCRIPTION, taskDetails);
                jsonObject.put(M3AdminConstants.PARAMS_MOB_ID, taskData.getUser_id());
                jsonObject.put(M3AdminConstants.PARAMS_TASK_DATE, serverFormatDate);
                jsonObject.put(M3AdminConstants.PARAMS_TASK_STATUS, "Active");
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

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");

        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.d(TAG, "Failed to create directory for storing images");
                return;
            }
        }

        final String fname = PreferenceStorage.getUserId(this) + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Log.d(TAG, "camera output Uri" + outputFileUri);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Profile Photo");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_IMAGE_GET);
    }

}
