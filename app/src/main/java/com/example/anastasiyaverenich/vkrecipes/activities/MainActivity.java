package com.example.anastasiyaverenich.vkrecipes.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.ui.EndlessScrollListview;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
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

public class MainActivity extends AppCompatActivity{
    private static final String API_URL = "https://api.vk.com";
    private static final String OWNER_ID="-39009769";
    private int OFFSET=0;
    private static final int COUNT=3;
    private static final String FILTER="all";
    private static final String VERSION="5.7";
    private List<Recipe.Feed> feedList;
    private FeedAdapter adapter;
    private IApiMethods methods;
    private Callback callback;
    private ImageLoader imageLoader;

    ListView lvMain;
    View footerView;
    private DrawerLayout drawerLayout;
    private View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        setupDrawerLayout();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = (View) inflater.inflate(R.layout.footer, null);
        feedList = new ArrayList<Recipe.Feed>();
        lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.addFooterView(footerView);
        adapter = new FeedAdapter(MainActivity.this, R.layout.recipe_list_item, feedList);
        lvMain.setAdapter(adapter);
        lvMain.removeFooterView(footerView);
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
                Log.e("TAG", "SUCCESS");
                feedList.addAll(results.response);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Log.e("TAG", "ERROR " );
            }
        };
        methods.getFeeds(OWNER_ID, OFFSET, COUNT, FILTER, VERSION, callback);
        ListView lvItems = (ListView) findViewById(R.id.lvMain);
        lvItems.setOnScrollListener(new EndlessScrollListview() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if( lvMain.getFooterViewsCount() == 0){
                    lvMain.addFooterView(footerView);
                }
                OFFSET = OFFSET + COUNT;
                methods.getFeeds(OWNER_ID, OFFSET, COUNT, FILTER, VERSION, callback);
                if((COUNT==0) ||(COUNT<3 ))
                {
                    lvMain.removeFooterView(footerView);
                }

            }
        });
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_restaurant_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
