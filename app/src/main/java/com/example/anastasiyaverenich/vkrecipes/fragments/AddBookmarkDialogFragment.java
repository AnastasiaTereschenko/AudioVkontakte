package com.example.anastasiyaverenich.vkrecipes.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;


public class AddBookmarkDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_edit_name_of_bookmark, null);
        final EditText editTextInputNewBookmark = (EditText) view.findViewById(R.id.feb_edit_bookmark);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyAlertDialogStyle ).
        setTitle(R.string.write_new_bookmark);
        builder.setView(view);
        builder.setPositiveButton(R.string.button_add_name_of_bookmark,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(editTextInputNewBookmark!=null) {
                    Editable name = editTextInputNewBookmark.getText();
                    Log.v("EditText value=", editTextInputNewBookmark.getText().toString());
                    BookmarkCategoryUtils.addCategory(editTextInputNewBookmark.getText().toString());
                }
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
