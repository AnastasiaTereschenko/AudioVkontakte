package com.example.anastasiyaverenich.vkrecipes.modules;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeFromInstagram {
    public List<Feed> data;
    public static class Feed{
        public Images images;
    }
    public static class Images{
        @SerializedName("standard_resolution")
        public Resolution standardResolution;
    }
    public static class Resolution{
        public String url;
    }
}
