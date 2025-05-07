package com.cmloopy.quizzi.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.DetailTopCollectionAdapter;
import com.cmloopy.quizzi.models.DetailTopCollectionItem;

import java.util.ArrayList;
import java.util.List;

public class DetailTopCollections extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DetailTopCollectionAdapter quizAdapter;
    private List<DetailTopCollectionItem> quizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_collections);

        recyclerView = findViewById(R.id.detailCollectionRecyclerQuiz);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        quizList = new ArrayList<>();
        quizList.add(new DetailTopCollectionItem(R.drawable.img_02, "Re-Train Your Brain", "Thad Eddings", "2 weeks ago", "2.6K plays"));
        quizList.add(new DetailTopCollectionItem(R.drawable.img_02, "Book is a Window", "Sarojetta Ordonez", "2 months ago", "5.1K plays"));
        quizList.add(new DetailTopCollectionItem(R.drawable.img_02, "Back to School Quiz", "Albruna Schuster", "2 years ago", "16.4K plays"));

        quizAdapter = new DetailTopCollectionAdapter(quizList);
        recyclerView.setAdapter(quizAdapter);
    }
}
