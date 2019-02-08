package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.happysanz.m3admin.R;

public class PiaActivity extends AppCompatActivity {

    private static final String TAG = PiaActivity.class.getName();

    ImageView addNewPia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pia);
        addNewPia = (ImageView) findViewById(R.id.add_pia);
        addNewPia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNewPiaActivity.class);
                startActivity(intent);
            }
        });

    }
}