package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.fragments.EditBookmarkDialogFragment;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView textViewData;
        ImageView buttonDelete;
        ImageView buttonEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.nobli_swipe);
            textViewData = (TextView) itemView.findViewById(R.id.ll_tv_name_of_bookmark_list);
            buttonDelete = (ImageView) itemView.findViewById(R.id.nobli_ll_trash);
            buttonEdit = (ImageView) itemView.findViewById(R.id.nobli_ll_edit);
        }
    }

    private final Context mContext;
    public final int mResourceId;
    private List<BookmarkCategory> objectOfCategory;
    private boolean isEdit;
    // private int currentDeleteAndEditPosition = -1;
    BookmarkItemClickListener listener;

    public RecyclerViewAdapter(Context context, int resource, List<BookmarkCategory> objects) {
        this.mContext = context;
        mResourceId = resource;
        objectOfCategory = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final View convertView = LayoutInflater.from(mContext).inflate(R.layout.name_of_bookmark_list_item, parent, false);
        return new SimpleViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder simpleViewHolder, final int position) {
        final List<BookmarkCategory> nameOfBookmark = BookmarkCategoryUtils.getArrayOfCategoty();
        String item = objectOfCategory.get(position).getNameOfCategory();
        final int idCategory = nameOfBookmark.get(position).getCategoryId();
        simpleViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        simpleViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        simpleViewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.BookmarkItemClick(position);
                }
            }
        });
        simpleViewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.nobli_ll_trash));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.nobli_ll_edit));
            }
        });
        simpleViewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BookmarkCategoryUtils.checkCategories(idCategory)) {
                    BookmarkCategoryUtils.deleteBookmark(idCategory);
                    closeItem(position);
                    notifyDatasetChanged();
                    Toast.makeText(view.getContext(), "Удалена категория " +
                                    simpleViewHolder.textViewData.getText().toString().toLowerCase() + ".",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        simpleViewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = (FragmentActivity) (mContext);
                FragmentManager fm = activity.getSupportFragmentManager();
                String lastNameOfCategory = objectOfCategory.get(position).getNameOfCategory();
                EditBookmarkDialogFragment editBookmarkDialogFragment = new EditBookmarkDialogFragment
                        (lastNameOfCategory, position);
                editBookmarkDialogFragment.show(fm, "fragmentalert");
                editBookmarkDialogFragment.setListener(new EditBookmarkDialogFragment.BookmarkItemEditListener() {
                    @Override
                    public void bookmarkEditClick(String nameOfCategory, int position) {
                        BookmarkCategoryUtils.updateNameOfCategory(nameOfCategory, position);
                        objectOfCategory.get(position).setNameOfCategory(nameOfCategory);
                        //currentDeleteAndEditPosition = position;
                        notifyDataSetChanged();
                    }
                });
                mItemManger.closeAllItems();
            }
        });
        simpleViewHolder.textViewData.setText(item);
        mItemManger.bind(simpleViewHolder.itemView, position);
    }

    public void onEdit() {
        if (isEdit) {
            closeAllItems();
        } else {
            openAllItems(getItemCount());
        }
        isEdit = !isEdit;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return objectOfCategory.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.nobli_swipe;
    }

    public void setListener(BookmarkItemClickListener listener) {
        this.listener = listener;
    }

    public interface BookmarkItemClickListener {
        void BookmarkItemClick(int position);
    }
}