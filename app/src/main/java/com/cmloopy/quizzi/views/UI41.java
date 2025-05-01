package com.cmloopy.quizzi.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.UI41AdapterFriends;
import com.cmloopy.quizzi.adapter.UI41SelectFriendsAdapter;
import com.cmloopy.quizzi.models.UI41Friends;

import java.util.ArrayList;
import java.util.List;

public class UI41 extends AppCompatActivity {
    private RecyclerView recyclerFriends, recyclerSelectedFriends;
    private UI41AdapterFriends friendAdapter;
    private UI41SelectFriendsAdapter selectedFriendAdapter;
    private List<UI41Friends> friendList, selectedFriends;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_41);

        recyclerFriends = findViewById(R.id.UI41recyclerFriends);
        recyclerSelectedFriends = findViewById(R.id.UI41recyclerSelectedFriends);

        friendList = new ArrayList<>();
        selectedFriends = new ArrayList<>();

        // Thêm danh sách bạn bè (Demo)
        friendList.add(new UI41Friends("Pedro Dorrance", R.drawable.ic_15_top_authors_1));
        friendList.add(new UI41Friends("Tyra Shelburne", R.drawable.ic_15_top_authors_1));
        friendList.add(new UI41Friends("Clinton Mcclure", R.drawable.ic_15_top_authors_1));
        friendList.add(new UI41Friends("Daryl Kulikowski", R.drawable.ic_15_top_authors_1));
        friendList.add(new UI41Friends("Freida Ordonez", R.drawable.ic_15_top_authors_1));

        friendAdapter = new UI41AdapterFriends(friendList, position -> {
            UI41Friends friend = friendList.get(position);
            if (friend.isSelected()) {
                selectedFriends.add(friend);
            } else {
                selectedFriends.remove(friend);
            }
            selectedFriendAdapter.notifyDataSetChanged();
        });

        selectedFriendAdapter = new UI41SelectFriendsAdapter(selectedFriends);

        recyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerFriends.setAdapter(friendAdapter);

        recyclerSelectedFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerSelectedFriends.setAdapter(selectedFriendAdapter);
    }
}
