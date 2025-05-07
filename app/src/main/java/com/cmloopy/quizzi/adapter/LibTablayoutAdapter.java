package com.cmloopy.quizzi.adapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cmloopy.quizzi.fragment.FavoriteFragment;
import com.cmloopy.quizzi.fragment.MyQuizzoFragment;

import java.util.Arrays;
import java.util.List;

public class LibTablayoutAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;
    public LibTablayoutAdapter(Fragment fragment){
        super(fragment);
        fragments = Arrays.asList(
                new MyQuizzoFragment(),
                new FavoriteFragment()
        );
    }
    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }
}
