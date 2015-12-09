package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.EndlessScrollListener;
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

public class FeedFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int COOK_GOOD = 0;
    public static final int FITNESS_RECIPE = 1;
    public static final int HEALTH_FOOD = 2;
    public static final int BEST_RECIPE = 3;
    public static final int USEFUL_RECIPE = 4;
    private static final String API_URL = "https://api.vk.com";
    private static final int COOK_GOOD_ID = -39009769;
    private static final int FITNESS_RECIPE_ID = -80410546;
    private static final int HEALTH_FOOD_ID = -77109534;
    private static final int BEST_RECIPE_ID = -32194285;
    private static final int USEFUL_RECIPE_ID = -76882174;
    private int OFFSET = 0;
    private static final int COUNT = 15;
    private static final String FILTER = "all";
    private static final String VERSION = "5.7";
    private IApiMethods methods;
    private Callback callback;
    private ImageLoader imageLoader;
    private FeedAdapter adapter;
    ListView lvMain;
    View footerView;
    private List<Recipe.Feed> feedList;
    private SwipeRefreshLayout swipeRefresh;
    private int currentGroupId;

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
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        lvMain = (ListView) view.findViewById(R.id.lvMain);
        footerView = (View) inflater.inflate(R.layout.footer, null);
            if (isBestRecipe()){
                setCurrentParam(BEST_RECIPE_ID);
            } else if (isCookGood())
                setCurrentParam(COOK_GOOD_ID);
            else if (isHealthFood())
                setCurrentParam (HEALTH_FOOD_ID);
            else if (isFitnessRecipe())
                setCurrentParam (FITNESS_RECIPE_ID);
            else if (isUsefulRecipe())
                setCurrentParam (USEFUL_RECIPE_ID);
            FeedUtils.setFeeds(VkRApplication.get().getMySQLiteHelper().getAllFeeds(currentGroupId));
            loadingFeeds();
        return view ;
    }

    private void setCurrentParam(int groupId) {
        currentGroupId = groupId;
    }

    private void loadingFeeds(){
        feedList = new ArrayList<Recipe.Feed>();
        if (FeedUtils.getFeeds()!= null){
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
                if (OFFSET==0){
                    if (!feedList.isEmpty()){
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
                retrofitError.printStackTrace();
                endlessScrollListener.setLoading(false);
                swipeRefresh.setRefreshing(false);
                Log.e("TAG", "ERROR ");
            }
        };
        methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
        lvMain.setOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onRefresh() {
        if (lvMain.getFooterViewsCount() != 0) {
            lvMain.removeFooterView(footerView);
        }
            feedList.clear();
            OFFSET = 0;
            methods.getFeeds(currentGroupId, OFFSET, COUNT, FILTER, VERSION, callback);
        swipeRefresh.setRefreshing(false);
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

    private boolean isCookGood(){
        return getPosition() == COOK_GOOD;
    }
    private boolean isFitnessRecipe() {
        return getPosition() == FITNESS_RECIPE;
    }
    private boolean isHealthFood() {
        return getPosition() == HEALTH_FOOD;
    }
    private boolean isBestRecipe() {
        return getPosition() == BEST_RECIPE;
    }
    private boolean isUsefulRecipe() {
        return getPosition() == USEFUL_RECIPE;
    }
}

