package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.quiz.QuizResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface QuizzApi {
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
    @GET("quizzes/{quizId}")
    Call<QuizResponse> getQuizById(@Path("quizId") long quizId);
}
