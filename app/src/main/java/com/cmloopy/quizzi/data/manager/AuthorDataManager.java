package com.cmloopy.quizzi.data.manager;

import android.content.Context;
import android.widget.Toast;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorDataManager {
    private static AuthorDataManager instance;
    private List<User> cachedUsers = new ArrayList<>();
    private List<RecommendUser> cachedTopAuthors = new ArrayList<>();
    private boolean dataLoaded = false;

    private AuthorDataManager() {
        // Constructor riêng tư
    }

    public static AuthorDataManager getInstance() {
        if (instance == null) {
            instance = new AuthorDataManager();
        }
        return instance;
    }

    public interface OnAuthorsLoadedListener {
        void onAuthorsLoaded(List<RecommendUser> authors);
        void onError(String message);
    }

    public void updateTopAuthorsFromHomeFragment(List<RecommendUser> sourceList) {
        if (sourceList == null || sourceList.isEmpty()) {
            return;
        }

        // Xóa cache hiện tại
        cachedTopAuthors.clear();

        // Thêm từng phần tử từ sourceList
        for (RecommendUser author : sourceList) {
            // Tạo mới một đối tượng RecommendUser nếu cần
            RecommendUser authorCopy = new RecommendUser(
                    author.getName(),
                    author.getUsername(),
                    author.getProfileImageResource()
            );
            authorCopy.setId(author.getId());
            authorCopy.setAvatarUrl(author.getAvatarUrl());

            // Thêm vào cache
            cachedTopAuthors.add(authorCopy);
        }

        // Đánh dấu là đã tải dữ liệu
        dataLoaded = true;
    }

    public void loadTopAuthors(final Context context, final OnAuthorsLoadedListener listener) {
        // Nếu đã có cache, sử dụng cache
        if (dataLoaded && !cachedTopAuthors.isEmpty()) {
            if (listener != null) {
                listener.onAuthorsLoaded(cachedTopAuthors);
            }
            return;
        }

        // Nếu chưa có cache, tải từ API
        UserApi userApi = RetrofitClient.getUserApi();
        Call<List<User>> call = userApi.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();

                    // Lưu vào cache
                    cachedUsers.clear();
                    cachedUsers.addAll(users);

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

                    // Chuyển đổi User thành RecommendUser
                    cachedTopAuthors.clear();
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

                        cachedTopAuthors.add(author);
                    }

                    dataLoaded = true;

                    // Gọi callback
                    if (listener != null) {
                        listener.onAuthorsLoaded(cachedTopAuthors);
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Không thể tải dữ liệu tác giả");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (listener != null) {
                    listener.onError("Lỗi mạng: " + t.getMessage());
                }
            }
        });
    }

    // Lấy tất cả các tác giả (cho màn hình RecommendAuthorActivity)
    public void loadAllAuthors(final Context context, final OnAuthorsLoadedListener listener) {
        // Tải từ API
        UserApi userApi = RetrofitClient.getUserApi();
        Call<List<User>> call = userApi.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();

                    // Lưu vào cache
                    cachedUsers.clear();
                    cachedUsers.addAll(users);

                    // Sắp xếp người dùng theo totalQuizs giảm dần
                    Collections.sort(users, new Comparator<User>() {
                        @Override
                        public int compare(User u1, User u2) {
                            return Integer.compare(u2.getTotalQuizs(), u1.getTotalQuizs());
                        }
                    });

                    // Chuyển đổi tất cả User thành RecommendUser
                    List<RecommendUser> allAuthors = new ArrayList<>();
                    for (User user : users) {
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

                        allAuthors.add(author);
                    }

                    // Gọi callback
                    if (listener != null) {
                        listener.onAuthorsLoaded(allAuthors);
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Không thể tải dữ liệu tác giả");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (listener != null) {
                    listener.onError("Lỗi mạng: " + t.getMessage());
                }
            }
        });
    }

    public List<RecommendUser> getCachedTopAuthors() {
        return cachedTopAuthors;
    }

    public List<User> getCachedUsers() {
        return cachedUsers;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void clearCache() {
        cachedUsers.clear();
        cachedTopAuthors.clear();
        dataLoaded = false;
    }

    public void setTopAuthorsDirectly(List<RecommendUser> authors) {
        if (authors != null) {
            cachedTopAuthors.clear();
            cachedTopAuthors.addAll(authors);
            dataLoaded = true;
        }
    }
}