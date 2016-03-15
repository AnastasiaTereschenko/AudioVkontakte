package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;
import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.activities.MainActivity;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.adapters.NameOfBookmarkAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends android.support.v4.app.Fragment implements ObservableScrollViewCallbacks {
    public static final String CURRENT_POSITION = "CurrentPosition";
    ObservableListView lvBookmark;
    NameOfBookmarkAdapter adapterNameOfBookmark;
    public int currentPosition = -1;
    public FeedAdapter bookmarkAdapter;

    final List<BookmarkCategory> nameOfBookmark = VkRApplication.get()
            .getMySQLiteHelper().getAllCategoties();

    public static BookmarkFragment newInstance() {
        BookmarkFragment fragment = new BookmarkFragment();
        return fragment;
    }

    public BookmarkFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        lvBookmark = (ObservableListView) view.findViewById(R.id.lvBookmark);
        lvBookmark.setScrollViewCallbacks(this);
        adapterNameOfBookmark = new NameOfBookmarkAdapter(getActivity(), R.layout.sample_together,
                nameOfBookmark);
        lvBookmark.setAdapter(adapterNameOfBookmark);
        adapterNameOfBookmark.setListener(new NameOfBookmarkAdapter.BookmarkItemClickListener() {
            @Override
            public void BookmarkItemClick(int position) {
                currentPosition = position;
                showCheckedCategory(currentPosition);
            }
        });
        BookmarkCategoryUtils.setArrayOfCategoty(nameOfBookmark);
        adapterNameOfBookmark.setMode(Attributes.Mode.Multiple);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            if (currentPosition != -1)
                showCheckedCategory(currentPosition);
        }
        return view;
    }


    public void showCheckedCategory(int position) {
        final List<BookmarkCategory> nameOfBookmark = VkRApplication.get()
                .getMySQLiteHelper().getAllCategoties();
        BookmarkCategory checkedCategory = nameOfBookmark.get(position);
        BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper()
                .getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
        List<Recipe.Feed> allBookmarks = BookmarkUtils.getBookmarks(VkRApplication.get()
                .getMySQLiteHelper().getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
        bookmarkAdapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, allBookmarks);
        lvBookmark.setAdapter(bookmarkAdapter);
        ((MainActivity) getActivity()).onBookmarkDetailsOpened();
    }

    public void displaySearchBookmark(String stringForSearchInDB) {
        final List<BookmarkCategory> nameOfBookmark = VkRApplication.get()
                .getMySQLiteHelper().getAllCategoties();
        if (currentPosition == -1) {
            List<Recipe.Feed> searchBookmarks = VkRApplication.get()
                    .getMySQLiteHelper().searchBookmarkForALLCategory(stringForSearchInDB);
            bookmarkAdapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, searchBookmarks);
            lvBookmark.setAdapter(bookmarkAdapter);
        } else {
            BookmarkCategory checkedCategory = nameOfBookmark.get(currentPosition);
            List<Recipe.Feed> searchBookmarks = VkRApplication.get()
                    .getMySQLiteHelper().searchBookmarkForCertainCategory(stringForSearchInDB, checkedCategory.getCategoryId());
            bookmarkAdapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, searchBookmarks);
            lvBookmark.setAdapter(bookmarkAdapter);
        }
    }

    public void clearScreen() {
        List<Recipe.Feed> clearArray = new ArrayList<>();
        bookmarkAdapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, clearArray);
        lvBookmark.setAdapter(bookmarkAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, currentPosition);
    }

    public boolean canGoBack() {
        if (currentPosition == -1) {
            return true;
        } else {
            return false;
        }
    }

    public void goBack() {
        lvBookmark.setAdapter(adapterNameOfBookmark);
        currentPosition = -1;
    }


    public void onEdit() {
        adapterNameOfBookmark.onEdit();
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!actionBar.isShowing()) {
                actionBar.show();
            }
        }
    }
}
