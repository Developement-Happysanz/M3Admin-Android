package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    //    private CenterPhotosData centerPhotosData;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
//        centerPhotosData = (CenterPhotosData) getIntent().getSerializableExtra("eventObj");
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);
        mImageView = findViewById(R.id.zoom_image);

        String url = "";
        url = getIntent().getStringExtra("eventObj");


        if (M3Validator.checkNullString(url)) {
            Picasso.get().load(url).into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.ic_profile);
        }

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f,
                    Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

}
