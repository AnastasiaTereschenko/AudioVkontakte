package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

import java.util.List;

public class BookmarkFragment extends android.support.v4.app.Fragment {
    public static final String CURRENT_POSITION = "CurrentPosition";
    ListView lvBookmark;
    NameOfBookmarkAdapter adapterNameOfBookmark;
    int currentPosition = -1;
    MenuItem menuItem;
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
        lvBookmark = (ListView) view.findViewById(R.id.lvBookmark);
        menuItem = (MenuItem) view.findViewById(R.id.action_edit);
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
        /*lvBookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = adapterNameOfBookmark.showCheckedCategory (position);
            }
        });*/
        return view;
    }

    public void showCheckedCategory(int position) {
        final List<BookmarkCategory> nameOfBookmark = VkRApplication.get()
                .getMySQLiteHelper().getAllCategoties();
        FeedAdapter bookmarkAdapter;
        BookmarkCategory checkedCategory = nameOfBookmark.get(position);
        BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper()
                .getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
        List<Recipe.Feed> allBookmarks = BookmarkUtils.getBookmarks(VkRApplication.get()
                .getMySQLiteHelper().getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
        bookmarkAdapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, allBookmarks);
        lvBookmark.setAdapter(bookmarkAdapter);
        ((MainActivity) getActivity()).onBookmarkDetailsOpened();
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

}
