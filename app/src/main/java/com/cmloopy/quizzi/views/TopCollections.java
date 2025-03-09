package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.models.TopCollectionsCategory;

import java.util.ArrayList;
import java.util.List;

public class TopCollections extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TopCollectionsCategoryAdapter adapter;
    private List<TopCollectionsCategory> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_collections);

        recyclerView = findViewById(R.id.TopCollectionsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryList = new ArrayList<>();
        categoryList.add(new TopCollectionsCategory("Education", R.drawable.img_19_education));
        categoryList.add(new TopCollectionsCategory("Games", R.drawable.img_19_game));
        categoryList.add(new TopCollectionsCategory("Business", R.drawable.img_19_business));
        categoryList.add(new TopCollectionsCategory("Entertainment", R.drawable.img_19_entertainment));
        categoryList.add(new TopCollectionsCategory("Art", R.drawable.img_19_art));
        categoryList.add(new TopCollectionsCategory("Plants", R.drawable.img_19_plan));
        categoryList.add(new TopCollectionsCategory("Finance", R.drawable.img_19_finance));
        categoryList.add(new TopCollectionsCategory("Food & Drink", R.drawable.img_19_food_and_drink));
        categoryList.add(new TopCollectionsCategory("Health", R.drawable.img_19_health));
        categoryList.add(new TopCollectionsCategory("Kids", R.drawable.img_19_kids));
        categoryList.add(new TopCollectionsCategory("Sports", R.drawable.img_19_sports));
        categoryList.add(new TopCollectionsCategory("Lifestyle", R.drawable.img_19_lifestyle));


        adapter = new TopCollectionsCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
    }
}
