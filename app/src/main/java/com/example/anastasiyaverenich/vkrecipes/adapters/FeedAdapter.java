package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.activities.ImageActivity;
import com.example.anastasiyaverenich.vkrecipes.futils.FeedUtils;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends ArrayAdapter<Recipe.Feed> {
    private final Context mContext;
    private final int mResourceId;
    private DisplayImageOptions options;
    private List<Recipe.Feed> feeds;

    public FeedAdapter(Context context, int resource, List<Recipe.Feed> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
        this.feeds = objects;
    }

    static class ViewHolder {
        TextView textName;
        TextView textDescription;
        LinearLayout container;
    }

    @Override
    public int getCount() {
        return feeds.size();
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.container.removeAllViews();
        final Recipe.Feed feedAll = getItem(position);
        final Recipe.Feed feed = feeds.get(position);
        int size = feed.text.toString().length();
        if (feed.text.toString() != "") {
                int index = feed.text.toString().indexOf("<br>");
                if (index == -1) {
                    viewHolder.textName.setText(Html.fromHtml(feed.text.toString()));
                    viewHolder.textDescription.setText(" ");

                } else {
                    String Name = feed.text.toString().substring(0, index);
                    String Description = feed.text.toString().substring(index, size);
                    Name = feed.text.toString().substring(0, index);
                    Description = feed.text.toString().substring(index, size);
                    viewHolder.textName.setText(Html.fromHtml(Name.toString()));
                    viewHolder.textDescription.setText(Html.fromHtml(Description.toString()));
                }
        //    } else {
          //      feeds.remove(position);
            //}

        }
        else{ viewHolder.textName.setText(" ");
            viewHolder.textDescription.setText(" ");}

        // image = new ImageView(mContext);
        final ArrayList<Recipe.Photo> photos= FeedUtils.getPhotosFromAttachments(feed.attachments);
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
            viewHolder.container.post(new Runnable() {
                @Override
                public void run() {
                    if (viewHolder.container.getWidth() == 0) {
                        return;
                    }
                    int relativeHeight = (int) ((float) photo.height / photo.width * viewHolder.container.getWidth());
                    android.view.ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
                    layoutParams.height = relativeHeight;
                    image.setLayoutParams(layoutParams);
                }
            });
            image.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (image.getWidth() == 0) {
                        if (viewHolder.container.indexOfChild(image) != -1) {
                            image.postDelayed(this, 300);
                        }
                        return;
                    }
                    int relativeHeight = (int) ((float) photo.height / photo.width * image.getWidth());
                    android.view.ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
                    layoutParams.height = relativeHeight;
                    image.setLayoutParams(layoutParams);
                }
            }, 300);
            ImageLoader.getInstance().displayImage(photo.src_big, image, options);

            final int finalX = x;
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("OnImageButton", "Clicked");
                  //  Log.i("OnImageButton", String.valueOf(position));
                    System.err.println(finalX);
                    Intent newActivity = new Intent(mContext, ImageActivity.class);
                    newActivity.putExtra(ImageActivity.POSITION, finalX);
                    newActivity.putExtra(ImageActivity.PHOTOS, photos);
                    mContext.startActivity(newActivity);
                }
            });

        }
        viewHolder.container.requestLayout();
        //  else {
        //    imageView.setVisibility(View.GONE);
        //}

        return convertView;
    }

}