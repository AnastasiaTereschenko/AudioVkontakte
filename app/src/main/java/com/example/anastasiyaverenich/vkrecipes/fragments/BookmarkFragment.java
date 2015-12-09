package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.FeedAdapter;
import com.example.anastasiyaverenich.vkrecipes.adapters.NameOfBookmarkAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;

import java.util.List;

public class BookmarkFragment extends android.support.v4.app.Fragment {
    ListView lvBookmark;
    NameOfBookmarkAdapter adapterNameOfBookmark;
    FeedAdapter bookmarkAdapter;
    int currentPosition = -1;

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
        final List<BookmarkCategory> nameOfBookmark = VkRApplication.get().getMySQLiteHelper().getAllCategoties();
        lvBookmark = (ListView) view.findViewById(R.id.lvBookmark);
        adapterNameOfBookmark = new NameOfBookmarkAdapter(getActivity(), R.layout.name_of_bookmark_list_item, nameOfBookmark);
        lvBookmark.setAdapter(adapterNameOfBookmark);
        lvBookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                BookmarkCategory checkedCategory = nameOfBookmark.get(position);
                BookmarkUtils.setBookmarks(VkRApplication.get().getMySQLiteHelper().getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
                List<Recipe.Feed> allBookmarks = BookmarkUtils.getBookmarks(VkRApplication.get().getMySQLiteHelper().getBookmarksForCertainCategory(checkedCategory.getCategoryId()));
                bookmarkAdapter = new FeedAdapter(getActivity(), R.layout.recipe_list_item, allBookmarks);
                lvBookmark.setAdapter(bookmarkAdapter);
            }
        });
        lvBookmark.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> adapter, View view,
                                           int position, long id) {
                final List<BookmarkCategory> nameOfBookmark = VkRApplication.get().getMySQLiteHelper().getAllCategoties();
                int idCategory = nameOfBookmark.get(position).getCategoryId();
                VkRApplication.get().getMySQLiteHelper().deleteCategory(idCategory);
                updateListView();
                return true;
            }

        });
        return view;
    }

    public void updateListView() {
        final List<BookmarkCategory> values = VkRApplication.get().getMySQLiteHelper().getAllCategoties();
        final ArrayAdapter<BookmarkCategory> adapterNameOfBookmark = new NameOfBookmarkAdapter(getActivity(),
                R.layout.name_of_bookmark_list_item, values);
        lvBookmark.setAdapter(adapterNameOfBookmark);
    }
    public boolean canGoBack(){
        if (currentPosition == -1) {
            return true;
        }
        else{
            return false;
        }
    }
    public void goBack(){
        lvBookmark.setAdapter(adapterNameOfBookmark);
        currentPosition = -1;
    }

}
