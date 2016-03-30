package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.daimajia.swipe.util.Attributes;
import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.activities.MainActivity;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapterRecycler;
import com.example.anastasiyaverenich.vkrecipes.adapters.RecyclerViewAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.ui.DividerItemDecoration;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class BookmarkFragment extends android.support.v4.app.Fragment {
    public static final String CURRENT_POSITION = "CurrentPosition";
    RecyclerView recyclerView;
    ListView lvBookmark;
    RecyclerView.Adapter adapterNameOfBookmark;
    public int currentPosition = -1;
    public FeedAdapterRecycler bookmarkAdapter;
    private LinearLayoutManager linearLayoutManager;

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
      //  View view1 = inflater.inflate(R.layout., container, false);
        //lvBookmark = (ListView) view.findViewById(R.id.lvBookmark);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_bookmark);
        adapterNameOfBookmark = new RecyclerViewAdapter(getActivity(), R.layout.name_of_bookmark_list_item,
                nameOfBookmark);
        ((RecyclerViewAdapter) adapterNameOfBookmark).setMode(Attributes.Mode.Multiple);
        recyclerView.setAdapter(adapterNameOfBookmark);
        ((RecyclerViewAdapter) adapterNameOfBookmark).setListener(new RecyclerViewAdapter.BookmarkItemClickListener() {
            @Override
            public void BookmarkItemClick(int position) {
                currentPosition = position;
                showCheckedCategory(currentPosition);
            }
        });
        BookmarkCategoryUtils.setArrayOfCategoty(nameOfBookmark);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
            if (currentPosition != -1)
                showCheckedCategory(currentPosition);
        }
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
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
        bookmarkAdapter = new FeedAdapterRecycler(getActivity(), R.layout.recipe_list_item, allBookmarks,
                recyclerView);
        recyclerView.setAdapter(bookmarkAdapter);
        ((MainActivity) getActivity()).onBookmarkDetailsOpened();
    }

    public void displaySearchBookmark(String stringForSearchInDB) {
        final List<BookmarkCategory> nameOfBookmark = VkRApplication.get()
                .getMySQLiteHelper().getAllCategoties();
        if (currentPosition == -1) {
            List<Recipe.Feed> searchBookmarks = VkRApplication.get()
                    .getMySQLiteHelper().searchBookmarkForALLCategory(stringForSearchInDB);
            bookmarkAdapter = new FeedAdapterRecycler(getActivity(), R.layout.recipe_list_item, searchBookmarks,
                    recyclerView);
            recyclerView.setAdapter(bookmarkAdapter);
        } else {
            BookmarkCategory checkedCategory = nameOfBookmark.get(currentPosition);
            List<Recipe.Feed> searchBookmarks = VkRApplication.get()
                    .getMySQLiteHelper().searchBookmarkForCertainCategory(stringForSearchInDB, checkedCategory.getCategoryId());
            bookmarkAdapter = new FeedAdapterRecycler(getActivity(), R.layout.recipe_list_item, searchBookmarks,
                    recyclerView);
            recyclerView.setAdapter(bookmarkAdapter);
        }
    }

    public void clearScreen() {
        List<Recipe.Feed> clearArray = new ArrayList<>();
        bookmarkAdapter = new FeedAdapterRecycler(getActivity(), R.layout.recipe_list_item, clearArray,
                recyclerView);
        recyclerView.setAdapter(bookmarkAdapter);
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
        recyclerView.setAdapter(adapterNameOfBookmark);
        currentPosition = -1;
    }


    public void onEdit() {
        ((RecyclerViewAdapter) adapterNameOfBookmark).onEdit();
    }
}
