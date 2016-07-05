package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedRecyclerAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.Ads;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.ProgreesBar;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.OnLoadMoreListener;
import com.example.anastasiyaverenich.vkrecipes.utils.FeedUtils;
import com.google.android.gms.ads.AdView;
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
    private Callback<Recipe> callback;
    private ImageLoader imageLoader;
    public FeedRecyclerAdapter adapter;
    RecyclerView recyclerView;
    private List<Recipe.Feed> feedList;
    private SwipeRefreshLayout swipeRefresh;
    private int currentGroupId;
    MenuItem menuItem;
    int offsetErrorLoading;
    protected Handler handler;
    private LinearLayoutManager linearLayoutManager;
    private AdView mAdView;
    List<Object> listOfObject = new ArrayList<>();
    String nameOfFragment;

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
        //AdView mAdView = (AdView) view.findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
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
        Log.e("t", "on create " + hashCode() + " adapter " + adapter.hashCode() + " recv " + recyclerView.hashCode());
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                listOfObject.add(new ProgreesBar());
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemInserted(listOfObject.size() - 1);
                    }
                });
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OFFSET = OFFSET + COUNT;
                        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
                    }
                }, 5000);
                Log.e("t", "on lm " + FeedFragment.this.hashCode() + " adapter " + adapter.hashCode() + " recv " + recyclerView.hashCode());
                Log.e("TAG", "onLoadMore " + adapter.getLoaded()+ getName());
            }
        });
        return view;
    }
    public void setName(String name){
        nameOfFragment = name;
    }
    public String getName(){
        return nameOfFragment;
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
                addFeedAdsProgressInListOfObject(feedList);
                adapter.notifyDataSetChanged();
                if ((listOfObject.get(listOfObject.size() - 1)) instanceof ProgreesBar) {
                    listOfObject.remove(listOfObject.size() - 1);
                    adapter.notifyItemRemoved(listOfObject.size());
                    //adapter.setLoaded();
                }
                adapter.setLoaded();
                Log.e("TAG", "Загрузка произошла успешно " +adapter.getLoaded()+ getName());
                swipeRefresh.setRefreshing(false);
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
                listOfObject.remove(listOfObject.size() - 1);
                adapter.notifyItemRemoved(listOfObject.size());
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
                Log.e("TAG", "Ошибка загрузки данных " + adapter.getLoaded()+ getName());
                Toast.makeText(getActivity(), "Невозможно обновить ленту ", Toast.LENGTH_LONG)
                        .show();
            }
        };
        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
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
        addFeedAdsProgressInListOfObject(feeds);
        // changingStateOfFragment =
        adapter = new FeedRecyclerAdapter(getActivity(), R.layout.recipe_list_item, listOfObject, recyclerView);
        recyclerView.setAdapter(adapter);

    }

    public void addFeedAdsProgressInListOfObject(List<Recipe.Feed> objectsOfFeed) {
        listOfObject.clear();
        int j = 0;
        for (int i = 0; i < objectsOfFeed.size(); i++) {
            if ((i % 6 == 0) && (i != 0)) {
                listOfObject.add(j, new Ads());
                listOfObject.add(j + 1, objectsOfFeed.get(i));
                j = j + 2;
            } else {
                listOfObject.add(j, objectsOfFeed.get(i));
                j = j + 1;
            }
        }
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}

