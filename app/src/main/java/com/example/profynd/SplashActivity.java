package com.example.profynd;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.profynd.MainActivity;
import com.example.profynd.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private Animation logoAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize logoImageView and load the logo image
        logoImageView = findViewById(R.id.imageView6);
        logoImageView.setImageResource(R.drawable.logo);

        // Initialize logoAnimation with the desired animation
        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);

        // Start the animation on logoImageView
        logoImageView.startAnimation(logoAnimation);

        // Wait for the animation to finish before moving to the next activity
        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
