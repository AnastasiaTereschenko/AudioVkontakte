package com.example.anastasiyaverenich.vkrecipes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.PhotosPagerAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.HackyViewPager;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    public static final String PHOTOS = "Photos";
    public static final String POSITION = "Position";
    TextView countImages;
    ImageButton menuButton;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        menuButton = (ImageButton) findViewById(R.id.menu_for_image);
        countImages = (TextView) findViewById(R.id.count_images);
        final Intent intent = getIntent();
        final ArrayList<Recipe.Photo> photos = (ArrayList<Recipe.Photo>) intent.getSerializableExtra(ImageActivity.PHOTOS);
        final int position = intent.getIntExtra(ImageActivity.POSITION, 0);
        HackyViewPager viewPager = (HackyViewPager) findViewById(R.id.view_pager);
        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(photos, this);
        viewPager.setAdapter(photosPagerAdapter);
        viewPager.setCurrentItem(position);
        String tempCountImages = String.valueOf(position + 1) + " из " + String.valueOf(photos.size());
        countImages.setText(tempCountImages);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        menuButton.setOnClickListener(viewClickListener);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                int curruntPosition = arg0;
                String tempCountImages = String.valueOf(curruntPosition + 1) + " из " + String.valueOf(photos.size());
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

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_for_image);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.save_image:
                        Toast.makeText(getApplicationContext(),
                                "Вы выбрали сохранить изображение",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.copy_link:
                        Toast.makeText(getApplicationContext(),
                                "Вы выбрали скопировать ссылку ",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.send_link_on_image:
                        Toast.makeText(getApplicationContext(),
                                "Вы выбрали поделиться ссылкой",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }
}
