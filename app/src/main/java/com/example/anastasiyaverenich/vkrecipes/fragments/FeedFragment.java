package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapterRecycler;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.OnLoadMoreListener;
import com.example.anastasiyaverenich.vkrecipes.utils.FeedUtils;
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

public class FeedFragment extends android.support.v4.app.Fragment implements
        SwipeRefreshLayout.OnRefreshListener {
    private static final String API_URL = "https://api.vk.com";
    private int OFFSET = 0;
    private static final int COUNT = 15;
    private static final String FILTER = "all";
    private static final String VERSION = "5.7";
    private IApiMethods methods;
    private Callback callback;
    private ImageLoader imageLoader;
    public FeedAdapterRecycler adapter;
    RecyclerView recyclerView;
    private List<Recipe.Feed> feedList;
    private SwipeRefreshLayout swipeRefresh;
    private int currentGroupId;
    MenuItem menuItem;
    int offsetErrorLoading;
    protected Handler handler;
    private LinearLayoutManager linearLayoutManager;

    public static FeedFragment newInstance(int position) {
        FeedFragment fragment = new FeedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FeedFragment() {
    }

    int getPosition() {
        return getArguments().getInt("position", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
        menuItem = (MenuItem) view.findViewById(R.id.action_edit);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_feed);
        handler = new Handler();
        setHasOptionsMenu(true);
        setCurrentParam(getPosition());
        FeedUtils.setFeeds(VkRApplication.get().getMySQLiteHelper().getAllFeeds(currentGroupId));
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        loadingFeeds();
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                feedList.add(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemInserted(feedList.size() - 1);
                        feedList.remove(feedList.size() - 1);
                        adapter.notifyItemRemoved(feedList.size());
                        adapter.notifyDataSetChanged();
                        OFFSET = OFFSET + COUNT;
                        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
                    }
                }, 3000);
            }
        });
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
    }

    private void setCurrentParam(int groupId) {
        currentGroupId = groupId;
    }

    private void loadingFeeds() {
        feedList = new ArrayList<Recipe.Feed>();
        if (FeedUtils.getFeeds() != null) {
            feedList = FeedUtils.getFeeds();
        }
        initAdapter(feedList);
        Gson gson = new GsonBuilder().
                registerTypeAdapterFactory(new RecipeTypeAdapterFactory()).create();
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        methods = restAdapter.create(IApiMethods.class);
        callback = new Callback<Recipe>() {
            @Override
            public void success(Recipe results, Response response) {
                Log.e("TAG", "SUCCESS " + results.response.size());
                final ArrayList<Recipe.Feed> feedNew = FeedUtils.getFeedsWithoutAds(results.response);
                if (OFFSET == 0) {
                    if (!feedList.isEmpty()) {
                        feedList.clear();
                    }
                    FeedUtils.saveRefreshData(feedNew, currentGroupId);
                }
                feedList.addAll(feedNew);
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                if ((results.response.size() != 0) || (results.response.size() == COUNT)) {
                    adapter.setLoaded();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("TAG", "ERROR ");
                OFFSET = offsetErrorLoading;
                swipeRefresh.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          swipeRefresh.setRefreshing(false);
                                      }
                                  }
                );
                retrofitError.printStackTrace();
                adapter.setLoaded();
                Toast.makeText(getActivity(), "Невозможно обновить ленту ", Toast.LENGTH_LONG)
                        .show();
            }
        };
        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
        adapter.setLoaded();
    }

    @Override
    public void onRefresh() {
        offsetErrorLoading = OFFSET;
        adapter.removeAllKeys();
        OFFSET = 0;
        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
    }


    private void initAdapter(List<Recipe.Feed> feeds) {
        swipeRefresh.post(new Runnable() {
                              @Override
                              public void run() {
                                  swipeRefresh.setRefreshing(true);
                              }
                          }
        );
        adapter = new FeedAdapterRecycler(getActivity(), R.layout.recipe_list_item, feeds, recyclerView);
        recyclerView.setAdapter(adapter);

    }
}

