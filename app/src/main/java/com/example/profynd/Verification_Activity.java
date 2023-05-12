package com.example.profynd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Verification_Activity extends AppCompatActivity {

    ImageView backBtn;
    LottieAnimationView verificationAnimation;
    Button continueBtn;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification2);

        /*backBtn = findViewById(R.id.verificationBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

         */
        verificationAnimation = findViewById(R.id.verificationAnimation);
        user = FirebaseAuth.getInstance().getCurrentUser();
        continueBtn = findViewById(R.id.continueBtn);


        verificationAnimation.playAnimation();
        Verification();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (user.isEmailVerified()) {
                            startActivity(new Intent(getApplicationContext(), OnboardingScreensActivity.class));
                            finish();
                        } else
                            Toast.makeText(Verification_Activity.this, "Please verify your account and try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
            private void Verification () {

                user = FirebaseAuth.getInstance().getCurrentUser();
                boolean b = false;
                while (!b) {
                    if (user != null) {
                        user.sendEmailVerification();
                        b = true;
                    }
                }
            }
        }