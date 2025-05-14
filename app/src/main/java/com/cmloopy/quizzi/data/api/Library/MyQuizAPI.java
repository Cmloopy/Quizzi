package com.cmloopy.quizzi.data.api.Library;

import com.cmloopy.quizzi.models.HomeLibrary.MyQuizzo.QuizResponse;

import com.cmloopy.quizzi.models.HomeLibrary.MyQuizzo.QuizCollection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyQuizAPI {
    @GET("quizzes/user/{userId}")
    Call<List<QuizResponse>> getUserQuizzes(@Path("userId") int userId);

    @GET("quiz-collections")
    Call<List<QuizCollection>> getQuizCollectionsByAuthor(@Query("authorId") int authorId);

    @GET("api/quiz-collections/{id}")
    Call<QuizCollection> getCollectionById(@Path("id") int id);
}