package com.happysanz.m3admin.activity.piamodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.tnsrlmmodule.AddNewPiaActivity;
import com.happysanz.m3admin.adapter.StudentFragmentAdapter;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONObject;

public class ProspectsActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ImageView addNewPia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        addNewPia = (ImageView) findViewById(R.id.add_prospect);
        addNewPia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectAddCandidateMethod.class);
                startActivity(intent);
                finish();
            }
        });
        if (PreferenceStorage.getTnsrlmCheck(this)){
            addNewPia.setVisibility(View.GONE);
        }
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setText("All Candidates"));
        tabLayout.addTab(tabLayout.newTab().setText("Qualified"));
        tabLayout.addTab(tabLayout.newTab().setText("Unqualified"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        final StudentFragmentAdapter adapter = new StudentFragmentAdapter
                (getSupportFragmentManager());


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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