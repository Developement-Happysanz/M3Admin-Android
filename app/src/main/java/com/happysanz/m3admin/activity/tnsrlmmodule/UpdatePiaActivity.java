package com.happysanz.m3admin.activity.tnsrlmmodule;

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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class UpdatePiaActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = PiaActivity.class.getName();

    Button addNewPia;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    String uniqueNumber, name, address, email, phone, resDa;
    EditText uNumber, piaName, piaAddress, piaEmail, piaPhone;
    private EditText etPiaStatus;
    private List<String> mStatusList = new ArrayList<String>();
    private ArrayAdapter<String> mStatusAdapter = null;
    private String piaID;
    private ImageView profileImg;


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
        setContentView(R.layout.activity_update_pia);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        piaID = getIntent().getStringExtra("eventObj");

        uNumber = findViewById(R.id.pia_id);
        piaName = findViewById(R.id.pia_name);
        piaAddress = findViewById(R.id.pia_address);
        piaEmail = findViewById(R.id.pia_mail);
        piaPhone = findViewById(R.id.pia_phone);

        getProfileData();

        profileImg = findViewById(R.id.new_logo);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        etPiaStatus = findViewById(R.id.pia_status);
        etPiaStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatusList();
            }
        });
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
        addNewPia = (Button) findViewById(R.id.save_pia);
        addNewPia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()){
                    resDa = "updateData";
                    uniqueNumber = uNumber.getText().toString();
                    name = piaName.getText().toString();
                    address = piaAddress.getText().toString();
                    email = piaEmail.getText().toString();
                    phone = piaPhone.getText().toString();

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(M3AdminConstants.PARAMS_PIA_ID, piaID);
                        jsonObject.put(M3AdminConstants.PARAMS_UNIQUE_NUMBER, uniqueNumber);
                        jsonObject.put(M3AdminConstants.PARAMS_NAME, name);
                        jsonObject.put(M3AdminConstants.PARAMS_ADDRESS, address);
                        jsonObject.put(M3AdminConstants.PARAMS_EMAIL, email);
                        jsonObject.put(M3AdminConstants.PARAMS_PHONE, phone);
                        jsonObject.put(M3AdminConstants.PARAMS_STATUS, etPiaStatus.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = M3AdminConstants.BUILD_URL + M3AdminConstants.UPDATE_PIA;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }

            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PiaActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
                        etPiaStatus.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void getProfileData() {
        resDa = "getData";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(M3AdminConstants.PARAMS_PIA_ID, piaID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.DETAILS_PIA;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
            if (resDa.equalsIgnoreCase("getData")) {

                try {
                    JSONArray data = response.getJSONArray("userList");

                    uNumber.setText(data.getJSONObject(0).getString("pia_unique_number"));
                    piaName.setText(data.getJSONObject(0).getString("pia_name"));
                    piaAddress.setText(data.getJSONObject(0).getString("pia_address"));
                    piaEmail.setText(data.getJSONObject(0).getString("pia_email"));
                    piaPhone.setText(data.getJSONObject(0).getString("pia_phone"));
                    etPiaStatus.setText(data.getJSONObject(0).getString("status"));
                    String url = data.getJSONObject(0).getString("profile_pic");
                    if (((url != null) && !(url.isEmpty()))) {
                        Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profileImg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Intent intent = new Intent(this, PiaActivity.class);
                Toast.makeText(UpdatePiaActivity.this, "Pia updated", Toast.LENGTH_SHORT).show();
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    private boolean validateFields() {

        if (!M3Validator.checkNullString(this.uNumber.getText().toString().trim())) {
            uNumber.setError(getString(R.string.err_entry));
            requestFocus(uNumber);
            return false;
        } else if (!M3Validator.checkNullString(this.piaName.getText().toString().trim())) {
            piaName.setError(getString(R.string.empty_entry));
            requestFocus(piaName);
            return false;
        }else if (!M3Validator.checkNullString(this.piaAddress.getText().toString().trim())) {
            piaAddress.setError(getString(R.string.empty_entry));
            requestFocus(piaAddress);
            return false;
        }else if (!M3Validator.checkNullString(this.piaEmail.getText().toString().trim())) {
            piaEmail.setError(getString(R.string.empty_entry));
            requestFocus(piaEmail);
            return false;
        }else if (!M3Validator.isEmailValid(this.piaEmail.getText().toString().trim())) {
            piaEmail.setError(getString(R.string.err_mail));
            requestFocus(piaEmail);
            return false;
        }else if (!M3Validator.checkMobileNumLength(this.piaPhone.getText().toString().trim())) {
            piaPhone.setError(getString(R.string.err_number));
            requestFocus(piaPhone);
            return false;
        }else if (!M3Validator.checkNullString(this.piaPhone.getText().toString().trim())) {
            piaPhone.setError(getString(R.string.empty_entry));
            requestFocus(piaPhone);
            return false;
        } else if (!M3Validator.checkUniqueNumLength(this.uNumber.getText().toString().trim())) {
            uNumber.setError(getString(R.string.err_min_uniq_length));
            requestFocus(uNumber);
            return false;
        }else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void openImageIntent() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Photo", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdatePiaActivity.this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
//                    openCamera();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Uri f = FileProvider.getUriForFile(UpdatePiaActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, f);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    openImagesDocument();
                } else if (options[item].equals("Remove Photo")) {
                    PreferenceStorage.saveUserPicture(UpdatePiaActivity.this, "");
                    profileImg.setBackground(ContextCompat.getDrawable(UpdatePiaActivity.this, R.drawable.ic_profile));
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
                MediaScannerConnection.scanFile(UpdatePiaActivity.this,
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

        new UpdatePiaActivity.UploadFileToServer().execute();
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
            httppost = new HttpPost(String.format(M3AdminConstants.BUILD_URL + M3AdminConstants.UPLOAD_USER_PIC + PreferenceStorage.getPIAProfileId(getApplicationContext()) +"/"));

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
//                            PreferenceStorage.saveUserPicture(getApplicationContext(), mUpdatedImageUrl);

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
                Toast.makeText(UpdatePiaActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveUserPicture(UpdatePiaActivity.this, mUpdatedImageUrl);
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