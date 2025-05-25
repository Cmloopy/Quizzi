package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.HomeLibrary.MyQuizzo.QuizCollection;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @GET("quizzes")
    Call<List<QuizResponse>> getAllQuiz();
    @GET("quizzes/user/{userId}")
    Call<List<QuizResponse>> getQuizByUser(@Path("userId") int userId);
    @GET("quiz-collections")
    Call<List<QuizResponse>> getQuizCollectionsByAuthor(@Query("authorId") int authorId);
}
