// File: com/cmloopy/quizzi/data/api/CollectionService.java
package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.TopCollections.QuizCollection;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface QuizCollectionAPI {
    @GET("quiz-collections")
    Call<List<QuizCollection>> getAllCollections();

    @GET("quiz-collections/{id}")
    Call<QuizCollection> getCollectionById(@Path("id") int id);

    @Multipart
    @PUT("quiz-collections/{quizCollectionId}")
    Call<QuizCollection> updateQuizCollection(
            @Path("quizCollectionId") Long quizCollectionId,
            @Part("authorId") RequestBody userId,
            @Part("category") RequestBody title,
            @Part("visibleTo") RequestBody visible,
            @Part MultipartBody.Part coverPhotoFile
    );

    @Multipart
    @DELETE("quiz-collections/{quizCollectionId}")
    Call<Void> deleteQuizCollection(@Path("quizCollectionId") Long quizCollectionId);
}