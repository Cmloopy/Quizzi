package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.RecommendUserAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.TopCollectionsCategory;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private EditText searchInput;
    private RadioGroup searchCategory;
    private ImageButton cancelButton;
    private TextView emptyStateView;
    private RecyclerView.Adapter<?> adapter = null;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_TIME = 300; // 300ms debounce

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }

}