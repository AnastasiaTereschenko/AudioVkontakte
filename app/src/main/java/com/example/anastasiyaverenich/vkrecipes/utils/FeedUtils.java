package com.example.anastasiyaverenich.vkrecipes.utils;

import com.example.anastasiyaverenich.vkrecipes.SQLite.MySQLiteHelper;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;

import java.util.ArrayList;
import java.util.List;

public class FeedUtils {
    private static List<Recipe.Feed> feeds;
    private static MySQLiteHelper sqLiteHelper = VkRApplication.get().getMySQLiteHelper();

    public static List<Recipe.Feed> getFeeds() {
        return feeds;
    }

    public static void setFeeds(List<Recipe.Feed> feeds) {
        FeedUtils.feeds = feeds;
    }

    public static void addFeeds(List<Recipe.Feed> feeds, int groupId) {
        sqLiteHelper.addFeeds(feeds, groupId);
    }

    public static void updateFeeds(List<Recipe.Feed> feeds, int idGroup) {
        sqLiteHelper.updateFeeds(feeds, idGroup);
    }

    public static void saveRefreshData(List<Recipe.Feed> feeds, int groupId) {
        if (sqLiteHelper.getAllFeeds(groupId) == null) {
            addFeeds(feeds, groupId);
        } else {
            updateFeeds(feeds, groupId);
        }
    }

    public static ArrayList<Recipe.Photo> getPhotosFromAttachments(List<Recipe.Attach> attaches) {
        ArrayList<Recipe.Photo> photos = new ArrayList<>();
        if (attaches == null) {
            return null;
        }
        for (int i = 0; i < attaches.size(); i++) {
            Recipe.Attach attach = attaches.get(i);
            if (attach.photo != null) {
                photos.add(attach.photo);
            }
        }
        return photos;
    }

    public static Boolean isPhotosFromAttachments(List<Recipe.Attach> attaches) {
        Boolean isPhoto = false;
        for (int i = 0; i < 1; i++) {
            Recipe.Attach attach = attaches.get(i);
            if (attach.photo != null) {
                isPhoto = true;
            } else {
                isPhoto = false;
            }
        }
        return isPhoto;
    }

    public static ArrayList<Recipe.Feed> getFeedsWithoutAds(List<Recipe.Feed> feeds) {
        ArrayList<Recipe.Feed> feedsNew = new ArrayList<>();
        if (!feeds.equals(null)) {
            for (int i = 0; i < feeds.size(); i++) {
                Recipe.Feed feed = feeds.get(i);
                String text = feed.text;
                if (!text.equals("")) {
                    if (text.charAt(0) == '#') {
                        if(text.contains("<br>")){
                        int index = text.indexOf("<br>");
                        int size = text.length();
                        String feedTextNew = feed.text.substring(index, size);
                        int j=0;
                        while(feedTextNew.subSequence(j,j+4).equals("<br>")){
                            int split=j+4;
                            feedTextNew = feedTextNew.substring(split, feedTextNew.length());
                            feedTextNew = feedTextNew.trim();
                        }
                        feed.text = feedTextNew;
                        feedsNew.add(feed);
                    }
                        else{
                            feedsNew.add(feed);
                        }
                    }
                   /* else {
                        feedsNew.add(feed);
                    }*/
                    if (((text.charAt(0) >= 'а') && ((text.charAt(0) <= 'я')) ||
                            (((text.charAt(0) >= 'А') && ((text.charAt(0) <= 'Я')))))) {
                        feedsNew.add(feed);
                    }
                    if ((text.endsWith("]")) || (text.toUpperCase().contains("ПОДПИСЫВАЙТЕСЬ")) ||
                            (text.toUpperCase().contains("РЕГИСТРИРУЙТЕСЬ")) ||
                            (text.toUpperCase().contains("ДОБАВЬ")) ||
                            (text.toUpperCase().contains("ДОБАВЛЯЙТЕСЬ")) ||
                            (text.toUpperCase().contains("ВСТУПАЙТЕ")) ||
                            (text.toUpperCase().contains("ЛАЙК"))) {
                        feedsNew.remove(feed);
                    }
                    if (feed.attachments != null) {
                        if (!isPhotosFromAttachments(feed.attachments)) {
                            feedsNew.remove(feed);
                        }
                    }

                }
            }
        }
        return feedsNew;
    }

}