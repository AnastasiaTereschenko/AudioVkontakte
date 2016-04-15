package com.example.anastasiyaverenich.vkrecipes.application;

import android.os.Environment;

import java.io.File;

public class VersionConfig {
    public static String getSavedImageLocation(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getName();
    }

    public static String getName(){
        String name;
        if(Config.isRecipes()){
           return name  = "Recipes";
        } else{
        return name = "Psyhology";
    }
    }

}
