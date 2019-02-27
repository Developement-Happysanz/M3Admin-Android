package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.CenterPhotosData;
import com.happysanz.m3admin.utils.M3Validator;
import com.squareup.picasso.Picasso;

/**
 * Created by Admin on 18-01-2018.
 */

public class ZoomImageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack, ivZoomImage;
    private CenterPhotosData centerPhotosData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        centerPhotosData = (CenterPhotosData) getIntent().getSerializableExtra("eventObj");
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);
        ivZoomImage = findViewById(R.id.zoom_image);

        if (M3Validator.checkNullString(centerPhotosData.getCenterPhotos())) {
            Picasso.get().load(centerPhotosData.getCenterPhotos()).into(ivZoomImage);
        } else {
            ivZoomImage.setImageResource(R.drawable.ic_profile);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }
}
