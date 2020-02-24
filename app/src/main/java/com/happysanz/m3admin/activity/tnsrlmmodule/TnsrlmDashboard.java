package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.loginmodule.ChangePasswordActivity;
import com.happysanz.m3admin.activity.loginmodule.SplashScreenActivity;
import com.happysanz.m3admin.activity.piamodule.AboutUsActivity;
import com.happysanz.m3admin.activity.piamodule.SchemeDetailActivity;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.CommonUtils;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class TnsrlmDashboard extends AppCompatActivity  implements IServiceListener, DialogClickListener, View.OnClickListener {
    private static final String TAG = TnsrlmDashboard.class.getName();
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    Context context;
    RelativeLayout pia, mobilizationPlan, tnsrlmStaff, piaScheme, dashBoard;
    LinearLayout piaTnsrlm, centerTnsrlm, mobilizerTnsrlm, studentsTnsrlm, graph;
    TextView piaCount, centerCount, mobilizerCount, studentsCount;
    Boolean visib = false;
    boolean doubleBackToExitPressedOnce = false;
    TextView userName;
    private TextView profile, aboutUs, changePassword, logout;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_tnsrlm);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        callGetClassTestService();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initializeNavigationDrawer();
        context = getApplicationContext();
        pia = (RelativeLayout) findViewById(R.id.pia_layout);
        pia.setOnClickListener(this);
        mobilizationPlan = (RelativeLayout) findViewById(R.id.mobilization_layout);
        mobilizationPlan.setOnClickListener(this);
        tnsrlmStaff = (RelativeLayout) findViewById(R.id.tnsrlm_layout);
        tnsrlmStaff.setOnClickListener(this);
        piaScheme = (RelativeLayout) findViewById(R.id.scheme_layout);
        piaScheme.setOnClickListener(this);
        profile = findViewById(R.id.user_profile);
        profile.setOnClickListener(this);
        dashBoard = (RelativeLayout) findViewById(R.id.dash_layout);
        dashBoard.setOnClickListener(this);

        profile = (TextView) findViewById(R.id.user_profile);
        profile.setOnClickListener(this);
        aboutUs = (TextView) findViewById(R.id.about_us);
        aboutUs.setOnClickListener(this);
        changePassword = (TextView) findViewById(R.id.change_password);
        changePassword.setOnClickListener(this);
        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(this);

        profileImg = findViewById(R.id.img_profile_image);
        profileImg.setOnClickListener(this);
        String url = PreferenceStorage.getUserPicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profileImg);
        }

        piaTnsrlm = findViewById(R.id.tnsrlm_pia_layout);
        piaTnsrlm.setOnClickListener(this);
        centerTnsrlm = findViewById(R.id.tnsrlm_center_layout);
        centerTnsrlm.setOnClickListener(this);
        mobilizerTnsrlm = findViewById(R.id.tnsrlm_mobilizer_layout);
        mobilizerTnsrlm.setOnClickListener(this);
        studentsTnsrlm = findViewById(R.id.tnsrlm_student_layout);
        studentsTnsrlm.setOnClickListener(this);

        piaCount = findViewById(R.id.pia_count);
        centerCount = findViewById(R.id.center_count);
        mobilizerCount = findViewById(R.id.mobiliser_count);
        studentsCount = findViewById(R.id.student_count);
//        piaCount.setText(PreferenceStorage.getPIACount(this));
//        centerCount.setText(PreferenceStorage.getcenterCount(this));
//        mobilizerCount.setText(PreferenceStorage.getmobCount(this));
//        studentsCount.setText(PreferenceStorage.getstudentCount(this));

        graph = findViewById(R.id.graph_layout);
        graph.setOnClickListener(this);


        userName = findViewById(R.id.user_profile_name);
        userName.setText(PreferenceStorage.getUserName(this));

        PreferenceStorage.saveTnsrlmCheck(this,true);

    }

    public void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().commit();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(this, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    private void initializeNavigationDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {
//                String userProfileName = PreferenceStorage.getName(getApplicationContext());
//                String url = PreferenceStorage.getUserPicture(ParentDashBoardActivity.this);
//
//                Log.d(TAG, "user name value" + userProfileName);
//                if ((userProfileName != null) && !userProfileName.isEmpty()) {
//                    String[] splitStr = userProfileName.split("\\s+");
//                    navUserProfileName.setText("Hi, " + splitStr[0]);
//                }
//
//                if (((url != null) && !(url.isEmpty())) && !(url.equalsIgnoreCase(mCurrentUserProfileUrl))) {
//                    Log.d(TAG, "image url is " + url);
//                    mCurrentUserProfileUrl = url;
//                    Picasso.with(ParentDashBoardActivity.this).load(url).noPlaceholder().error(R.drawable.ab_logo).into(imgNavProfileImage);
//                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // enable ActionBar app icon to behave as action to toggle nav drawer
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        if (view == pia){
            Intent intent = new Intent(getApplicationContext(), PiaActivity.class);
            startActivity(intent);
        }
        if (view == mobilizationPlan){
            Intent intent = new Intent(getApplicationContext(), MobilizationPlanActivity.class);
            startActivity(intent);
        }
        if (view == piaScheme){
            Intent intent = new Intent(getApplicationContext(), PiaSchemeActivity.class);
            startActivity(intent);
        }
        if (view == tnsrlmStaff){
            Intent intent = new Intent(getApplicationContext(), TnsrlmStaffActivity.class);
            startActivity(intent);
        }
        if (view == profile){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        if (view == aboutUs){
            Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
            startActivity(intent);
        }
        if (view == changePassword){
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
        }
        if (view == dashBoard){
            Intent intent = new Intent(getApplicationContext(), TnsrlmDashboard.class);
            startActivity(intent);
        }
        if (view == piaTnsrlm){
            Intent intent = new Intent(getApplicationContext(), PiaActivity.class);
            startActivity(intent);
        }
        if (view == centerTnsrlm){
            Intent intent = new Intent(getApplicationContext(), PiaCenterActivity.class);
            startActivity(intent);
        }
        if (view == mobilizerTnsrlm){
            Intent intent = new Intent(getApplicationContext(), PiaMobilizerActivity.class);
            startActivity(intent);
        }
        if (view == studentsTnsrlm){
            Intent intent = new Intent(getApplicationContext(), PiaProspectsActivity.class);
            startActivity(intent);
        }
        if (view == graph){
            Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
            startActivity(intent);
        }
        if (view == logout){
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Logout");
            alertDialogBuilder.setMessage("Do you really want to logout?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    sharedPreferences.edit().clear().commit();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

                    Intent homeIntent = new Intent(context, SplashScreenActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    finish();
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
            try {
                saveTNSRLMProfile(response.getJSONObject("dashboardData"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    private void saveTNSRLMProfile(JSONObject TNSRLMProfile) {

        Log.d(TAG, "TNSRLMProfile dictionary" + TNSRLMProfile.toString());

        String piaCount1 = "";
        String mobCount1 = "";
        String centerCount1 = "";
        String studentCount1 = "";

        try {

            if (TNSRLMProfile != null) {

//                // PIA Preference - PIA profile Id
                piaCount1 = TNSRLMProfile.getString("pia_count");
                if ((piaCount1 != null) && !(piaCount1.isEmpty()) && !piaCount1.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIACount(this, piaCount1);
                }

                // PIA Preference - PIA profile Id
                mobCount1 = TNSRLMProfile.getString("mobilizer_count");
                if ((mobCount1 != null) && !(mobCount1.isEmpty()) && !mobCount1.equalsIgnoreCase("null")) {
                    PreferenceStorage.savemobCount(this, mobCount1);
                }

                // PIA Preference - PIA PRN Number
                centerCount1 = TNSRLMProfile.getString("center_count");
                if ((centerCount1 != null) && !(centerCount1.isEmpty()) && !centerCount1.equalsIgnoreCase("null")) {
                    PreferenceStorage.savecenterCount(this, centerCount1);
                }

                // PIA Preference - PIA Name
                studentCount1 = TNSRLMProfile.getString("student_count");
                if ((studentCount1 != null) && !(studentCount1.isEmpty()) && !studentCount1.equalsIgnoreCase("null")) {
                    PreferenceStorage.savestudentCount(this, studentCount1);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        piaCount.setText(piaCount1);
        centerCount.setText(centerCount1);
        mobilizerCount.setText(mobCount1);
        studentsCount.setText(studentCount1);
    }

    @Override
    public void onError(String error) {

    }

    public void callGetClassTestService() {

        if (CommonUtils.isNetworkAvailable(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(M3AdminConstants.KEY_USER_ID, "1");

            } catch (JSONException e) {
                e.printStackTrace();
            }

//                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = M3AdminConstants.BUILD_URL + M3AdminConstants.ADMIN_DASHBOARD;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }
}