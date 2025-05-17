package com.cmloopy.quizzi.data.api.QuestionCreate;

import com.cmloopy.quizzi.models.quiz.QuizCollectionResponse;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface QuizAPI {
    @GET("quizzes")
    Call<List<QuizResponse>> getAllQuizzes();

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

    @Multipart
    @PUT("quizzes/{quizId}")
    Call<QuizResponse> updateQuiz(
            @Path("quizId") Long quizId,
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

    @Multipart
    @POST("quiz-collections")
    Call<QuizCollectionResponse> uploadQuizCollection(
            @Part("authorId") RequestBody userId,
            @Part("category") RequestBody title,
            @Part("visible") RequestBody visible,
            @Part MultipartBody.Part coverPhotoFile
    );

    @Multipart
    @PUT("quiz-collections/{quizCollectionId}")
    Call<QuizCollectionResponse> updateQuizCollection(
            @Path("quizCollectionId") Long quizCollectionId,
            @Part("authorId") RequestBody userId,
            @Part("category") RequestBody title,
            @Part("visible") RequestBody visible,
            @Part MultipartBody.Part coverPhotoFile
    );


}

