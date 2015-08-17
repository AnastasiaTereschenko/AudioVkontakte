package com.example.anastasiyaverenich.vkrecipes.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.anastasiyaverenich.vkrecipes.modules.Recipe;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class PhotosPagerAdapter extends PagerAdapter {
    private ArrayList<Recipe.Photo> photos;
    //private int position;

    public PhotosPagerAdapter(ArrayList<Recipe.Photo> photos){
        this.photos = photos;
       // this.position = position;
    }
    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        ImageLoader.getInstance().displayImage(photos.get(position).src_big, photoView);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
