package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.happysanz.m3admin.R;

public class AddPlanActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}