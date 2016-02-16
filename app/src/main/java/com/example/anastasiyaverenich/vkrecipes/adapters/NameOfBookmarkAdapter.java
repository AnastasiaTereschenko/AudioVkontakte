package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.ui.CustomSwipeItemMangerImpl;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;

import java.util.List;

public class NameOfBookmarkAdapter extends BaseSwipeAdapter {
    private final Context mContext;
    private final int mResourceId;
    private List<BookmarkCategory> objectOfCategory;
    private boolean isEdit;
    private int currentDeletePosition = -1;
    BookmarkItemClickListener listener;
    public NameOfBookmarkAdapter(Context context, int resource, List<BookmarkCategory> objects) {
        this.mContext = context;
        mResourceId = resource;
        objectOfCategory = objects;
        mItemManger = new CustomSwipeItemMangerImpl(this);
    }


    public View generateView(final int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.sample_together, parent, false);
        SwipeLayout sample2 = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        sample2.setShowMode(SwipeLayout.ShowMode.LayDown);
        sample2.addDrag(SwipeLayout.DragEdge.Right, sample2.findViewWithTag("Bottom2"));
        sample2.setShowMode(SwipeLayout.ShowMode.PullOut);
        sample2.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.BookmarkItemClick(position);
                }
            }
        });
        convertView.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<BookmarkCategory> nameOfBookmark = BookmarkCategoryUtils.getArrayOfCategoty();
                int idCategory = nameOfBookmark.get(position).getCategoryId();
                if (BookmarkCategoryUtils.checkCategories(idCategory)) {
                    BookmarkCategoryUtils.deleteBookmark(idCategory);
                    currentDeletePosition = position;
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView tvNameOfCategoryBookmark = (TextView) convertView.findViewById(R.id.ll_tv_name_of_bookmark_list);
        tvNameOfCategoryBookmark.setText(objectOfCategory.get(position).getNameOfCategory());
        if (currentDeletePosition == position) {
            currentDeletePosition = -1;
            closeItem(position);
        }
    }


    public void onEdit() {
        if (isEdit) {
            closeAllItems();
        } else {
            ((CustomSwipeItemMangerImpl) mItemManger).openAllItems();
        }
        isEdit = !isEdit;
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
        return R.id.sample2;
    }

    public void setListener(BookmarkItemClickListener listener) {
        this.listener = listener;
    }

    public interface BookmarkItemClickListener {
        void BookmarkItemClick(int position);
    }

}
