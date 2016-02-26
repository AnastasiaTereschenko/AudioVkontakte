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

public class EditBookmarkDialogFragment extends DialogFragment{
    EditText editTextInputNewBookmark;
    int position;
    BookmarkItemEditListener listener;
    String lastNameOfCategory;

    public EditBookmarkDialogFragment(String lastNameOfCategory, int position) {
        this.lastNameOfCategory = lastNameOfCategory;
        this.position = position;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_edit_name_of_bookmark, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyAlertDialogStyle ).
                setTitle(R.string.write_new_bookmark);
        builder.setView(view);
        editTextInputNewBookmark = (EditText)view.findViewById(R.id.feb_edit_bookmark);
        editTextInputNewBookmark.setText(lastNameOfCategory);
        builder.setPositiveButton(R.string.button_edit_name_of_bookmark,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (listener != null) {
                    listener.bookmarkEditClick(editTextInputNewBookmark.getText().toString(), position);
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
    public void setListener(EditBookmarkDialogFragment.BookmarkItemEditListener listener) {
        this.listener = listener;
    }
    public interface BookmarkItemEditListener {
        void bookmarkEditClick(String nameOfCategory, int position);
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
