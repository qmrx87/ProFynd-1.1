package com.example.profynd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class login_activity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText mEmailInput,mPasswordInput;
    private CheckBox mRememberMeCheckbox;
    private Button mLoginButton;
    private LinearLayout mGoogleLoginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarlog);
        mEmailInput = findViewById(R.id.emailtext);
        mPasswordInput = findViewById(R.id.passwordtext);
        mRememberMeCheckbox = findViewById(R.id.rem_me);
        mLoginButton = findViewById(R.id.logginbtn);
        mGoogleLoginButton = findViewById(R.id.google_loging);
        progressBar.setVisibility(View.GONE);
        mLoginButton.setOnClickListener(this);
        mGoogleLoginButton.setOnClickListener(this);
}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logginbtn:
                signInWithEmail();
                break;
            case R.id.google_loging:
                signInWithGoogle();
                break;
        }
    }

    private void signInWithGoogle() {

    }
    private void rememberMe(boolean remember, String email, String password) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember", remember);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private boolean isRemembered() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("remember", false);
    }

    private String getRememberedEmail() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("email", "");
    }

    private String getRememberedPassword() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("password", "");
    }

    protected void onResume() {
        super.onResume();
        mRememberMeCheckbox.setChecked(isRemembered());
        if (isRemembered()) {
            mEmailInput.setText(getRememberedEmail());
            mPasswordInput.setText(getRememberedPassword());
        }
    }
    protected void onPause() {
        super.onPause();
        rememberMe(mRememberMeCheckbox.isChecked(), mEmailInput.getText().toString(), mPasswordInput.getText().toString());
    }
    private void signInWithEmail() {
        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            mEmailInput.setError("Email is required");
            mEmailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailInput.setError("Please enter a valid email");
            mEmailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordInput.setError("Password is required");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           Intent intent = new Intent(login_activity.this, BottomNavigationActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(login_activity.this, "Email or Password is incorrect , Please check again", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}
