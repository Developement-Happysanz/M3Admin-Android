package com.happysanz.m3admin.activity.piamodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.loginmodule.ChangePasswordActivity;
import com.happysanz.m3admin.activity.loginmodule.SplashScreenActivity;
import com.happysanz.m3admin.bean.pia.ProjectPeriodList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class PiaDashboard extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = "PiaDashboardFragment";
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    Context context;
    private RelativeLayout prospect, user, addPlan, task, tracking, controlPanel, expView, dashBoard;
    private TextView scheme, center, project, trade, batch, tradeAndBatch, time, userName;
    private LinearLayout subMenu, mobilizerDash, studentDash, centerInfoDash, tradeDash, taskDash, headerView;
    private ImageView profileImg;
    private Boolean visib = false;
    boolean doubleBackToExitPressedOnce = false;
    private TextView profile, aboutUs, changePassword, logout;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private String localRes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_pia);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
//        getProfileData();
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initializeNavigationDrawer();
        context = getApplicationContext();
        subMenu = (LinearLayout) findViewById(R.id.sub_menu);
        expView = (RelativeLayout) findViewById(R.id.exp_view);
        expView.setOnClickListener(this);
        prospect = (RelativeLayout) findViewById(R.id.pros_layout);
        prospect.setOnClickListener(this);
        user = (RelativeLayout) findViewById(R.id.user_layout);
        user.setOnClickListener(this);
        addPlan = (RelativeLayout) findViewById(R.id.add_plan_layout);
        addPlan.setOnClickListener(this);
        task = (RelativeLayout) findViewById(R.id.task_layout_sidemenu);
        task.setOnClickListener(this);
        tracking = (RelativeLayout) findViewById(R.id.tracking_layout);
        tracking.setOnClickListener(this);
        controlPanel = (RelativeLayout) findViewById(R.id.control_panel_layout);
        controlPanel.setOnClickListener(this);

        profileImg = findViewById(R.id.img_profile_image);
        String url = PreferenceStorage.getUserPicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profileImg);
        }

        userName = findViewById(R.id.user_profile_name);
        userName.setText(PreferenceStorage.getPIAName(this));

        scheme = (TextView) findViewById(R.id.txt_scheme);
        scheme.setOnClickListener(this);
//        project = (TextView) findViewById(R.id.txt_project);
//        project.setOnClickListener(this);
        center = (TextView) findViewById(R.id.txt_center);
        center.setOnClickListener(this);
        trade = (TextView) findViewById(R.id.txt_trade);
        trade.setOnClickListener(this);
        batch = (TextView) findViewById(R.id.txt_batch);
        batch.setOnClickListener(this);
        tradeAndBatch = (TextView) findViewById(R.id.txt_trade_batch);
        tradeAndBatch.setOnClickListener(this);
        time = (TextView) findViewById(R.id.txt_time);
        time.setOnClickListener(this);

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

        mobilizerDash = findViewById(R.id.mobilizer_layout);
        mobilizerDash.setOnClickListener(this);
        studentDash = findViewById(R.id.student_layout);
        studentDash.setOnClickListener(this);
        centerInfoDash = findViewById(R.id.center_layout);
        centerInfoDash.setOnClickListener(this);
        tradeDash = findViewById(R.id.trade_layout);
        tradeDash.setOnClickListener(this);
        taskDash = findViewById(R.id.task_layout);
        taskDash.setOnClickListener(this);
        headerView = findViewById(R.id.headet);
        headerView.setOnClickListener(this);
    }

    private void getProfileData() {
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

    public void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().commit();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(this, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        this.finish();

//        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
//        alertDialogBuilder.setTitle("Logout");
//        alertDialogBuilder.setMessage("Do you really want to logout?");
//        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                sharedPreferences.edit().clear().commit();
////        TwitterUtil.getInstance().resetTwitterRequestToken();
//
//                Intent homeIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
//                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(homeIntent);
//                finish();
//            }
//        });
//        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        alertDialogBuilder.show();

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
        if (view == expView){
            visib = !visib;
            if (visib) {
                subMenu.setVisibility(View.VISIBLE);
            } else {
                subMenu.setVisibility(View.GONE);
            }
        }
        if (view == prospect){
            Intent intent = new Intent(getApplicationContext(), ProspectsActivity.class);
            startActivity(intent);
        }
        if (view == user){
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
        }
        if (view == addPlan){
            Intent intent = new Intent(getApplicationContext(), ProjectPlanActivity.class);
            startActivity(intent);
        }
        if (view == task){
            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
            startActivity(intent);
        }
        if (view == tracking){
            Intent intent = new Intent(getApplicationContext(), TrackingUserSelectionActivity.class);
            startActivity(intent);
        }
        if (view == controlPanel){
            Intent intent = new Intent(getApplicationContext(), ControlPanelActivity.class);
            startActivity(intent);
        }
        if (view == scheme){
            Intent intent = new Intent(getApplicationContext(), SchemeActivity.class);
            startActivity(intent);
        }
        if (view == center){
            Intent intent = new Intent(getApplicationContext(), CenterActivity.class);
            startActivity(intent);
        }
//        if (view == project){
//            Intent intent = new Intent(getApplicationContext(), ProjectPlanActivity.class);
//            startActivity(intent);
//        }
        if (view == trade){
            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
            startActivity(intent);
        }
        if (view == batch){
            Intent intent = new Intent(getApplicationContext(), BatchActivity.class);
            startActivity(intent);
        }
        if (view == tradeAndBatch){
            Intent intent = new Intent(getApplicationContext(), TradeAndBatchActivity.class);
            startActivity(intent);
        }
        if (view == time){
            Intent intent = new Intent(getApplicationContext(), ProjectPeriodActivity.class);
            startActivity(intent);
        }
        if (view == mobilizerDash){
            Intent intent = new Intent(getApplicationContext(), MobilizerActivity.class);
            startActivity(intent);
        }
        if (view == studentDash){
            Intent intent = new Intent(getApplicationContext(), ProspectsActivity.class);
            startActivity(intent);
        }
        if (view == centerInfoDash){
            Intent intent = new Intent(getApplicationContext(), CenterActivity.class);
            startActivity(intent);
        }
        if (view == tradeDash){
            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
            startActivity(intent);
        }
        if (view == profile){
            Intent intent = new Intent(getApplicationContext(), PiaProfileActivity.class);
            startActivity(intent);
        }
        if (view == aboutUs){
            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
            startActivity(intent);
        }
        if (view == changePassword){
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
        }
        if (view == taskDash){
            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
            startActivity(intent);
        } if (view == logout){
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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            if (localRes.equalsIgnoreCase("pia")) {
                try {
                    JSONArray getData = response.getJSONArray("userprofile");
                    savePIAProfile(getData.getJSONObject(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}