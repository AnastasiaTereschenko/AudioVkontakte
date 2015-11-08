package com.example.anastasiyaverenich.vkrecipes.modules;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IApiMethods {

    @GET("/method/wall.get")
    void getFeeds(
            @Query("owner_id") int owner,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("filter") String filter,
            @Query("version") String version,
            Callback<Recipe> cb
    );
}