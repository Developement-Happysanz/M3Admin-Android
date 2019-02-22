package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.M3Validator;
import com.happysanz.m3admin.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class CenterDetailActivity extends AppCompatActivity implements View.OnClickListener {

    Centers centers;
    ImageView centerBanner;
    TextView centerTitle, CenterInfo;
    LinearLayout galleryLayout, videoLayout;


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
        centerBanner = findViewById(R.id.center_logo12331);

        CenterInfo = findViewById(R.id.center_detail);
        galleryLayout = findViewById(R.id.gallery_layout);
        galleryLayout.setOnClickListener(this);
        videoLayout = findViewById(R.id.video_gallery_layout);
        videoLayout.setOnClickListener(this);
        centerTitle = findViewById(R.id.center_title);
        centerTitle.setText(centers.getCenter_name());
        CenterInfo.setText(centers.getcenter_info());
        String url = "";
        url = M3AdminConstants.BUILD_URL + M3AdminConstants.ASSETS_URL_LOGO + centers.getcenter_banner();
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).into(centerBanner);
        } else {
            centerBanner.setImageResource(R.drawable.ic_profile);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == galleryLayout) {
            Intent intent = new Intent(this, PhotoGalleryActivity.class);
            intent.putExtra("cent", centers);
            startActivity(intent);
        } else if (view == videoLayout) {
            Intent intent = new Intent(this, VideoGalleryActivity.class);
            PreferenceStorage.saveCenterId(this, centers.getid());
            startActivity(intent);
        }
    }
}