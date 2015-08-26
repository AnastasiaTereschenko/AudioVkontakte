package com.example.anastasiyaverenich.vkrecipes.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    Gson gson = new Gson();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "FeedDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create feed table
        String CREATE_BOOK_TABLE = "CREATE TABLE feeds ( " +
                "id INTEGER PRIMARY KEY, " +
                "content TEXT )";

        // create feeds table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older feeds table if existed
        db.execSQL("DROP TABLE IF EXISTS feeds");

        // create fresh feeds table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    // Feeds table name
    private static final String TABLE_FEEDS = "feeds";

    // Feeds Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CONTENT = "content";

    public void addFeed(Recipe.Feed feed){
        String tempSaveDb;
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        tempSaveDb = gson.toJson(feed);
        Log.d("addFeed", tempSaveDb);
        values.put(KEY_ID, feed.id);
        values.put(KEY_CONTENT, tempSaveDb);
        // 3. insert
        db.insert(TABLE_FEEDS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        // 4. close
        db.close();
    }

    // Get All Feeds
    public List<Recipe.Feed> getAllFeeds() {
        List<Recipe.Feed> feeds = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_FEEDS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build feed and add it to list
        if (cursor.moveToFirst()) {
            do {
                String tempVarForDisplay = cursor.getString(1);
                Recipe.Feed tempFeed = gson.fromJson(tempVarForDisplay, Recipe.Feed.class);
                // Add feed to feeds
                feeds.add(tempFeed);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBooks()", feeds.toString());
        // return feeds
        return feeds;
    }

    // Deleting single feed
    public void deleteFeed(Recipe.Feed feed) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_FEEDS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(feed.id) });

        // 3. close
        db.close();

        Log.d("deleteFeed", feed.toString());

    }
}

