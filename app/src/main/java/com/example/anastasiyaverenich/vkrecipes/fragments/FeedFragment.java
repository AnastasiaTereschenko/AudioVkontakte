package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.futils.BookmarkUtils;
import com.example.anastasiyaverenich.vkrecipes.futils.FeedUtils;
import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.EndlessScrollListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class FeedFragment extends android.support.v4.app.Fragment {
    public static final int FEEDS=0;
    public static final int BOOKMARKS=1;
    private static final String API_URL = "https://api.vk.com";
    private static final String OWNER_ID = "-39009769";
    private int OFFSET = 0;
    private static final int COUNT = 5;
    private static final String FILTER = "all";
    private static final String VERSION = "5.7";
    private IApiMethods methods;
    private Callback callback;
    private ImageLoader imageLoader;
    private FeedAdapter adapter;
    ListView lvMain;
    View footerView;
    private List<Recipe.Feed> feedList;
    private int currentItem;



    public static FeedFragment newInstance(int position) {
        FeedFragment fragment = new FeedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    int getPosition() {
        return getArguments().getInt("position", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed,container,false);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        lvMain = (ListView) view.findViewById(R.id.lvMain);
        if (isBookmark()) {
            initAdapter(BookmarkUtils.getBookmarks());

        } else {
            footerView = (View) inflater.inflate(R.layout.footer, null);
            feedList = new ArrayList<Recipe.Feed>();
            initAdapter(feedList);
            Gson gson = new GsonBuilder().
                    registerTypeAdapterFactory(new RecipeTypeAdapterFactory()).create();
            final RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .setConverter(new GsonConverter(gson))
                    .build();
            methods = restAdapter.create(IApiMethods.class);
            BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper().getAllFeeds());
            callback = new Callback<Recipe>() {
                @Override
                public void success(Recipe results, Response response) {
                    Log.e("TAG", "SUCCESS " + results.response.size());
                    final ArrayList<Recipe.Feed> feedNew = FeedUtils.getFeedsWithoutAds(results.response);
                    feedList.addAll(feedNew);
                    adapter.notifyDataSetChanged();
                    if ((results.response.size() == 0) || (results.response.size() < COUNT)) {
                        lvMain.removeFooterView(footerView);
                    }
                    else{
                        endlessScrollListener.setLoading(false);
                    }
                }
                @Override
                public void failure(RetrofitError retrofitError) {
                    retrofitError.printStackTrace();
                    endlessScrollListener.setLoading(false);
                    Log.e("TAG", "ERROR ");
                }
            };
            methods.getFeeds(OWNER_ID, OFFSET, COUNT, FILTER, VERSION, callback);
            lvMain.setOnScrollListener(endlessScrollListener);
        }
    return view;
}

EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
    @Override
    public void loadData() {
        if (lvMain.getFooterViewsCount() == 0) {
            lvMain.addFooterView(footerView);
        }
        OFFSET = OFFSET + COUNT;
        methods.getFeeds(OWNER_ID, OFFSET, COUNT, FILTER, VERSION, callback);
    }
};
    private void initAdapter(List<Recipe.Feed> feeds) {
        if (lvMain.getFooterViewsCount() != 0) {
            lvMain.removeFooterView(footerView);
        }
        if (!isBookmark()) {
            lvMain.addFooterView(footerView);
        }
        adapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, feeds, isBookmark());
        lvMain.setAdapter(adapter);
    }

    private boolean isBookmark() {
        return getPosition() == BOOKMARKS;
    }


}

