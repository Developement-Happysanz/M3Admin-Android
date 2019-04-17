package com.happysanz.m3admin.activity.piamodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.MobilizationPlanListAdapter;
import com.happysanz.m3admin.adapter.TaskDataListAdapter;
import com.happysanz.m3admin.bean.pia.MobilizationPlan;
import com.happysanz.m3admin.bean.pia.MobilizationPlanList;
import com.happysanz.m3admin.bean.pia.TaskData;
import com.happysanz.m3admin.bean.pia.TaskDataList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.util.Log.d;

public class ProjectPlanActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    MobilizationPlanListAdapter taskDataListAdapter;
    ArrayList<MobilizationPlan> taskDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    ImageView addNewTask;

    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = ProjectPlanActivity.class.getSimpleName();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_plan);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.listView_task);
        loadMoreListView.setOnItemClickListener(this);
        taskDataArrayList = new ArrayList<>();
        addNewTask = findViewById(R.id.add_plan);
        addNewTask.setOnClickListener(this);

        loadMobPlan();
    }

    private void loadMobPlan() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("1")) {
            id = PreferenceStorage.getPIAProfileId(this);
        } else {
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.MOBILIZATION_PLAN_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == addNewTask) {
            Intent intent = new Intent(getApplicationContext(), AddPlanActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        MobilizationPlan taskData = null;
        if ((taskDataListAdapter != null) && (taskDataListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = taskDataListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            taskData = taskDataArrayList.get(actualindex);
        } else {
            taskData = taskDataArrayList.get(position);
        }
        url = taskData.getDocUrl();
        new DownloadFile().execute(url);
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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    progressDialogHelper.hideProgressDialog();

                    Gson gson = new Gson();
                    MobilizationPlanList taskDataList = gson.fromJson(response.toString(), MobilizationPlanList.class);
                    if (taskDataList.getTaskData() != null && taskDataList.getTaskData().size() > 0) {
                        totalCount = taskDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(taskDataList.getTaskData());
                    }
                }
            });
        }
    }

    @Override
    public void onError(final String error) {

    }

    protected void updateListAdapter(ArrayList<MobilizationPlan> taskDataArrayList) {
        this.taskDataArrayList.addAll(taskDataArrayList);
//        if (taskDataListAdapter == null) {
        taskDataListAdapter = new MobilizationPlanListAdapter(this, this.taskDataArrayList);
        loadMoreListView.setAdapter(taskDataListAdapter);
//        } else {
        taskDataListAdapter.notifyDataSetChanged();
//        }
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ProjectPlanActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "Download/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }
    
}
