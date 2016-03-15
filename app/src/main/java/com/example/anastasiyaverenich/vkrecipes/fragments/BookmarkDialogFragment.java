package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.adapters.NameOfBookmarkAdapterOnFeedFragment;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkUtils;

import java.util.List;

public class BookmarkDialogFragment extends DialogFragment{
    private Recipe.Feed feedsFromAdapter;
    NameOfBookmarkAdapterOnFeedFragment adapterNameOfBookmark;
    OnBookmarkItemClickListener listener;
    final List<BookmarkCategory> nameOfBookmark = VkRApplication.get().getMySQLiteHelper()
            .getAllCategoties();

    public BookmarkDialogFragment(Recipe.Feed feeds) {
        feedsFromAdapter = feeds;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapterNameOfBookmark = new NameOfBookmarkAdapterOnFeedFragment(getActivity(),
                R.layout.name_of_bookmark_list_item, nameOfBookmark);
        final AlertDialog.Builder builder = builderDialogAddBookmark;
        builder.setPositiveButton(R.string.button_add_bookmrk, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FragmentActivity activity = (FragmentActivity) (getActivity());
                FragmentManager fm = activity.getSupportFragmentManager();
                AddBookmarkDialogFragment bookmarkDialog = new AddBookmarkDialogFragment();
                bookmarkDialog.show(fm, "fragmentalert");
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
      return builder.create();
    }
    AlertDialog.Builder builderDialogAddBookmark = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).
            setTitle(R.string.change_bookmark)
            .setAdapter(adapterNameOfBookmark, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    BookmarkCategory checkedCategory = nameOfBookmark.get(which);
                    BookmarkUtils.addBookmark(feedsFromAdapter, checkedCategory.getCategoryId());
                    if (listener != null) {
                        listener.onBookmarkItemClick();
                    }
                }
            });

    @Override
    public void onStart() {
        super.onStart();

        Button pButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        Button nButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);

        pButton.setTextColor(getResources().getColor(R.color.fun_blue));
        nButton.setTextColor(getResources().getColor(R.color.fun_blue));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pButton.getLayoutParams();
        layoutParams.leftMargin = 50;
        layoutParams.rightMargin = 12;
        pButton.setLayoutParams(layoutParams);
    }

    public void setListener(OnBookmarkItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnBookmarkItemClickListener {
        void onBookmarkItemClick();
    }
}
