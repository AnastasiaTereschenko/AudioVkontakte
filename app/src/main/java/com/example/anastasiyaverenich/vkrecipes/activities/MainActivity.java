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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedRecyclerAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.Config;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.fragments.BookmarkFragment;
import com.example.anastasiyaverenich.vkrecipes.fragments.FeedFragment;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String CURRENT_ITEM = "ItemOfFragment";
    private DrawerLayout drawerLayout;
    private int currentItem;
    String currentTag;
    FeedFragment feedFragment;
    BookmarkFragment bookmarkFragment;
    private Menu _menu;
    private FragmentManager fragmentManager;
    boolean needToHideTheMenu;
    SearchView searchView;
    MenuItem searchItem;
    Handler handlerDelayChangeText;
    private boolean isPressBackMenuSearch;
    List<MenuItem> items;
    int indexOfBookmark;
    int idBookmark;
    String nameBookmark;
    List<Integer> itemMenuId;
    List<String> itemMenuName;
    NavigationView view;
    Window window;
    final String TAG = "myLogs";
    private Tracker mTracker;
    public FeedRecyclerAdapter adapter;
    List<Object> listOfObject = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.deep_blue));
        fragmentManager = getSupportFragmentManager();
        view = (NavigationView) findViewById(R.id.navigation_view);
        items = new ArrayList<>();
        BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper().getAllBookmarks());
        setContentView(R.layout.activity_main);
        initToolbar();
        itemMenuId = new ArrayList<>();
        itemMenuName = new ArrayList<>();
        setNameAndIdOfCategory();
        indexOfBookmark = itemMenuId.size() - 1;
        idBookmark = itemMenuId.get(indexOfBookmark);
        nameBookmark = itemMenuName.get(indexOfBookmark);
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        VkRApplication application = (VkRApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
        setupDrawerLayout();
        _menu = null;
        isPressBackMenuSearch = false;
        handlerDelayChangeText = new Handler();
        if (savedInstanceState == null) {
            feedFragment = (FeedFragment) changeFragmentOnClick(feedFragment, itemMenuId.get(0),
                    view.getMenu().getItem(0));
        } else {
            currentItem = savedInstanceState.getInt(CURRENT_ITEM);
            setTitleOnFragment(currentItem);
            currentTag = getTagById(currentItem);
            Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
            for (int i = 0; i < itemMenuId.size(); i++) {
                if (currentItem == itemMenuId.get(i)) {
                    setCurrentItem(currentItem, view.getMenu().getItem(i));
                    break;
                }
            }
            for (int i = 0; i < itemMenuId.size(); i++) {
                hideFragmentByTag(itemMenuName.get(i));
            }
            bookmarkFragment = (BookmarkFragment) fragmentManager.findFragmentByTag(nameBookmark);
            if (currentFragment != null) {
                fragmentManager.beginTransaction()
                        .show(currentFragment)
                        .commit();
            }
        }
        sendScreenFragmentName();
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
    protected void onResume() {
        super.onResume();
        sendScreenFragmentName();
    }

    public String getCurrentFragmentTitle(){
        String nameOfScreen = new String();
        for (int i = 0; i < itemMenuId.size(); i++) {
            if (itemMenuId.get(i) == currentItem) {
                nameOfScreen = itemMenuName.get(i);
                break;
            }
        }
        return nameOfScreen;
    }

    public void sendScreenFragmentName() {
        String name = getCurrentFragmentTitle();
        // [START screen_view_hit]
        // Log.e("TAG", "Setting screen name:  " + name);
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Fragment~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
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
                        closeButtonOnBookmarkMenu();
                    }
                });
                searchView.setOnSearchClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchClickOnBookmarkMenu();
                    }
                });

                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        if (currentItem == idBookmark && !bookmarkFragment.canGoBack()) {
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

                if (needToHideTheMenu) {
                    getMenu().findItem(R.id.action_edit).setVisible(false);
                }
                return super.onCreateOptionsMenu(menu);
            }

    private void searchClickOnBookmarkMenu() {
        if (currentItem == idBookmark && bookmarkFragment.canGoBack()) {
            bookmarkFragment.clearScreen();
            getMenu().findItem(R.id.action_edit).setVisible(false);
            searchView.setOnQueryTextListener(searchViewTextChangeListener);
        }
    }

    private void closeButtonOnBookmarkMenu() {
        if (currentItem == idBookmark && bookmarkFragment.canGoBack()) {
            bookmarkFragment.clearScreen();
        } else {
            bookmarkFragment.showCheckedCategory(bookmarkFragment.currentPosition);
            EditText editText = (EditText) findViewById(R.id.search_src_text);
            editText.setText("");
        }
        searchView.setOnQueryTextListener(null);
        searchView.setQuery("", false);
        searchView.setOnQueryTextListener(searchViewTextChangeListener);
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
        if (stringForSearch.length() >= 3) {
            Runnable startChangeText = new Runnable() {
                @Override
                public void run() {
                    Log.e("handler ", "Search for query " + stringForSearch);
                    bookmarkFragment.displaySearchBookmark(stringForSearch);
                }
            };
            handlerDelayChangeText.removeCallbacksAndMessages(null);
            handlerDelayChangeText.postDelayed(startChangeText, 300);
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
            if (Config.isRecipes()) {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_restaurant_menu_black_24dp);
            } else {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawerLayout() {
        view = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final Menu menu = view.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            items.add(menu.getItem(i));
        }
        view.setItemIconTintList(null);
        view.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int position = items.indexOf(menuItem);
                int checkedItemId = itemMenuId.get(position);
                currentTag = getTagById(checkedItemId);
                Fragment newFragment = fragmentManager.findFragmentByTag(currentTag);
                if (itemMenuId.size() - 1 == position) {
                    bookmarkFragment = (BookmarkFragment) changeFragmentOnClick(newFragment,
                            checkedItemId, menuItem);
                } else {
                    feedFragment = (FeedFragment) changeFragmentOnClick(newFragment, checkedItemId, menuItem);
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
            if (currentItem == idBookmark && !bookmarkFragment.canGoBack()) {
                onBackPressed();
                return true;
            }
            if (currentItem == idBookmark && bookmarkFragment.canGoBack()
                    && isPressBackMenuSearch) {
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
        String nameOfCurrentFragment = "";
        for (int i = 0; i < itemMenuId.size(); i++) {
            if (itemMenuId.get(i) == currentId) {
                nameOfCurrentFragment = itemMenuName.get(i);
                break;
            }
        }
        return nameOfCurrentFragment;
    }

    private Fragment changeFragmentOnClick(Fragment newFragment, int groupId, MenuItem menuItem) {
        currentTag = getTagById(currentItem);
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
        setTitleOnFragment(groupId);
        if (currentItem == groupId) {
            return newFragment;
        }
        if (currentFragment != null) {
            hideFragment(currentFragment);
            Log.e("TAG", "currentFragments " + currentFragment.hashCode());
        }
        String newTag = getTagById(groupId);
        if (newFragment == null && idBookmark == groupId) {
            newFragment = BookmarkFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.container, newFragment, newTag)
                    .commit();
        } else if (newFragment == null) {
            newFragment = FeedFragment.newInstance(groupId);
            fragmentManager.beginTransaction()
                    .add(R.id.container, newFragment, newTag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .show(newFragment)
                    .commit();
        }
        Log.e("TAG", "newFragments " + newFragment.hashCode());
        setCurrentItem(groupId, menuItem);
        if(newFragment instanceof FeedFragment)
          ((FeedFragment)newFragment).setName(getCurrentFragmentTitle());
        return newFragment;
    }

    private void setTitleOnFragment(int groupId) {
        for (int i = 0; i < itemMenuId.size(); i++) {
            if (itemMenuId.get(i) == groupId) {
                getSupportActionBar().setTitle(itemMenuName.get(i));
                break;
            }
        }
    }

    private void setCurrentItem(int groupId, MenuItem menuItem) {
        currentItem = groupId;
        menuItem.setChecked(true);

    }

    @Override
    public void onBackPressed() {
        if ((bookmarkFragment != null && !bookmarkFragment.canGoBack() &&
                idBookmark == currentItem) || (bookmarkFragment != null &&
                bookmarkFragment.canGoBack() && idBookmark == currentItem &&
                isPressBackMenuSearch)) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getMenu().findItem(R.id.action_edit).setVisible(true);
            getMenu().findItem(R.id.action_search).setVisible(true);
            if (Config.isRecipes()) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_restaurant_menu_black_24dp);
            } else {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            }
            bookmarkFragment.goBack(itemMenuName.get(itemMenuName.size()-1));
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
            currentItem = idBookmark;
            needToHideTheMenu = true;
        }
    }

    public void setNameAndIdOfCategory() {
        int[] arrayDrawer = (getResources().getIntArray(R.array.array_drawer_item_id));
        for (int x : arrayDrawer) {
            itemMenuId.add(x);
        }
        itemMenuName = Arrays.asList(getResources().getStringArray(R.array.array_drawer_item_name));
    }
}
