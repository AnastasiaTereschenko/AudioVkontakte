package com.example.anastasiyaverenich.vkrecipes.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    Gson gson = new Gson();
    private static final int DATABASE_VERSION = 39;
    private static final String DATABASE_NAME = "DB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FEED_TABLE = "CREATE TABLE feeds ( " +
                "groupId INTEGER PRIMARY KEY, " +
                "contentFeed TEXT )";
        String CREATE_BOOKMARK_TABLE = "CREATE TABLE bookmarks ( " +
                "bookmarkId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "contentBookmark TEXT, categoryId INTEGER)";
        String CREATE_CATEGORY_TABLE = "CREATE TABLE categories ( " +
                "categoryId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "nameOfCategory TEXT )";
        db.execSQL(CREATE_FEED_TABLE);
        db.execSQL(CREATE_BOOKMARK_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        String INSERT_CATEGORY_TABLE_1 = "INSERT INTO categories (nameOfCategory, categoryId) " +
                "VALUES ('Первые блюда', 0)";
        db.execSQL(INSERT_CATEGORY_TABLE_1);
        String INSERT_CATEGORY_TABLE_2 = "INSERT INTO categories (nameOfCategory, categoryId) " +
                "VALUES ('Вторые блюда', 1)";
        db.execSQL(INSERT_CATEGORY_TABLE_2);
        String INSERT_CATEGORY_TABLE_3 = "INSERT INTO categories (nameOfCategory, categoryId) " +
                "VALUES ('Салаты', 2)";
        db.execSQL(INSERT_CATEGORY_TABLE_3);
        String INSERT_CATEGORY_TABLE_4 = "INSERT INTO categories (nameOfCategory, categoryId) " +
                "VALUES ('Десерты', 3)";
        db.execSQL(INSERT_CATEGORY_TABLE_4);
        String INSERT_CATEGORY_TABLE_5 = "INSERT INTO categories (nameOfCategory, categoryId) " +
                "VALUES ('Напитки', 4)";
        db.execSQL(INSERT_CATEGORY_TABLE_5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS feeds");
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        db.execSQL("DROP TABLE IF EXISTS categories");

        this.onCreate(db);
    }

    private static final String TABLE_BOOKMARKS = "bookmarks";
    private static final String KEY_ID_BOOKMARK = "bookmarkId";
    private static final String KEY_CONTENT_BOOKMARK = "contentBookmark";
    private static final String TABLE_FEEDS = "feeds";
    private static final String KEY_ID_GROUP = "groupId";
    private static final String KEY_CONTENT_FEED = "contentFeed";
    private static final String KEY_ID_CATEGORIES_FOR_BOOKMARK = "categoryId";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String KEY_ID_CATEGORY = "categoryId";
    private static final String KEY_NAME_OF_CATEGORY = "nameOfCategory";

    public void addFeeds(List<Recipe.Feed> feeds, int groupId) {
        String tempSaveDb;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        tempSaveDb = gson.toJson(feeds);
        Log.d("addFeed", tempSaveDb);
        values.put(KEY_ID_GROUP, groupId);
        values.put(KEY_CONTENT_FEED, tempSaveDb);
        db.insert(TABLE_FEEDS, null, values);
        db.close();
    }

    public List<Recipe.Feed> getAllFeeds(int groupId) {
        List<Recipe.Feed> feeds;
        String id = Integer.toString(groupId);
        Log.e("getAllFeeds()", "SELECT  * FROM " + TABLE_FEEDS + " WHERE " + KEY_ID_GROUP + " = '" + groupId + "'");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_FEEDS + " WHERE " + KEY_ID_GROUP + " = ?", new String[]{id});
        if (cursor.moveToFirst()) {
            String tempVarForDisplay = cursor.getString(1);
            Type listType = new TypeToken<ArrayList<Recipe.Feed>>() {
            }.getType();
            feeds = new Gson().fromJson(tempVarForDisplay, listType);
        } else {
            return null;
        }
        return feeds;
    }

    public void updateFeeds(List<Recipe.Feed> feeds, int groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tempSaveDb;
        ContentValues values = new ContentValues();
        tempSaveDb = gson.toJson(feeds);
        values.put(KEY_CONTENT_FEED, tempSaveDb);
        String id = Integer.toString(groupId);
        db.update(TABLE_FEEDS, values, KEY_ID_GROUP + " = ?", new String[]{id});
    }

    //-----------------------------Bookmarks-----------------------------------------------
    public void addBookmarks(Recipe.Feed feed, int categoryId) {
        String tempSaveDb;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        tempSaveDb = gson.toJson(feed);
        values.put(KEY_ID_BOOKMARK, feed.id);
        values.put(KEY_CONTENT_BOOKMARK, tempSaveDb);
        values.put(KEY_ID_CATEGORIES_FOR_BOOKMARK, categoryId);
        db.insert(TABLE_BOOKMARKS, null, values);
        db.close();
    }

    public List<Recipe.Feed> getAllBookmarks() {
        List<Recipe.Feed> feeds = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARKS, null, null,
                null, null, null, KEY_ID_BOOKMARK + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String tempVarForDisplay = cursor.getString(1);
                Recipe.Feed tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
                feeds.add(tempFeed);
            } while (cursor.moveToNext());
        }
        return feeds;
    }

    public List<Recipe.Feed> getBookmarksForCertainCategory(int categoryId) {
        List<Recipe.Feed> feeds = new ArrayList<>();
        String id = Integer.toString(categoryId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKMARKS + " WHERE " + KEY_ID_CATEGORIES_FOR_BOOKMARK +
                " = ?" + " ORDER BY " + KEY_ID_BOOKMARK + " DESC", new String[]{id});
        if (cursor.moveToFirst()) {
            do {
                String tempVarForDisplay = cursor.getString(1);
                Recipe.Feed tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
                feeds.add(tempFeed);
            } while (cursor.moveToNext());
        }
        return feeds;
    }

    public void deleteBookmark(Recipe.Feed feed) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS, KEY_ID_BOOKMARK + " = ?", new String[]{String.valueOf(feed.id)});
        db.close();
    }

    public List<Recipe.Feed> searchBookmarkForCertainCategory(String stringForSearch, int bookmarkCategory){
        List<Recipe.Feed> feeds = new ArrayList<>();
        String id = Integer.toString(bookmarkCategory);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(" SELECT * FROM " + TABLE_BOOKMARKS + " WHERE " + KEY_CONTENT_BOOKMARK +
                " LIKE '%" + stringForSearch + "%'"+ " AND " +  KEY_ID_CATEGORIES_FOR_BOOKMARK
                +" = ?" + " ORDER BY " + KEY_ID_BOOKMARK + " DESC", new String[]{id}) ;
        if (cursor.moveToFirst()) {
            do {
                String tempVarForDisplay = cursor.getString(1);
                Recipe.Feed tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
                feeds.add(tempFeed);
            } while (cursor.moveToNext());
        }
        return feeds;
    }
    public List<Recipe.Feed> searchBookmarkForALLCategory(String stringForSearch){
        List<Recipe.Feed> feeds = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT * FROM " + TABLE_BOOKMARKS + " WHERE " + KEY_CONTENT_BOOKMARK +
                " LIKE '%" + stringForSearch + "%'"+ " ORDER BY " + KEY_ID_BOOKMARK + " DESC";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                String tempVarForDisplay = cursor.getString(1);
                Recipe.Feed tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
                feeds.add(tempFeed);
            } while (cursor.moveToNext());
        }
        return feeds;
    }

    //-----------------------------Categories-----------------------------------------------
    public void addCategories(String nameOfCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String query = "SELECT * FROM " + TABLE_CATEGORIES;
        Cursor cursor = db.rawQuery(query, null);
        values.put(KEY_NAME_OF_CATEGORY, nameOfCategory);
        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public void updateNameOfCategory(String newNameOfCategory, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_OF_CATEGORY, newNameOfCategory);
        String idString = Integer.toString(id);
        db.update(TABLE_CATEGORIES, values, KEY_ID_CATEGORY + " = ?", new String[]{idString});
    }

    public List<BookmarkCategory> getAllCategoties() {
        List<BookmarkCategory> arrayOfCategoty = new ArrayList<BookmarkCategory>();
        String query = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BookmarkCategory objectOfCategory = new BookmarkCategory();
                objectOfCategory.setCategoryId(cursor.getInt((cursor.getColumnIndex(KEY_ID_CATEGORY))));
                objectOfCategory.setNameOfCategory(cursor.getString((cursor.getColumnIndex(KEY_NAME_OF_CATEGORY))));
                arrayOfCategoty.add(objectOfCategory);
            } while (cursor.moveToNext());
        }
        return arrayOfCategoty;
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_ID_CATEGORY + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public BookmarkCategory getLastCategoty() {
        BookmarkCategory lastCategoty = new BookmarkCategory();
        String query = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToLast()) {
            BookmarkCategory objectOfCategory = new BookmarkCategory();
            objectOfCategory.setCategoryId(cursor.getInt((cursor.getColumnIndex(KEY_ID_CATEGORY))));
            objectOfCategory.setNameOfCategory(cursor.getString((cursor.getColumnIndex(KEY_NAME_OF_CATEGORY))));
            lastCategoty = objectOfCategory;
        }
        return lastCategoty;
    }
}

