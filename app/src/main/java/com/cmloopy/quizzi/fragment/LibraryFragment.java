package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.LibTablayoutAdapter;
import com.cmloopy.quizzi.views.CreateCollectionActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";

    private LibTablayoutAdapter libTablayoutAdapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fab;
    private final String[] tabTitles = {"My Quizzo", "Favorites"};
    private int userId = -1;
    private MyQuizzoFragment myQuizzoFragment;

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
        Log.d(TAG, "onCreate called with userId: " + userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Get userId from arguments with fallback
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }

        fab = view.findViewById(R.id.fab_add_collection);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateCollectionActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called with userId: " + userId);

        // Setup MyQuizzoFragment after view is created
        setupMyQuizzoFragment();
    }

    private void setupMyQuizzoFragment() {
        Log.d(TAG, "Setting up MyQuizzoFragment with userId: " + userId);

        if (userId == -1) {
            Log.e(TAG, "Invalid userId, cannot setup MyQuizzoFragment");
            return;
        }

        // Always create a new fragment instance to ensure proper initialization
        myQuizzoFragment = MyQuizzoFragment.newInstance(userId);

        // Use commitNow() to ensure the transaction is executed immediately
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.library_fragment_container, myQuizzoFragment)
                .commitNow();

        Log.d(TAG, "MyQuizzoFragment setup completed");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        // Refresh data when fragment becomes visible
        refreshChildFragmentData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint called: " + isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            refreshChildFragmentData();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged called: " + hidden);

        if (!hidden && isResumed()) {
            refreshChildFragmentData();
        }
    }

    private void refreshChildFragmentData() {
        Log.d(TAG, "Refreshing child fragment data");

        if (getView() != null && myQuizzoFragment != null && myQuizzoFragment.isAdded()) {
            // Post with a longer delay to ensure fragment is fully ready
            getView().postDelayed(() -> {
                if (myQuizzoFragment != null && myQuizzoFragment.isAdded() && isAdded()) {
                    Log.d(TAG, "Calling refreshData on MyQuizzoFragment");
                    myQuizzoFragment.refreshData();
                } else {
                    Log.w(TAG, "MyQuizzoFragment not ready for refresh, attempting to recreate");
                    // Recreate the fragment if it's not properly initialized
                    setupMyQuizzoFragment();
                }
            }, 300); // Increased delay
        } else {
            Log.w(TAG, "Fragment not ready, attempting to setup MyQuizzoFragment");
            if (getView() != null) {
                getView().postDelayed(() -> {
                    if (isAdded()) {
                        setupMyQuizzoFragment();
                    }
                }, 200);
            }
        }
    }
}