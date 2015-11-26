package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apmem.tools.layouts.FlowLayout;

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
    private int widthSize;

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
            viewHolder.container = (FlowLayout) convertView.findViewById(R.id.rli_ll_images_container);
            viewHolder.imageViewBM = (ImageView) convertView.findViewById(R.id.rli_iv_panel_favourite_item);
            viewHolder.flBookmarkImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_favourite_item);
            viewHolder.flShareImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_share_item);
            viewHolder.flSaveImage = (FrameLayout) convertView.findViewById(R.id.rli_fl_panel_save_images);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Recipe.Feed feed = feeds.get(position);
        int size = feed.text.toString().length();
        final int index = feed.text.toString().indexOf("<br>");
        if (!feed.text.toString().equals("")) {
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
        final ArrayList<Recipe.Photo> photos = FeedUtils.getPhotosFromAttachments(feed.attachments);

        setFeedImages(viewHolder, photos);
        viewHolder.flSaveImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < photos.size(); i++) {
                    final File src = ImageLoader.getInstance().getDiskCache().get(photos.get(i).src_big);
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

                    ImageLoader.getInstance().loadImage(photos.get(i).src_big, options, new ImageLoadingListener() {
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
                            Toast.makeText(mContext, "Изображения сохранены в папку Recipes.", Toast.LENGTH_SHORT).show();
                        }

                        @Override

                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    });
                }
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
        if (BookmarkUtils.checkBookmarks(feed.id)) {
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        viewHolder.flBookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BookmarkUtils.checkBookmarks(feed.id)) {
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
        });


        viewHolder.container.requestLayout();

        return convertView;
    }

    private void setFeedImages(final ViewHolder viewHolder, final ArrayList<Recipe.Photo> photos) {
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
                    mContext.startActivity(newActivity);
                }
            });
        }
    }
}