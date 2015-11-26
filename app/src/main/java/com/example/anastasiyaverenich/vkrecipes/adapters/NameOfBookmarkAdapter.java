package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.R;
import com.example.anastasiyaverenich.vkrecipes.modules.BookmarkCategory;

import java.util.List;

public class NameOfBookmarkAdapter extends ArrayAdapter<BookmarkCategory>{
    private final Context mContext;
    private final int mResourceId;
    private List<BookmarkCategory> objectOfCategory;
    public NameOfBookmarkAdapter(Context context,int resource, List<BookmarkCategory> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
        objectOfCategory = objects;
    }
    static class ViewHolder{
        TextView tvNameOfCategoryBookmark;
    }
    @Override
    public int getCount() {
        return objectOfCategory.size();
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
            viewHolder.tvNameOfCategoryBookmark = (TextView) convertView.findViewById(R.id.ll_tv_name_of_bookmark_list);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvNameOfCategoryBookmark.setText(objectOfCategory.get(position).getNameOfCategory());
        return convertView;
    }
}
