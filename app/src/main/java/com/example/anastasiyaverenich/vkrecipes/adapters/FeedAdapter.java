package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.activities.ImageActivity;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.attachments.Attachment;
import com.example.anastasiyaverenich.vkrecipes.attachments.ImagesLayoutManager;
import com.example.anastasiyaverenich.vkrecipes.attachments.ThumbAttachment;
import com.example.anastasiyaverenich.vkrecipes.fragments.BookmarkDialogFragment;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.CommonUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.FeedUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.FileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeedAdapter extends ArrayAdapter<Recipe.Feed> {
    private final Context mContext;
    private final int mResourceId;
    private DisplayImageOptions options;
    private List<Recipe.Feed> feeds;
    private int widthSize;
    String descriptionOfFeed;
    boolean isOpenMore;
    Set<Integer> keyOpenFeed = new HashSet<>();
    boolean isEmptyOrLessFiveString;
    Handler handlerDelayChangeText;
    int textViewLineCount;

    public FeedAdapter(Context context, int resource, List<Recipe.Feed> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
        this.feeds = objects;
        options = VkRApplication.get().getOptions();
        DisplayMetrics localDisplayMetrics = VkRApplication.get().getResources().getDisplayMetrics();
        widthSize = Math.min(localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels) - Math.round(2 * CommonUtils.scale(34.0F));
    }

    static class ViewHolder {
        TextView textName;
        TextView textDescription;
        FlowLayout container;
        ImageView imageViewBM;
        FrameLayout flBookmarkImage;
        FrameLayout flShareImage;
        FrameLayout flSaveImage;
        RelativeLayout ibMoreInformation;
        ImageView imMoreInformation;
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
            viewHolder.container = (FlowLayout) convertView.findViewById(R.id.rli_ll_images_container);
            viewHolder.imageViewBM = (ImageView) convertView.findViewById(R.id.rli_iv_panel_favourite_item);
            viewHolder.flBookmarkImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_favourite_item);
            viewHolder.flShareImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_share_item);
            viewHolder.flSaveImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_save_images);
            viewHolder.ibMoreInformation = (RelativeLayout) convertView.findViewById(R.id.rli_rl_more);
            viewHolder.imMoreInformation = (ImageView) convertView.findViewById(R.id.rli_im_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            if (keyOpenFeed.contains(position)) {
                viewHolder.textDescription.setMaxLines(viewHolder.textDescription.getLineCount());
                isOpenMore = true;
                viewHolder.imMoreInformation.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.textDescription.setMaxLines(5);
                isOpenMore = false;
                viewHolder.imMoreInformation.setVisibility(View.VISIBLE);
            }
        }
        final Recipe.Feed feed = feeds.get(position);
        final int index = feed.text.toString().indexOf("<br>");
        setTextOnTextView(feed,viewHolder,index);
        final ArrayList<Recipe.Photo> photos = FeedUtils.getPhotosFromAttachments(feed.attachments);
        setFeedImages(viewHolder, photos, feed);
        viewHolder.flSaveImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int numberOfPhotos = photos.size();
                for (int i = 0; i < numberOfPhotos; i++) {
                    final File src = ImageLoader.getInstance().getDiskCache().get(photos.get(i).src_big);
                    String loadPhoto = photos.get(i).src_big;
                    FileUtils.saveImagesOrImageOnDisk(src, mContext, loadPhoto, numberOfPhotos);
                }
            }
        });
        viewHolder.ibMoreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAndShowDescriptionBookmark(position, viewHolder);
            }
        });
        viewHolder.flShareImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FileUtils.shareLink(feed, index, photos, mContext);
            }
        });
        if (BookmarkUtils.checkBookmarks(feed.id)) {
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        viewHolder.flBookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long feedId = feed.id;
                saveBookmarkOnCheck(feedId, viewHolder, position, feed);
            }
        });
        viewHolder.container.requestLayout();
        return convertView;
    }

    private void setTextOnTextView(final Recipe.Feed feed, final ViewHolder viewHolder, int index) {
        handlerDelayChangeText = new Handler();
        int size = feed.text.toString().length();
        if (!feed.text.toString().equals("")) {
            if (index == -1) {
                viewHolder.textName.setText(Html.fromHtml(feed.text.toString()));
                viewHolder.textDescription.setText(" ");
                enableOrDisableMoreInformation(viewHolder);
            } else {
                String nameOfFeed = feed.text.substring(0, index);
                descriptionOfFeed = feed.text.substring(index, size);
                viewHolder.textName.setText(Html.fromHtml(nameOfFeed.toString()));
                viewHolder.textDescription.setText(Html.fromHtml(descriptionOfFeed.toString()));
                Runnable startChangeText = new Runnable() {
                    @Override
                    public void run() {
                        enableOrDisableMoreInformation(viewHolder);
                    }
                };
                handlerDelayChangeText.postDelayed(startChangeText, 300);
            }
        } else {
            viewHolder.textName.setText(" ");
            viewHolder.textDescription.setText(" ");
            enableOrDisableMoreInformation(viewHolder);
        }
    }

    private void saveBookmarkOnCheck(long feedId, final ViewHolder viewHolder, int position, Recipe.Feed feed) {
        if (BookmarkUtils.checkBookmarks(feedId)) {
            BookmarkUtils.deleteBookmark(feed);
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
            notifyDataSetChanged();
        } else {
            FragmentActivity activity = (FragmentActivity) (mContext);
            FragmentManager fm = activity.getSupportFragmentManager();
            BookmarkDialogFragment bookmarkDialog = new BookmarkDialogFragment(feeds.get(position));
            bookmarkDialog.setListener(new BookmarkDialogFragment.OnBookmarkItemClickListener() {
                @Override
                public void onBookmarkItemClick() {
                    viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
                }
            });
            bookmarkDialog.show(fm, "fragmentalert");
        }
    }

    private void hideAndShowDescriptionBookmark(int position, ViewHolder viewHolder) {
        if (enableOrDisableMoreInformation(viewHolder)) {
            return;
        }
        if ((!isOpenMore) && (!isEmptyOrLessFiveString)) {
            keyOpenFeed.add(position);
            viewHolder.textDescription.setMaxLines(viewHolder.textDescription.getLineCount());
            isOpenMore = true;
            viewHolder.imMoreInformation.setVisibility(View.INVISIBLE);
        } else if ((isOpenMore) && (!isEmptyOrLessFiveString)) {
            if (keyOpenFeed.contains(position)) {
                keyOpenFeed.remove(position);
            }
            viewHolder.textDescription.setMaxLines(5);
            isOpenMore = false;
            viewHolder.imMoreInformation.setVisibility(View.VISIBLE);
        }
    }

    private boolean enableOrDisableMoreInformation(ViewHolder viewHolder) {
        if (viewHolder.textDescription.getLineCount() <= 5) {
            viewHolder.imMoreInformation.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public void removeAllKeys() {
        keyOpenFeed.removeAll(keyOpenFeed);
    }

    private void setFeedImages(final ViewHolder viewHolder, final ArrayList<Recipe.Photo> photos, final Recipe.Feed feed) {
        viewHolder.container.removeAllViews();
        ArrayList<ThumbAttachment> attachments = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            Attachment attachment = new Attachment();
            attachment.photo = photos.get(i);
            attachments.add(attachment);
        }
        ImagesLayoutManager.processThumbs(widthSize, widthSize, attachments);
        for (int x = 0; x < photos.size(); x++) {
            final ImageView image = new ImageView(mContext);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setBackgroundColor(0xfff0f0f0);
            final Recipe.Photo photo = photos.get(x);
            Attachment attachment = (Attachment) attachments.get(x);
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(attachment.getWidth(),
                    attachment.getHeight());
            image.setLayoutParams(lp);
            lp.setMargins(CommonUtils.scale(2), CommonUtils.scale(2), 0, 0);
            viewHolder.container.addView(image);
            ImageLoader.getInstance().displayImage(photo.src_big, image, options);
            final int finalX = x;
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("OnImageButton", "Clicked");
                    System.err.println(finalX);
                    Intent newActivity = new Intent(mContext, ImageActivity.class);
                    newActivity.putExtra(ImageActivity.POSITION, finalX);
                    newActivity.putExtra(ImageActivity.PHOTOS, photos);
                    newActivity.putExtra(ImageActivity.FEED, feed);
                    mContext.startActivity(newActivity);
                }
            });
        }
    }
}