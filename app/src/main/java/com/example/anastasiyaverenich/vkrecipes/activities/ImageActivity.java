package com.example.anastasiyaverenich.vkrecipes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.ui.HackyViewPager;
import com.example.anastasiyaverenich.vkrecipes.adapters.PhotosPagerAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;

import java.util.ArrayList;

public class ImageActivity extends ActionBarActivity {
    public static final String PHOTOS = "Photos";
    public static final String POSITION = "Position";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        ArrayList<Recipe.Photo> photos = (ArrayList<Recipe.Photo>) intent.getSerializableExtra(ImageActivity.PHOTOS);
        int position = intent.getIntExtra(ImageActivity.POSITION, 0);
        //int position = Integer.valueOf(positionStr);
        HackyViewPager viewPager = (HackyViewPager) findViewById(R.id.view_pager);
        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(photos);
        viewPager.setAdapter(photosPagerAdapter);
        viewPager.setCurrentItem(position);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
