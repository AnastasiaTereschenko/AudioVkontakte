package com.example.anastasiyaverenich.vkrecipes.gsonFactories;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteResponseTypeAdapter extends TypeAdapter<Recipe> {

    private final TypeAdapter<JsonElement> jsonElementTypeAdapter;
    private final TypeAdapter<Recipe> distanceTypeAdapter;
    private final TypeAdapter<Recipe.Feed> feedTypeAdapter;

    public RouteResponseTypeAdapter(Gson gson) {
        this.jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);
        this.distanceTypeAdapter = gson.getAdapter(Recipe.class);
        this.feedTypeAdapter = gson.getAdapter(Recipe.Feed.class);
    }

    @Override
    public void write(JsonWriter out, Recipe value) throws IOException {
        distanceTypeAdapter.write(out,value);
    }

    @Override
    public Recipe read(JsonReader jsonReader) throws IOException {
        Recipe result = new Recipe();
        List<Recipe.Feed> feeds = new ArrayList<>();
        result.response = feeds;
        if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
            JsonObject responseObject = (JsonObject) jsonElementTypeAdapter.read(jsonReader);
            JsonArray response = responseObject.getAsJsonArray("response");
            if (response != null) {
                for (JsonElement element: response) {
                    if(!element.isJsonPrimitive()){
                        feeds.add(feedTypeAdapter.fromJsonTree(element));
                    }
                }
            }
        }
        return result;
    }
}