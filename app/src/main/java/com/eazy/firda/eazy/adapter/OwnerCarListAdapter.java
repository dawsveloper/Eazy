package com.eazy.firda.eazy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * Created by firda on 3/30/2018.
 */

public class OwnerCarListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Car> cars;
    public String car_id;
    TextView txtStatus;
    ImageView imgStatus;
    RelativeLayout layStatus;

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    public OwnerCarListAdapter(Activity activity, List<Car> cars){
        this.activity = activity;
        this.cars = cars;
    }

    @Override
    public int getCount() { return cars.size(); }

    public Object getItem(int location) {
        return cars.get(location);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View vw, ViewGroup parent) {

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

        layStatus = vw.findViewById(R.id.layStatus);
        imgStatus = vw.findViewById(R.id.imgStatus);
        txtStatus = vw.findViewById(R.id.txtStatus);
        layStatus.setVisibility(View.INVISIBLE);

        TextView name = (TextView)vw.findViewById(R.id.row_car_name);
        TextView price = (TextView)vw.findViewById(R.id.row_car_price);
        NetworkImageView thumbnail = (NetworkImageView) vw.findViewById(R.id.row_car_img);
        TextView year = (TextView)vw.findViewById(R.id.row_car_year);

        Car c = cars.get(position);

        int status = c.getStatus();
        Log.v("car status", String.valueOf(status));

        if(status == 0){
            layStatus.setVisibility(View.VISIBLE);
            imgStatus.setImageDrawable(vw.getResources().getDrawable(R.drawable.rect_red));
            txtStatus.setText("Waiting Approval");
        }
        else if(status == 1){
            layStatus.setVisibility(View.VISIBLE);
            imgStatus.setImageDrawable(vw.getResources().getDrawable(R.drawable.rect_green));
            txtStatus.setText("Approved");
        }

        thumbnail.setImageUrl(c.getCarImage(), imageLoader);
        name.setText(c.getName());
        year.setText(Integer.toString(c.getYear()));

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        price.setText("RM " + formatter.format((int)c.getPrice()));
        car_id = c.getCar_id();
        return vw;
    }
}
