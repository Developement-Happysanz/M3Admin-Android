package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.happysanz.m3admin.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}