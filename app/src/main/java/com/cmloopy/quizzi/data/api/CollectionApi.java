package com.cmloopy.quizzi.data.api;

import com.cmloopy.quizzi.models.CollectionModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CollectionApi {
    /**
     * Lấy tất cả các collections
     * @return Danh sách các collection
     */
    @GET("quiz-collections")
    Call<List<CollectionModel>> getAllCollections();

    /**
     * Lấy collection theo ID
     * @param id ID của collection
     * @return Collection có ID tương ứng
     */
    @GET("quiz-collections/{id}")
    Call<CollectionModel> getCollectionById(@Path("id") int id);
}