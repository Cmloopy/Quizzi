package com.cmloopy.quizzi.views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.UI41FollowerAdapter;
import com.cmloopy.quizzi.models.UI41Follower;

import java.util.ArrayList;
import java.util.List;

public class UI41 extends AppCompatActivity {
    private Button btnFollowers, btnFollowing;
    private RecyclerView recyclerView;
    private UI41FollowerAdapter adapter;
    private List<UI41Follower> userList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_41);

        recyclerView = findViewById(R.id.ui41RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userList.add(new UI41Follower("Augustina Midgett", "@augustina_midgett", R.drawable.ic_15_top_authors_1, false));
        userList.add(new UI41Follower("Benny Spanbauer", "@benny_spanbauer", R.drawable.ic_15_top_authors_2, false));
        userList.add(new UI41Follower("Cyndy Lillibridge", "@cyndy_lillibridge", R.drawable.ic_15_top_authors_1, false));
        userList.add(new UI41Follower("Thad Eddings", "@thad_eddings", R.drawable.ic_15_top_authors_2, false));
        userList.add(new UI41Follower("Lauralee Quintero", "@lauralee_quintero", R.drawable.ic_15_top_authors_1, false));

        adapter = new UI41FollowerAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        btnFollowers = findViewById(R.id.btnFollowers);
        btnFollowing = findViewById(R.id.btnFollowing);

        // Mặc định chọn tab Followers trước
        setActiveButton(btnFollowers);
        setInactiveButton(btnFollowing);

        btnFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(btnFollowers);
                setInactiveButton(btnFollowing);
            }
        });

        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveButton(btnFollowing);
                setInactiveButton(btnFollowers);
            }
        });
    }

    private void setActiveButton(Button button) {
        button.setBackgroundResource(R.drawable.quizz_details_button_friends_background); // Nền màu tím
        button.setTextColor(Color.WHITE); // Chữ màu trắng
    }

    private void setInactiveButton(Button button) {
        button.setBackgroundResource(R.drawable.quizz_details_button_solo_background); // Nền màu trắng
        button.setTextColor(Color.parseColor("#7A48E3")); // Chữ màu tím
    }
}