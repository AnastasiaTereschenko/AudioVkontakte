package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.fragments.EditBookmarkDialogFragment;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.ui.CustomSwipeItemMangerImpl;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;

import java.util.List;

public class NameOfBookmarkAdapter extends BaseSwipeAdapter {
    private final Context mContext;
    public final int mResourceId;
    private List<BookmarkCategory> objectOfCategory;
    private boolean isEdit;
    private int currentDeleteAndEditPosition = -1;
    BookmarkItemClickListener listener;

    public NameOfBookmarkAdapter(Context context, int resource, List<BookmarkCategory> objects) {
        this.mContext = context;
        mResourceId = resource;
        objectOfCategory = objects;
        mItemManger = new CustomSwipeItemMangerImpl(this);
    }

    public View generateView(final int position, final ViewGroup parent) {
        final View convertView = LayoutInflater.from(mContext).inflate(R.layout.name_of_bookmark_list_item, parent, false);
        final SwipeLayout bookmarkSwipe = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        bookmarkSwipe.setShowMode(SwipeLayout.ShowMode.LayDown);
        bookmarkSwipe.addDrag(SwipeLayout.DragEdge.Right, bookmarkSwipe.findViewWithTag("Top"));
        bookmarkSwipe.setShowMode(SwipeLayout.ShowMode.PullOut);
        bookmarkSwipe.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.BookmarkItemClick(position);
                }
            }
        });
        convertView.findViewById(R.id.nobli_ll_trash).setOnClickListener(new OnBookmarkDeleteClick(position));
        convertView.findViewById(R.id.nobli_ll_edit).setOnClickListener(new OnBookmarkEditClick(position));
        return convertView;
    }

    private class OnBookmarkEditClick implements View.OnClickListener {
        private int position;

        public OnBookmarkEditClick(int position) {
            this.position = position;
        }

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
                    currentDeleteAndEditPosition = position;
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class OnBookmarkDeleteClick implements View.OnClickListener {
        private int position;

        public OnBookmarkDeleteClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            final List<BookmarkCategory> nameOfBookmark = BookmarkCategoryUtils.getArrayOfCategoty();
            int idCategory = nameOfBookmark.get(position).getCategoryId();
            if (BookmarkCategoryUtils.checkCategories(idCategory)) {
                BookmarkCategoryUtils.deleteBookmark(idCategory);
                currentDeleteAndEditPosition = position;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView tvNameOfCategoryBookmark = (TextView) convertView.findViewById(R.id.ll_tv_name_of_bookmark_list);
        tvNameOfCategoryBookmark.setText(objectOfCategory.get(position).getNameOfCategory());
        if (currentDeleteAndEditPosition == position) {
            currentDeleteAndEditPosition = -1;
            closeItem(position);
        }
    }


    public void onEdit() {
        if (isEdit) {
            closeAllItems();
        } else {
            ((CustomSwipeItemMangerImpl) mItemManger).openAllItems();
        }
        //isEdit = !isEdit;
    }

    @Override
    public int getCount() {
        return objectOfCategory.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        //return R.id.ll_name_of_bookmark;
        return 5;
    }

    public void setListener(BookmarkItemClickListener listener) {
        this.listener = listener;
    }

    public interface BookmarkItemClickListener {
        void BookmarkItemClick(int position);
    }
}
