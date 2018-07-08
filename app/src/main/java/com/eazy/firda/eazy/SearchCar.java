package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.CarListAdapter;
import com.eazy.firda.eazy.models.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class SearchCar extends AppCompatActivity {

    private ProgressDialog pDialog;
    private List<Car> cars = new ArrayList<Car>();
    private ListView lvcar;
    private CarListAdapter adapter;
    private String url = "http://new.entongproject.com/api/customer/list_car/";
    private String callUrl = "http://new.entongproject.com/api/customer/custom_list_car";
    float distance;
    Location startPoint, endPoint;

    boolean distAsc, priceAsc, rateAsc = true;

    SimpleDateFormat day_name, day_number, month_name, date, time;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String dateStart, timeStart, dateEnd, timeEnd, producer, transmission, member_id;
    String[] ds, de;
    String data;
    int type1, type2, type3, type4, minVal, maxVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        month_name = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);
        date = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        time = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        ds = sp.getString("search_dateStart", null).split("/");
        de = sp.getString("search_dateEnd", null).split("/");

        Date date1 = new Date();
        Date date2 = new Date();

        try{
            date1 = date.parse(sp.getString("search_dateStart", null));
            date2 = date.parse(sp.getString("search_dateEnd", null));
            Log.v("date", day_name.format(date1));
        }catch(ParseException e){
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View v = getSupportActionBar().getCustomView();
        ImageView back = (ImageView) v.findViewById(R.id.btn_back);
        TextView location = (TextView)v.findViewById(R.id.location);
        TextView date = (TextView)v.findViewById(R.id.date);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(member_id == null){
                    startActivity(new Intent(SearchCar.this, GuestMain.class));
                }
                else{
                    startActivity(new Intent(SearchCar.this, SearchActivity.class));
                }
                finish();
            }
        });
        location.setText(sp.getString("location_pickup", null));
        date.setText(day_name.format(date1) + ", " + ds[1] + " "
                + month_name.format(date1) + ", " + sp.getString("search_timeStart", null)
                + " - " + day_name.format(date2) + ", " + de[1] + " "
                + month_name.format(date2) + ", " + sp.getString("search_timeEnd", null));

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(SearchCar.this);
        Log.v("shared Lat", String.valueOf(sharedPreferences.getLong("user_lat",0)));
        Log.v("shared Lng", String.valueOf(sharedPreferences.getLong("user_lang", 0)));

        Double latStart = Double.longBitsToDouble(sharedPreferences.getLong("user_lat", 0));
        Double lngStrat = Double.longBitsToDouble(sharedPreferences.getLong("user_lang", 0));
        Log.v("location", latStart.toString() + " " + lngStrat.toString());

        member_id = sharedPreferences.getString("user_id", null);
        dateStart = sharedPreferences.getString("search_dateStart", null);
        timeStart = sharedPreferences.getString("search_timeStart", null);
        dateEnd = sharedPreferences.getString("search_dateEnd", null);
        timeEnd = sharedPreferences.getString("search_timeEnd", null);
        producer = sharedPreferences.getString("search_producer", null);
        transmission = sharedPreferences.getString("search_transmission", null);
        type1 = sharedPreferences.getInt("search_type1", 0);
        type2 = sharedPreferences.getInt("search_type2", 0);
        type3 = sharedPreferences.getInt("search_type3", 0);
        type4 = sharedPreferences.getInt("search_type4", 0);
        minVal = sharedPreferences.getInt("search_minVal", 0);
        maxVal = sharedPreferences.getInt("search_maxVal", 0);

        data = dateStart + "#" + timeStart + "#" + dateEnd + "#" + timeEnd +
                "#" + producer + "#" + type1 + "#" + type2 + "#" + type3 + "#" + type4 +
                "#" + transmission + "#" + minVal + "#" + maxVal;
        Log.v("data", data);

        startPoint = new Location("Point A");
        startPoint.setLatitude(latStart);
        startPoint.setLongitude(lngStrat);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_filter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                Intent filter = new Intent(SearchCar.this,FilterActivity.class);
                SearchCar.this.startActivity(filter);
            }
        });

        FloatingActionButton fab_map = (FloatingActionButton) findViewById(R.id.fab_maps);
        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(SearchCar.this, CarMap.class);
                startActivity(map);
            }
        });

        lvcar = (ListView) findViewById(R.id.lv_car);
        adapter = new CarListAdapter(this, cars);
        cars.clear();

        //getCars();
        callCars cc = new callCars();
        cc.execute();

        SegmentedButtonGroup sbg = (SegmentedButtonGroup)findViewById(R.id.segmentedButtonGroup);

        sbg.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if(position == 0){
                    if(distAsc){
                        sortByDistAsc();
                        distAsc = false;
                    }
                    else{
                        sortByDistDesc();
                        distAsc = true;
                    }
                }
                else if(position == 1){
                    if(priceAsc){
                        sortByPriceAsc();
                        priceAsc = false;
                    }
                    else{
                        sortByPriceDesc();
                        priceAsc = true;
                    }
                }
                else if(position == 2){
                    if(rateAsc){
                        sortByRateAsc();
                        rateAsc = false;
                    }
                    else{
                        sortByRateDesc();
                        rateAsc = true;
                    }
                }
            }
        });

        adapter.notifyDataSetChanged();
        lvcar.setAdapter(adapter);

        lvcar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Car car = (Car) parent.getItemAtPosition(position);
                editor.putString("car_id", car.getCar_id());
                editor.apply();
                Intent intent = new Intent(view.getContext(), DetailCarActivity.class);
                Bundle extras = new Bundle();
                extras.putString("screen", "search");
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        /*
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(member_id == null){
            startActivity(new Intent(SearchCar.this, GuestMain.class));
        }
        else{
            startActivity(new Intent(SearchCar.this, HomeActivity.class));
        }
        finish();
    }

    private class callCars extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(SearchCar.this);
            progressDialog.setMessage("");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            Log.v("url", callUrl);
            Log.v("data", data);
            JSONObject response = jParser.getJsonFromUrl(callUrl, data);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            progressDialog.dismiss();
            String result;

            try{
                //result = response.getString("result");

                if(response.getString("result").equals("success")){
                    JSONArray mCars = response.getJSONArray("cars");
                    for(int i = 0, size = mCars.length(); i < size; i++){
                        JSONObject carDetail = mCars.getJSONObject(i);
                        Log.v("carDetail", carDetail.toString());

                        endPoint = new Location("Point B");
                        String location =  "not identified";
                        String city = "not identified";

                        if ((!carDetail.isNull("location_lat") || carDetail.getDouble("location_lat") != 0.0)
                                && (!carDetail.isNull("location_long")|| carDetail.getDouble("location_long") != 0.0)) {
                            Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(carDetail.getDouble("location_lat"),
                                        carDetail.getDouble("location_long"), 1);
                                Address obj = addresses.get(0);
                                city = addresses.get(0).getLocality();
                                location = obj.getAddressLine(0);
                                //location = obj.getFeatureName();
                                endPoint.setLatitude(carDetail.getDouble("location_lat"));
                                endPoint.setLongitude(carDetail.getDouble("location_long"));
                                distance = (startPoint.distanceTo(endPoint))/1000;

                                Log.v("address", addresses.toString());
                                Log.v("obj", obj.toString());

                            }
                            catch(Exception e){
                                Log.e("address", "error", e);
                            }
                        }

                        Log.v("location", location);
                        Log.v("distance", String.valueOf(distance));

                        String imageCar = carDetail.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : carDetail.getString("photo");
                        Car car = new Car(carDetail.getString("id"), carDetail.getString("car_name"),
                                carDetail.getString("owner_name"), imageCar, carDetail.getInt("year"), carDetail.getDouble("price"),
                                carDetail.getDouble("total_rating"), location, city, 0, distance);

                        cars.add(car);
                    }
                }
                else{
                    Log.v("Error", response.getString("message"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void sortByDistAsc() {
        Comparator<Car> comparator = new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return Double.compare(o1.getDistance(), o2.getDistance());
            }
        };
        Collections.sort(cars, comparator);
        adapter.notifyDataSetChanged();
    }

    public void sortByDistDesc() {
        Comparator<Car> comparator = new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return Double.compare(o2.getDistance(), o1.getDistance());
            }
        };
        Collections.sort(cars, comparator);
        adapter.notifyDataSetChanged();
    }

    public void sortByPriceAsc() {
        Comparator<Car> comparator = new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        };
        Collections.sort(cars, comparator);
        adapter.notifyDataSetChanged();
    }

    public void sortByPriceDesc() {
        Comparator<Car> comparator = new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return Double.compare(o2.getPrice(), o1.getPrice());
            }
        };
        Collections.sort(cars, comparator);
        adapter.notifyDataSetChanged();
    }

    public void sortByRateAsc(){
        Comparator<Car> comparator = new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return Double.compare(o1.getRating(), o2.getRating());
            }
        };
        Collections.sort(cars, comparator);
        adapter.notifyDataSetChanged();

    }

    public void sortByRateDesc(){
        Comparator<Car> comparator = new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return Double.compare(o2.getRating(), o1.getRating());
            }
        };
        Collections.sort(cars, comparator);
        adapter.notifyDataSetChanged();

    }

    private double distance(double lat1, double lng1, double lat2, double lng2){

        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1))
                    *Math.sin(deg2rad(lat2))
                    +Math.cos(deg2rad(lat1))
                    *Math.cos(deg2rad(lat2))
                    *Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist;
    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad){
        return (rad * 180.0 / Math.PI);
    }
}