package com.eazy.firda.eazy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.models.Car;

import java.util.List;

/**
 * Created by firda on 3/31/2018.
 */

public class CarImageSlider extends RecyclerView.Adapter<CarImageSlider.MyView>  {

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();
    private List<Car> cars;
    private LayoutInflater inflater;
    private Context mContext;
    public class MyView extends RecyclerView.ViewHolder {

//        public ImageView thumbnail;
        public NetworkImageView thumbnail;
        public String car_id;


        public MyView(View view) {
            super(view);

            thumbnail = view.findViewById(R.id.row_car_img);

        }
    }

    public CarImageSlider(Context context, List<Car> horizontalList) {
        this.cars = horizontalList;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public CarImageSlider.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_row, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(CarImageSlider.MyView holder, int position) {
        final Car c = cars.get(position);
        holder.thumbnail.setImageUrl(c.getCarImage(), imageLoader);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }
}
