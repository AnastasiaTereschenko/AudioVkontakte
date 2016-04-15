package com.example.anastasiyaverenich.vkrecipes.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.application.VersionConfig;
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

    public static void saveImagesOrImageOnDisk(final File src, final Context context,
                                               String loadPhoto, final int numberOfPhoto) {
        DisplayImageOptions options = VkRApplication.get().getOptions();
        File dst = new File(VersionConfig.getSavedImageLocation());
        if (!dst.exists()) {
            dst.mkdirs();
        }
        dst = new File(VersionConfig.getSavedImageLocation() + File.separator + System.currentTimeMillis() + ".jpg");

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
                if (numberOfPhoto == 1) {
                    Toast.makeText(context, "Изображение сохранено в папку " + VersionConfig.getName() +
                            '.', Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Изображения сохранены в папку " + VersionConfig.getName() +
                            '.', Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

    public static void copyLink(String link, android.text.ClipboardManager clipboard) {
        clipboard.setText(link);
    }

    public static void shareLink(Recipe.Feed feed, int index, ArrayList<Recipe.Photo> photos, Context context) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, feed.text.substring(0, index) + " \n" + photos.get(0).src_big);
        context.startActivity(Intent.createChooser(share, "Share Text").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
