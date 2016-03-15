package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;

import java.util.List;

public class NameOfBookmarkAdapterOnFeedFragment extends BaseAdapter{
    private final Context mContext;
    public final int mResourceId;
    private List<BookmarkCategory> objectOfCategory;

    public NameOfBookmarkAdapterOnFeedFragment(Context context, int resource, List<BookmarkCategory> objects) {
        this.mContext = context;
        mResourceId = resource;
        objectOfCategory = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.bookmark_menu, parent, false);
        TextView tvNameOfCategoryBookmark = (TextView) convertView.findViewById(R.id.ll_tv_name_of_bookmark_list);
        tvNameOfCategoryBookmark.setText(objectOfCategory.get(position).getNameOfCategory());
        return convertView;
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



}
