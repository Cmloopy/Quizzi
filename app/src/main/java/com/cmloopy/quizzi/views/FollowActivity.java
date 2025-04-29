package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.FollowAdapter;
import com.cmloopy.quizzi.databinding.ActivityFollowBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowActivity extends AppCompatActivity {
    private ActivityFollowBinding binding;
    private FollowAdapter adapter;
    private final String[] tabTitles = {"Follower", "Following"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new FollowAdapter(this);
        binding.vpgFollowEr.setAdapter(adapter);
        new TabLayoutMediator(binding.tabFollowEr, binding.vpgFollowEr, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitles[position]);
            }
        }).attach();

    }
}