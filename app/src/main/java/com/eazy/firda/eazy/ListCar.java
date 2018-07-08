package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.eazy.firda.eazy.adapter.CarListAdapter;
import com.eazy.firda.eazy.models.Car;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class ListCar extends AppCompatActivity implements AAH_FabulousFragment.Callbacks {

    ImageView back;
    TextView dateStart, timeStart, dateEnd, timeEnd, nullCar;
    LinearLayout layTime;
    FloatingActionButton filter;
    SegmentedButtonGroup sbg;
    ScrollView svMain;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    JsonObject json = new JsonObject();
    List<Car> cars = new ArrayList<Car>();
    ListView lvcar;
    CarListAdapter adapter;
    filter_frag dialogFrag;
    SimpleDateFormat month_name, month_number, day_name, day_number, year_number, date;
    Date c_date;
    Calendar c;

    String member_id, transmission, year, type, fuel, producer;
    String s_dateStart, s_timeStart, s_dateEnd, s_timeEnd, defDateStart, defDateEnd, defTimeStart, defTimeEnd;
    String newDayStart, newMonthStart, newDayEnd, newMonthEnd, newHourStart, newMinStart, newHourEnd, newMinEnd;
    String[] ds, de;
    int minPrice, maxPrice;
    int yearStart, monthStart, dayStart, hourStart, minStart, yearEnd, monthEnd, dayEnd, hourEnd, minEnd;
    boolean distAsc, priceAsc, rateAsc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        svMain = findViewById(R.id.svMain);
        nullCar = findViewById(R.id.nullCar);

        month_name = new SimpleDateFormat("MMM", Locale.ENGLISH);
        month_number = new SimpleDateFormat("MM", Locale.ENGLISH);
        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);
        year_number = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        date = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        c = Calendar.getInstance(TimeZone.getDefault());
        c_date = c.getTime();
        yearStart = c.get(Calendar.YEAR);
        monthStart = c.get(Calendar.MONTH);
        dayStart = c.get(Calendar.DAY_OF_MONTH);
        hourStart = c.get(Calendar.HOUR_OF_DAY);
        minStart = c.get(Calendar.MINUTE);

        Log.v("calendar", c.toString());
        Log.v("year", String.valueOf(yearStart));
        Log.v("month", String.valueOf(monthStart));
        Log.v("day", String.valueOf(dayStart));
        Log.v("hour", String.valueOf(hourStart));
        Log.v("min", String.valueOf(minStart));

        if((monthStart+1) < 10){
            newMonthStart = "0" + (monthStart+1);
        }else{
            newMonthStart = ""+ (monthStart+1);
        }

        if(dayStart < 10){
            newDayStart = "0" + dayStart;
        }
        else{
            newDayStart = "" + dayStart;
        }

        if(hourStart < 10){
            newHourStart = "0" + hourStart;
        }else{
            newHourStart = ""+ hourStart;
        }

        if(minStart < 10) {
            newMinStart = "0" + minStart;
        }else{
            newMinStart = "" + minStart;
        }

        defDateStart = newMonthStart  + "/" + newDayStart + "/" + yearStart;
        defTimeStart = newHourStart + ":" + newMinStart;

        c.add(Calendar.DATE, 3);
        yearEnd = c.get(Calendar.YEAR);
        monthEnd = c.get(Calendar.MONTH);
        dayEnd = c.get(Calendar.DAY_OF_MONTH);
        hourEnd = c.get(Calendar.HOUR_OF_DAY);
        minEnd = c.get(Calendar.MINUTE);

        if((monthEnd+1) < 10){
            newMonthEnd = "0" + (monthEnd+1);
        }else{
            newMonthEnd = ""+ (monthEnd+1);
        }

        if(dayEnd < 10){
            newDayEnd = "0" + dayEnd;
        }
        else{
            newDayEnd = "" + dayEnd;
        }

        if(hourEnd < 10){
            newHourEnd = "0" + hourEnd;
        }else{
            newHourEnd = ""+ hourEnd;
        }

        if(minEnd < 10) {
            newMinEnd = "0" + minEnd;
        }else{
            newMinEnd = "" + minEnd;
        }

        defDateEnd = newMonthEnd  + "/" + newDayEnd + "/" + yearEnd;
        defTimeEnd = newHourEnd + ":" + newMinEnd;

        s_dateStart = sp.getString("search_dateStart", null);
        if(s_dateStart == null){
            s_dateStart = defDateStart;
        }
        s_dateEnd = sp.getString("search_dateEnd", null);
        if(s_dateEnd == null){
            s_dateEnd = defDateEnd;
        }
        Log.v("s_dateStart", s_dateStart);
        Log.v("s_dateEnd", s_dateEnd);

        ds = s_dateStart.split("/");
        de = s_dateEnd.split("/");
        Date date1 = new Date();
        Date date2 = new Date();

        try{
            date1 = date.parse(s_dateStart);
            date2 = date.parse(s_dateEnd);
        }catch(ParseException e){
            e.printStackTrace();
        }

        s_timeStart = sp.getString("search_timeStart", null);
        if(s_timeStart == null){
            s_timeStart = defTimeStart;
        }
        s_timeEnd = sp.getString("search_timeEnd", null);
        if(s_timeEnd == null){
            s_timeEnd = defTimeEnd;
        }
        Log.v("s_timeStart", s_timeStart);
        Log.v("s_timeEnd", s_timeEnd);

        member_id = sp.getString("user_id", null);
        transmission = sp.getString("search_transmission", "Both");
        year = sp.getString("search_year", "All");
        type = sp.getString("search_type", "All");
        fuel = sp.getString("search_fuel", "All");
        producer = sp.getString("search_producer", "All");
        minPrice = sp.getInt("search_minPrice", 0);
        maxPrice = sp.getInt("search_maxPrice", 1000000);

        json.addProperty("user_id", member_id);
        json.addProperty("transmission", transmission);
        json.addProperty("year", year);
        json.addProperty("type", type);
        json.addProperty("fuel", fuel);
        json.addProperty("producer", producer);
        json.addProperty("minPrice",minPrice);
        json.addProperty("maxPrice",maxPrice);

        lvcar = findViewById(R.id.lv_car);
        adapter = new CarListAdapter(this, cars);
        cars.clear();

        callCars cc = new callCars();
        cc.execute();

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
                extras.putString("screen", "search2");
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        View v = getSupportActionBar().getCustomView();
        back = v.findViewById(R.id.btn_back);
        dateStart = v.findViewById(R.id.dateStart);
        timeStart = v.findViewById(R.id.timeStart);
        dateEnd = v.findViewById(R.id.dateEnd);
        timeEnd = v.findViewById(R.id.timeEnd);
        layTime = v.findViewById(R.id.layDate);

        dateStart.setText(day_name.format(date1) + ", " + ds[1] + " " + month_name.format(date1));
        timeStart.setText(s_timeStart);
        dateEnd.setText(day_name.format(date2) + ", " + de[1] + " " + month_name.format(date2));
        timeEnd.setText(s_timeEnd);

        editor.putLong("search_timeInterval", 3);
        editor.putString("search_dateStart", s_dateStart);
        editor.putString("search_dateEnd", s_dateEnd);
        editor.putString("search_timeStart", s_timeStart);
        editor.putString("search_timeEnd", s_timeEnd);
        editor.apply();

        Log.v("search date start", sp.getString("search_dateStart", null));
        Log.v("search date end", sp.getString("search_dateEnd", null));

        layTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chgDate = new Intent(ListCar.this, DateActivity.class);
                startActivity(chgDate);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(member_id == null){
                    startActivity(new Intent(ListCar.this, GuestMain.class));
                }
                else{
//                    startActivity(new Intent(ListCar.this, SidebarActivity.class));
                    startActivity(new Intent(ListCar.this, HomeActivity.class));
                }
                finish();
            }
        });

        filter = findViewById(R.id.fab_filter);
        dialogFrag = filter_frag.newInstance();
        dialogFrag.setParentFab(filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
//                Intent filter = new Intent(ListCar.this,FilterActivity.class);
//                startActivity(filter);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {

               if(position == 0){
                    if(priceAsc){
                        sortByPriceAsc();
                        priceAsc = false;
                    }
                    else{
                        sortByPriceDesc();
                        priceAsc = true;
                    }
                }
                else if(position == 1){
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
    }

    @Override
    public void onResult(Object result) {
        Log.d("result on activity", result.toString());
        if(result.toString().equalsIgnoreCase("swiped_down")){

        }else{

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }

    }

    private class callCars extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;
        String callUrl = "http://new.entongproject.com/api/customer/filter_car";

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ListCar.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Log.v("json body", json.toString());
                Ion.with(getBaseContext())
                        .load("POST", callUrl)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(e == null){
                                    Log.v("ion result", result.toString());
                                    String msg = result.get("result").getAsString();
                                    if(msg.equals("success")){
                                        JsonArray mCars = result.get("cars").getAsJsonArray();
                                        if(mCars.size() == 0){
                                            svMain.setVisibility(View.INVISIBLE);
                                            nullCar.setVisibility(View.VISIBLE);
                                        }
                                        else if(mCars.size() > 0){
                                            svMain.setVisibility(View.VISIBLE);
                                            nullCar.setVisibility(View.INVISIBLE);
                                            for(int i = 0, size = mCars.size(); i < size; i++){
                                                try{

                                                    JsonObject carDetail = mCars.get(i).getAsJsonObject();
                                                    Log.v("car", carDetail.toString());
                                                    String location =  "not identified";
                                                    String city = "not identified";

                                                    if((carDetail.get("location_lat").getAsDouble() != 0)
                                                            && (carDetail.get("location_long").getAsDouble() != 0)){
                                                        Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());
                                                        try{
                                                            List<Address> addresses = geocoder.getFromLocation(carDetail.get("location_lat").getAsDouble(),
                                                                    carDetail.get("location_long").getAsDouble(), 1);
                                                            Address obj = addresses.get(0);
                                                            city = addresses.get(0).getLocality();
                                                            location = obj.getAddressLine(0);
                                                        }catch(Exception egeo){
                                                            egeo.printStackTrace();
                                                        }
                                                    }

                                                    String imageCar = carDetail.get("photo").isJsonNull()? "http://new.entongproject.com/images/car/car_default.png"
                                                            : carDetail.get("photo").getAsString();
                                                    String id = carDetail.get("id").getAsString();
                                                    String name = carDetail.get("car_name").getAsString();
                                                    String owner = carDetail.get("owner_name").getAsString();
                                                    int year = carDetail.get("year").getAsInt();
                                                    double price = carDetail.get("price").getAsDouble();
                                                    double rating = carDetail.get("total_rating").getAsDouble();
                                                    int trips = carDetail.get("trips").getAsInt();

                                                    Car car = new Car(id, name, owner, imageCar, year, price, rating, location, city, trips);
                                                    cars.add(car);
                                                }catch(UnsupportedOperationException err){
                                                    err.printStackTrace();
                                                }
                                            }
                                        }
                                    }else if(msg.equals("error")){
                                        Toast.makeText(getBaseContext(), result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    e.printStackTrace();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
            }catch(UnsupportedOperationException err){
                err.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
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
}
