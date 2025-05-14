package com.cmloopy.quizzi.data;

import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.cmloopy.quizzi.data.api.CollectionApi;
import com.cmloopy.quizzi.data.api.GamePlayApi;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.deserializer.DateDeserializer;
import com.cmloopy.quizzi.data.api.QuestionCreate.serializer.QuestionDeserializer;
import com.cmloopy.quizzi.data.api.Topcollection.CollectionService;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.data.api.Library.MyQuizAPI;
import com.cmloopy.quizzi.models.QuestionCreate.Question;

import java.util.Date;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.101.69:8080/api/";
//    private static final String GITHUB_CODESPACE_BASE_URL = "https://upgraded-telegram-9v4jgg9jvjjh465-8080.app.github.dev/api/";
    private static final String GITHUB_CODESPACE_BASE_URL = "http://192.168.100.150:8080/api/";
    private static Retrofit retrofit;


    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .registerTypeAdapter(Question.class, new QuestionDeserializer()) // Của đức
                    .registerTypeAdapter(Question.class, new QuestionDeserializer()) //PlayQuiz 
                    .create();
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

    public static QuizAPI getQuizAPI() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_CODESPACE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static MyQuizAPI getQuizApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_CODESPACE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(MyQuizAPI.class);
    }

    public static CollectionService getCollectionService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_CODESPACE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CollectionService.class);
    }


    public static UserApi getUserApi() {
        return getRetrofit().create(UserApi.class);
    }

    public static QuizzApi getQuizzApi() {
        return getRetrofit().create(QuizzApi.class);
    }

    public static QuestionApi getQuestionApi() {
        return getRetrofit().create(QuestionApi.class);
    }

    public static QuestionAPI getQuestionCreateApi() {
        return getRetrofit().create(QuestionAPI.class);
    }

    public static QuizAPI getQuizCreateApi() {
        return getRetrofit().create(QuizAPI.class);
    }

    public static GamePlayApi playGame() {
        return getRetrofit().create(GamePlayApi.class);
    }

}