package com.example.anastasiyaverenich.vkrecipes.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.PhotosPagerAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.HackyViewPager;
import com.example.anastasiyaverenich.vkrecipes.utils.FileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity implements PhotoViewAttacher.OnPhotoTapListener {
    public static final String PHOTOS = "Photos";
    public static final String POSITION = "Position";
    public static final String FEED = "Feeds";
    TextView countImages;
    ImageButton menuButton;
    ImageButton backButton;
    HackyViewPager viewPager;
    int currentPosition;
    RelativeLayout imageActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageActionBar = (RelativeLayout) findViewById(R.id.image_action_bar);
        menuButton = (ImageButton) findViewById(R.id.menu_for_image);
        countImages = (TextView) findViewById(R.id.count_images);
        final Intent intent = getIntent();
        final ArrayList<Recipe.Photo> photos = (ArrayList<Recipe.Photo>) intent.getSerializableExtra(ImageActivity.PHOTOS);
        final int position = intent.getIntExtra(ImageActivity.POSITION, 0);
        viewPager = (HackyViewPager) findViewById(R.id.view_pager);
        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(photos, this, this);
        viewPager.setAdapter(photosPagerAdapter);
        viewPager.setCurrentItem(position);
        currentPosition = position;
        String tempCountImages = String.valueOf(position + 1) + " из " + String.valueOf(photos.size());
        countImages.setText(tempCountImages);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        menuButton.setOnClickListener(viewClickListener);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentPosition = arg0;
                String tempCountImages = String.valueOf(currentPosition + 1) + " из " + String.valueOf(photos.size());
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
    public void onPhotoTap(View view, float v, float v1) {
        if (imageActionBar.getVisibility() == View.INVISIBLE) {
            imageActionBar.setVisibility(View.VISIBLE);
        } else {
            imageActionBar.setVisibility(View.INVISIBLE);
        }
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        final Intent intent = getIntent();
        final ArrayList<Recipe.Photo> photos = (ArrayList<Recipe.Photo>) intent.getSerializableExtra
                (ImageActivity.PHOTOS);
        final Recipe.Feed feed = (Recipe.Feed) intent.getSerializableExtra(ImageActivity.FEED);
        PopupMenu popupMenu = new PopupMenu(this , v);
        popupMenu.inflate(R.menu.menu_for_image);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save_image:
                        String savePhoto = photos.get(currentPosition).src_big;
                        File src = ImageLoader.getInstance().getDiskCache().get(photos.get(
                                currentPosition).src_big);
                        FileUtils.saveImagesOrImageOnDisk(src, getApplicationContext(), savePhoto,1);
                        return true;
                    case R.id.copy_link:
                        String copyPhoto = photos.get(currentPosition).src_big;
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        FileUtils.copyLink(copyPhoto, clipboard);
                        Toast.makeText(getApplicationContext(),
                                "Ссылка скопирована в буфер обмена",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.send_link_on_image:
                        int index = feed.text.toString().indexOf("<br>");
                        FileUtils.shareLink(feed, index, photos, getApplicationContext());
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}

