package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;
import com.example.anastasiyaverenich.vkrecipes.utils.BookmarkCategoryUtils;

import java.util.List;

public class NameOfBookmarkAdapter extends BaseSwipeAdapter {
    private final Context mContext;
    private final int mResourceId;
    private List<BookmarkCategory> objectOfCategory;

    public NameOfBookmarkAdapter(Context context, int resource, List<BookmarkCategory> objects) {
        this.mContext = context;
        mResourceId = resource;
        objectOfCategory = objects;
    }

    public View generateView(final int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.name_of_bookmark_list_item, null);
        SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<BookmarkCategory> nameOfBookmark = BookmarkCategoryUtils.getArrayOfCategoty();
                int idCategory = nameOfBookmark.get(position).getCategoryId();
                if (BookmarkCategoryUtils.checkCategories(idCategory)) {
                    BookmarkCategoryUtils.deleteBookmark(idCategory);
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }
   /* public void updateListView() {
        final List<BookmarkCategory> values = VkRApplication.get().getMySQLiteHelper().getAllCategoties();
        NameOfBookmarkAdapter adapterNameOfBookmark = new NameOfBookmarkAdapter(mContext,
                R.layout.name_of_bookmark_list_item, values);
        lvBookmark.setAdapter(adapterNameOfBookmark);
    }*/

    @Override
    public void fillValues(int position, View convertView) {
        TextView tvNameOfCategoryBookmark = (TextView) convertView.findViewById(R.id.ll_tv_name_of_bookmark_list);
        tvNameOfCategoryBookmark.setText(objectOfCategory.get(position).getNameOfCategory());
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
        return R.id.swipe;
    }
}
