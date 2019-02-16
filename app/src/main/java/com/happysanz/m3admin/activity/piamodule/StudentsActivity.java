package com.happysanz.m3admin.activity.piamodule;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.StudentFragmentAdapter;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;

import org.json.JSONObject;

public class StudentsActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setText("All Prospects"));
        tabLayout.addTab(tabLayout.newTab().setText("Rejected"));
        tabLayout.addTab(tabLayout.newTab().setText("Confirmed"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        final StudentFragmentAdapter adapter = new StudentFragmentAdapter
                (getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}