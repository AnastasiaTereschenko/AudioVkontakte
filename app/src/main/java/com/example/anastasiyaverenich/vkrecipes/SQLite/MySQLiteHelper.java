package com.example.anastasiyaverenich.vkrecipes.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    Gson gson = new Gson();
    // Database Version
    private static final int DATABASE_VERSION = 15;
    // Database Name
    private static final String DATABASE_NAME = "DB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKMARK_TABLE = "CREATE TABLE bookmarks ( " +
                "idBookmark INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "contentBookmark TEXT )";
        String CREATE_FEED_TABLE = "CREATE TABLE feeds ( " +
                "groupId INTEGER PRIMARY KEY, " +
                "contentFeed TEXT )";
        db.execSQL(CREATE_FEED_TABLE);
        db.execSQL(CREATE_BOOKMARK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older feeds table if existed
        db.execSQL("DROP TABLE IF EXISTS feeds");
        db.execSQL("DROP TABLE IF EXISTS bookmarks");


        // create fresh feeds table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    // Bookmark table name
    private static final String TABLE_BOOKMARKS = "bookmarks";

    // Bookmark Table Columns names
    private static final String KEY_ID_BOOKMARK = "idBookmark";
    private static final String KEY_CONTENT_BOOKMARK = "contentBookmark";
    // Feed table name
    private static final String TABLE_FEEDS = "feeds";
    // Feed Table Columns names
    private static final String KEY_ID_GROUP = "groupId";
    private static final String KEY_CONTENT_FEED = "contentFeed";

    public void addFeeds(List<Recipe.Feed>  feeds, int groupId){
        String tempSaveDb;
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        tempSaveDb = gson.toJson(feeds);
        Log.d("addFeed", tempSaveDb);
        values.put(KEY_ID_GROUP, groupId);
        values.put(KEY_CONTENT_FEED, tempSaveDb);
        // 3. insert
        db.insert(TABLE_FEEDS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        // 4. close
        db.close();
    }
    // Get All Feeds
    public List<Recipe.Feed> getAllFeeds(int groupId) {
        List<Recipe.Feed> feeds;
        String id = Integer.toString(groupId);
        //String query = "SELECT  * FROM " + TABLE_FEEDS + " WHERE " +  KEY_ID_GROUP + " = '" + groupId +"'" ;
        // 2. get reference to writable DB
        Log.e("getAllFeeds()", "SELECT  * FROM " + TABLE_FEEDS + " WHERE " +  KEY_ID_GROUP + " = '" + groupId +"'");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_FEEDS + " WHERE " + KEY_ID_GROUP + " = ?", new String[] {id});
        if (cursor.moveToFirst()) {
            String tempVarForDisplay = cursor.getString(1);
            Type listType = new TypeToken<ArrayList<Recipe.Feed>>() {}.getType();
            feeds = new Gson().fromJson(tempVarForDisplay, listType);
            //List<Recipe.Feed> tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
            // Add feed to feeds
        }
        else {
            return null;
        }
        return feeds;
    }

    public void updateFeeds(List<Recipe.Feed>  feeds, int groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tempSaveDb;
        ContentValues values = new ContentValues();
        tempSaveDb = gson.toJson(feeds);
        values.put(KEY_CONTENT_FEED, tempSaveDb);
        String id = Integer.toString(groupId);
        db.update(TABLE_FEEDS, values, KEY_ID_GROUP + " = ?", new String[] {id});
    }
    //-----------------------------Bookmarks-----------------------------------------------
    public void addBookmarks(Recipe.Feed feed){
        String tempSaveDb;
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        String query = "SELECT * FROM " + TABLE_BOOKMARKS ;
        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);
            tempSaveDb = gson.toJson(feed);
            values.put(KEY_CONTENT_BOOKMARK, tempSaveDb);
            // 3. insert
            db.insert(TABLE_BOOKMARKS, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            // 4. close
        //onUpgrade(db, version, mNewVersion);
            db.close();
    }

    // Get All Feeds
    public List<Recipe.Feed> getAllBookmarks() {
        List<Recipe.Feed> feeds = new ArrayList<>();
        // 1. build the query
        String query = "SELECT * FROM " + TABLE_BOOKMARKS ;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARKS, null, null,
                null, null, null, KEY_ID_BOOKMARK + " DESC", null);
        //Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build feed and add it to list
        if (cursor.moveToFirst()) {
            do {
                String tempVarForDisplay = cursor.getString(1);
                Recipe.Feed tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
                // Add feed to feeds
                feeds.add(tempFeed);
            } while (cursor.moveToNext());
        }
        return feeds;
    }

    // Deleting single feed
    public void deleteBookmark(Recipe.Feed feed) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKMARKS,
                KEY_ID_BOOKMARK+" = ?",
                new String[] { String.valueOf(feed.id) });

        // 3. close
        db.close();
    }
}

