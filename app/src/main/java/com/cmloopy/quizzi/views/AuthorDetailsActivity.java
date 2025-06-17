package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.databinding.ActivityAuthorDetailsBinding;
import com.cmloopy.quizzi.models.user.User;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorDetailsActivity extends AppCompatActivity {
    private ActivityAuthorDetailsBinding binding;
    private int authorId;
    private int userId;
    private ImageView btnBack;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userId = getIntent().getIntExtra("userId", -1);
        authorId = getIntent().getIntExtra("authorId", -1);

        userApi = RetrofitClient.getUserApi();

        Call<User> call =  userApi.getInfoUserById(authorId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if(user != null){
                    Picasso.get().load(user.getAvatar()).into(binding.profileImage);
                    binding.profileName.setText(user.getFullName());
                    binding.profileUsername.setText("@" + user.getUsername());
                    binding.quizzoCount.setText(user.getTotalQuizs()+ "");
                    binding.collectionsCount.setText(user.getTotalCollections()+"");
                    binding.playsCount.setText(user.getTotalPlays()+"");
                    binding.playersCount.setText(user.getTotalPlayers()+"");
                    binding.followersCount.setText(user.getTotalFollowers()+"");
                    binding.followingCount.setText(user.getTotalFollowees()+"");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                binding.profileName.setText("");
                binding.profileUsername.setText("");
                binding.quizzoCount.setText("");
                binding.collectionsCount.setText("");
                binding.playsCount.setText("");
                binding.playersCount.setText("");
                binding.followersCount.setText("");
                binding.followingCount.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}