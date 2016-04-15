package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.RecipeFromInstagram;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class FeedFromInstagramAdapter extends ArrayAdapter<RecipeFromInstagram.Feed> {
    private final Context mContext;
    private final int mResourceId;
    private DisplayImageOptions options;
    public List<RecipeFromInstagram.Feed> feeds;

    public FeedFromInstagramAdapter(Context context, int resource, List<RecipeFromInstagram.Feed> objects){
        super(context,resource,objects);
        mContext = context;
        mResourceId = resource;
        options = VkRApplication.get().getOptions();
        this.feeds = objects;
    }
    static class ViewHolder {;
        ImageView imageView;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.rlii_iv_images_inst);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final RecipeFromInstagram.Feed feed = feeds.get(position);
            //viewHolder.imageView.setBackgroundColor(0xfff0f0f0);
            ImageLoader.getInstance().displayImage(feed.images.standardResolution.url,viewHolder.imageView, options);
        return convertView;
    }
}
