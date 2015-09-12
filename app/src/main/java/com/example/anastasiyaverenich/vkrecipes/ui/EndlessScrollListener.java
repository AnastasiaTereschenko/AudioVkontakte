package com.example.anastasiyaverenich.vkrecipes.ui;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private boolean isLoading = true;

    public EndlessScrollListener() {
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount)
    {

        if (view.getAdapter() == null)
            return ;

        if (view.getAdapter().getCount() < 1)
            return ;

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            isLoading = true;
            loadData();
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void loadData();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }

    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;

    }
}


