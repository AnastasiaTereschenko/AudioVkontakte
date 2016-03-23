package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.EndlessScrollListener;
import com.example.anastasiyaverenich.vkrecipes.utils.FeedUtils;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
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
        SwipeRefreshLayout.OnRefreshListener, ObservableScrollViewCallbacks {
    private static final String API_URL = "https://api.vk.com";
    private int OFFSET = 0;
    private static final int COUNT = 15;
    private static final String FILTER = "all";
    private static final String VERSION = "5.7";
    private IApiMethods methods;
    private Callback callback;
    private ImageLoader imageLoader;
    private FeedAdapter adapter;
    ObservableListView lvMain;
    View footerView;
    private List<Recipe.Feed> feedList;
    private SwipeRefreshLayout swipeRefresh;
    private int currentGroupId;
    MenuItem menuItem;
    int offsetErrorLoading;

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
        lvMain = (ObservableListView) view.findViewById(R.id.lvMain);
        lvMain.setScrollViewCallbacks(this);
        footerView = (View) inflater.inflate(R.layout.footer, null);
        setHasOptionsMenu(true);
        setCurrentParam(getPosition());
        FeedUtils.setFeeds(VkRApplication.get().getMySQLiteHelper().getAllFeeds(currentGroupId));
        loadingFeeds();
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
                if ((results.response.size() == 0) || (results.response.size() < COUNT)) {
                    lvMain.removeFooterView(footerView);
                } else {
                    endlessScrollListener.setLoading(false);
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
                    endlessScrollListener.setLoading(false);
                    Toast.makeText(getActivity(), "Невозможно обновить ленту ", Toast.LENGTH_LONG)
                            .show();
            }
        }
        ;
        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
        lvMain.setOnScrollListener(endlessScrollListener);
        }

        @Override
        public void onRefresh () {
            if (lvMain.getFooterViewsCount() != 0) {
                lvMain.removeFooterView(footerView);
            }
            offsetErrorLoading = OFFSET;
            adapter.removeAllKeys();
            OFFSET = 0;
            methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
        }

        EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
            @Override
            public void loadData() {
                if (lvMain.getFooterViewsCount() == 0) {
                    lvMain.addFooterView(footerView);
                }
                OFFSET = OFFSET + COUNT;
                methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
            }
        };

    private void initAdapter(List<Recipe.Feed> feeds) {
        if (lvMain.getFooterViewsCount() != 0) {
            lvMain.removeFooterView(footerView);
        }
        swipeRefresh.post(new Runnable() {
                              @Override
                              public void run() {

                                  swipeRefresh.setRefreshing(true);
                              }
                          }
        );
        lvMain.addFooterView(footerView);

        adapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, feeds);
        lvMain.setAdapter(adapter);

    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!actionBar.isShowing()) {
                actionBar.show();
            }
        }
    }

}

