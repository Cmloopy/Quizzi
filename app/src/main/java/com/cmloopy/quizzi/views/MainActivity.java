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
    private int idUser = -1;

    // Keep references to fragments to avoid recreation
    private HomeFragment homeFragment;
    private LibraryFragment libraryFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get user id from intent
        idUser = getIntent().getIntExtra("userId", -1);

        // Handle case where we're coming back from another activity
        if (getIntent().hasExtra("idUser")) {
            idUser = getIntent().getIntExtra("idUser", -1);
        }

        // Initialize fragments only once
        if (savedInstanceState == null) {
            initializeFragments();
            // Show home fragment by default
            replaceFragment(homeFragment);
            binding.bottomNav.setSelectedItemId(R.id.ic_home);
        } else {
            // Restore fragment references after configuration change
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
            libraryFragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag("LIBRARY_FRAGMENT");
            profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PROFILE_FRAGMENT");
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.ic_home) {
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance(idUser);
                }
                replaceFragment(homeFragment, "HOME_FRAGMENT");
            } else if (itemId == R.id.ic_library) {
                if (libraryFragment == null) {
                    libraryFragment = LibraryFragment.newInstance(idUser);
                }
                replaceFragment(libraryFragment, "LIBRARY_FRAGMENT");
            } else if (itemId == R.id.ic_profile) {
                if (profileFragment == null) {
                    profileFragment = ProfileFragment.newInstance(idUser);
                }
                replaceFragment(profileFragment, "PROFILE_FRAGMENT");
            } else if (itemId == R.id.ic_create) {
                Intent intent = new Intent(this, CreateQuizActivity.class);
                intent.putExtra("userId", idUser);
                startActivity(intent);
                return true; // Don't change the selected tab
            } else if (itemId == R.id.ic_join) {
                Intent intent = new Intent(this, UI65.class);
                intent.putExtra("userId", idUser);
                startActivity(intent);
                return true; // Don't change the selected tab
            } else {
                return false;
            }
            return true;
        });
    }

    private void initializeFragments() {
        homeFragment = HomeFragment.newInstance(idUser);
        libraryFragment = LibraryFragment.newInstance(idUser);
        profileFragment = ProfileFragment.newInstance(idUser);
    }

    private void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frm_container, fragment, tag)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Handle new intent (when coming back from other activities)
        int newUserId = intent.getIntExtra("userId", -1);
        if (newUserId == -1) {
            newUserId = intent.getIntExtra("idUser", -1);
        }

        if (newUserId != -1 && newUserId != idUser) {
            // User ID changed, recreate fragments
            idUser = newUserId;
            initializeFragments();

            // Show the currently selected tab with new user data
            int selectedItemId = binding.bottomNav.getSelectedItemId();
            if (selectedItemId == R.id.ic_home) {
                replaceFragment(homeFragment, "HOME_FRAGMENT");
            } else if (selectedItemId == R.id.ic_library) {
                replaceFragment(libraryFragment, "LIBRARY_FRAGMENT");
            } else if (selectedItemId == R.id.ic_profile) {
                replaceFragment(profileFragment, "PROFILE_FRAGMENT");
            }
        }
    }
}