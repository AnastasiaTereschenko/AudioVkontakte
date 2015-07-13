package com.example.anastasiyaverenich.vkrecipes.modules;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IApiMethods {

    @GET("/method/wall.get")
    void getParam(
            @Query("owner_id") String owner,
            @Query("count") String count,
            @Query("filter") String filter,
            @Query("version") String version,
            Callback<Recipe> cb
    );
}
