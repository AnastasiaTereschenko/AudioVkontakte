package com.example.anastasiyaverenich.vkrecipes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.PhotosPagerAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.HackyViewPager;

import java.util.ArrayList;

public class ImageActivity extends ActionBarActivity {
    public static final String PHOTOS = "Photos";
    public static final String POSITION = "Position";
    TextView countImages;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        countImages = (TextView) findViewById(R.id.count_images);
        final Intent intent = getIntent();
        final ArrayList<Recipe.Photo> photos = (ArrayList<Recipe.Photo>) intent.getSerializableExtra(ImageActivity.PHOTOS);
        final int position = intent.getIntExtra(ImageActivity.POSITION, 0);
        HackyViewPager viewPager = (HackyViewPager) findViewById(R.id.view_pager);
        ImageButton imageButton = (ImageButton) findViewById(R.id.back_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(photos, this);
        viewPager.setAdapter(photosPagerAdapter);
        viewPager.setCurrentItem(position);
        String tempCountImages = String.valueOf(position+1) +" из "+ String.valueOf(photos.size());
        countImages.setText(tempCountImages);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                int curruntPosition = arg0;
                String tempCountImages = String.valueOf(curruntPosition+1) +" из "+ String.valueOf(photos.size());
                countImages.setText(tempCountImages);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
