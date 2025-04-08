package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.UI42PlayerAdapter;
import com.cmloopy.quizzi.models.UI42Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UI42 extends AppCompatActivity {
    RecyclerView recyclerPlayers;
    UI42PlayerAdapter adapter;
    List<UI42Player> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_42);

        recyclerPlayers = findViewById(R.id.UI42recyclerPlayers);

        // Tạo danh sách giả lập người chơi
        playerList = new ArrayList<>();
        playerList.add(new UI42Player("Andrew", R.drawable.ic_15_top_authors_1));
        playerList.add(new UI42Player("Clinton", R.drawable.ic_15_top_authors_2));
        playerList.add(new UI42Player("Tyra", R.drawable.img_1));
        playerList.add(new UI42Player("Pedro", R.drawable.ic_15_top_authors_4));
        playerList.add(new UI42Player("Leif", R.drawable.ic_15_top_authors_5));
        playerList.add(new UI42Player("Freida", R.drawable.ic_15_top_authors_6));

        // Xáo trộn danh sách ngẫu nhiên
        Collections.shuffle(playerList);

        // Áp dụng Adapter
        adapter = new UI42PlayerAdapter(this, playerList);
        recyclerPlayers.setAdapter(adapter);

        // Sử dụng GridLayoutManager với số cột thay đổi ngẫu nhiên
        int columnCount = new Random().nextInt(3) + 2; // 2 - 4 cột
        recyclerPlayers.setLayoutManager(new GridLayoutManager(this, columnCount));
    }
}
