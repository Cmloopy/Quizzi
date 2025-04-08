package com.cmloopy.quizzi.views;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.UI65ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class UI65 extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ConstraintLayout rootLayout;
    private final String[] tabTitles = new String[]{"Enter PIN", "Scan QR Code"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_65_john_game);


        rootLayout = findViewById(R.id.UI65JoinGameLayout);
        tabLayout = findViewById(R.id.UI65tabLayout);
        viewPager = findViewById(R.id.viewPager);

        UI65ViewPagerAdapter adapter = new UI65ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Đổi màu nền dựa theo tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rootLayout.setBackgroundResource(R.drawable.ui_65_bg_gradient);
                    tabLayout.setBackgroundResource(R.drawable.ui_65_bg_gradient);
                } else {
                    rootLayout.setBackgroundColor(Color.BLACK);
                    tabLayout.setBackgroundColor(Color.BLACK);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Set mặc định khi load lần đầu (Enter PIN)
        rootLayout.setBackgroundResource(R.drawable.ui_65_bg_gradient);
        tabLayout.setBackgroundColor(Color.parseColor("#7B1FA2"));

    }
}
