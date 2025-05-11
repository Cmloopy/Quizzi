package com.cmloopy.quizzi.data.api.QuestionCreate;

import com.cmloopy.quizzi.models.QuestionCreate.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuestionAPI {
    @GET("questions/quiz/{quizId}")
    Call<List<Question>> getQuizQuestions(@Path("quizId") int quizId);
    @GET("questions/{questionId}")
    Call<Question> getQuestionById(@Path("questionId") int questionId);

}
