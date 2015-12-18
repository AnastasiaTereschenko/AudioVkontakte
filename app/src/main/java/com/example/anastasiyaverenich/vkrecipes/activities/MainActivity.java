package com.example.anastasiyaverenich.vkrecipes.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.fragments.BookmarkFragment;
import com.example.anastasiyaverenich.vkrecipes.fragments.FeedFragment;
import com.example.anastasiyaverenich.vkrecipes.fragments.FeedFromInstagramFragment;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private int currentItem;
    FeedFragment cookGoodFragment;
    FeedFragment fitnessRecipeFragment;
    FeedFragment healthFoodFragment;
    FeedFragment bestRecipeFragment;
    FeedFragment usefulRecipeFragment;
    BookmarkFragment bookmarkFragment;
    FeedFromInstagramFragment feedFromInstagramFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper().getAllBookmarks());
        setContentView(R.layout.activity_main);
        initToolbar();
        setupDrawerLayout();
        cookGoodFragment = (FeedFragment) changeFragmentOnClick(cookGoodFragment, FeedFragment.COOK_GOOD, R.id.drawer_cook_good);
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_restaurant_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setDisplayShowTitleEnabled(true);
            //actionBar.setHomeButtonEnabled(true);
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
                        cookGoodFragment = (FeedFragment) changeFragmentOnClick(cookGoodFragment, FeedFragment.COOK_GOOD, R.id.drawer_cook_good);
                        break;
                    case R.id.drawer_fitness_recipe:
                        fitnessRecipeFragment = (FeedFragment) changeFragmentOnClick(fitnessRecipeFragment, FeedFragment.FITNESS_RECIPE, R.id.drawer_fitness_recipe);
                        break;
                    case R.id.drawer_health_food:
                        healthFoodFragment = (FeedFragment) changeFragmentOnClick(healthFoodFragment, FeedFragment.HEALTH_FOOD, R.id.drawer_health_food);
                        break;
                    case R.id.drawer_best_recipe:
                        bestRecipeFragment = (FeedFragment) changeFragmentOnClick(bestRecipeFragment, FeedFragment.BEST_RECIPE, R.id.drawer_best_recipe);
                        break;
                    case R.id.drawer_useful_recipe:
                        usefulRecipeFragment = (FeedFragment) changeFragmentOnClick(usefulRecipeFragment, FeedFragment.USEFUL_RECIPE, R.id.drawer_useful_recipe);
                        break;
                    case R.id.drawer_bookmark:
                        bookmarkFragment = (BookmarkFragment) changeFragmentOnClick(bookmarkFragment, 0, R.id.drawer_bookmark);

                        break;
                    case R.id.drawer_instagram_recipes:
                        feedFromInstagramFragment = (FeedFromInstagramFragment) changeFragmentOnClick(feedFromInstagramFragment, 0, R.id.drawer_instagram_recipes);
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (currentItem == R.id.drawer_bookmark && !bookmarkFragment.canGoBack()) {
                onBackPressed();
                return true;
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public Fragment getFragmentById(int currentId) {
        if (R.id.drawer_cook_good == currentId) {
            return cookGoodFragment;
        } else if (R.id.drawer_useful_recipe == currentId) {
            return usefulRecipeFragment;
        } else if (R.id.drawer_best_recipe == currentId) {
            return bestRecipeFragment;
        } else if (R.id.drawer_fitness_recipe == currentId) {
            return fitnessRecipeFragment;
        } else if (R.id.drawer_health_food == currentId) {
            return healthFoodFragment;
        } else if (R.id.drawer_instagram_recipes == currentId) {
            return feedFromInstagramFragment;
        } else return bookmarkFragment;
    }

    private Fragment changeFragmentOnClick(Fragment newFragment, int newInstanceOfFragment, int groupId) {
        Fragment currentFragment = getFragmentById(currentItem);
        setTitleOnFragment(groupId);
        if (currentItem == groupId) {
            return newFragment;
        }
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .commit();
        }
        if (newFragment == null && R.id.drawer_bookmark == groupId) {
            newFragment = BookmarkFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, newFragment, "bookmarkFragment")
                    .commit();
        } else if (newFragment == null && R.id.drawer_instagram_recipes == groupId) {
            newFragment = FeedFromInstagramFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, newFragment, "feedFromInstagramFragment")
                    .commit();
        } else if (newFragment == null) {
            newFragment = FeedFragment.newInstance(newInstanceOfFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, newFragment, "feedFragment")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .show(newFragment)
                    .commit();
        }
        setCurrentItem(groupId);
        return newFragment;
    }

    private void setTitleOnFragment(int groupId) {
        if (R.id.drawer_cook_good == groupId) {
            getSupportActionBar().setTitle(getString(R.string.cook_good));
        } else if (R.id.drawer_useful_recipe == groupId) {
            getSupportActionBar().setTitle(getString(R.string.useful_recipe));
        } else if (R.id.drawer_best_recipe == groupId) {
            getSupportActionBar().setTitle(getString(R.string.best_recipe));
        } else if (R.id.drawer_fitness_recipe == groupId) {
            getSupportActionBar().setTitle(getString(R.string.fitness_recipe));
        } else if (R.id.drawer_health_food == groupId) {
            getSupportActionBar().setTitle(getString(R.string.health_food));
        } else if (R.id.drawer_instagram_recipes == groupId) {
            getSupportActionBar().setTitle(getString(R.string.recipes_from_instagram));
        } else getSupportActionBar().setTitle(getString(R.string.bookmark));
    }

    private void setCurrentItem(int groupId) {
        currentItem = groupId;
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.getMenu().findItem(groupId).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (bookmarkFragment != null && !bookmarkFragment.canGoBack() && R.id.drawer_bookmark == currentItem) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_restaurant_menu_black_24dp);
            bookmarkFragment.goBack();
            return;
        }
        super.onBackPressed();
    }

    public void onBookmarkDetailsOpened(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }

}
