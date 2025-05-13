package com.cmloopy.quizzi.data;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.cmloopy.quizzi.data.api.CollectionApi;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.serializer.QuestionDeserializer;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.101.69:8080/api/";
    private static final String GITHUB_CODESPACE_BASE_URL = "https://upgraded-telegram-9v4jgg9jvjjh465-8080.app.github.dev/api/";
    private static Retrofit retrofit;

    // Thêm phương thức getRetrofitInstance để sử dụng trong phương thức getQuizAPI
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_CODESPACE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static UserApi getUserApi() {
        return getRetrofitInstance().create(UserApi.class);
    }

    public static QuestionAPI getQuestionApi() {
        // Trong trường hợp cần xử lý đặc biệt với Gson cho Question
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Question.class, new QuestionDeserializer())
                .create();

        // Tạo retrofit mới với Gson tùy chỉnh
        Retrofit customRetrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_CODESPACE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return customRetrofit.create(QuestionAPI.class);
    }

    public static CollectionApi getCollectionApi() {
        return getRetrofitInstance().create(CollectionApi.class);
    }

    public static Retrofit getRetrofit() {
        return getRetrofitInstance();
    }

    // Sử dụng getRetrofitInstance để lấy QuizAPI instance
    public static QuizAPI getQuizAPI() {
        // Log để debug
        Log.d("RETROFIT_DEBUG", "Getting QuizAPI with base URL: " + GITHUB_CODESPACE_BASE_URL);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_CODESPACE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return getRetrofitInstance().create(QuizAPI.class);
    }

}