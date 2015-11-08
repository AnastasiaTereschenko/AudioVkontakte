package com.example.anastasiyaverenich.vkrecipes.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.fragments.FeedFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private int currentItem;
    FeedFragment cookGoodFragment;
    FeedFragment fitnessRecipeFragment;
    FeedFragment healthFoodFragment;
    FeedFragment bestRecipeFragment;
    FeedFragment usefulRecipeFragment;
    FeedFragment bookmarkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        setupDrawerLayout();
        cookGoodFragment = FeedFragment.newInstance(FeedFragment.COOK_GOOD);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, cookGoodFragment).commit();
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_restaurant_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_cook_good:
                        if (cookGoodFragment == null || currentItem != R.id.drawer_cook_good) {
                            cookGoodFragment = FeedFragment.newInstance(FeedFragment.COOK_GOOD);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, cookGoodFragment).commit();
                        }
                        break;
                    case R.id.drawer_fitness_recipe:
                        if (fitnessRecipeFragment == null || currentItem != R.id.drawer_fitness_recipe) {
                            fitnessRecipeFragment = FeedFragment.newInstance(FeedFragment.FITNESS_RECIPE);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, fitnessRecipeFragment).commit();
                        }
                        break;
                    case R.id.drawer_health_food:
                        if (healthFoodFragment == null || currentItem != R.id.drawer_health_food) {
                            healthFoodFragment = FeedFragment.newInstance(FeedFragment.HEALTH_FOOD);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, healthFoodFragment).commit();
                        }
                        break;
                    case R.id.drawer_best_recipe:
                        if (bestRecipeFragment == null || currentItem != R.id.drawer_best_recipe) {
                            bestRecipeFragment = FeedFragment.newInstance(FeedFragment.BEST_RECIPE);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, bestRecipeFragment).commit();
                        }
                        break;
                    case R.id.drawer_useful_recipe:
                        if (usefulRecipeFragment == null || currentItem != R.id.drawer_useful_recipe) {
                            usefulRecipeFragment = FeedFragment.newInstance(FeedFragment.USEFUL_RECIPE);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, usefulRecipeFragment).commit();
                        }
                        break;
                    case R.id.drawer_favourite:
                        if (bookmarkFragment == null || currentItem != R.id.drawer_favourite) {
                            bookmarkFragment = FeedFragment.newInstance(FeedFragment.BOOKMARKS);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, bookmarkFragment).commit();
                        }
                        break;
                }
                    currentItem = menuItem.getItemId();
                    drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
