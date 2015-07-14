package com.example.anastasiyaverenich.vkrecipes;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends ActionBarActivity {
    private static final String API_URL = "https://api.vk.com";
    private static final String OWNER_ID="-39009769";
    private static final int OFFSET=0;
    private static final int COUNT=1;
    private static final String FILTER="all";
    private static final String VERSION="5.7";
    private List<Recipe.Feed> feedList;
    private FeedAdapter adapter;
    private IApiMethods methods;
    private Callback callback;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        feedList = new ArrayList<Recipe.Feed>();
        final ListView lvMain = (ListView) findViewById(R.id.lvMain);
        adapter = new FeedAdapter(MainActivity.this, R.layout.recipe_list, feedList);
        lvMain.setAdapter(adapter);
        textView = (TextView) findViewById(R.id.textView);
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
                if (OFFSET<100){
                    methods.getParam(OWNER_ID,OFFSET+10,COUNT,FILTER,VERSION,callback);
                }
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("TAG","ERROR");
            }
        };
        methods.getParam(OWNER_ID,OFFSET,COUNT,FILTER,VERSION,callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
