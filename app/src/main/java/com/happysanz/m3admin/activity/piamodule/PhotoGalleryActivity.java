package com.happysanz.m3admin.activity.piamodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.CenterPhotosListAdapter;
import com.happysanz.m3admin.bean.pia.CenterPhotosData;
import com.happysanz.m3admin.bean.pia.CenterPhotosList;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.dialogfragments.CompoundAlertDialogFragment;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.util.Log.d;

public class PhotoGalleryActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = PhotoGalleryActivity.class.getName();
    Centers centers;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView addPhoto;
    //    private CenterPhotosData centerPhotosData;
    protected ListView loadMoreListView;
    protected CenterPhotosListAdapter centerPhotosListAdapter;
    protected ArrayList<CenterPhotosData> centerPhotosDataArrayList;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    protected boolean isLoadingForFirstTime = true;
    String res;
    ImageView imageview;

    private TextView messageText;
//    private Button uploadButton, btnselectpic;
//    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    private String upLoadServerUri = "";
    private String imagepath="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        centers = (Centers) getIntent().getSerializableExtra("cent");
        imageview = findViewById(R.id.sample);

//        taskData = (TaskData) getIntent().getSerializableExtra("eventObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(PhotoGalleryActivity.this);
                alertDialogBuilder.setTitle("Upload Image");
                alertDialogBuilder.setMessage("Select Option");
                alertDialogBuilder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCameraForResult(0);
                    }
                });
                alertDialogBuilder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);
                    }
                });
                alertDialogBuilder.show();
            }
        });


        loadMoreListView = (ListView) findViewById(R.id.photo_list);
        loadMoreListView.setOnItemClickListener(this);

        centerPhotosDataArrayList = new ArrayList<>();

        viewCenterPhotos();
    }

    private void openCameraForResult(int requestCode){
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri  = Uri.parse("file:///sdcard/photo.jpg");
        photo.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(photo,requestCode);
    }

    @Override
    public void onClick(View v) {
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
            if (res.equalsIgnoreCase("upload")) {
                AlertDialogHelper.showSimpleAlertDialog(this, "Uploaded Successfully");

                finish();
            } else {
                Gson gson = new Gson();
                CenterPhotosList taskPictureList = gson.fromJson(response.toString(), CenterPhotosList.class);
                if (taskPictureList.getCenterPhotosData() != null && taskPictureList.getCenterPhotosData().size() > 0) {
                    totalCount = taskPictureList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(taskPictureList.getCenterPhotosData());
                }
            }
        }
    }

//    public String getPath(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//
//        return path;
//    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"+imagepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"+ imagepath);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                upLoadServerUri = M3AdminConstants.BUILD_URL + M3AdminConstants.ADD_PHOTO;
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" F:/wamp/wamp/www/uploads";
                            messageText.setText(msg);
                            Toast.makeText(PhotoGalleryActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(PhotoGalleryActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(PhotoGalleryActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block 
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String path = getPath(selectedImage);
                    uploadFile(path);
//                    imageview.setImageURI(selectedImage);

                    res = "upload";
//                    JSONObject jsonObject = new JSONObject();
//                    String userId;
//                    if(PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")){
//                        userId = PreferenceStorage.getPIAProfileId(getApplicationContext());
//                    } else {
//                        userId = PreferenceStorage.getUserId(getApplicationContext());
//                    }
//                    try {
//                        jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
//                        jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, centers.getid());
//                        jsonObject.put(M3AdminConstants.PARAMS_CENTER_PHOTO, path);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//                    String url = M3AdminConstants.BUILD_URL + M3AdminConstants.ADD_PHOTO;
//                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageview.setImageURI(selectedImage);
//                    JSONObject jsonObject = new JSONObject();
//                    String userId;
//                    if(PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")){
//                        userId = PreferenceStorage.getPIAProfileId(getApplicationContext());
//                    } else {
//                        userId = PreferenceStorage.getUserId(getApplicationContext());
//                    }
//                    try {
//                        jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
//                        jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, centers.getid());
//                        jsonObject.put(M3AdminConstants.PARAMS_CENTER_PHOTO, selectedImage.toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//                    String url = M3AdminConstants.BUILD_URL + M3AdminConstants.ADD_PHOTO;
//                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
                break;
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(PhotoGalleryActivity.this, error);
    }

    private void viewCenterPhotos() {
        res = "photo";
        JSONObject jsonObject = new JSONObject();
        String userId;
        if(PreferenceStorage.getUserId(getApplicationContext()).equalsIgnoreCase("1")){
            userId = PreferenceStorage.getPIAProfileId(getApplicationContext());
        } else {
            userId = PreferenceStorage.getUserId(getApplicationContext());
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, userId);
            jsonObject.put(M3AdminConstants.PARAMS_CENTER_ID, centers.getid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.VIEW_GALLERY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<CenterPhotosData> centerPhotosDataArrayList) {
        this.centerPhotosDataArrayList.addAll(centerPhotosDataArrayList);
//        if (taskDataListAdapter == null) {
        centerPhotosListAdapter = new CenterPhotosListAdapter(PhotoGalleryActivity.this, this.centerPhotosDataArrayList);
        loadMoreListView.setAdapter(centerPhotosListAdapter);
//        } else {
        centerPhotosListAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        CenterPhotosData taskData = null;
        if ((centerPhotosListAdapter != null) && (centerPhotosListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = centerPhotosListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            taskData = centerPhotosDataArrayList.get(actualindex);
        } else {
            taskData = centerPhotosDataArrayList.get(position);
        }
        Intent intent = new Intent(getApplicationContext(), ZoomImageActivity.class);
        intent.putExtra("eventObj", taskData);
        startActivity(intent);
    }
}