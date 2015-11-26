package com.example.anastasiyaverenich.vkrecipes.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.example.anastasiyaverenich.vkrecipes.SQLite.MySQLiteHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

public class VkRApplication extends Application {
    private MySQLiteHelper mySQLiteHelper;
    private DisplayImageOptions options;
    private static VkRApplication instance;
    public static VkRApplication get(){
        return instance;
    }
    public DisplayImageOptions getOptions() {
        return options;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("unused")
    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        initImageLoader(getApplicationContext());
        mySQLiteHelper = new MySQLiteHelper(getApplicationContext());
    }

    public void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        ImageLoader.getInstance().init(config.build());
        L.writeLogs(false);
        L.writeDebugLogs(false);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();
    }

    public MySQLiteHelper getMySQLiteHelper() {
        return mySQLiteHelper;
    }
}