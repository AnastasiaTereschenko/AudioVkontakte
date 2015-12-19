package com.example.anastasiyaverenich.vkrecipes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.HackyViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    public static final String PHOTOS = "Photos";
    public static final String POSITION = "Position";
    TextView countImages;
    ImageButton menuButton;
    ImageButton backButton;
    int currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                        saveImageOnDisk();
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

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public void saveImageOnDisk() {
        DisplayImageOptions options = VkRApplication.get().getOptions();
        final Intent intent = getIntent();
        final ArrayList<Recipe.Photo> photos = (ArrayList<Recipe.Photo>) intent.getSerializableExtra(ImageActivity.PHOTOS);
        final File src = ImageLoader.getInstance().getDiskCache().get(photos.get(currentPosition).src_big);
        File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Recipes");
        if (dst.exists() == false) {
            dst.mkdirs();
            File dst1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "Recipes" + File.separator + System.currentTimeMillis() + ".jpg");
            dst = dst1;
        } else {
            File dst1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "Recipes" + File.separator + System.currentTimeMillis() + ".jpg");
            dst = dst1;
        }
        final File finalDst = dst;

        ImageLoader.getInstance().loadImage(photos.get(currentPosition).src_big, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    copy(src, finalDst);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(ImageActivity.this, "Изображения сохранены в папку Recipes.", Toast.LENGTH_SHORT).show();
            }

            @Override

            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }
    public void copyInClipboard(){

    }
}

