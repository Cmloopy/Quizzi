package com.cmloopy.quizzi.data;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.cmloopy.quizzi.data.api.*;
import com.cmloopy.quizzi.data.api.QuestionCreate.*;
import com.cmloopy.quizzi.data.api.QuestionCreate.deserializer.DateDeserializer;
import com.cmloopy.quizzi.data.api.QuestionCreate.serializer.QuestionCreateDeserializer;
import com.cmloopy.quizzi.data.api.Topcollection.CollectionService;
import com.cmloopy.quizzi.data.api.Library.MyQuizAPI;

import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class RetrofitClient {

//    private static final String BASE_URL = "http://192.168.101.69:8080/api/";
    private static final String BASE_URL = "https://8080-mduc2610-temp-txa6cej4fdy.ws-us118.gitpod.io/api/";

    private static Retrofit retrofit;
    private static Retrofit customRetrofit;

    private static Retrofit getCustomRetrofit() {


        if(customRetrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .registerTypeAdapter(QuestionCreate.class, new QuestionCreateDeserializer())
                    .create();

            customRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return customRetrofit;
    }
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // Các API dùng retrofit mặc định
    public static UserApi getUserApi() {
        return getCustomRetrofit().create(UserApi.class);
    }

    public static QuizzApi getQuizzApi() {
        return getCustomRetrofit().create(QuizzApi.class);
    }

    public static QuestionApi getQuestionApi() {
        return getRetrofit().create(QuestionApi.class);
    }

    public static CollectionApi getCollectionApi() {
        return getCustomRetrofit().create(CollectionApi.class);
    }

    public static CollectionService getCollectionService() {
        return getCustomRetrofit().create(CollectionService.class);
    }

    public static MyQuizAPI getMyQuizApi() {
        return getCustomRetrofit().create(MyQuizAPI.class);
    }
    public static QuizAPI getQuizApi(){
        return getCustomRetrofit().create(QuizAPI.class);
    }

    public static GamePlayApi playGame() {
        return getCustomRetrofit().create(GamePlayApi.class);
    }

    public static QuestionAPI getQuestionCreateApi() {
        return getCustomRetrofit().create(QuestionAPI.class);
    }

    public static QuizAPI getQuizCreateApi() {
        return getCustomRetrofit().create(QuizAPI.class);
    }

}
