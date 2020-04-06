package com.happysanz.m3admin.activity.piamodule;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.ProspectDocListAdapter;
import com.happysanz.m3admin.bean.pia.ProspectDoc;
import com.happysanz.m3admin.bean.pia.ProspectDoc;
import com.happysanz.m3admin.bean.pia.ProspectDocList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
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

public class DocumentDownloadActivity  extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = DocumentUploadActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView ivBack;
    private LinearLayout cast, disab;
    private TextView aadhaar, transferCertificate, communityCertificate, disability, rationCard, voterId, jobCard, passBook;
    private ProspectDocListAdapter prospectDocListAdapter;
    ArrayList<ProspectDoc> taskDataArrayList = new ArrayList<>();

    private Button done;
    private ImageView addDoc;
    ListView loadMoreListView;
    private String url;
    private String prospectID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_prospect_docs);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);

        prospectID = PreferenceStorage.getAdmissionId(this);

        done = findViewById(R.id.btn_done);
        done.setOnClickListener(this);

        addDoc = findViewById(R.id.add_document);
        addDoc.setOnClickListener(this);

        loadMoreListView = findViewById(R.id.doc_listview);
        loadMoreListView.setOnItemClickListener(this);

        getDocStatus();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        ProspectDoc taskData = null;
        if ((prospectDocListAdapter != null) && (prospectDocListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = prospectDocListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            taskData = taskDataArrayList.get(actualindex);
        } else {
            taskData = taskDataArrayList.get(position);
        }
        url = taskData.getfile_name();
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Download");
        alertDialogBuilder.setMessage("Do you want to download " + taskData.getdoc_name() + "?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                new DownloadFile().execute(url);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
    
    @Override
    public void onClick(View v) {
        if (v == done) {
//            if (docOne && docTwo && docThree && docFour && docFive && docSix && docSeven && docEight) {
                finish();
//            } else {
//                AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), "Upload all necessary documents");
//            }
        } else if (v == addDoc) {
            Intent newi = new Intent (this, DocumentUploadActivity.class);
            startActivity(newi);
        } else if (v == ivBack) {
            finish();
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
            Gson gson = new Gson();
            ProspectDocList taskDataList = gson.fromJson(response.toString(), ProspectDocList.class);
            if (taskDataList.getTaskData() != null && taskDataList.getTaskData().size() > 0) {
                
                updateListAdapter(taskDataList.getTaskData());
            }
        }
    }

    protected void updateListAdapter(ArrayList<ProspectDoc> taskDataArrayList) {
        this.taskDataArrayList.addAll(taskDataArrayList);
//        if (taskDataListAdapter == null) {
        prospectDocListAdapter = new ProspectDocListAdapter(this, this.taskDataArrayList);
        loadMoreListView.setAdapter(prospectDocListAdapter);
//        } else {
        prospectDocListAdapter.notifyDataSetChanged();
//        }
    }
    
    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void getDocStatus() {

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(M3AdminConstants.PROSPECT_ID, prospectID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = M3AdminConstants.BUILD_URL + M3AdminConstants.DOC_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
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
            this.progressDialog = new ProgressDialog(DocumentDownloadActivity.this);
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