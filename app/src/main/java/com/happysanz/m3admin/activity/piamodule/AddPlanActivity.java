package com.happysanz.m3admin.activity.piamodule;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AppValidator;
import com.happysanz.m3admin.utils.FilePath;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import static android.util.Log.d;

public class AddPlanActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = AddPlanActivity.class.getSimpleName();
//    EditText edtSelectPlanFile;
    String uploadedFileName, first;
    StringTokenizer tokens;
    private ProgressDialogHelper progressDialogHelper;
    private ServiceHelper serviceHelper;
    Uri selectedFileURI = null;
    String planID = "";
    private static final int PICK_FILE_REQUEST = 1;
    private String selectedFilePath;
//    ImageView ivAttachment;
    Button bUpload, bChoose;
//    TextView tvFileName;
    ProgressDialog dialog;
    EditText fileName;
    File sizeCge;
    DatePicker planDate;
    String start;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        planDate = findViewById(R.id.plan_month);
//        ivAttachment = (ImageView) findViewById(R.id.ivAttachment);
        bChoose = (Button) findViewById(R.id.choose_file);
        bUpload = (Button) findViewById(R.id.upload_file);
//        tvFileName = (TextView) findViewById(R.id.tv_file_name);
//        ivAttachment.setOnClickListener(this);
        bUpload.setOnClickListener(this);
        bChoose.setOnClickListener(this);
        bUpload.setVisibility(View.GONE);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProjectPlanActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fileName = findViewById(R.id.plan_title);
//        edtSelectPlanFile = findViewById(R.id.plan_link);
//        edtSelectPlanFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFileChooser();
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
                    sizeCge = new File (selectedFilePath);
                    if (sizeCge.length() >= 9000000)  {
                        AlertDialogHelper.showSimpleAlertDialog(this, "File size too large");
                        selectedFilePath = null;
                    } else {
                        Toast.makeText(this,"File ready to Upload",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        if ( v == bUpload){
            title = fileName.getText().toString();

            int day_start = planDate.getDayOfMonth();
            int month_start = planDate.getMonth() + 1;
            int year_start = planDate.getYear();

            String formattedMonth = "" + month_start;
            String formattedDayOfMonth = "" + day_start;

            if(month_start < 10){

                formattedMonth = "0" + month_start;
            }
            if(day_start < 10){

                formattedDayOfMonth  = "0" + day_start;
            }

            start = "" + year_start + "-" + formattedMonth + "-" + formattedDayOfMonth;
            if (validateFields()) {
                dialog = ProgressDialog.show(AddPlanActivity.this,"","Uploading File...",true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //creating new thread to handle Http Operations
//                        uploadFile(selectedFilePath);
                        new PostDataAsyncTask().execute();
                    }
                }).start();
            }
        }
        if ( v == bChoose){
            showFileChooser();
            bUpload.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateFields() {
        if (!AppValidator.checkNullString(this.fileName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid title");
            return false;
        } if (!AppValidator.checkNullString(start.trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select Date");
            return false;
        } if (selectedFilePath == null || selectedFilePath.equals("")){
            AlertDialogHelper.showSimpleAlertDialog(this, "Select File to upload");
            return false;
        } else {
            return true;
        }
    }

    private class PostDataAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        //android upload file to server
        private String uploadFile(){

            int serverResponseCode = 0;
            String serverResponseMessage = null;
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead,bytesAvailable,bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);
            double len = selectedFile.length();

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length-1];

            if (!selectedFile.isFile()){
                dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                    }
                });
                return "";
            } else{
                try{
                    String id = "";
                    if (PreferenceStorage.getUserId(AddPlanActivity.this).equalsIgnoreCase("1")) {
                        id = PreferenceStorage.getPIAProfileId(AddPlanActivity.this);
                    } else {
                        id = PreferenceStorage.getUserId(AddPlanActivity.this);
                    }
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    String SERVER_URL = M3AdminConstants.BUILD_URL + M3AdminConstants.MOBILIZATION_PLAN_FILE_ID + "" + id +"/"+title+"/"+start+"/";
                    URI uri = new URI(SERVER_URL.replace(" ", "%20"));
                    String baseURL = uri.toString();
                    URL url = new URL(baseURL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("doc_file", selectedFilePath);
//                    connection.setRequestProperty("user_id", id);
//                    connection.setRequestProperty("doc_name", title);
//                    connection.setRequestProperty("doc_month_year", start);

                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"doc_file\";filename=\""
                            + selectedFilePath + "\"" + lineEnd);

                    dataOutputStream.writeBytes(lineEnd);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0){
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer,0,bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,maxBufferSize);
                        bytesRead = fileInputStream.read(buffer,0,bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = connection.getResponseCode();
                    serverResponseMessage = connection.getResponseMessage();

                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status OK
                    if(serverResponseCode == 200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
//                                tvFileName.setText("File Upload completed.\n\n"+ fileName);
                            }
                        });
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();



                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddPlanActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPlanActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPlanActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                return serverResponseMessage;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            progressDialogHelper.hideProgressDialog();

            super.onPostExecute(result);
            if ((result.contains("OK"))) {
                Toast.makeText(AddPlanActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProjectPlanActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AddPlanActivity.this, "Unable to upload file", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
    }

    @Override
    public void onError(String error) {

    }
}