package com.example.anastasiyaverenich.vkrecipes.modules;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.anastasiyaverenich.vkrecipes.R;

import java.util.List;

public class FeedAdapter extends ArrayAdapter<Recipe.Feed> {
    private final Context mContext;
    private final int mResourceId;

    public FeedAdapter(Context context, int resource, List<Recipe.Feed> objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
    }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final View row = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
            final Recipe.Feed feed = getItem(position);

            TextView label = (TextView) row.findViewById(R.id.textView);
            label.setText( Html.fromHtml(feed.text).toString());
            return row;
        }
    }