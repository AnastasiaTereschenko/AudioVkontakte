package com.example.anastasiyaverenich.vkrecipes.ui;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private boolean isLoading = true;

    public EndlessScrollListener() {
    }
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

    public abstract void loadData();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;

    }
}


