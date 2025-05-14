// File: com/cmloopy/quizzi/data/api/CollectionService.java
package com.cmloopy.quizzi.data.api.Topcollection;

import com.cmloopy.quizzi.models.TopCollections.QuizCollection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CollectionService {
    @GET("quiz-collections")
    Call<List<QuizCollection>> getAllCollections();

    @GET("quiz-collections/{id}")
    Call<QuizCollection> getCollectionById(@Path("id") int id);
}