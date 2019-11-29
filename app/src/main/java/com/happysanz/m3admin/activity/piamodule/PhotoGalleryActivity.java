package com.happysanz.m3admin.activity.piamodule;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.CenterPhotosListAdapter;
import com.happysanz.m3admin.bean.pia.CenterPhotosData;
import com.happysanz.m3admin.bean.pia.CenterPhotosList;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AndroidMultiPartEntity;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.util.Log.d;

public class PhotoGalleryActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "ImageScalling";
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
    private Uri outputFileUri;
    private File file;
    private File sourceFile;
    private File destFile;
    static final int REQUEST_IMAGE_GET = 1;
    static final int CROP_PIC = 2;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        centers = (Centers) getIntent().getSerializableExtra("cent");
        imageview = findViewById(R.id.sample);

//        taskData = (TaskData) getIntent().getSerializableExtra("eventObj");
        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);
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
                checkPermission(getApplicationContext());
                openImageIntent();
            }
        });

        if (PreferenceStorage.getTnsrlmCheck(this)){
            findViewById(R.id.add_photo).setVisibility(View.GONE);
        }

        loadMoreListView = (ListView) findViewById(R.id.photo_list);
        loadMoreListView.setOnItemClickListener(this);

        centerPhotosDataArrayList = new ArrayList<>();

        viewCenterPhotos();
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
        Calendar newCalendar = Calendar.getInstance();
        int month = newCalendar.get(Calendar.MONTH) + 1;
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        int hours = newCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = newCalendar.get(Calendar.MINUTE);
        int seconds = newCalendar.get(Calendar.SECOND);
        final String fname = PreferenceStorage.getUserId(this)+"_"+ day + "_"+ month +"_"+ year +"_"+ hours + "_"+ minutes + "_" + seconds + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
        destFile = sdImageMainDirectory;
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Log.d(TAG, "camera output Uri" + outputFileUri);

        // Camera.
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }


                if (isCamera) {
                    d(TAG, "Add to gallery");
                    mSelectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(mSelectedImageUri);
                } else {
//                    selectedImageUri = data == null ? null : data.getData();
//                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
//                    Log.d(TAG, "path to image is" + mActualFilePath);

                    if (data != null && data.getData() != null) {
                        try {
                            mSelectedImageUri = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(mSelectedImageUri,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mActualFilePath = getRealPathFromURI(this, mSelectedImageUri);
                            cursor.close();
                            File f1 = new File(mActualFilePath);
                            mCurrentUserImageBitmap = decodeFile(f1);
                            //return Image Path to the Main Activity
                            Intent returnFromGalleryIntent = new Intent();
                            returnFromGalleryIntent.putExtra("picturePath", mActualFilePath);
                            setResult(RESULT_OK, returnFromGalleryIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Intent returnFromGalleryIntent = new Intent();
                            setResult(RESULT_CANCELED, returnFromGalleryIntent);
                            finish();
                        }
                    } else {
                        Log.i(TAG, "RESULT_CANCELED");
                        Intent returnFromGalleryIntent = new Intent();
                        setResult(RESULT_CANCELED, returnFromGalleryIntent);
                        finish();
                    }

                }
                d(TAG, "image Uri is" + mSelectedImageUri);
                if (mSelectedImageUri != null) {
                    d(TAG, "image URI is" + mSelectedImageUri);
//                    performCrop();
//                    setPic(mSelectedImageUri);
                    mUpdatedImageUrl = null;
                    mCurrentUserImageBitmap = decodeFile(destFile);
                    new UploadFileToServer().execute();
                }
            }

        }
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        destFile = new File(file, "img_"
                + dateFormatter.format(new Date()).toString() + ".png");
        mActualFilePath = destFile.getPath();
        try {
            FileOutputStream out = new FileOutputStream(destFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UploadFileToServer";
        private HttpClient httpclient;
        HttpPost httppost;
        public boolean isTaskAborted = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(String.format(M3AdminConstants.BUILD_URL + M3AdminConstants.ADD_PHOTO + Integer.parseInt(centers.getid())+"/" +Integer.parseInt(PreferenceStorage.getUserId(getApplicationContext()))+"/"));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });
                Log.d(TAG, "actual file path is" + mActualFilePath);
                if (mActualFilePath != null) {

                    File sourceFile = new File(mActualFilePath);

                    // Adding file data to http body
                    //fileToUpload
                    entity.addPart("center_photo", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(PhotoGalleryActivity.this)));
                    entity.addPart("center_id", new StringBody(PreferenceStorage.getCenterId(PhotoGalleryActivity.this)));
//                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(PhotoGalleryActivity.this)));

//                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                        try {
                            JSONObject resp = new JSONObject(responseString);
                            String successVal = resp.getString("status");

//                            mUpdatedImageUrl = resp.getString("picture_url");

                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            progressDialogHelper.hideProgressDialog();

            super.onPostExecute(result);
            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
                Toast.makeText(PhotoGalleryActivity.this, "Unable to upload picture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PhotoGalleryActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
//            saveProfileData();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void galleryAddPic(Uri urirequest) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(urirequest.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);

            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null");
            }
        } catch (Exception e) {
            result = null;
            Toast.makeText(this, "Was unable to save  image", Toast.LENGTH_SHORT).show();

        } finally {
            return result;
        }
    }

    private void sendSuccessMessage() {
        Log.d(TAG, "Updated image succesfully");
        Toast.makeText(getApplicationContext(), "Upload succesful", Toast.LENGTH_SHORT).show();
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
                startActivity(getIntent());
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
        String url = taskData.getCenterPhotos();
        Intent intent = new Intent(getApplicationContext(), ZoomImageActivity.class);
        intent.putExtra("eventObj", url);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


}