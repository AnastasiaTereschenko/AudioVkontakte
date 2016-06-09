package com.example.anastasiyaverenich.vkrecipes.utils;

import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;

import java.util.ArrayList;
import java.util.List;

public class BookmarkCategoryUtils {
    private static List<BookmarkCategory> arrayOfCategoty = new ArrayList<BookmarkCategory>();

    public static List<BookmarkCategory> getArrayOfCategoty() {
        return arrayOfCategoty;
    }

    public static void setArrayOfCategoty(List<BookmarkCategory> arrayOfCategoty) {
        BookmarkCategoryUtils.arrayOfCategoty = arrayOfCategoty;
    }
    public static boolean checkCategories(long id){
        for(int i = 0; i < arrayOfCategoty.size(); i++){
            if(id == arrayOfCategoty.get(i).getCategoryId()) {
                return true;
            }
        }
        return false;
    }
    public static void addCategory(String nameOfCategory){
        VkRApplication.get().getMySQLiteHelper().addCategories(nameOfCategory);
        BookmarkCategory tempBookmarkCategory = VkRApplication.get().getMySQLiteHelper().getLastCategoty();
        arrayOfCategoty.add(tempBookmarkCategory);
    }
    public static void deleteCategory(int idCategory){
        for(int i = 0; i < arrayOfCategoty.size(); i++){
            if(idCategory == arrayOfCategoty.get(i).getCategoryId()) {
                arrayOfCategoty.remove(i);
                break;
            }
        }
        VkRApplication.get().getMySQLiteHelper().deleteCategory(idCategory);
    }
    public static void updateNameOfCategory(String nameOfCategory, int id){
        VkRApplication.get().getMySQLiteHelper().updateNameOfCategory(nameOfCategory,id);

    }
}
