package com.example.profynd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.profynd.adapter.OnboardingAdapter;
import com.example.profynd.models.OnboardingItemModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingScreensActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
  private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        next=findViewById(R.id.buttonOnboardingAction);
        setupOnboardingItems();

        ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(getApplicationContext(), FeedActivity.class));
                    finish();
                }
            }
        });

    }

    private void setupOnboardingItems() {

        List<OnboardingItemModel> onboardingItemModels = new ArrayList<OnboardingItemModel>();

        OnboardingItemModel item1 = new OnboardingItemModel();
        item1.setTitle("Discover Expert Tutors");
        item1.setDescription("Find the perfect tutor to help you achieve your goals with ProFynd");
        item1.setImage(R.drawable.welcomepage1);

        OnboardingItemModel item2 = new OnboardingItemModel();
        item2.setTitle("Study any subject");
        item2.setDescription("Whether you need help with math, science, or language arts, ProFynd has the perfect tutor for you");
        item2.setImage(R.drawable.welcomepage2);

        OnboardingItemModel item3 = new OnboardingItemModel();
        item3.setTitle("Personalized Learning Experience");
        item3.setDescription("Experience personalized learning like never before with ProFynd. Our expert tutors work with you one-on-one to provide customized support and guidance tailored to your unique learning style");
        item3.setImage(R.drawable.welcomepage3);

        onboardingItemModels.add(item1);
        onboardingItemModels.add(item2);
        onboardingItemModels.add(item3);

        onboardingAdapter = new OnboardingAdapter(onboardingItemModels);

    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        LayoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(LayoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == onboardingAdapter.getItemCount() - 1) {
            next.setText("Start");
        } else {
            next.setText("Next");
        }
    }
}

