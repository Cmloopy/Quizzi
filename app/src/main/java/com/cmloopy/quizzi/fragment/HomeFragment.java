package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.CollectionModel;
import com.cmloopy.quizzi.models.HomeCollection;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.utils.QuizMapper;
import com.cmloopy.quizzi.views.DiscoveryActivity;
import com.cmloopy.quizzi.views.RecommendAuthorActivity;
import com.cmloopy.quizzi.views.SearchActivity;
import com.cmloopy.quizzi.views.TopCollections;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.cmloopy.quizzi.data.manager.AuthorDataManager;

import java.io.IOException;
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
    private HomeDiscoverAdapter discoverAdapter;
    private UserApi userApi;
    private CollectionApi collectionApi;
    private QuizAPI quizApi;

    private List<RecommendUser> topAuthorsList = new ArrayList<>();
    private List<HomeCollection> collectionList = new ArrayList<>();
    private List<Quiz> discoverQuizzes = new ArrayList<>(); // Thêm danh sách quiz cho DISCOVER

    private List<Quiz> trendingQuizzes = new ArrayList<>();
    private List<Quiz> topPickQuizzes = new ArrayList<>();
    private HomeDiscoverAdapter trendingAdapter;
    private HomeDiscoverAdapter topPickAdapter;

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
        quizApi = RetrofitClient.getQuizApi();
        int idUser = getActivity().getIntent().getIntExtra("userId", -1);


        // DISCOVER
        DiscoverRcl = view.findViewById(R.id.rcl_home_discover);
        DiscoverRcl.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        discoverAdapter = new HomeDiscoverAdapter(discoverQuizzes);
        DiscoverRcl.setAdapter(discoverAdapter);

        // TRENDING QUIZ
        TrendingQuiz = view.findViewById(R.id.rcl_home_trending_quiz);
        TrendingQuiz.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trendingAdapter = new HomeDiscoverAdapter(trendingQuizzes);
        TrendingQuiz.setAdapter(trendingAdapter);

        // TOP PICK
        TopPick = view.findViewById(R.id.rcl_home_top_pick);
        TopPick.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topPickAdapter = new HomeDiscoverAdapter(topPickQuizzes);
        TopPick.setAdapter(topPickAdapter);

        // Gọi API để lấy dữ liệu quiz cho tất cả sections
        fetchQuizzes();

        // Top Author
        TopAuthor = view.findViewById(R.id.rcl_home_top_author);
        TopAuthor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topAuthorAdapter = new HomeAuthorAdapter(topAuthorsList);
        TopAuthor.setAdapter(topAuthorAdapter);
        fetchTopAuthors();

        // Collection
        Collectjon = view.findViewById(R.id.rcl_home_top_collection);
        Collectjon.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        collectionAdapter = new HomeCollectionAdapter(collectionList);
        Collectjon.setAdapter(collectionAdapter);
        fetchTopCollections();

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
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt2.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RecommendAuthorActivity.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt3.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), TopCollections.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt4.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt5.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });

        return view;
    }

    // Thêm phương thức mới để gọi API quizzes cho phần DISCOVER
    private void fetchQuizzes() {
        if (quizApi == null) {
            Log.e("API_DEBUG", "quizApi is null!");
            return;
        }

        Log.d("API_DEBUG", "Fetching quizzes for all sections...");
        Call<List<QuizResponse>> call = quizApi.getAllQuizzes();

        call.enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                Log.d("API_DEBUG", "Response received, code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> allQuizzes = response.body();
                    Log.d("API_DEBUG", "Received " + allQuizzes.size() + " quizzes");

                    // Lọc chỉ lấy các quiz có visible = true
                    List<QuizResponse> visibleQuizzes = new ArrayList<>();
                    for (QuizResponse quiz : allQuizzes) {
                        if (quiz.isVisible()) {
                            visibleQuizzes.add(quiz);
                        }
                    }
                    Log.d("API_DEBUG", "Filtered " + visibleQuizzes.size() + " visible quizzes");

                    if (visibleQuizzes.size() > 0) {
                        // Phân loại quizzes cho các sections khác nhau

                        // 1. DISCOVER - Lấy tất cả quiz và sắp xếp theo score giảm dần
                        List<QuizResponse> discoverList = new ArrayList<>(visibleQuizzes);
                        Collections.sort(discoverList, new Comparator<QuizResponse>() {
                            @Override
                            public int compare(QuizResponse q1, QuizResponse q2) {
                                Integer score1 = q1.getScore() != null ? q1.getScore() : 0;
                                Integer score2 = q2.getScore() != null ? q2.getScore() : 0;
                                return Integer.compare(score2, score1);
                            }
                        });
                        int discoverLimit = Math.min(10, discoverList.size());
                        List<QuizResponse> limitedDiscoverList = discoverList.subList(0, discoverLimit);

                        // 2. TRENDING - Lấy top quizzes mới nhất (theo createdAt)
                        List<QuizResponse> trendingList = new ArrayList<>(visibleQuizzes);
                        Collections.sort(trendingList, new Comparator<QuizResponse>() {
                            @Override
                            public int compare(QuizResponse q1, QuizResponse q2) {
                                return q2.getCreatedAt().compareTo(q1.getCreatedAt());
                            }
                        });
                        int trendingLimit = Math.min(10, trendingList.size());
                        List<QuizResponse> limitedTrendingList = trendingList.subList(0, trendingLimit);

                        // 3. TOP PICKS - Lấy các quizzes có keyword đặc biệt hoặc score cao nhất
                        List<QuizResponse> topPicksList = new ArrayList<>(visibleQuizzes);
                        // Sắp xếp theo điểm cao nhất cho top picks
                        Collections.sort(topPicksList, new Comparator<QuizResponse>() {
                            @Override
                            public int compare(QuizResponse q1, QuizResponse q2) {
                                Integer score1 = q1.getScore() != null ? q1.getScore() : 0;
                                Integer score2 = q2.getScore() != null ? q2.getScore() : 0;
                                return Integer.compare(score2, score1);
                            }
                        });
                        int topPicksLimit = Math.min(10, topPicksList.size());
                        List<QuizResponse> limitedTopPicksList = topPicksList.subList(0, topPicksLimit);

                        // Chuyển đổi và cập nhật tất cả adapters

                        // Cập nhật DISCOVER
                        Log.d("API_DEBUG", "Updating DISCOVER with " + limitedDiscoverList.size() + " quizzes");
                        discoverQuizzes.clear();
                        discoverQuizzes.addAll(QuizMapper.fromResponseList(limitedDiscoverList));
                        discoverAdapter.notifyDataSetChanged();

                        // Cập nhật TRENDING
                        Log.d("API_DEBUG", "Updating TRENDING with " + limitedTrendingList.size() + " quizzes");
                        trendingQuizzes.clear();
                        trendingQuizzes.addAll(QuizMapper.fromResponseList(limitedTrendingList));
                        trendingAdapter.notifyDataSetChanged();

                        // Cập nhật TOP PICKS
                        Log.d("API_DEBUG", "Updating TOP PICKS with " + limitedTopPicksList.size() + " quizzes");
                        topPickQuizzes.clear();
                        topPickQuizzes.addAll(QuizMapper.fromResponseList(limitedTopPicksList));
                        topPickAdapter.notifyDataSetChanged();

                    } else {
                        Log.d("API_DEBUG", "No visible quizzes found");
                        Toast.makeText(getContext(), "Không có quiz nào hiển thị", Toast.LENGTH_SHORT).show();

                        // Sử dụng dữ liệu mẫu cho tất cả sections
                        List<Quiz> sampleData = Collections.emptyList();

                        discoverQuizzes.clear();
                        discoverQuizzes.addAll(sampleData);
                        discoverAdapter.notifyDataSetChanged();

                        trendingQuizzes.clear();
                        trendingQuizzes.addAll(sampleData);
                        trendingAdapter.notifyDataSetChanged();

                        topPickQuizzes.clear();
                        topPickQuizzes.addAll(sampleData);
                        topPickAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API_DEBUG", "API error: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_DEBUG", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(getContext(), "Không thể tải dữ liệu quizzes", Toast.LENGTH_SHORT).show();

                    // Sử dụng dữ liệu mẫu
                    List<Quiz> sampleData = Collections.emptyList();

                    discoverQuizzes.clear();
                    discoverQuizzes.addAll(sampleData);
                    discoverAdapter.notifyDataSetChanged();

                    trendingQuizzes.clear();
                    trendingQuizzes.addAll(sampleData);
                    trendingAdapter.notifyDataSetChanged();

                    topPickQuizzes.clear();
                    topPickQuizzes.addAll(sampleData);
                    topPickAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                Log.e("API_DEBUG", "Network error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Sử dụng dữ liệu mẫu khi có lỗi mạng
                List<Quiz> sampleData = Collections.emptyList();

                discoverQuizzes.clear();
                discoverQuizzes.addAll(sampleData);
                discoverAdapter.notifyDataSetChanged();

                trendingQuizzes.clear();
                trendingQuizzes.addAll(sampleData);
                trendingAdapter.notifyDataSetChanged();

                topPickQuizzes.clear();
                topPickQuizzes.addAll(sampleData);
                topPickAdapter.notifyDataSetChanged();
            }
        });
    }

    // Phương thức fetchTopAuthors hiện có
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

                    // Cập nhật cache trong AuthorDataManager sử dụng phương thức mới
                    AuthorDataManager.getInstance().setTopAuthorsDirectly(topAuthorsList);

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

    // Phương thức fetchTopCollections hiện có
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