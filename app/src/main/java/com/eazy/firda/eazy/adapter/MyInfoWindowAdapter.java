package com.eazy.firda.eazy.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.application.EazyApplication;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by firda on 2/25/2018.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater mInflater;
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    public MyInfoWindowAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = mInflater
                .inflate(R.layout.info_window, null);

        TextView car_name = view.findViewById(R.id.car_name);
        TextView owner_name = view.findViewById(R.id.owner_name);

        TextView address = view.findViewById(R.id.address);
        TextView price = view.findViewById(R.id.price);

        NetworkImageView photo = view.findViewById(R.id.row_car_img);

        if(marker.getSnippet() != null){
            /*
            Car car = (Car) marker.getTag();
            car_name.setText(car.getCarName());
            owner_name.setText(car.getOwnerName());
            address.setText(car.getAddress());
            rating.setText(car.getRating() + " / 5");
            */
            Log.v("marker snippet", marker.getSnippet());
            String[] info = marker.getSnippet().toString().split("#");
            car_name.setText(info[1]);
            owner_name.setText(info[2]);
            address.setText(info[3]);

            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setGroupingSeparator(',');
            DecimalFormat formatter = new DecimalFormat("###,###", symbols);

            price.setText("RM " + formatter.format(Integer.parseInt(info[4])));

            String imageUrl = "";
            Log.v("imageUrl", info[6]);
            if(info[6] == null){
                imageUrl = info[6];
            }
            else{
                imageUrl = "http://new.entongproject.com/images/car/car_default.png";
            }
            photo.setImageUrl(imageUrl, imageLoader);
//            new DownloadImageTask((ImageView)view.findViewById(R.id.row_car_img)).execute("http://new.entongproject.com/images/car/car_default.png");
        }
        else{
            Log.v("marker snippet:", "Null");
        }
        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Image Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

