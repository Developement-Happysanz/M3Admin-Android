package com.happysanz.m3admin.activity.piamodule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.happysanz.m3admin.BuildConfig;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.AndroidMultiPartEntity;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.util.Log.d;

public class PiaProfileActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = "PiaProfileActivity";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private EditText piaName, PRN, piaAddress, piaEmail, piaPhone;
    private Button saveDetails;
    private ImageView profileImg;
    String localRes, res;


    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    long totalSize = 0;
    File image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pia_profile);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        initiateFields();
        getProfileData();
    }

    private void initiateFields() {
        piaName = findViewById(R.id.user_name);
        piaName.setText(PreferenceStorage.getPIAName(this));

        PRN = findViewById(R.id.prn);
        PRN.setText(PreferenceStorage.getPIAPRNNumber(this));
        PRN.setFocusable(false);
        PRN.setClickable(false);

        piaAddress = findViewById(R.id.address);
        piaAddress.setText(PreferenceStorage.getPIAAddress(this));

        piaEmail = findViewById(R.id.email);
        piaEmail.setText(PreferenceStorage.getPIAEmail(this));

        piaPhone = findViewById(R.id.phone);
        piaPhone.setText(PreferenceStorage.getPIAPhone(this));

        profileImg = findViewById(R.id.new_logo);
        profileImg.setOnClickListener(this);
        String url = PreferenceStorage.getUserPicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profileImg);
        }

        saveDetails = findViewById(R.id.save_user);
    }

    private void getProfileData() {
        res = "getData";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("1")) {
            localRes = "admin";
            id = PreferenceStorage.getPIAProfileId(this);
        } else {
            localRes = "pia";
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.USER_PROFILE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void sendProfileData() {
        res = "sendData";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);
            jsonObject.put(M3AdminConstants.PARAMS_PIA_ID, PreferenceStorage.getPIAProfileId(this));
            jsonObject.put(M3AdminConstants.KEY_PIA_PHONE, piaPhone.getText().toString());
            jsonObject.put(M3AdminConstants.KEY_PIA_ADDRESS, piaAddress.getText().toString());
            jsonObject.put(M3AdminConstants.KEY_PIA_NAME, piaName.getText().toString());
            jsonObject.put(M3AdminConstants.KEY_PIA_EMAIL, piaEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.UPDATE_PIA_USER_PROFILE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View view) {
        if (view == profileImg) {
            openImageIntent();
        } else if (view == saveDetails) {
            if (validateFields()){
                sendProfileData();
            }
        }
    }

    private boolean validateFields() {
        if (!M3Validator.checkNullString(this.piaName.getText().toString().trim())) {
            piaName.setError(getString(R.string.empty_entry));
            requestFocus(piaName);
            return false;
        } else if (!M3Validator.checkNullString(this.piaAddress.getText().toString().trim())) {
            piaAddress.setError(getString(R.string.empty_entry));
            requestFocus(piaAddress);
            return false;
        } else if (!M3Validator.checkNullString(this.piaName.getText().toString().trim())) {
            piaName.setError(getString(R.string.empty_entry));
            requestFocus(piaName);
            return false;
        } else if (!M3Validator.checkNullString(this.piaEmail.getText().toString().trim())) {
            piaEmail.setError(getString(R.string.empty_entry));
            requestFocus(piaEmail);
            return false;
        } else if (!M3Validator.checkNullString(this.piaPhone.getText().toString().trim())) {
            piaPhone.setError(getString(R.string.empty_entry));
            requestFocus(piaPhone);
            return false;
        }  else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            if (res.equalsIgnoreCase("getData")) {
                if (localRes.equalsIgnoreCase("pia")) {
                    try {
                        JSONArray getData = response.getJSONArray("userprofile");
                        savePIAProfile(getData.getJSONObject(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                PreferenceStorage.savePIAName(this, piaName.getText().toString());
                PreferenceStorage.savePIAAddress(this, piaAddress.getText().toString());
                PreferenceStorage.savePIAPhone(this, piaPhone.getText().toString());
                PreferenceStorage.savePIAEmail(this, piaEmail.getText().toString());
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePIAProfile(JSONObject piaProfile) {

        Log.d(TAG, "piaProfile dictionary" + piaProfile.toString());

        String piaProfileId = "";
        String piaPRNNumber = "";
        String piaName = "";
        String piaAddress = "";
        String piaPhone = "";
        String piaEmail = "";
        String piaProfilePic = "";

        try {

            if (piaProfile != null) {

                // PIA Preference - PIA PRN Number
                piaPRNNumber = piaProfile.getString("pia_unique_number");
                if ((piaPRNNumber != null) && !(piaPRNNumber.isEmpty()) && !piaPRNNumber.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAPRNNumber(this, piaPRNNumber);
                }

                // PIA Preference - PIA Name
                piaName = piaProfile.getString("pia_name");
                if ((piaName != null) && !(piaName.isEmpty()) && !piaName.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAName(this, piaName);
                }

                // PIA Preference - PIA Address
                piaAddress = piaProfile.getString("pia_address");
                if ((piaAddress != null) && !(piaAddress.isEmpty()) && !piaAddress.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAAddress(this, piaAddress);
                }

                // PIA Preference - PIA Phone
                piaPhone = piaProfile.getString("pia_phone");
                if ((piaPhone != null) && !(piaPhone.isEmpty()) && !piaPhone.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAPhone(this, piaPhone);
                }

                // PIA Preference - PIA Email
                piaEmail = piaProfile.getString("pia_email");
                if ((piaEmail != null) && !(piaEmail.isEmpty()) && !piaEmail.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAEmail(this, piaEmail);
                }

                // PIA Preference - PIA Pic
                piaProfilePic = piaProfile.getString("profile_pic");
                if ((piaProfilePic != null) && !(piaProfilePic.isEmpty()) && !piaProfilePic.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserPicture(this, piaProfilePic);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onError(String error) {

    }

    private void openImageIntent() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Photo", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PiaProfileActivity.this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
//                    openCamera();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Uri f = FileProvider.getUriForFile(PiaProfileActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, f);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    openImagesDocument();
                } else if (options[item].equals("Remove Photo")) {
                    PreferenceStorage.saveUserPicture(PiaProfileActivity.this, "");
                    profileImg.setBackground(ContextCompat.getDrawable(PiaProfileActivity.this, R.drawable.ic_profile));
                    mSelectedImageUri = Uri.parse("android.resource://com.palprotech.heylaapp/drawable/ic_default_profile");
                    mActualFilePath = mSelectedImageUri.getPath();
                    saveUserImage();
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
//                Uri uri = Uri.parse(mActualFilePath);
//                openCropActivity(uri, uri);
                final File file = new File(mActualFilePath);
                try {
                    InputStream ims = new FileInputStream(file);
                    profileImg.setImageBitmap(BitmapFactory.decodeStream(ims));
                } catch (FileNotFoundException e) {
                    return;
                }

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(PiaProfileActivity.this,
                        new String[]{mActualFilePath}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
//                                performCrop(uri);
                                Uri destinationUri = Uri.fromFile(file);  // 3
                                openCropActivity(uri, destinationUri);
                            }
                        });
            } else if (requestCode == 2) {
                Uri sourceUri = data.getData(); // 1
                File file = null; // 2
                try {
                    file = getImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4
            } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = UCrop.getOutput(data);
                    profileImg.setImageURI(uri);
//                    mActualFilePath = uri.getPath();
                    saveUserImage();
                }
            }
        }
    }

    private File getImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        mActualFilePath = image.getAbsolutePath();
        return image;
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".png",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mActualFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorPrimary));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(100, 100)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    private void saveUserImage() {

        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();
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
            httppost = new HttpPost(String.format(M3AdminConstants.BUILD_URL + M3AdminConstants.UPLOAD_USER_PIC + PreferenceStorage.getUserId(getApplicationContext()) +"/"));

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
                    entity.addPart("user_pic", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
//                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(getApplicationContext())));
//                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(ProfileActivity.this)));

                    totalSize = entity.getContentLength();
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

                            mUpdatedImageUrl = resp.getString("picture_url");
                            PreferenceStorage.saveUserPicture(getApplicationContext(), mUpdatedImageUrl);

                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                                Log.d(TAG, "Updated image succesfully");
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
                Toast.makeText(PiaProfileActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveUserPicture(PiaProfileActivity.this, mUpdatedImageUrl);
                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }

            Toast.makeText(getApplicationContext(), "User profile image successfully...", Toast.LENGTH_SHORT).show();
//            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}
