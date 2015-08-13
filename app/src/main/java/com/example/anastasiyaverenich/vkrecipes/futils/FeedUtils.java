package com.example.anastasiyaverenich.vkrecipes.futils;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;

import java.util.ArrayList;
import java.util.List;

public class FeedUtils {
    public static ArrayList<Recipe.Photo> getPhotosFromAttachments(List<Recipe.Attach> attaches) {
        ArrayList<Recipe.Photo> photos = new ArrayList<>();
        for (int i = 0; i < attaches.size(); i++) {
            Recipe.Attach attach = attaches.get(i);
            if (attach.photo != null){
                photos.add(attach.photo);
            }
        }
        return photos;
    }
}
