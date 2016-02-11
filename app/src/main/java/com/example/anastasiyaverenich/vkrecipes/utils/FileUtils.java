package com.example.anastasiyaverenich.vkrecipes.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
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

public class FileUtils {
    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
    public static void saveImagesOnDisk(final File src, final Context context, String loadPhoto)
    {
        DisplayImageOptions options  = VkRApplication.get().getOptions();
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

        ImageLoader.getInstance().loadImage(loadPhoto, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason
                    failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    copy(src, finalDst);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(context, "Изображения сохранены в папку Recipes.", Toast.LENGTH_SHORT).show();
            }

            @Override

            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }
    public static void saveImageOnDisk(final File src, final Context context, String loadPhoto)
    {
        DisplayImageOptions options  = VkRApplication.get().getOptions();
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

        ImageLoader.getInstance().loadImage(loadPhoto, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason
                    failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    copy(src, finalDst);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(context, "Изображение сохранено в папку Recipes.", Toast.LENGTH_SHORT).show();
            }

            @Override

            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }
    public static void copyLink(String link,  android.text.ClipboardManager clipboard){
        clipboard.setText(link);
    }
    public static void shareLink(Recipe.Feed feed, int index, ArrayList<Recipe.Photo> photos, Context context ){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, feed.text.substring(0, index) + " \n" + photos.get(0).src_big);
        context.startActivity(Intent.createChooser(share, "Share Text").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
