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
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedRecyclerAdapter;
import com.example.anastasiyaverenich.vkrecipes.adapters.NameOfBookmarkRecyclerAdapter;
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
    public FeedRecyclerAdapter bookmarkAdapter;
    private LinearLayoutManager linearLayoutManager;
    List<Object> listOfBookmark = new ArrayList<>();

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
        adapterNameOfBookmark = new NameOfBookmarkRecyclerAdapter(getActivity(), R.layout.name_of_bookmark_list_item,
                nameOfBookmark);
        ((NameOfBookmarkRecyclerAdapter) adapterNameOfBookmark).setMode(Attributes.Mode.Multiple);
        recyclerView.setAdapter(adapterNameOfBookmark);
        ((NameOfBookmarkRecyclerAdapter) adapterNameOfBookmark).setListener(new NameOfBookmarkRecyclerAdapter.BookmarkItemClickListener() {
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
        BookmarkCategory checkedCategory = nameOfBookmark.get(position);
        BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper()
                .getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
        List<Recipe.Feed> allBookmarks = BookmarkUtils.getBookmarks(VkRApplication.get()
                .getMySQLiteHelper().getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
        int categoryIdForCheckedCategory = checkedCategory.getCategoryId();
        if (!listOfBookmark.isEmpty()) {
            listOfBookmark.clear();
        }
        for (int i = 0; i < allBookmarks.size(); i++) {
            listOfBookmark.add(i,allBookmarks.get(i));
        }
        bookmarkAdapter = new FeedRecyclerAdapter(getActivity(), R.layout.recipe_list_item, listOfBookmark,
                recyclerView);
        recyclerView.setAdapter(bookmarkAdapter);
        ((MainActivity) getActivity()).onBookmarkDetailsOpened();
        String nameOfBookmark = VkRApplication.get().getMySQLiteHelper().getNameOfCategory(categoryIdForCheckedCategory);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(nameOfBookmark);
    }

    public void displaySearchBookmark(String stringForSearchInDB) {
        if (currentPosition == -1) {
            List<Recipe.Feed> searchBookmarks = VkRApplication.get()
                    .getMySQLiteHelper().searchBookmarkForAllCategory(stringForSearchInDB);
            if (!listOfBookmark.isEmpty()) {
                listOfBookmark.clear();
            }
            for (int i = 0; i < searchBookmarks.size(); i++) {
                listOfBookmark.add(i,searchBookmarks.get(i));
            }
            bookmarkAdapter = new FeedRecyclerAdapter(getActivity(), R.layout.recipe_list_item, listOfBookmark,
                    recyclerView);
            recyclerView.setAdapter(bookmarkAdapter);
        } else {
            BookmarkCategory checkedCategory = nameOfBookmark.get(currentPosition);
            List<Recipe.Feed> searchBookmarks = VkRApplication.get()
                    .getMySQLiteHelper().searchBookmarkForCertainCategory(stringForSearchInDB, checkedCategory.getCategoryId());
            if (!listOfBookmark.isEmpty()) {
                listOfBookmark.clear();
            }
            for (int i = 0; i < searchBookmarks.size(); i++) {
            listOfBookmark.add(i,searchBookmarks.get(i));
            }
            bookmarkAdapter = new FeedRecyclerAdapter(getActivity(), R.layout.recipe_list_item, listOfBookmark,
                    recyclerView);
            recyclerView.setAdapter(bookmarkAdapter);
        }
    }

    public void clearScreen() {
        List<Recipe.Feed> clearArray = new ArrayList<>();
      //  if (!listOfBookmark.isEmpty()) {
            listOfBookmark.clear();
       // }
        // listOfBookmark.add(clearArray);
        bookmarkAdapter = new FeedRecyclerAdapter(getActivity(), R.layout.recipe_list_item, listOfBookmark,
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

    public void goBack(String nameOfBookmark) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(nameOfBookmark);
        recyclerView.setAdapter(adapterNameOfBookmark);
        currentPosition = -1;
    }


    public void onEdit() {
        ((NameOfBookmarkRecyclerAdapter) adapterNameOfBookmark).onEdit();
    }
}
