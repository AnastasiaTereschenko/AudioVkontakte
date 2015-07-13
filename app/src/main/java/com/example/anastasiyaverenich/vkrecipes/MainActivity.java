package com.example.anastasiyaverenich.vkrecipes;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.gsonFactories.RecipeTypeAdapterFactory;
import com.example.anastasiyaverenich.vkrecipes.modules.IApiMethods;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends ActionBarActivity {
    private static final String API_URL = "https://api.vk.com";
    private static final String OWNER_ID="-39009769";
    private static final String COUNT="10";
    private static final String FILTER="all";
    private static final String VERSION="5.7";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        Gson gson = new GsonBuilder().
                registerTypeAdapterFactory(new RecipeTypeAdapterFactory()).create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        IApiMethods methods = restAdapter.create(IApiMethods.class);
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                Log.e("TAG", "SUCCESS");
                Recipe recipes = (Recipe)o;
                for (Recipe.Feed feed : recipes.response) {
                    if(feed != null && feed.text != null) {
                        textView.setText(textView.getText() + Html.fromHtml(feed.text).toString() +
                                "\n+++++++++++++++++++++" + "\n");
                    }
                }
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("TAG","ERROR");
            }
        };
        methods.getParam(OWNER_ID,COUNT,FILTER,VERSION,callback);
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
