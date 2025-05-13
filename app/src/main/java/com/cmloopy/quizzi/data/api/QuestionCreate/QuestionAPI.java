package com.cmloopy.quizzi.data.api.QuestionCreate;

import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTypeText;

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
    Call<List<Question>> getQuizQuestions(@Path("quizId") Long quizId);
    @GET("questions/{questionId}")
    Call<Question> getQuestionById(@Path("questionId") int questionId);

    @Multipart
    @POST("questions/batch")
    Call<List<Question>> createQuestionsBatch(
            @Part("quizId") RequestBody quizId,
            @Part("questionsJson") RequestBody questionsJson,
            @Part List<MultipartBody.Part> files
    );
    @Multipart
    @POST("questions")
    Call<Question> createQuestion(
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
    Call<Question> updateQuestion(
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
    Call<QuestionTrueFalse> createTrueFalseQuestion(
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
    Call<QuestionTrueFalse> updateTrueFalseQuestion(
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
    Call<QuestionChoice> createChoiceQuestion(
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
    Call<QuestionChoice> updateChoiceQuestion(
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
    Call<QuestionSlider> createSliderQuestion(
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
    Call<QuestionSlider> updateSliderQuestion(
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
    Call<QuestionPuzzle> createPuzzleQuestion(
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
    Call<QuestionPuzzle> updatePuzzleQuestion(
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
    Call<QuestionTypeText> createTextQuestion(
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
    Call<QuestionTypeText> updateTextQuestion(
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
