package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.models.user.CheckLoginUser;
import com.cmloopy.quizzi.models.user.LoginResponse;
import com.cmloopy.quizzi.models.user.RegisterUser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserApi {
    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body CheckLoginUser loginRequest);

    @Multipart
    @POST("quizzes")
    Call<QuizResponse> uploadQuiz(
            @Part("userId") RequestBody userId,
            @Part("quizCollectionId") RequestBody quizCollectionId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("keyword") RequestBody keyword,
            @Part("visible") RequestBody visible,
            @Part("visibleQuizQuestion") RequestBody visibleQuizQuestion,
            @Part("shuffle") RequestBody shuffle,
            @Part MultipartBody.Part coverPhotoFile
    );

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterUser registerUser);
}
