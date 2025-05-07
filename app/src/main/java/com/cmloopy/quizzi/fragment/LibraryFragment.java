package com.cmloopy.quizzi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.LibTablayoutAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {

    private LibTablayoutAdapter libTablayoutAdapter;
    private TabLayout tabLayout;

    private ViewPager2 viewPager;
    private final String[] tabTitles = {"My Quizzo", "Favorites"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        libTablayoutAdapter = new LibTablayoutAdapter(this);
        tabLayout = view.findViewById(R.id.tab_layout_lib);
        viewPager = view.findViewById(R.id.vpg_lib);

        viewPager.setAdapter(libTablayoutAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitles[position]);
            }
        }).attach();

        return view;
    }
}