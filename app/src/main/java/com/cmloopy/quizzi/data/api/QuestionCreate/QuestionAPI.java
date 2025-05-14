package com.cmloopy.quizzi.data.api.QuestionCreate;

import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface QuestionAPI {
    @GET("questions/quiz/{quizId}")
    Call<List<QuestionCreate>> getQuizQuestions(@Path("quizId") Long quizId);
    @GET("questions/{questionId}")
    Call<QuestionCreate> getQuestionById(@Path("questionId") int questionId);

    @Multipart
    @POST("questions/batch")
    Call<List<QuestionCreate>> createQuestionsBatch(
            @Part("quizId") RequestBody quizId,
            @Part("questionsJson") RequestBody questionsJson,
            @Part List<MultipartBody.Part> files
    );
    @Multipart
    @POST("questions")
    Call<QuestionCreate> createQuestion(
            @Part("quizId") RequestBody quizId,
            @Part("questionTypeId") RequestBody questionTypeId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @DELETE("questions/quiz/{quizId}")
    Call<Void> deleteAllQuizQuestions(@Path("quizId") Long quizId);

    @Multipart
    @PUT("questions/{id}")
    Call<QuestionCreate> updateQuestion(
            @Path("id") Long id,
            @Part("quizId") RequestBody quizId,
            @Part("questionTypeId") RequestBody questionTypeId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @DELETE("questions/{id}")
    Call<Void> deleteQuestion(@Path("id") Long id);

    @Multipart
    @POST("questions/true-false")
    Call<QuestionCreateTrueFalse> createTrueFalseQuestion(
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part("correctAnswer") RequestBody correctAnswer,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @PUT("questions/true-false/{id}")
    Call<QuestionCreateTrueFalse> updateTrueFalseQuestion(
            @Path("id") Long id,
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part("correctAnswer") RequestBody correctAnswer,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @POST("questions/choice")
    Call<QuestionCreateChoice> createChoiceQuestion(
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> choiceOptionsParts,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @PUT("questions/choice/{id}")
    Call<QuestionCreateChoice> updateChoiceQuestion(
            @Path("id") Long id,
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> choiceOptionsParts,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @POST("questions/slider")
    Call<QuestionCreateSlider> createSliderQuestion(
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part("minValue") RequestBody minValue,
            @Part("maxValue") RequestBody maxValue,
            @Part("defaultValue") RequestBody defaultValue,
            @Part("correctAnswer") RequestBody correctAnswer,
            @Part("color") RequestBody color,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @PUT("questions/slider/{id}")
    Call<QuestionCreateSlider> updateSliderQuestion(
            @Path("id") Long id,
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part("minValue") RequestBody minValue,
            @Part("maxValue") RequestBody maxValue,
            @Part("defaultValue") RequestBody defaultValue,
            @Part("correctAnswer") RequestBody correctAnswer,
            @Part("color") RequestBody color,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @POST("questions/puzzle")
    Call<QuestionCreatePuzzle> createPuzzleQuestion(
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> puzzlePieces,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @PUT("questions/puzzle/{id}")
    Call<QuestionCreatePuzzle> updatePuzzleQuestion(
            @Path("id") Long id,
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> puzzlePieces,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @POST("questions/text")
    Call<QuestionCreateTypeText> createTextQuestion(
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part("caseSensitive") RequestBody caseSensitive,
            @Part List<MultipartBody.Part> acceptedAnswersParts,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );

    @Multipart
    @PUT("questions/text/{id}")
    Call<QuestionCreateTypeText> updateTextQuestion(
            @Path("id") Long id,
            @Part("quizId") RequestBody quizId,
            @Part("content") RequestBody content,
            @Part("position") RequestBody position,
            @Part("point") RequestBody point,
            @Part("timeLimit") RequestBody timeLimit,
            @Part("description") RequestBody description,
            @Part("caseSensitive") RequestBody caseSensitive,
            @Part List<MultipartBody.Part> acceptedAnswersParts,
            @Part MultipartBody.Part imageFile,
            @Part MultipartBody.Part audioFile
    );
}
