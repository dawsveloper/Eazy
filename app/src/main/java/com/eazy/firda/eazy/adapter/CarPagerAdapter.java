package com.eazy.firda.eazy.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.application.EazyApplication;

import java.util.ArrayList;

/**
 * Created by firda on 3/13/2018.
 */

public class CarPagerAdapter extends PagerAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<String> url;
    public String car_id;
    private Context mContext;
    private static final String TAG = "MyPagerAdapter";
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    public CarPagerAdapter(Context context, ArrayList<String> url){
        this.activity = activity;
        this.url = url;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        // Inflate a new layout from our resources

        View vw = inflater.inflate(R.layout.ads_row, collection, false);
        if(inflater == null){
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(imageLoader == null){
            imageLoader = EazyApplication.getInstance().getImageLoader();
        }

        NetworkImageView thumbnail = (NetworkImageView) vw.findViewById(R.id.row_car_img);

        thumbnail.setImageUrl(url.get(position), imageLoader);

        vw.setTag(url);
        // Add the newly created View to the ViewPager
        collection.addView(vw);
        Log.i(TAG, "instantiateItem() [position: " + position + "]" + " childCount:" + collection.getChildCount());
        // Return the View
        return vw;
    }

    @Override
    public int getCount() {
        return url.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(TAG, "destroyItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
    }
}
