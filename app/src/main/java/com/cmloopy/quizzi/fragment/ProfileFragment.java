package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.TopCollectionsCategory;
import com.cmloopy.quizzi.views.FollowActivity;
import com.cmloopy.quizzi.views.SetiingActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;

    private GridLayoutManager gridLayoutManager;

    private MaterialTextView materialTextView;

    private MaterialTextView btn;

    private ShapeableImageView btnSetting;

    RecyclerView.Adapter<?> adapter = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        RadioGroup radioGroup = view.findViewById(R.id.tabGroup1);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                handleSearchCategoryChange(checkId);
            }
        });
        radioGroup.post(() -> {
            radioGroup.check(R.id.radioLibQuizzoBtn);
            handleSearchCategoryChange(R.id.radioLibQuizzoBtn);
        });

        recyclerView = view.findViewById(R.id.rcl_view_profile);
        materialTextView = view.findViewById(R.id.txt_title_lib_my_quizzo_profile);
        btn = view.findViewById(R.id.follower_btn);
        btnSetting = view.findViewById(R.id.btn_setting);

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FollowActivity.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SetiingActivity.class);
            startActivity(intent);
        });
        return view;
    }

    private void handleSearchCategoryChange(int checkedId) {

        if (checkedId == R.id.radioLibQuizzoBtn) {
            materialTextView.setText("45 Quizzo");
            adapter = this.getQuizAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);
        } else if (checkedId == R.id.radioLibCollectionBtn) {
            materialTextView.setText("7 Collections");
            gridLayoutManager = new GridLayoutManager(requireContext(), 2);
            adapter = this.getCollectionAdapter();
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
        }

    }

    private QuizAdapter getQuizAdapter() {
        List<Quiz> items = new ArrayList<>();

        items.add(new Quiz(R.drawable.ic_launcher_background, "Get Smarter with Prod...",
                "2 months ago", "5.5K plays", "Titus Kitamura", R.drawable.ic_launcher_background, "16 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Great Ideas Come from...",
                "6 months ago", "10.3K plays", "Alfonzo Schuessler", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Having Fun & Always S...",
                "2 years ago", "18.5K plays", "Daryl Nehls", R.drawable.ic_launcher_background, "12 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Can You Imagine, Worl...",
                "3 months ago", "4.9K plays", "Edgar Torrey", R.drawable.ic_launcher_background, "20 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Back to School, Get Sm...",
                "1 year ago", "12.4K plays", "Darcel Ballentine", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "What is Your Favorite ...",
                "5 months ago", "6.2K plays", "Elmer Laverty", R.drawable.ic_launcher_background, "16 Qs"));
        QuizAdapter quizAdapter  = new QuizAdapter(items);
        return quizAdapter;
    }

    private TopCollectionsCategoryAdapter getCollectionAdapter() {
        List<TopCollectionsCategory> categoryList = new ArrayList<>();
        categoryList.add(new TopCollectionsCategory("Education", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Games", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Business", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Entertainment", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Art", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Plants", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Finance", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Food & Drink", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Health", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Kids", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Sports", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Lifestyle", R.drawable.img_02));

        TopCollectionsCategoryAdapter collectionAdapter = new TopCollectionsCategoryAdapter(requireContext(), categoryList);
        return  collectionAdapter;
    }
}