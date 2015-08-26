package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.activities.ImageActivity;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.futils.BookmarkUtils;
import com.example.anastasiyaverenich.vkrecipes.futils.FeedUtils;
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
import java.util.List;

public class FeedAdapter extends ArrayAdapter<Recipe.Feed> {
    private final Context mContext;
    private final int mResourceId;
    private DisplayImageOptions options;
    private List<Recipe.Feed> feeds;
    private boolean isBookmark;

    public FeedAdapter(Context context, int resource, List<Recipe.Feed> objects, boolean isBookmark) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
        this.feeds = objects;
        options = VkRApplication.get().getOptions();
        this.isBookmark = isBookmark;
    }

    static class ViewHolder {
        TextView textName;
        TextView textDescription;
        LinearLayout container;
        ImageView imageViewBM;
        FrameLayout flBookmarkImage;
        FrameLayout flShareImage;
        FrameLayout flSaveImage;
    }
    @Override
    public int getCount() {
        return feeds.size();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
            viewHolder.textDescription = (TextView) convertView.findViewById(R.id.rli_tv_description_list);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.rli_tv_name_list);
            viewHolder.container = (LinearLayout) convertView.findViewById(R.id.rli_ll_images_container);
            viewHolder.imageViewBM = (ImageView) convertView.findViewById(R.id.rli_iv_panel_favourite_item);
            viewHolder.flBookmarkImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_favourite_item);
            viewHolder.flShareImage = (FrameLayout)convertView.findViewById(R.id.rli_fl_panel_share_item);
            viewHolder.flSaveImage = (FrameLayout)convertView.findViewById(R.id.rli_fl_panel_save_images);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Recipe.Feed feed = feeds.get(position);
        int size = feed.text.toString().length();
        final int index = feed.text.toString().indexOf("<br>");;
        if (feed.text.toString() != "") {
            if (index == -1) {
                viewHolder.textName.setText(Html.fromHtml(feed.text.toString()));
                viewHolder.textDescription.setText(" ");

            } else {
                String Name = feed.text.substring(0, index);
                String Description = feed.text.substring(index, size);
                viewHolder.textName.setText(Html.fromHtml(Name.toString()));
                viewHolder.textDescription.setText(Html.fromHtml(Description.toString()));
            }
        } else {
            viewHolder.textName.setText(" ");
            viewHolder.textDescription.setText(" ");
        }
        // image = new ImageView(mContext);
        viewHolder.container.removeAllViews();
        final ArrayList<Recipe.Photo> photos = FeedUtils.getPhotosFromAttachments(feed.attachments);
        for (int x = 0; x < photos.size(); x++) {
            final ImageView image = new ImageView(mContext);
            image.setBackgroundColor(0xfff0f0f0);
            Log.i("TAG", "index :" + feed.attachments.size());
            final Recipe.Photo photo = photos.get(x);
            Log.d("Image", photo.src_big);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // image.setAdjustViewBounds(true);
            image.setLayoutParams(lp);
            lp.setMargins(0, 16, 0, 0);
            viewHolder.container.addView(image);
            if (viewHolder.container.getWidth() != 0) {
                setImageViewHeight(viewHolder, image, photo);
            } else {
                viewHolder.container.post(new Runnable() {
                    @Override
                    public void run() {
                        if (viewHolder.container.getWidth() == 0) {
                            return;
                        }
                        setImageViewHeight(viewHolder, image, photo);
                    }
                });
            }
            ImageLoader.getInstance().displayImage(photo.src_big, image, options);
            final int finalX = x;
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("OnImageButton", "Clicked");
                    System.err.println(finalX);
                    Intent newActivity = new Intent(mContext, ImageActivity.class);
                    newActivity.putExtra(ImageActivity.POSITION, finalX);
                    newActivity.putExtra(ImageActivity.PHOTOS, photos);
                   // mContext.startActivity(newActivity);
                    Toast.makeText(mContext,
                            ImageLoader.getInstance().getDiskCache().get(photo.src_big).getPath(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        viewHolder.flSaveImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final File src = ImageLoader.getInstance().getDiskCache().get(photos.get(0).src_big);
                File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Recipes" );
                if(dst.exists()==false)
                {
                    dst.mkdirs();
                    File dst1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator +"Recipes"+ File.separator + System.currentTimeMillis()+".jpg");
                    dst = dst1;
                }
                else{
                    File dst1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator +"Recipes"+ File.separator + System.currentTimeMillis()+".jpg");
                    dst = dst1;
                }
                final File finalDst = dst;
                ImageLoader.getInstance().loadImage(photos.get(0).src_big, options, new ImageLoadingListener() {
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
                    }

                    @Override

                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });

            }
        });
        viewHolder.flShareImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, feed.text.substring(0, index) + " \n" + photos.get(0).src_big);
                mContext.startActivity(Intent.createChooser(share, "Share Text"));
            }
        });
        if (BookmarkUtils.checkBookmarks(feed.id)){
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
        }
        else {
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        viewHolder.flBookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BookmarkUtils.checkBookmarks(feed.id)) {
                    BookmarkUtils.deleteBookmark(feed);
                    viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
                    if (isBookmark) {
                        notifyDataSetChanged();
                    }
                } else {
                    BookmarkUtils.addBookmark(feed);
                    viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
                    if (isBookmark) {
                        notifyDataSetChanged();
                    }
                }
            }
        });

        viewHolder.container.requestLayout();
        //  else {
        //    imageView.setVisibility(View.GONE);
        //}

        return convertView;
    }

    private void setImageViewHeight(ViewHolder viewHolder, ImageView image, Recipe.Photo photo) {
        int relativeHeight = (int) ((float) photo.height / photo.width * viewHolder.container.getWidth());
        ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
        layoutParams.height = relativeHeight;
        image.setLayoutParams(layoutParams);
    }

}