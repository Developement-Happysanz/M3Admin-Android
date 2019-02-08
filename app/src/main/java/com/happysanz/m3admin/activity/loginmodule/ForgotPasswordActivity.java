package com.happysanz.m3admin.activity.loginmodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.happysanz.m3admin.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView txUsername;
    private Button btSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txUsername = (TextView)findViewById(R.id.txtUsername);

    }
}
