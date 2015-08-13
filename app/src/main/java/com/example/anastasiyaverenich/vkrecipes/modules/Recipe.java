package com.example.anastasiyaverenich.vkrecipes.modules;

import java.io.Serializable;
import java.util.List;

public class Recipe {
    public List<Feed> response;
    public static class Feed {
        public List<Attach> attachments;
        public String text;
    }
    public static class Attach {
        public Photo photo;
    }
    public static class Photo implements Serializable {
        public String src_big;
        public int height;
        public int width;
    }
}
