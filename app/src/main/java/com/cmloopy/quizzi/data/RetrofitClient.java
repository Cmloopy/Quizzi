package com.cmloopy.quizzi.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.deserializer.DateDeserializer;
import com.cmloopy.quizzi.data.api.QuestionCreate.serializer.QuestionDeserializer;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.101.69:8080/api/";
    private static final String GITHUB_CODESPACE_BASE_URL = "https://upgraded-telegram-9v4jgg9jvjjh465-8080.app.github.dev/api/";
    private static Retrofit retrofit;

    public static UserApi getUserApi() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_CODESPACE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(UserApi.class);
    }

    public static QuestionAPI getQuestionApi() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(Question.class, new QuestionDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_CODESPACE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(QuestionAPI.class);
    }

    public static QuizAPI getQuizApi() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_CODESPACE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(QuizAPI.class);
    }


    public static Retrofit getRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_CODESPACE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
