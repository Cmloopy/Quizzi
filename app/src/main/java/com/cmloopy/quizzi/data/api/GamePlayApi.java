package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.tracking.QuizGameTracking;
import com.cmloopy.quizzi.models.tracking.QuizTrackingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface GamePlayApi {
    @POST("quiz-game-tracking")
    Call<QuizTrackingResponse> saveTotalPoint(@Body QuizTrackingResponse quizTrackingResponse);
    @GET("quiz-game-tracking/quiz/{quizId}")
    Call<List<QuizGameTracking>> getQuizTracking(@Path("quizId") long quizId);
}
