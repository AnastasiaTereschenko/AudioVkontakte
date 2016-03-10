package com.example.anastasiyaverenich.vkrecipes.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.fragments.BookmarkFragment;
import com.example.anastasiyaverenich.vkrecipes.fragments.FeedFragment;
import com.example.anastasiyaverenich.vkrecipes.fragments.FeedFromInstagramFragment;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;

public class MainActivity extends AppCompatActivity {
    public static final String BOOKMARK_FRAGMENT_TAG = "BookmarkFragment";
    public static final String COOK_GOOD_FRAGMENT_TAG = "CookGoodFragment";
    public static final String USEFUL_RECIPE_FRAGMENT_TAG = "UsefulRecipeFragment";
    public static final String BEST_RECIPE_FRAGMENT_TAG = "BestRecipeFragment";
    public static final String FITNESS_RECIPE_FRAGMENT_TAG = "FitnessRecipeFragment";
    public static final String HEALTH_FOOD_FRAGMENT_TAG = "HealthFoodFragment";
    public static final String INSTAGRAM_FRAGMENT_TAG = "InstagramFragment";
    public static final String CURRENT_ITEM = "ItemOfFragment";
    private DrawerLayout drawerLayout;
    private int currentItem;
    String currentTag;
    FeedFragment cookGoodFragment;
    FeedFragment fitnessRecipeFragment;
    FeedFragment healthFoodFragment;
    FeedFragment bestRecipeFragment;
    FeedFragment usefulRecipeFragment;
    BookmarkFragment bookmarkFragment;
    FeedFromInstagramFragment feedFromInstagramFragment;
    private Menu _menu;
    private FragmentManager fragmentManager;
    boolean needToHideTheMenu;
    SearchView searchView;
    MenuItem searchItem;
    Handler handlerDelayChangeText;
    private boolean isPressBackMenuSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper().getAllBookmarks());
        setContentView(R.layout.activity_main);
        initToolbar();
        setupDrawerLayout();
        _menu = null;
        isPressBackMenuSearch = false;
        handlerDelayChangeText = new Handler();
        if (savedInstanceState == null) {
            cookGoodFragment = (FeedFragment) changeFragmentOnClick(cookGoodFragment,
                    FeedFragment.COOK_GOOD, R.id.drawer_cook_good);
        } else {
            currentItem = savedInstanceState.getInt(CURRENT_ITEM);
            setTitleOnFragment(currentItem);
            setCurrentItem(currentItem);
            currentTag = getTagById(currentItem);
            Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
            hideFragmentByTag(BOOKMARK_FRAGMENT_TAG);
            hideFragmentByTag(COOK_GOOD_FRAGMENT_TAG);
            hideFragmentByTag(USEFUL_RECIPE_FRAGMENT_TAG);
            hideFragmentByTag(HEALTH_FOOD_FRAGMENT_TAG);
            hideFragmentByTag(INSTAGRAM_FRAGMENT_TAG);
            hideFragmentByTag(FITNESS_RECIPE_FRAGMENT_TAG);
            hideFragmentByTag(BEST_RECIPE_FRAGMENT_TAG);
            bookmarkFragment = (BookmarkFragment) fragmentManager.findFragmentByTag(BOOKMARK_FRAGMENT_TAG);
            if (currentFragment != null) {
                fragmentManager.beginTransaction()
                        .show(currentFragment)
                        .commit();
            }
        }
    }

    public void hideFragmentByTag(String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        hideFragment(fragment);
    }

    public void hideFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        } else
            fragmentManager.beginTransaction()
                    .hide(fragment)
                    .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_ITEM, currentItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        getMenuInflater().inflate(R.menu.menu_find_in_db, menu);
        searchItem = menu.findItem(R.id.action_search);
        _menu = menu;
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        ImageView closeButton = (ImageView) this.searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem == R.id.drawer_bookmark && bookmarkFragment.canGoBack()) {
                    bookmarkFragment.clearScreen();
                }
                else{
                    bookmarkFragment.showCheckedCategory(bookmarkFragment.currentPosition);
                    EditText editText = (EditText) findViewById(R.id.search_src_text);
                    editText.setText("");
                }
                searchView.setOnQueryTextListener(null);
                searchView.setQuery("", false);
                searchView.setOnQueryTextListener(searchViewTextChangeListener);
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem == R.id.drawer_bookmark && bookmarkFragment.canGoBack()) {
                    bookmarkFragment.clearScreen();
                    getMenu().findItem(R.id.action_edit).setVisible(false);
                    searchView.setOnQueryTextListener(searchViewTextChangeListener);
                }
            }
        });

        MenuItemCompat.setOnActionExpandListener(
                searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        if(currentItem == R.id.drawer_bookmark && !bookmarkFragment.canGoBack()){
                            searchView.setOnQueryTextListener(searchViewTextChangeListener);
                        }
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        searchView.setOnQueryTextListener(null);
                        if (bookmarkFragment.canGoBack()) {
                            isPressBackMenuSearch = true;
                            onBackPressed();
                        }
                        return true;
                    }
                });

        if (needToHideTheMenu == true) {
            getMenu().findItem(R.id.action_edit).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener searchViewTextChangeListener
            = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(final String query) {
            if (query.length() < 3) {
                bookmarkFragment.displaySearchBookmark(query);
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(final String query) {
            searchInDB(query);
            return true;
        }
    };

    private void searchInDB(final String stringForSearch) {
        if(stringForSearch.length() >= 3){
            Runnable runChangeText = new Runnable() {
                @Override
                public void run() {
                    Log.e("handler ", "Search for query " + stringForSearch);
                    bookmarkFragment.displaySearchBookmark(stringForSearch);
                }
            };
            handlerDelayChangeText.removeCallbacksAndMessages(null);
            handlerDelayChangeText.postDelayed(runChangeText, 300);
        }
    }

    private Menu getMenu() {
        return _menu;
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
                    //case R.id.drawer_instagram_recipes:
                      //  feedFromInstagramFragment = (FeedFromInstagramFragment) changeFragmentOnClick(feedFromInstagramFragment, 0, R.id.drawer_instagram_recipes);
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
            }
            if (currentItem == R.id.drawer_bookmark && bookmarkFragment.canGoBack()
                    && isPressBackMenuSearch == true) {
                onBackPressed();
                return true;
            }
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.action_edit) {
            bookmarkFragment.onEdit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getTagById(int currentId) {
        if (R.id.drawer_cook_good == currentId) {
            return COOK_GOOD_FRAGMENT_TAG;
        } else if (R.id.drawer_useful_recipe == currentId) {
            return USEFUL_RECIPE_FRAGMENT_TAG;
        } else if (R.id.drawer_best_recipe == currentId) {
            return BEST_RECIPE_FRAGMENT_TAG;
        } else if (R.id.drawer_fitness_recipe == currentId) {
            return FITNESS_RECIPE_FRAGMENT_TAG;
        } else if (R.id.drawer_health_food == currentId) {
            return HEALTH_FOOD_FRAGMENT_TAG;
        } //else if (R.id.drawer_instagram_recipes == currentId) {
           // return INSTAGRAM_FRAGMENT_TAG;
            else return BOOKMARK_FRAGMENT_TAG;
    }

    private Fragment changeFragmentOnClick(Fragment newFragment, int newInstanceOfFragment, int groupId) {
        // todo change to getTagByCurrentItem and fragmentManager.findFragmentByTag
        currentTag = getTagById(currentItem);
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
        setTitleOnFragment(groupId);
        if (currentItem == groupId) {
            return newFragment;
        }
        if (currentFragment != null) {
            hideFragment(currentFragment);
        }
        String newTag = getTagById(groupId);
        if (newFragment == null && R.id.drawer_bookmark == groupId) {
            newFragment = BookmarkFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.container, newFragment, newTag)
                    .commit();
        } //else if (newFragment == null && R.id.drawer_instagram_recipes == groupId) {
           // newFragment = FeedFromInstagramFragment.newInstance();
            //fragmentManager.beginTransaction()
                    //.add(R.id.container, newFragment, newTag)
                   // .commit();}
        else if (newFragment == null) {
            newFragment = FeedFragment.newInstance(newInstanceOfFragment);
            fragmentManager.beginTransaction()
                    .add(R.id.container, newFragment, newTag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
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
        }// else if (R.id.drawer_instagram_recipes == groupId) {
            //getSupportActionBar().setTitle(getString(R.string.recipes_from_instagram));}
        else getSupportActionBar().setTitle(getString(R.string.bookmark));
    }

    private void setCurrentItem(int groupId) {
        currentItem = groupId;
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.getMenu().findItem(groupId).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if ((bookmarkFragment != null && !bookmarkFragment.canGoBack() &&
                R.id.drawer_bookmark == currentItem) || (bookmarkFragment != null &&
                bookmarkFragment.canGoBack() && R.id.drawer_bookmark == currentItem &&
                isPressBackMenuSearch == true)) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getMenu().findItem(R.id.action_edit).setVisible(true);
            getMenu().findItem(R.id.action_search).setVisible(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_restaurant_menu_black_24dp);
            bookmarkFragment.goBack();
            isPressBackMenuSearch = false;
            return;
        }
        super.onBackPressed();
    }

    public void onBookmarkDetailsOpened() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getMenu().findItem(R.id.action_search).setVisible(true);
        if (getMenu() != null) {
            getMenu().findItem(R.id.action_edit).setVisible(false);
        } else {
            currentItem = R.id.drawer_bookmark;
            needToHideTheMenu = true;
        }
    }

}
