package com.example.anastasiyaverenich.vkrecipes.ui;

import android.widget.BaseAdapter;

import com.daimajia.swipe.implments.SwipeItemAdapterMangerImpl;

public class CustomSwipeItemMangerImpl extends SwipeItemAdapterMangerImpl {

    public CustomSwipeItemMangerImpl(BaseAdapter adapter) {
        super(adapter);
    }

    public void openAllItems() {
        for (int i = 0; i < mBaseAdapter.getCount(); i++) {
            if (!this.mOpenPositions.contains(Integer.valueOf(i))) {
                this.mOpenPositions.add(Integer.valueOf(i));
            }
        }

        if (this.mBaseAdapter != null) {
            this.mBaseAdapter.notifyDataSetChanged();
        } else if (this.mRecyclerAdapter != null) {
            // this.mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void closeAllItems() {
        for (int i = 0; i < mBaseAdapter.getCount(); i++) {
            if (this.mOpenPositions.contains(Integer.valueOf(i))) {
                this.mOpenPositions.clear();
            }
        }

        if (this.mBaseAdapter != null) {
            this.mBaseAdapter.notifyDataSetChanged();
        } else if (this.mRecyclerAdapter != null) {
            // this.mRecyclerAdapter.notifyDataSetChanged();
        }
    }
}

