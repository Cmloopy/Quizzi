package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.HomeAuthorAdapter;
import com.cmloopy.quizzi.adapter.HomeCollectionAdapter;
import com.cmloopy.quizzi.adapter.HomeDiscoverAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.CollectionApi;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.CollectionModel;
import com.cmloopy.quizzi.models.HomeCollection;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.DiscoveryActivity;
import com.cmloopy.quizzi.views.RecommendAuthorActivity;
import com.cmloopy.quizzi.views.SearchActivity;
import com.cmloopy.quizzi.views.TopCollections;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView DiscoverRcl;
    private RecyclerView TrendingQuiz;
    private RecyclerView TopPick;
    private RecyclerView TopAuthor;
    private RecyclerView Collectjon;
    private HomeAuthorAdapter topAuthorAdapter;
    private HomeCollectionAdapter collectionAdapter;
    private UserApi userApi;
    private CollectionApi collectionApi;
    private List<RecommendUser> topAuthorsList = new ArrayList<>();
    private List<HomeCollection> collectionList = new ArrayList<>();

    public static HomeFragment newInstance(int idUser) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("userId", idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo API client
        userApi = RetrofitClient.getUserApi();
        collectionApi = RetrofitClient.getCollectionApi();

        if (getActivity().getIntent() != null) {
            int idUser = getActivity().getIntent().getIntExtra("userId", -1);
            if (idUser != -1) {
                // Sử dụng idUser khi cần
            }
        }

        List<Quiz> ListDisCover = Quiz.CreateSampleData();

        //Discover
        DiscoverRcl = view.findViewById(R.id.rcl_home_discover);
        DiscoverRcl.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DiscoverRcl.setAdapter(new HomeDiscoverAdapter(ListDisCover));

        //Trending Quiz
        TrendingQuiz = view.findViewById(R.id.rcl_home_trending_quiz);
        TrendingQuiz.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TrendingQuiz.setAdapter(new HomeDiscoverAdapter(ListDisCover));

        //Top Pick
        TopPick = view.findViewById(R.id.rcl_home_top_pick);
        TopPick.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TopPick.setAdapter(new HomeDiscoverAdapter(ListDisCover));

        //Top Author - Khởi tạo với danh sách trống, sẽ được cập nhật từ API
        TopAuthor = view.findViewById(R.id.rcl_home_top_author);
        TopAuthor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topAuthorAdapter = new HomeAuthorAdapter(topAuthorsList);
        TopAuthor.setAdapter(topAuthorAdapter);

        // Lấy danh sách top authors từ API
        fetchTopAuthors();

        //Collection - Khởi tạo với danh sách trống, sẽ được cập nhật từ API
        Collectjon = view.findViewById(R.id.rcl_home_top_collection);
        Collectjon.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        collectionAdapter = new HomeCollectionAdapter(collectionList);
        Collectjon.setAdapter(collectionAdapter);

        // Lấy danh sách top collections từ API
        fetchTopCollections();

        //Click
        MaterialTextView txt1 = view.findViewById(R.id.view_all_discover);
        MaterialTextView txt2 = view.findViewById(R.id.view_all_top_author);
        MaterialTextView txt3 = view.findViewById(R.id.view_all_top_collection);
        MaterialTextView txt4 = view.findViewById(R.id.view_all_trending);
        MaterialTextView txt5 = view.findViewById(R.id.view_all_top_pick);
        ShapeableImageView shape1 = view.findViewById(R.id.btn_find);
        shape1.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });
        txt1.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            startActivity(intent);
        });
        txt2.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), RecommendAuthorActivity.class);
            startActivity(intent);
        });
        txt3.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), TopCollections.class);
            startActivity(intent);
        });
        txt4.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            startActivity(intent);
        });
        txt5.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchTopAuthors() {
        Call<List<User>> call = userApi.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();

                    // Sắp xếp người dùng theo totalQuizs giảm dần
                    Collections.sort(users, new Comparator<User>() {
                        @Override
                        public int compare(User u1, User u2) {
                            return Integer.compare(u2.getTotalQuizs(), u1.getTotalQuizs());
                        }
                    });

                    // Lấy 10 người dùng tạo nhiều quiz nhất
                    int limit = Math.min(10, users.size());
                    List<User> topUsers = users.subList(0, limit);

                    // Chuyển đổi User thành RecommendUser cho adapter
                    topAuthorsList.clear();
                    for (User user : topUsers) {
                        // Bỏ qua người dùng không có fullName
                        if (user.getFullName() == null) continue;

                        // Tạo đối tượng RecommendUser mới
                        RecommendUser author = new RecommendUser(
                                user.getFullName(),
                                user.getUsername(),
                                R.drawable.ic_launcher_background  // Hình mặc định
                        );
                        // Đặt ID cho RecommendUser
                        author.setId(user.getId());
                        // Đặt URL avatar
                        author.setAvatarUrl(user.getAvatar());

                        topAuthorsList.add(author);
                    }

                    // Thông báo cho adapter về sự thay đổi dữ liệu
                    topAuthorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu tác giả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTopCollections() {
        Call<List<CollectionModel>> call = collectionApi.getAllCollections();
        call.enqueue(new Callback<List<CollectionModel>>() {
            @Override
            public void onResponse(Call<List<CollectionModel>> call, Response<List<CollectionModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CollectionModel> collections = response.body();

                    // Lọc chỉ các collection có thể hiển thị công khai
                    List<CollectionModel> visibleCollections = new ArrayList<>();
                    for (CollectionModel collection : collections) {
                        if (collection.isVisibleTo()) {
                            visibleCollections.add(collection);
                        }
                    }

                    // Sắp xếp theo timestamp (mới nhất lên đầu)
                    Collections.sort(visibleCollections, new Comparator<CollectionModel>() {
                        @Override
                        public int compare(CollectionModel c1, CollectionModel c2) {
                            return c2.getTimestamp().compareTo(c1.getTimestamp());
                        }
                    });

                    // Lấy 5 phần tử đầu hoặc ít hơn
                    int limit = Math.min(5, visibleCollections.size());
                    if (visibleCollections.size() > 0) {
                        List<CollectionModel> topCollections = visibleCollections.subList(0, limit);

                        // Chuyển đổi sang model UI của bạn
                        collectionList.clear();
                        for (CollectionModel collection : topCollections) {
                            // Sử dụng hình ảnh mặc định nếu coverPhoto là null
                            int imageRes = R.drawable.ic_launcher_background;

                            // Tạo đối tượng HomeCollection mới
                            HomeCollection homeCollection = new HomeCollection(
                                    imageRes,
                                    collection.getCategory()
                            );

                            // Nếu HomeCollection có method để set ID, sử dụng nó
                            try {
                                // Kiểm tra xem có method setId trong lớp HomeCollection không
                                java.lang.reflect.Method setIdMethod = HomeCollection.class.getMethod("setId", int.class);
                                setIdMethod.invoke(homeCollection, collection.getId());
                            } catch (Exception e) {
                                // Không có method setId, hoặc có lỗi khi gọi, bỏ qua
                            }

                            collectionList.add(homeCollection);
                        }

                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        collectionAdapter.notifyDataSetChanged();
                    } else {
                        // Không có collections hiển thị
                        Toast.makeText(getContext(), "Không có bộ sưu tập nào hiển thị", Toast.LENGTH_SHORT).show();

                        // Sử dụng dữ liệu mẫu thay thế
                        collectionList.clear();
                        collectionList.addAll(createSampleCollections());
                        collectionAdapter.notifyDataSetChanged();
                    }
                } else {
                    // Có lỗi khi lấy dữ liệu từ API
                    Toast.makeText(getContext(), "Không thể tải dữ liệu collections", Toast.LENGTH_SHORT).show();

                    // Sử dụng dữ liệu mẫu thay thế
                    collectionList.clear();
                    collectionList.addAll(createSampleCollections());
                    collectionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CollectionModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Sử dụng dữ liệu mẫu khi có lỗi mạng
                collectionList.clear();
                collectionList.addAll(createSampleCollections());
                collectionAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<HomeCollection> createSampleCollections() {
        List<HomeCollection> collections = new ArrayList<>();
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Animals"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Cars"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Weathers"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Water"));
        collections.add(new HomeCollection(R.drawable.ic_launcher_background, "Money"));
        return collections;
    }
}