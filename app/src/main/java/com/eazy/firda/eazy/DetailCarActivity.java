package com.eazy.firda.eazy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.CarImageSlider;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.entity.LinePagerIndicatorDecoration;
import com.eazy.firda.eazy.models.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class DetailCarActivity extends AppCompatActivity {


    String car_detail_url = "http://new.entongproject.com/api/customer/detail_car";
    String submit_booking= "http://new.entongproject.com/api/customer/book_car";

    int duration, screen_status,screen_status2 , query_result = 0;
    String car_id, guest, member_id, price_type, check_login, date_start, time_start, date_end, time_end, book;

    HashMap<String, String> params = new HashMap<String, String>();
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    CollapsingToolbarLayout collapsingToolbar;
    NetworkImageView imageView;

    int priceDay, priceWeek, priceMonth;

    String screen, login_as;

    RecyclerView rvCar;
    RecyclerView.LayoutManager layoutManager;
    List<Car> cars = new ArrayList<Car>();
    CarImageSlider rAdapter;
    SnapHelper snapHelper = new PagerSnapHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageView = (NetworkImageView) findViewById(R.id.backdrop);

        rvCar = findViewById(R.id.rvCar);
        rvCar.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCar.setLayoutManager(layoutManager);
        rAdapter = new CarImageSlider(this, cars);
        rAdapter.notifyDataSetChanged();
        rvCar.setAdapter(rAdapter);
        snapHelper.attachToRecyclerView(rvCar);
        rvCar.addItemDecoration(new LinePagerIndicatorDecoration());

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(DetailCarActivity.this);
        editor = sharedPreferences.edit();
        login_as = sharedPreferences.getString("login_as", null);

        params.put("car_id", sharedPreferences.getString("car_id", null));
        car_id = sharedPreferences.getString("car_id", null);

        check_login = sharedPreferences.getString("login_status", "0");
        member_id =  sharedPreferences.getString("user_id", null);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        screen = extras.getString("screen");

        Log.v("login_status", check_login);
        if(member_id != null){
            Log.v("user_id", member_id);
        }
        ImageView user_detail = (ImageView) findViewById(R.id.user_profile);
        Button rent = (Button)findViewById(R.id.book_car);

        Log.v("screen_status", Integer.toString(screen_status));

        final cardetail jparse = new cardetail();

        if(screen_status == 0){
            jparse.execute();
            //new JSONParse().execute();
        }

        user_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailCarActivity.this, DetailOwnerActivity.class);
                Bundle datas = new Bundle();
                datas.putString("car_id", car_id);
                intent.putExtras(datas);
                startActivity(intent);
            }
        });

        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("login", sharedPreferences.getString("login_status", "0"));

                Intent book_intent = new Intent(DetailCarActivity.this, BookActivity.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", car_id);
                extras.putInt("price_daily", priceDay);
                extras.putInt("price_weekly", priceWeek);
                extras.putInt("price_monthly", priceMonth);
                book_intent.putExtras(extras);

                startActivity(book_intent);

            }
        });
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
        Intent redirect = null;

        if(login_as.equals("member")){
            if(screen.equals("home")){
                redirect = new Intent(DetailCarActivity.this, HomeActivity.class);
            }
            else if(screen.equals("search")){
                redirect = new Intent(DetailCarActivity.this, SearchCar.class);
            }
            else if(screen.equals("search2")){
                redirect = new Intent(DetailCarActivity.this, ListCar.class);
            }
            else if(screen.equals("map")){
                redirect = new Intent(DetailCarActivity.this, CarMap.class);
            }
        }
        else if(login_as.equals("guest")){
            if(screen.equals("search2")){
                redirect = new Intent(DetailCarActivity.this, ListCar.class);
            }
            else{
                redirect = new Intent(DetailCarActivity.this, GuestMain.class);
            }
        }
        startActivity(redirect);
        finish();
    }

    private class cardetail extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog progressDialog;
        NetworkImageView owner_img = (NetworkImageView)findViewById(R.id.owner_img);
        TextView owner_name = (TextView)findViewById(R.id.owner_name);
        TextView car_gas = (TextView)findViewById(R.id.car_gas);
        ImageView car_model = (ImageView)findViewById(R.id.car_model);
        TextView car_transmission = (TextView)findViewById(R.id.car_transmission);
        TextView car_regulation = (TextView)findViewById(R.id.car_regulation);
        TextView car_desc = (TextView)findViewById(R.id.car_desc);
        TextView car_location = (TextView)findViewById(R.id.car_location);
        TextView car_daily = (TextView)findViewById(R.id.car_daily);
        TextView car_weekly = (TextView)findViewById(R.id.car_weekly);
        TextView car_monthly = (TextView)findViewById(R.id.car_monthly);

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(DetailCarActivity.this);
            progressDialog.setMessage("Getting Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args){

            while(screen_status == 0){
                JSONParser jParser = new JSONParser();

                Log.v("url", car_detail_url);
                Log.v("car_id", car_id);
                JSONObject response = jParser.getJsonFromUrl(car_detail_url, car_id);

                if (isCancelled())
                    break;

                return response;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            String result;

            try{
                result = response.getString("result");
                Log.v("JSON", response.toString());
                Log.v("result", result);

                if(result.equals("success")){
                    //JSONArray car = response.getJSONArray("car");
                    JSONObject c = response.getJSONObject("car");
                    //Log.v("JSONArray", car.toString());
                    Log.v("JSONObject", c.toString());

                    String location =  "not identified";

                    if ((!c.isNull("location_lat") || c.getDouble("location_lat") != 0.0)
                            && (!c.isNull("location_long")|| c.getDouble("location_long") != 0.0)) {
                        Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(c.getDouble("location_lat"),
                                    c.getDouble("location_long"), 1);
                            Address obj = addresses.get(0);
                            location = obj.getAddressLine(0);
                            //location = obj.getFeatureName();
//                            endPoint.setLatitude(carDetail.getDouble("location_lat"));
//                            endPoint.setLongitude(carDetail.getDouble("location_long"));
//                            distance = startPoint.distanceTo(endPoint);

                            Log.v("address", addresses.toString());
                            Log.v("obj", obj.toString());

                        }
                        catch(Exception e){
                            Log.e("address", "error", e);
                        }
                    }
                    String s_carPict = c.isNull("car_photo")? "http://new.entongproject.com/images/car/car_default.png" : c.getString("car_photo");
                    String s_userPict = c.isNull("owner_photo")?"http://new.entongproject.com/images/profile/user_def.png":c.getString("owner_photo");
                    String s_car_name = c.getString("car_name");
                    String s_owner_name = c.getString("owner_name");
                    String s_car_gas = c.getString("fuel_type");
                    String carModel = c.getString("body_type");
                    String s_car_transmission = c.getString("transmission");
                    String s_car_regulation = c.getString("fuel_regulation");
                    String s_car_desc = c.getString("description");
                    int s_car_daily = (int)c.getDouble("price");
                    int s_car_weekly = (int)c.getDouble("price_week");
                    int s_car_monthly = (int)c.getDouble("price_month");

                    priceDay = s_car_daily;
                    priceWeek = s_car_weekly;
                    priceMonth = s_car_monthly;

                    Log.v("strings", s_car_name + ":" + s_owner_name);

                    collapsingToolbar.setTitle(s_car_name);
                    owner_img.setImageUrl(s_userPict,imageLoader);
                    imageView.setImageUrl(s_carPict, imageLoader);
                    car_location.setText(location);
                    owner_name.setText(s_owner_name);
                    car_gas.setText(s_car_gas);

                    if(carModel.equals("hatchback")){

                    }else if(carModel.equals("sedan")){

                    }else if(carModel.equals("suv")){

                    }else if(carModel.equals("mpv")){

                    }
                    car_transmission.setText(s_car_transmission);
                    car_regulation.setText(s_car_regulation);
                    car_desc.setText(s_car_desc);

                    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
                    symbols.setGroupingSeparator(',');
                    DecimalFormat formatter = new DecimalFormat("###,###", symbols);

                    car_daily.setText("RM " + formatter.format(s_car_daily) + " / Day");
                    car_weekly.setText("RM " + formatter.format(s_car_weekly) + " / Week");
                    car_monthly.setText("RM " + formatter.format(s_car_monthly) + " / Month");

                    JSONArray mPhoto = response.getJSONArray("photo");
                    Log.v("photos", mPhoto.toString());
                    for(int i = 0, size = mPhoto.length(); i < size; i++){
                        JSONObject carPhoto = mPhoto.getJSONObject(i);

//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rsz_ic_car);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                        byte[] b = baos.toByteArray();
//
//                        String defCar = Base64.encodeToString(b, Base64.DEFAULT);
//                        String imageCar = carPhoto.isNull("path")? defCar
//                                : carPhoto.getString("path");
                        String imageCar = carPhoto.isNull("path")?"http://new.entongproject.com/images/car/car_default.png"
                                :carPhoto.getString("path");
                        Log.v("path", imageCar);
                        Car car = new Car(imageCar);
                        cars.add(car);
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            rAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }

    private class regis extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(DetailCarActivity.this);
            progressDialog.setMessage("Save Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String...data){

            while(screen_status2 == 0){
                String guest_register= "http://new.entongproject.com/api/guest/register";

                JSONParser jParser = new JSONParser();

                Log.v("guest_url", guest_register);
                Log.v("guest_data", guest);
                Log.v("screen_status", Integer.toString(screen_status));

                JSONObject res_guest = jParser.getJsonFromUrl(guest_register, guest);
                Log.v("res_guest", res_guest.toString());

                return res_guest;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject res_guest){
            progressDialog.dismiss();
            String result;
            try{
                result = res_guest.getString("result");
                Log.v("JSON", res_guest.toString());
                Log.v("result", result);

                if(result.equals("success")){
                    editor.putString("login_status", "1");
                    editor.putString("user_id", res_guest.getString("member_id"));
                    editor.apply();

                    member_id =  sharedPreferences.getString("user_id", "0");
                    query_result = 1;
                    Log.v("login_status", sharedPreferences.getString("login_status", "0"));
                    Log.v("member_id", member_id);
                }
                else{
                    Toast.makeText(getBaseContext(), "Cannot submit data", Toast.LENGTH_SHORT).show();
                    query_result = 0;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class booking extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(DetailCarActivity.this);
            progressDialog.setMessage("Save Boooking ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... data){

            JSONParser jParser = new JSONParser();

            Log.v("booking_url", submit_booking);
            Log.v("booking_data", book);
            Log.v("screen_status", Integer.toString(screen_status2));

            JSONObject mybook = jParser.getJsonFromUrl(submit_booking, book);
            Log.v("booking", mybook.toString());

            return mybook;
        }

        @Override
        protected void onPostExecute(JSONObject mybook){
            progressDialog.dismiss();
            String result;

            Log.v("booking_url", mybook.toString());
            Log.v("booking_data", book);

            try{
                result = mybook.getString("result");
                Log.v("JSON", mybook.toString());
                Log.v("result", result);

                if(result.equals("success")){
                    String book_number = mybook.getString("booking_number");
                    query_result = 1;
                    Log.v("Booking Number", book_number);
                    Log.v("member_id", member_id);
                    Toast.makeText(getApplicationContext(), book_number, Toast.LENGTH_LONG).show();
                    showBookNumber(book_number);
                }
                else{
                    Toast.makeText(getBaseContext(), "Cannot submit data", Toast.LENGTH_SHORT).show();
                    query_result = 0;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    private void showRegis(){

        try{
            query_result = 0;
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailCarActivity.this);
            LayoutInflater li = this.getLayoutInflater();
            final View prompt = li.inflate(R.layout.popup_guest_regis, null);
            final EditText name = (EditText)prompt.findViewById(R.id.guest_name);
            final EditText email = (EditText)prompt.findViewById(R.id.guest_mail);
            final EditText phone = (EditText)prompt.findViewById(R.id.guest_phone);
            final regis reg = new regis();

            alertDialogBuilder.setView(prompt);

            alertDialogBuilder.setTitle("Guest Registration");
            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.cancel();
                    String guest_name = name.getText().toString();
                    String guest_mail = email.getText().toString();
                    String guest_phone = phone.getText().toString();
                    guest = guest_name + ":" +  guest_mail + ":" + guest_phone;
                    Log.v("pre-guest", guest);
                    reg.execute();

                    if(query_result == 1){
                        reg.cancel(true);

                        name.setText("");
                        email.setText("");
                        phone.setText("");
                        dialog.cancel();
                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            Log.v("screen_status", Integer.toString(screen_status));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showBooking(){

        try{
            query_result = 0;
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailCarActivity.this);
            LayoutInflater li = this.getLayoutInflater();
            final View prompt = li.inflate(R.layout.popup_booking, null);
            final RadioGroup group_price = (RadioGroup)prompt.findViewById(R.id.group_payment);
            final ExtendedEditText book_duration = (ExtendedEditText) prompt.findViewById(R.id.book_interval);
            final TextView book_start_day = (TextView) prompt.findViewById(R.id.book_start_day);
            final TextView book_start_month = (TextView)prompt.findViewById(R.id.book_start_month);
            final TextView book_start_time = (TextView)prompt.findViewById(R.id.book_start_time);
            final TextView book_end_day = (TextView)prompt.findViewById(R.id.book_end_day);
            final TextView book_end_month = (TextView)prompt.findViewById(R.id.book_end_month);
            final TextView book_end_time = (TextView)prompt.findViewById(R.id.book_end_time);
            final SimpleDateFormat month_name = new SimpleDateFormat("MMMM", Locale.ENGLISH);
            final SimpleDateFormat day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
            final SimpleDateFormat day_number = new SimpleDateFormat("dd", Locale.ENGLISH);
            final SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            final SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            alertDialogBuilder.setView(prompt);

            alertDialogBuilder.setTitle("Pick your date");

            group_price.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case R.id.payment_day:
                            price_type = "Daily";
                            break;
                        case R.id.payment_week:
                            price_type = "Weekly";
                            break;
                        case R.id.payment_month:
                            price_type = "Monthly";
                            break;
                    }
                    Log.v("price_type", price_type);
                }
            });

            book_duration.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    book_start_day.setClickable(true);
                    if(book_duration.getText().toString().equals("")){
                        duration = 0;
                    }
                    else if(book_duration.getText().toString().equals(" ")){
                        duration = 0;
                    }
                    else{
                        duration = Integer.parseInt(book_duration.getText().toString());
                        Log.v("duration", String.valueOf(duration));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            book_start_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    int yy = calendar.get(Calendar.YEAR);
                    int mm = calendar.get(Calendar.MONTH);
                    int dd = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(DetailCarActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(year, month, dayOfMonth);
                            book_start_day.setText(String.valueOf(dayOfMonth));
                            book_start_month.setText(day_name.format(calendar.getTime()) + " | " + month_name.format(calendar.getTime()));
                            date_start = date.format(calendar.getTime());
                            Log.v("calendar", calendar.getTime().toString());
                            Log.v("date_start", date_start);

                            calendar.setTime(calendar.getTime());


                            if(price_type.equals("Daily")){
                                calendar.add(Calendar.DAY_OF_YEAR, duration);
                            }
                            else if(price_type.equals("Weekly")){
                                calendar.add(Calendar.WEEK_OF_YEAR, duration);
                            }
                            else if(price_type.equals("Monthly")){
                                calendar.add(Calendar.MONTH, duration);
                            }

                            book_end_day.setText(day_number.format(calendar.getTime()));
                            book_end_month.setText(day_name.format(calendar.getTime()) + " | " + month_name.format(calendar.getTime()));
                            date_end = date.format(calendar.getTime());
                            Log.v("date_end", date_end);

                        }
                    }, yy, mm, dd);
                    datePickerDialog.show();
                }
            });

            book_start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(DetailCarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            book_start_time.setText("Time: " + selectedHour + " : " + selectedMinute);
                            book_end_time.setText("Time: " + selectedHour + " : " + selectedMinute);
                            time_start = time.format(mcurrentTime.getTime());
                            time_end = time.format(mcurrentTime.getTime());
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    book = car_id + ":" + member_id + ":" + price_type + ":" + duration + ":" + date_start + " " + time_start + ":" + date_end + " " + time_end;

                    Log.v("booking", book);

                    new booking().execute();

                    if(query_result == 1){
                        dialog.cancel();
                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            screen_status2 = 1;
            Log.v("screen_status", Integer.toString(screen_status));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showBookNumber(String booking_number){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getBaseContext());

        alertDialogBuilder.setTitle("Your booking is proceed");

        alertDialogBuilder
                .setMessage(booking_number)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Intent search_car = new Intent(DetailCarActivity.this, SearchCar.class);
                        startActivity(search_car);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
