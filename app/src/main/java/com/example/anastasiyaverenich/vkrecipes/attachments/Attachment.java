package com.example.anastasiyaverenich.vkrecipes.attachments;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;

public class Attachment implements ThumbAttachment{
    public Recipe.Photo photo;
    private int displayH;
    private int displayW;


    @Override
    public int getHeight() {
        return displayH;
    }

    @Override
    public float getRatio() {
        if ((photo.width > 0) && (photo.height > 0)) {
            return  (1.0f*photo.width / photo.height);
        }
        return -1.0f;
    }

    @Override
    public String getThumbURL() {
        return null;
    }

    @Override
    public int getWidth() {
        return displayW;
    }

    @Override
    public int getWidth(char paramChar) {
        return photo.width;
    }

    @Override
    public void setPaddingAfter(boolean paramBoolean) {
    }

    @Override
    public void setViewSize(float width, float height, boolean breakAfter, boolean floating) {
        displayW = Math.round(width);
        displayH = Math.round(height);
    }
}
