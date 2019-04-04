package com.happysanz.m3admin.activity.piamodule;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AndroidMultiPartEntity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;

public class AddCenterDetailActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "TradeFragment";

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    EditText centerName, centerDetail, centerAddress;
    Button savedetails;
    String res = "";
    Boolean imgselect = false;
    LinearLayout before, after;
    ImageView logo;


    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    static final int CROP_PIC = 2;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private String mUpdatedImageUrl = null;
    private Bitmap mCurrentUserImageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_center_detail);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CenterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        logo = findViewById(R.id.new_logo);
        logo.setOnClickListener(this);
        before = findViewById(R.id.before_select);
        before.setOnClickListener(this);
        after = findViewById(R.id.after_select);
        centerName = findViewById(R.id.center_name);
        centerDetail = findViewById(R.id.center_detail);
        centerAddress = findViewById(R.id.center_address);
        savedetails = findViewById(R.id.save_center);
        savedetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == savedetails) {
            sendCenterDetails();
        }
        if (view == before) {
            before.setVisibility(View.GONE);
            after.setVisibility(View.VISIBLE);
            openImageIntent();
        }
        if ( view == logo) {
            openImageIntent();
        }
    }

    private void sendCenterDetails() {
        res = "data";
        String cName = centerName.getText().toString();
        String cDetail = centerDetail.getText().toString();
        String cAddress = centerAddress.getText().toString();
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            if (validateFields()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
                    jsonObject.put(M3AdminConstants.PARAMS_CENTER_NAME, cName);
                    jsonObject.put(M3AdminConstants.PARAMS_CENTER_ADDRESS, cAddress);
                    jsonObject.put(M3AdminConstants.PARAMS_CENTER_INFO, cDetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                String url = M3AdminConstants.BUILD_URL + M3AdminConstants.CREATE_CENTER;
                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }

    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.centerName.getText().toString().trim())) {
            centerName.setError(getString(R.string.empty_entry));
            requestFocus(centerName);
            return false;
        } else if (!M3Validator.checkNullString(this.centerDetail.getText().toString().trim())) {
            centerDetail.setError(getString(R.string.empty_entry));
            requestFocus(centerDetail);
            return false;
        } else if (!M3Validator.checkNullString(this.centerAddress.getText().toString().trim())) {
            centerAddress.setError(getString(R.string.empty_entry));
            requestFocus(centerAddress);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
        if (validateSignInResponse(response)){
            try {
                String id = response.getString(M3AdminConstants.PARAMS_CENTER_ID);
                PreferenceStorage.saveCenterId(this,id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mUpdatedImageUrl = null;
            if (!mActualFilePath.isEmpty()) {
                new UploadFileToServer().execute();
            } else {
                Intent intent = new Intent(getApplicationContext(), CenterActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onError(String error) {

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

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                Log.d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    Log.d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    Log.d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }


                if (isCamera) {
                    Log.d(TAG, "Add to gallery");
                    mSelectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(mSelectedImageUri);
                } else {
//                    selectedImageUri = data == null ? null : data.getData();
//                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
//                    Log.d(TAG, "path to image is" + mActualFilePath);

                    if (data != null && data.getData() != null) {
                        try{
                            mSelectedImageUri = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA };
                            Cursor cursor = getContentResolver().query(mSelectedImageUri,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mActualFilePath = getRealPathFromURI(this, mSelectedImageUri);
                            cursor.close();

                            //return Image Path to the Main Activity
                            Intent returnFromGalleryIntent = new Intent();
                            returnFromGalleryIntent.putExtra("picturePath",mActualFilePath);
                            setResult(RESULT_OK,returnFromGalleryIntent);
                        }catch(Exception e){
                            e.printStackTrace();
                            Intent returnFromGalleryIntent = new Intent();
                            setResult(RESULT_CANCELED, returnFromGalleryIntent);
                            finish();
                        }
                    }else{
                        Log.i(TAG,"RESULT_CANCELED");
                        Intent returnFromGalleryIntent = new Intent();
                        setResult(RESULT_CANCELED, returnFromGalleryIntent);
                        finish();
                    }

                }
                Log.d(TAG, "image Uri is" + mSelectedImageUri);
                if (mSelectedImageUri != null) {
                    Log.d(TAG, "image URI is" + mSelectedImageUri);
//                    performCrop();
                    setPic(mSelectedImageUri);
                }
            }  
//            else if (requestCode == CROP_PIC) {
//                // get the returned data
//                Bundle extras = data.getExtras();
//                // get the cropped bitmap
//                Bitmap thePic = extras.getParcelable("data");
//                logo.setImageBitmap(thePic);
//            }

        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UploadFileToServer";
        private HttpClient httpclient;
        HttpPost httppost;
        public boolean isTaskAborted = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            httppost = new HttpPost(String.format(M3AdminConstants.BUILD_URL + M3AdminConstants.ADD_LOGO+ Integer.parseInt(PreferenceStorage.getCenterId(AddCenterDetailActivity.this))));

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
                    entity.addPart("center_banner", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("center_id", new StringBody(PreferenceStorage.getCenterId(getApplicationContext())));
//                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(AddCenterDetailActivity.this)));

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

                            mUpdatedImageUrl = resp.getString("center_logo");

                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                                Log.d(TAG, "Updated succesfully");
//                                Intent intent = new Intent(getApplicationContext(), AddCenterDetailActivity.class);
//                                startActivity(intent);
                                finish();
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

            super.onPostExecute(result);
            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
                Toast.makeText(AddCenterDetailActivity.this, "Unable to upload picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveUserPicture(AddCenterDetailActivity.this, mUpdatedImageUrl);
                    Intent intent = new Intent(getApplicationContext(), CenterActivity.class);
                    startActivity(intent);
                    finish();
                }
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

    private void setPic(Uri selectedImageUri) {
        // Get the dimensions of the View
        int targetW = logo.getWidth();
        int targetH = logo.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        mSelectedImageUri = selectedImageUri;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
            logo.setImageBitmap(bitmap);
            mCurrentUserImageBitmap = bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}