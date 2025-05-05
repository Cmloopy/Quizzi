package com.cmloopy.quizzi.adapter;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cmloopy.quizzi.fragment.UI65EnterPinFragment;
import com.cmloopy.quizzi.fragment.UI65ScanQrFragment;

public class UI65ViewPagerAdapter extends FragmentStateAdapter {

    public UI65ViewPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new UI65EnterPinFragment() : new UI65ScanQrFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
