package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.application.VkRApplication;


public class AddBookmarkDialogFragment extends DialogFragment {
    EditText editTextInputNewBookmark;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_add_name_of_bookmark, null);
        builder.setTitle(R.string.change_bookmark);
        builder.setView(view);
        editTextInputNewBookmark = (EditText)view.findViewById(R.id.fab_add_new_bookmark);
        builder.setPositiveButton(R.string.button_add_name_bookmark,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                VkRApplication.get().getMySQLiteHelper().addCategories(editTextInputNewBookmark.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Button pButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        Button nButton =  ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE);

        pButton.setTextColor(getResources().getColor(R.color.fun_blue));
        nButton.setTextColor(getResources().getColor(R.color.fun_blue));
    }
}
