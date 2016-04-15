package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedFromInstagramAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethodsInstagram;
import com.example.anastasiyaverenich.vkrecipes.modules.RecipeFromInstagram;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class FeedFromInstagramFragment extends android.support.v4.app.Fragment{
    private FeedFromInstagramAdapter adapter;
    private static final String API_URL = "https://api.instagram.com";
    private static final String CLIENT_ID = "f1676673441b4a84a7c82f2ddea72a64";
    private List<RecipeFromInstagram.Feed> feedList;
    ListView lvMain;
    private IApiMethodsInstagram methods;
    private Callback callback;


    public static FeedFromInstagramFragment newInstance() {
        FeedFromInstagramFragment fragment = new FeedFromInstagramFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes_from_instagram, container, false);
        lvMain = (ListView) view.findViewById(R.id.lvMain);
        loadingFeeds();
        return view;
    }
    private void loadingFeeds(){
        feedList = new ArrayList<RecipeFromInstagram.Feed>();
        initAdapter(feedList);
        Gson gson = new Gson();
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        methods = restAdapter.create(IApiMethodsInstagram.class);
        callback = new Callback<RecipeFromInstagram>() {
            @Override
            public void success(RecipeFromInstagram results, Response response) {
                Log.e("TAG", "SUCCESS " + results.data.size());
                feedList.addAll(results.data);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Log.e("TAG", "ERROR ");
            }
        };
        methods.getPhotosFromInst(CLIENT_ID, callback);
        //lvMain.setOnScrollListener(endlessScrollListener);
    }

    private void initAdapter(List<RecipeFromInstagram.Feed> feeds) {
//        if (lvMain.getFooterViewsCount() != 0) {
//            lvMain.removeFooterView(footerView);
//        }
//        swipeRefresh.post(new Runnable() {
//                              @Override
//                              public void run() {
//
//                                  swipeRefresh.setRefreshing(true);
//                              }
//                          }
//        );
//        lvMain.addFooterView(footerView);

        adapter = new FeedFromInstagramAdapter(getActivity(), R.layout.recipe_list_item_instagram, feeds);
        lvMain.setAdapter(adapter);

    }
}

