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

public class AuthorDetailsQuizzo extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DetailTopCollectionAdapter quizAdapter;
    private List<DetailTopCollectionItem> quizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_details_quizzo);
    }
}
