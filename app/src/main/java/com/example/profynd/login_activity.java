package com.example.profynd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class login_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CheckedTextView frgt_pswd = findViewById(R.id.forgotpassword);
        TextView sign_up = findViewById(R.id.signup);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up = new Intent(login_activity.this , register_activity.class);
                startActivity(sign_up);
            }
        });
        frgt_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up = new Intent(login_activity.this , Forgot_Password_Activity.class);
                startActivity(sign_up);
            }
        });

}}
