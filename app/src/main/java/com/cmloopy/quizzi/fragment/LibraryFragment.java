package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.LibTablayoutAdapter;
import com.cmloopy.quizzi.views.CreateCollectionActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {

    private LibTablayoutAdapter libTablayoutAdapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fab;
    private final String[] tabTitles = {"My Quizzo", "Favorites"};
    private int userId = -1;

    public static LibraryFragment newInstance(int idUser) {
        LibraryFragment libraryFragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putInt("userId", idUser);
        libraryFragment.setArguments(args);
        return libraryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Lấy userId từ arguments
        int userId;
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        } else {
            userId = -1;
        }

        // Tạo adapter với userId
        libTablayoutAdapter = new LibTablayoutAdapter(this, userId);
        tabLayout = view.findViewById(R.id.tab_layout_lib);
        viewPager = view.findViewById(R.id.vpg_lib);

        viewPager.setAdapter(libTablayoutAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        fab = view.findViewById(R.id.fab_add_collection);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateCollectionActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        return view;
    }
}