package com.eazy.firda.eazy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.models.Car;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
/**
 * Created by firda on 2/9/2018.
 */

public class CarListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private List<Car> cars;
    public String car_id;

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    public CarListAdapter(Activity activity, List<Car> cars){
        this.activity = activity;
        this.cars = cars;
    }

    public int getCount(){
        return cars.size();
    }

    public Object getItem(int location) {
        return cars.get(location);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View vw, ViewGroup parent){

        if(inflater == null){
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(vw == null){
            vw = inflater.inflate(R.layout.car_list_row, null);
        }


        if(imageLoader == null){
            imageLoader = EazyApplication.getInstance().getImageLoader();
        }

        TextView name = (TextView)vw.findViewById(R.id.row_car_name);
        TextView distance = (TextView)vw.findViewById(R.id.row_car_distance);
        TextView price = (TextView)vw.findViewById(R.id.row_car_price);
        NetworkImageView thumbnail = (NetworkImageView) vw.findViewById(R.id.row_car_img);
        RatingBar ratingBar = (RatingBar)vw.findViewById(R.id.row_car_rate);
        TextView trips = (TextView)vw.findViewById(R.id.row_car_trips);
        TextView city = (TextView)vw.findViewById(R.id.row_car_city);
        TextView year = (TextView)vw.findViewById(R.id.row_car_year);

        Car c = cars.get(position);

        thumbnail.setImageUrl(c.getCarImage(), imageLoader);
        //thumbnail.setImageUrl("http://new.entongproject.com/images/car/car_default.png", imageLoader);
        ratingBar.setRating((float)c.getRating());
        name.setText(c.getName());
        year.setText(Integer.toString(c.getYear()));
        distance.setText(" ~" + new DecimalFormat("##.##").format(c.getDistance())+ "KM");

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        price.setText("RM " + formatter.format((int)c.getPrice()));
        trips.setText(c.getTrips() + " trip(s)");
        city.setText(c.getCity());
        car_id = c.getCar_id();

        return vw;
    }
}
