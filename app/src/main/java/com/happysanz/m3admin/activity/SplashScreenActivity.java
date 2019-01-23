package com.happysanz.m3admin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.database.SQLiteHelper;

public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    SQLiteHelper database;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        database = new SQLiteHelper(getApplicationContext());

        final int getStatus = database.appInfoCheck();
//        String GCMKey = PreferenceStorage.getGCM(getApplicationContext());
//        if (GCMKey.equalsIgnoreCase("")) {
//            String refreshedToken = FirebaseInstanceId.getInstance().getToken(); 
//            PreferenceStorage.saveGCM(getApplicationContext(), refreshedToken);
//        }

        if(!checkAutoDT(this)){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("Date and Time settings");

            // Setting Dialog Message
            alertDialog.setMessage("Automatic Date and Time is not enabled. Go to settings and enable to access application.");

            // On pressing the Settings button.
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                    startActivity(intent);
                }
            });

            // On pressing the cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

//                    if (getStatus == 1) {
//                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
//                        startActivity(i);
//                        finish();
//
//                    } else {
//                        Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
//                        startActivity(i);
//                        finish();
//                    }
                }
            }, SPLASH_TIME_OUT);
        }

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {

                *//*Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);*//*

         *//*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().clear().apply();*//*

                Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();

            } else {*/


//            }
//        }
    }
    public static boolean checkAutoDT(Context c){
        return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
    }
}

