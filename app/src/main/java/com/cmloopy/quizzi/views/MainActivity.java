package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.databinding.ActivityMainBinding;
import com.cmloopy.quizzi.fragment.HomeFragment;
import com.cmloopy.quizzi.fragment.LibraryFragment;
import com.cmloopy.quizzi.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeFragment HomeFragment = new HomeFragment();
        replaceFragment(HomeFragment);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.ic_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.ic_library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.ic_profile) {
                replaceFragment(new ProfileFragment());
            } else {
                return false;
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frm_container, fragment)
                .commit();
    }

}