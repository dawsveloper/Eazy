package com.eazy.firda.eazy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eazy.firda.eazy.CarOwnerPhotos;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.models.Car;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by firda on 4/15/2018.
 */

public class OwnerCarPhotosAdapter extends RecyclerView.Adapter<OwnerCarPhotosAdapter.MyView> {
    private List<Car> cars;
    private LayoutInflater inflater;
    private Context mContext;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public  String photoUrl, carId;

    public class MyView extends RecyclerView.ViewHolder{

        public ImageView photo, delPhoto;
        public MyView(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            delPhoto = itemView.findViewById(R.id.delPhoto);
        }
    }

    public OwnerCarPhotosAdapter(Context context, List<Car> horizontalList) {
        this.cars = horizontalList;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_car_photo, parent, false);

        sp = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        editor = sp.edit();

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, int position) {
        final Car c = cars.get(position);

        Ion.with(holder.photo)
                .placeholder(R.drawable.rsz_ic_car)
                .error(R.drawable.ic_close_black_18dp)
                .load(c.getCarImage());

        photoUrl = c.getCarImage();
        carId = c.getCar_id();
        Log.v("car id", carId);
//        ((MyView)holder).delPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v("car id", c.getCar_id());
//            }
//        });

        holder.delPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete del = new delete();
                del.execute(photoUrl, carId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    private class delete extends AsyncTask<String, Void, Void>{

//        private final ProgressDialog dialog = new ProgressDialog(mContext);
        String deleteUrl = "http://new.entongproject.com/api/provider/rental/api_delete_car_photo";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            this.dialog.setMessage("Deleting...");
//            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            Log.v("id", strings[0]);
            JSONParser jParser = new JSONParser();

            JSONObject response = jParser.getJsonFromUrl(deleteUrl, strings[0]+"|"+strings[1]);

            try{
                String result = response.getString("result");
                if(result.equals("success")){
                    Intent refresh = new Intent(mContext, CarOwnerPhotos.class);
                    Bundle extras = new Bundle();
                    extras.putString("car_id", carId);
                    refresh.putExtras(extras);
                    mContext.startActivity(refresh);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            this.dialog.dismiss();
        }
    }

}
