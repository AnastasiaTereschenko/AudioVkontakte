package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.activities.ImageActivity;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.attachments.Attachment;
import com.example.anastasiyaverenich.vkrecipes.attachments.ImagesLayoutManager;
import com.example.anastasiyaverenich.vkrecipes.attachments.ThumbAttachment;
import com.example.anastasiyaverenich.vkrecipes.fragments.BookmarkDialogFragment;
import com.example.anastasiyaverenich.vkrecipes.modules.Ads;
import com.example.anastasiyaverenich.vkrecipes.modules.ProgreesBar;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.OnLoadMoreListener;
import com.example.anastasiyaverenich.vkrecipes.ui.RecyclerViewPauseOnScrollListener;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.CommonUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.FeedUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.FileUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeedRecyclerAdapter extends RecyclerView.Adapter {
    public static final int MAX_LINES_IS_MORE = 5;
    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;
    private final int VIEW_AD = 2;
    private final Context mContext;
    public final int mResourceId;
    private DisplayImageOptions options;
    private List<Object> feeds;
    private int widthSize;
    String descriptionOfFeed;
    boolean isOpenMore;
    Set<Integer> keyOpenFeed = new HashSet<>();
    Map<Integer, Integer> countOpenPosition = new HashMap<>();
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    public OnLoadMoreListener onLoadMoreListener;
    boolean isLoaded;
    boolean pauseOnScroll = false; // or true
    boolean pauseOnFling = true; // or false

    public FeedRecyclerAdapter(Context context, int resource, List<Object> objects,
                               RecyclerView recyclerView) {
        mContext = context;
        mResourceId = resource;
        this.feeds = objects;
        options = VkRApplication.get().getOptions();
        DisplayMetrics localDisplayMetrics = VkRApplication.get().getResources().getDisplayMetrics();
        widthSize = Math.min(localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels)
                - Math.round(2 * CommonUtils.scale(34.0F));
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // Log.e("TAG", "onScrolled " + getLoaded());
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if ((onLoadMoreListener != null)) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
            recyclerView.setOnScrollListener(new RecyclerViewPauseOnScrollListener(ImageLoader.
                    getInstance(), pauseOnScroll, pauseOnFling));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (feeds.get(position) instanceof Ads) {
            return VIEW_AD;
        }
        if (feeds.get(position) instanceof ProgreesBar) {
            return VIEW_PROG;
        } else {
            return VIEW_ITEM;
        }
        //return feeds.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item,
                    parent, false);
            vh = new FeedViewHolder(view);
        } else if (viewType == VIEW_PROG) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_on_feed,
                    parent, false);
            vh = new ProgressViewHolder(view);
        } else if (viewType == VIEW_AD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_embedded_in_feed,
                    parent, false);
            /*AdView adview = new AdView(mContext);
            adview.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adview.setAdUnitId(mContext.getString(R.string.banner_ad_unit_id));
            float density = mContext.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.MEDIUM_RECTANGLE.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height);
            adview.setLayoutParams(params);*/
            vh = new AdViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FeedViewHolder) {
            final Recipe.Feed feed = (Recipe.Feed) feeds.get(position);
            final FeedViewHolder feedViewHolder = (FeedViewHolder) holder;
            final int index = feed.text.indexOf("<br>");
            setTextOnTextView(feed, ((FeedViewHolder) holder), index);
            final ArrayList<Recipe.Photo> photos = FeedUtils.getPhotosFromAttachments(feed.attachments);
            if (photos != null) {
                setFeedImages(feedViewHolder, photos, feed);
            }
            if (keyOpenFeed.contains(position)) {
                feedViewHolder.textDescription.setMaxLines(countOpenPosition.get(position));
                isOpenMore = true;
                feedViewHolder.imMoreInformation.setVisibility(View.INVISIBLE);
            } else {
                if (enableMoreInformation(feedViewHolder)) {
                    return;
                }
                feedViewHolder.textDescription.setMaxLines(MAX_LINES_IS_MORE);
                isOpenMore = false;
                feedViewHolder.imMoreInformation.setVisibility(View.VISIBLE);
            }
            holder.itemView.setTag(feed);
            feedViewHolder.flSaveImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int numberOfPhotos = photos.size();
                    for (int i = 0; i < numberOfPhotos; i++) {
                        final File src = ImageLoader.getInstance().getDiskCache().get(photos.get(i).src_big);
                        String loadPhoto = photos.get(i).src_big;
                        FileUtils.saveImagesOrImageOnDisk(src, mContext, loadPhoto, numberOfPhotos);
                    }
                }
            });
            final FeedViewHolder finalFeedViewHolder1 = feedViewHolder;
            feedViewHolder.ibMoreInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideAndShowDescriptionBookmark(position, finalFeedViewHolder1);
                }
            });
            feedViewHolder.flShareImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FileUtils.shareLink(feed, index, photos, mContext);
                }
            });
            if (BookmarkUtils.checkBookmarks(feed.id)) {
                feedViewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                feedViewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
            final FeedViewHolder finalFeedViewHolder = feedViewHolder;
            feedViewHolder.flBookmarkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long feedId = feed.id;
                    saveBookmarkOnCheck(feedId, finalFeedViewHolder, position, feed);
                }
            });
            feedViewHolder.container.requestLayout();
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        } else if (holder instanceof AdViewHolder) {
            AdRequest request = new AdRequest.Builder().build();
            ((AdViewHolder) holder).adView.setAdListener(new AdListener() {
                private void showToast(String message) {

                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoaded() {
                    showToast("Ad loaded.");
                    isLoaded = true;
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    showToast(String.format("Ad failed to load with error code %d.", errorCode));
                }

                @Override
                public void onAdOpened() {
                    showToast("Ad opened.");
                }

                @Override
                public void onAdClosed() {
                    showToast("Ad closed.");
                }

                @Override
                public void onAdLeftApplication() {
                    showToast("Ad left application.");
                }
            });

            // AdRequest adRequest = new AdRequest.Builder().build();
            if (!isLoaded) {
                ((AdViewHolder) holder).adView.loadAd(request);
                isLoaded = false;
            }
        }
        //((AdViewHolder) holder).adView.loadAd(request);
    }

    public void setLoaded() {
        loading = false;
    }

    public boolean getLoaded() {
        return loading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    protected static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textDescription;
        FlowLayout container;
        ImageView imageViewBM;
        FrameLayout flBookmarkImage;
        FrameLayout flShareImage;
        FrameLayout flSaveImage;
        RelativeLayout ibMoreInformation;
        ImageView imMoreInformation;

        public FeedViewHolder(View itemView) {
            super(itemView);
            textDescription = (TextView) itemView.findViewById(R.id.rli_tv_description_list);
            textName = (TextView) itemView.findViewById(R.id.rli_tv_name_list);
            container = (FlowLayout) itemView.findViewById(R.id.rli_ll_images_container);
            imageViewBM = (ImageView) itemView.findViewById(R.id.rli_iv_panel_favourite_item);
            flBookmarkImage = (FrameLayout) itemView.findViewById(R.id.rli_fl_panel_favourite_item);
            flShareImage = (FrameLayout) itemView.findViewById(R.id.rli_fl_panel_share_item);
            flSaveImage = (FrameLayout) itemView.findViewById(R.id.rli_fl_panel_save_images);
            ibMoreInformation = (RelativeLayout) itemView.findViewById(R.id.rli_rl_more);
            imMoreInformation = (ImageView) itemView.findViewById(R.id.rli_im_more);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarOnFeed);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public AdView adView;

        public AdViewHolder(View itemView) {
            super(itemView);
            adView = (AdView) itemView.findViewById(R.id.adView);
        }
    }


    private void setTextOnTextView(final Recipe.Feed feed, final FeedViewHolder viewHolder, int index) {
        int size = feed.text.length();
        if (!feed.text.equals("")) {
            if (index == -1) {
                viewHolder.textName.setText(Html.fromHtml(feed.text));
                viewHolder.textDescription.setText(" ");
            } else {
                String nameOfFeed = feed.text.substring(0, index);
                descriptionOfFeed = feed.text.substring(index, size);
                viewHolder.textName.setText(Html.fromHtml(nameOfFeed));
                viewHolder.textDescription.setText(Html.fromHtml(descriptionOfFeed));
            }
        } else {
            viewHolder.textName.setText(" ");
            viewHolder.textDescription.setText(" ");
        }
        enableMoreInformation(viewHolder);
    }

    private void saveBookmarkOnCheck(long feedId, final FeedViewHolder viewHolder, int position, Recipe.Feed feed) {
        if (BookmarkUtils.checkBookmarks(feedId)) {
            BookmarkUtils.deleteBookmark(feed);
            viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_border_black_24dp);
            notifyDataSetChanged();
        } else {
            FragmentActivity activity = (FragmentActivity) (mContext);
            FragmentManager fm = activity.getSupportFragmentManager();
            BookmarkDialogFragment bookmarkDialog = new BookmarkDialogFragment((Recipe.Feed) feeds.get(position));
            bookmarkDialog.setListener(new BookmarkDialogFragment.OnBookmarkItemClickListener() {
                @Override
                public void onBookmarkItemClick() {
                    viewHolder.imageViewBM.setImageResource(R.drawable.ic_star_black_24dp);
                }
            });
            bookmarkDialog.show(fm, "fragmentalert");
        }
    }

    private void hideAndShowDescriptionBookmark(int position, FeedViewHolder viewHolder) {
        if (enableMoreInformation(viewHolder)) {
            return;
        }
        if (!isOpenMore) {
            keyOpenFeed.add(position);
            countOpenPosition.put(position, viewHolder.textDescription.getLineCount());
            viewHolder.textDescription.setMaxLines(viewHolder.textDescription.getLineCount());
            isOpenMore = true;
            viewHolder.imMoreInformation.setVisibility(View.INVISIBLE);
        } else {
            if (keyOpenFeed.contains(position)) {
                keyOpenFeed.remove(position);
                countOpenPosition.remove(position);
            }
            viewHolder.textDescription.setMaxLines(MAX_LINES_IS_MORE);
            isOpenMore = false;
            viewHolder.imMoreInformation.setVisibility(View.VISIBLE);
        }
    }

    private boolean enableMoreInformation(final FeedViewHolder viewHolder) {
        final boolean[] flagEnable = new boolean[1];
        Runnable startChangeText = new Runnable() {
            @Override
            public void run() {
                if (viewHolder.textDescription.getLineCount() <= 5) {
                    viewHolder.imMoreInformation.setVisibility(View.INVISIBLE);
                    flagEnable[0] = true;
                }
            }
        };
        viewHolder.textDescription.post(startChangeText);
        return flagEnable[0];
    }

    public void removeAllKeys() {
        keyOpenFeed.removeAll(keyOpenFeed);
    }

    private void setFeedImages(final FeedViewHolder viewHolder,
                               final ArrayList<Recipe.Photo> photos, final Recipe.Feed feed) {
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
            //viewHolder.container.removeAllViews();
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