package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.NameOfBookmarkAdapter;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;

import java.util.List;

public class BookmarkDialogFragment extends DialogFragment {
    final String LOG_TAG = "myLogs";
    private Recipe.Feed feedsFromAdapter;
    NameOfBookmarkAdapter adapterNameOfBookmark;
    OnBookmarkItemClickListener listener;

    public BookmarkDialogFragment(Recipe.Feed feeds) {
        feedsFromAdapter = feeds;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<BookmarkCategory> nameOfBookmark = VkRApplication.get().getMySQLiteHelper().getAllCategoties();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        adapterNameOfBookmark = new NameOfBookmarkAdapter(getActivity(), R.layout.name_of_bookmark_list_item, nameOfBookmark);
        builder.setTitle(R.string.change_bookmark)
                .setAdapter(adapterNameOfBookmark, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BookmarkCategory checkedCategory = nameOfBookmark.get(which);
                        BookmarkUtils.addBookmark(feedsFromAdapter, checkedCategory.getCategoryId());
                        if (listener != null) {
                            listener.onBookmarkItemClick();
                        }
                    }
                });
        return builder.create();
    }

    public void setListener(OnBookmarkItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnBookmarkItemClickListener {
        void onBookmarkItemClick();
    }
}
