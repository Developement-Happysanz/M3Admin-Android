package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.utils.M3Validator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class CenterDetailActivity extends AppCompatActivity {

    Centers centers;
    ImageView centerBanner, img1, img2, img3;
    TextView centerTitle, CenterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_detail);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        centers = (Centers) getIntent().getSerializableExtra("cent");
        centerBanner = findViewById(R.id.center_logo);
        img1 = findViewById(R.id.img_one);
        img2 = findViewById(R.id.img_two);
        img3 = findViewById(R.id.img_three);
        CenterInfo = findViewById(R.id.center_detail);
        centerTitle = findViewById(R.id.center_title);
        centerTitle.setText(centers.getCenter_name());
        CenterInfo.setText(centers.getcenter_info());
        if (M3Validator.checkNullString(centers.getcenter_banner())) {
        } else {
            centerBanner.setImageResource(R.drawable.ic_splash_screen_logo);
        }

    }

}