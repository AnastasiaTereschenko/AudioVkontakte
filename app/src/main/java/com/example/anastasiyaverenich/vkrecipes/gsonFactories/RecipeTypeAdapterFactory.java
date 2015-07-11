package com.example.anastasiyaverenich.vkrecipes.gsonFactories;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class RecipeTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (type.getRawType() == Recipe.class) {
            return (TypeAdapter<T>)new RouteResponseTypeAdapter(gson);
        }
        return null;
    }
}