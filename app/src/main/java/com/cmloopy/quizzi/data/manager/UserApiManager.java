package com.cmloopy.quizzi.data.manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.user.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserApiManager {
    private static UserApiManager instance;
    private Map<Integer, User> userCache = new HashMap<>();
    private static final String TAG = "UserApiManager";

    private static final String BASE_URL = "https://api.example.com"; // Thay thế bằng base URL thực tế của bạn

    private UserApiManager() {
        // Constructor riêng tư
    }

    public static UserApiManager getInstance() {
        if (instance == null) {
            instance = new UserApiManager();
        }
        return instance;
    }

    // Interface để lắng nghe kết quả tải User
    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
        void onError(String message);
    }

    // Phương thức lấy thông tin User theo ID
    public void getUserById(Context context, int userId, OnUserLoadedListener listener) {
        // Kiểm tra xem đã có user trong cache chưa
        if (userCache.containsKey(userId)) {
            listener.onUserLoaded(userCache.get(userId));
            return;
        }

        // Nếu không có trong cache, gọi API
        String url = BASE_URL + "/users/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse JSONObject thành User sử dụng Gson
                            Gson gson = new Gson();
                            User user = gson.fromJson(response.toString(), User.class);

                            // Thêm vào cache
                            userCache.put(userId, user);

                            // Gọi callback
                            listener.onUserLoaded(user);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing user data", e);
                            listener.onError("Lỗi phân tích dữ liệu: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "API error", error);
                        listener.onError("Không thể tải dữ liệu từ máy chủ: " + error.getMessage());
                    }
                }) {
            // Có thể thêm header nếu cần
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // Thêm token nếu cần
                // headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Thêm request vào queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    // Phương thức để xóa cache khi cần thiết (ví dụ: sau khi đăng xuất)
    public void clearCache() {
        userCache.clear();
    }
}