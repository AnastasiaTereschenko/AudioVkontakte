package com.example.anastasiyaverenich.vkrecipes.futils;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;

import java.util.ArrayList;
import java.util.List;

public class FeedUtils {
    public static ArrayList<Recipe.Photo> getPhotosFromAttachments(List<Recipe.Attach> attaches) {
        ArrayList<Recipe.Photo> photos = new ArrayList<>();
        for (int i = 0; i < attaches.size(); i++) {
            Recipe.Attach attach = attaches.get(i);
            if (attach.photo != null) {
                photos.add(attach.photo);
            }
        }
        return photos;
    }

    public static ArrayList<Recipe.Feed> getFeedsWithoutAds(List<Recipe.Feed> feeds) {
        ArrayList<Recipe.Feed> feedsNew = new ArrayList<>();
        if (!feeds.equals(null)) {
            for (int i = 0; i < feeds.size(); i++) {
                Recipe.Feed feed = feeds.get(i);
                String text = feed.text.toString();
                if (!text.equals("")) {
                    if (((text.charAt(0) >= 'а') && ((text.charAt(0) <= 'я')) ||
                            (((text.charAt(0) >= 'А') && ((text.charAt(0) <= 'Я')))))) {
                        feedsNew.add(feed);
                    }
                    if ((text.endsWith("]")) || (text.toUpperCase().contains("ПОДПИСЫВАЙТЕСЬ")) ||
                            (text.toUpperCase().contains("РЕГИСТРИРУЙТЕСЬ"))||
                            (text.toUpperCase().contains("ДОБАВЬ"))||
                            (text.toUpperCase().contains("ДОБАВЛЯЙТЕСЬ"))||
                            (text.toUpperCase().contains("ВСТУПАЙТЕ"))){
                        feedsNew.remove(feed);
                    }
                }
            }
        }
        return feedsNew;
    }
}