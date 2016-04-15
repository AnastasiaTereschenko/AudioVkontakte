package com.example.anastasiyaverenich.vkrecipes.application;

import com.example.anastasiyaverenich.vkrecipes.BuildConfig;

public class Config {
    private static boolean isPsyhology(){
        return BuildConfig.FLAVOR == "vkrecipes";
    }

    public static boolean isRecipes(){
        return  BuildConfig.FLAVOR == "vkrecipes";
    }
}
