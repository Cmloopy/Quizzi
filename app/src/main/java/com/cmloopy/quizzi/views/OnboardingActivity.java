package com.cmloopy.quizzi.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.OnboardingAdapter;
import com.cmloopy.quizzi.models.OnboardingItem;

// OnboardingActivity.java
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutDots;
    private Button btnGetStarted;
    private Button btnAlreadyAccount;
    private ViewPager2 onboardingViewPager;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        layoutDots = findViewById(R.id.layoutDots);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnAlreadyAccount = findViewById(R.id.btnAlreadyAccount);
        onboardingViewPager = findViewById(R.id.onboardingViewPager);

        setupOnboardingItems();

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

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnboardingActivity.this, CreateAccountStep1Activity.class));
            }
        });

        btnAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnboardingActivity.this, SignInForm.class));
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem itemCreate = new OnboardingItem(
                R.drawable.img_02, // Make sure to add these drawables to your project
                "Create, share and play quizzes whenever and wherever you want",
            ""
//                "Create your own quizzes and share them with friends"
        );

        OnboardingItem itemFind = new OnboardingItem(
                R.drawable.img_03,
                "Find fun and interesting quizzes to boost up your knowledge",
          ""
//                "Discover quizzes on topics you love and learn new things"
        );

        OnboardingItem itemPlay = new OnboardingItem(
                R.drawable.img_04,
                "Play and take quiz challenges together with your friends",
          ""
//                "Challenge your friends and have fun learning together"
        );

        onboardingItems.add(itemCreate);
        onboardingItems.add(itemFind);
        onboardingItems.add(itemPlay);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupOnboardingIndicators() {
        dots = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.ui_onboarding_dot_inactive));
            dots[i].setLayoutParams(params);
            layoutDots.addView(dots[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(
                        this, R.drawable.ui_onboarding_dot_active));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(
                        this, R.drawable.ui_onboarding_dot_inactive));
            }
        }
    }
}