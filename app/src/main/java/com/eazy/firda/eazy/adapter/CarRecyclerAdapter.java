package com.eazy.firda.eazy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.DetailCarActivity;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.models.Car;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

/**
 * Created by firda on 3/14/2018.
 */

public class CarRecyclerAdapter extends RecyclerView.Adapter<CarRecyclerAdapter.MyView> {

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();
    private List<Car> cars;
    private String type;
    private LayoutInflater inflater;
    private Context mContext;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView name, price, trips, city, year, rate;
        public RatingBar ratingBar;
        public NetworkImageView thumbnail;
        public String car_id;

        public MyView(View view) {
            super(view);

            name = (TextView)view.findViewById(R.id.row_car_name);
            price = (TextView)view.findViewById(R.id.row_car_price);
            thumbnail = (NetworkImageView) view.findViewById(R.id.row_car_img);
            ratingBar = (RatingBar)view.findViewById(R.id.row_car_rate);
            trips = (TextView)view.findViewById(R.id.row_car_trips);
            city = (TextView)view.findViewById(R.id.row_car_city);
            year = (TextView)view.findViewById(R.id.row_car_year);
            rate = view.findViewById(R.id.txtRate);

        }
    }

    public CarRecyclerAdapter(Context context, List<Car> horizontalList, String fav) {
        this.cars = horizontalList;
        this.type = fav;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CarRecyclerAdapter(Context context, List<Car> horizontalList){
        this.cars = horizontalList;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public CarRecyclerAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_horizontal_row, parent, false);

        sp = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        editor = sp.edit();

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(CarRecyclerAdapter.MyView holder, int position) {
        final Car c = cars.get(position);

        holder.name.setText(c.getName());
        holder.thumbnail.setImageUrl(c.getCarImage(), imageLoader);
        //thumbnail.setImageUrl("http://new.entongproject.com/images/car/car_default.png", imageLoader);

//        if(type == "rating"){
//            holder.trips.setVisibility(View.GONE);
//            holder.ratingBar.setVisibility(View.VISIBLE);
//            holder.ratingBar.setRating((float)c.getRating());
//        }
//        else if(type == "trips"){
//            holder.trips.setVisibility(View.VISIBLE);
//            holder.ratingBar.setVisibility(View.GONE);
//            holder.trips.setText(c.getTrips() + " trip(s)");
//        }
        holder.trips.setVisibility(View.GONE);
        holder.ratingBar.setVisibility(View.GONE);

        holder.name.setText(c.getName());
        holder.year.setText(Integer.toString(c.getYear()));
        holder.rate.setText(String.valueOf(c.getRating()));

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        holder.price.setText("RM " + formatter.format((int)c.getPrice()));
        holder.city.setText(c.getCity());
        holder.car_id = c.getCar_id();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("recycler_id", c.getCar_id());

                editor.putString("car_id", c.getCar_id());
                editor.apply();

                Intent intent = new Intent(mContext, DetailCarActivity.class);
                Bundle extras = new Bundle();
                extras.putString("screen", "home");
                intent.putExtras(extras);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }
}
