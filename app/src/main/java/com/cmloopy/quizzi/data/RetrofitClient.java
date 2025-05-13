package com.cmloopy.quizzi.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.cmloopy.quizzi.data.api.GamePlayApi;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.101.69:8080/api/";
    private static Retrofit retrofit;

    public static UserApi getUserApi() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(UserApi.class);
    }
    public static QuizzApi getQuizzApi(){
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(QuizzApi.class);
    }
    public static QuestionApi getQuestionApi(){
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(QuestionApi.class);
    }
    public  static GamePlayApi playGame(){
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(GamePlayApi.class);
    }
}
