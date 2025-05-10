package com.cmloopy.quizzi.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.cmloopy.quizzi.data.api.UserApi;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.101.69:8080/api/";
    private static Retrofit retrofit;

    public static UserApi getUserApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(UserApi.class);
    }
}
