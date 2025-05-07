package com.cmloopy.quizzi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cmloopy.quizzi.fragment.FollowersFragment;
import com.cmloopy.quizzi.fragment.FollowingFragment;

import java.util.Arrays;
import java.util.List;

public class FollowAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;
    public FollowAdapter(FragmentActivity fragment){
        super(fragment);
        fragments = Arrays.asList(
                new FollowersFragment(),
                new FollowingFragment()
        );
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
