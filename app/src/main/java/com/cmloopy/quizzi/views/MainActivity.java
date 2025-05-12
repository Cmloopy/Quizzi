package com.cmloopy.quizzi.views;

import android.content.Intent;
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

        int idUser = getIntent().getIntExtra("userId", -1);

        HomeFragment homeFragment = new HomeFragment();
        replaceFragment(homeFragment);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.ic_home) {
                replaceFragment(HomeFragment.newInstance(idUser));
            } else if (itemId == R.id.ic_library) {
                replaceFragment(LibraryFragment.newInstance(idUser));
            } else if (itemId == R.id.ic_profile) {
                replaceFragment(ProfileFragment.newInstance(idUser));
            } else if (itemId == R.id.ic_create) {
                Intent intent = new Intent(this, CreateQuizActivity.class);
                intent.putExtra("idUser", idUser);
                startActivity(intent);
            } else if (itemId == R.id.ic_join) {
                Intent intent = new Intent(this, UI65.class);
                intent.putExtra("idUser", idUser);
                startActivity(intent);
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