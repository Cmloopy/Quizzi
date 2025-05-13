package com.cmloopy.quizzi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cmloopy.quizzi.fragment.FavoriteFragment;
import com.cmloopy.quizzi.fragment.MyQuizzoFragment;

public class LibTablayoutAdapter extends FragmentStateAdapter {
    private int userId;

    public LibTablayoutAdapter(Fragment fragment, int userId) {
        super(fragment);
        this.userId = userId;
    }

    @Override
    public int getItemCount() {
        return 2; // Hoặc bất kỳ số lượng tab nào bạn cần
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return MyQuizzoFragment.newInstance(userId);
        } else {
            return new FavoriteFragment(); // Hoặc FavoriteFragment.newInstance(userId) nếu cần
        }
    }
}