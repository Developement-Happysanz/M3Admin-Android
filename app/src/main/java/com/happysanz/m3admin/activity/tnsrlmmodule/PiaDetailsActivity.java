package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.piamodule.CenterActivity;
import com.happysanz.m3admin.activity.piamodule.MobilizerActivity;
import com.happysanz.m3admin.activity.piamodule.StudentsActivity;
import com.happysanz.m3admin.activity.piamodule.TaskActivity;
import com.happysanz.m3admin.activity.piamodule.TradeActivity;

public class PiaDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mobilizerDash, studentDash, centerInfoDash, tradeDash, taskDash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnsrlm_pia_dash);

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
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {

        if (view == mobilizerDash){
            Intent intent = new Intent(getApplicationContext(), MobilizerActivity.class);
            startActivity(intent);
        }
        if (view == studentDash){
            Intent intent = new Intent(getApplicationContext(), StudentsActivity.class);
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
        if (view == taskDash){
            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
            startActivity(intent);
        }

    }
}