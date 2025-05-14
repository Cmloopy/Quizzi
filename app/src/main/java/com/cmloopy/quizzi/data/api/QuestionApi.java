package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.question.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuestionApi {
    @GET("questions/quiz/{quizId}")
    Call<List<Question>> getQuestionByQuiz(@Path("quizId") int quizId);

    @GET("questions/{questionId}")
    Call<Question> getQuestionById(@Path("questionId") int questionId);
}
