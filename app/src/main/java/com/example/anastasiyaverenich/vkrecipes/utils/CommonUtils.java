package com.example.anastasiyaverenich.vkrecipes.utils;


import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;

public class CommonUtils {

    public static float displayDensity = VkRApplication.get().getResources().getDisplayMetrics().density;


    public static int scale(float paramFloat)
    {
        return Math.round(paramFloat * displayDensity);
    }
}
