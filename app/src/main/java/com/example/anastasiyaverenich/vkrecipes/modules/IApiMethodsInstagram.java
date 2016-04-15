package com.example.anastasiyaverenich.vkrecipes.modules;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IApiMethodsInstagram {

    @GET("/v1/users/351730456/media/recent")
    void getPhotosFromInst(
            @Query("client_id") String client_id,
            Callback<RecipeFromInstagram> cb
    );
}
