package com.eazy.firda.eazy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {

    Location location;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String memberId, logedAs, dateStart, timeStart, dateEnd, timeEnd, locationPickup, locationReturn;
    long diff, interval;

    EditText searchPlace, searchPlace2;
    TextView start_day, start_month, start_time, end_day, end_month, end_time;
    Button search;

    int year, month, dayofmonth, hour, min;
    String newMonth, newDay;

    SimpleDateFormat month_name, day_name, day_number, simpleDateFormat, anotherDate, sdf_time;
    Calendar c, c2;
    Date date, dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.getSupportActionBar().setTitle("");

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(SearchActivity.this);
        editor = sharedPreferences.edit();

        logedAs = sharedPreferences.getString("login_as", null);

        searchPlace = (EditText)findViewById(R.id.search_location);
        searchPlace2 = (EditText)findViewById(R.id.search_location2);

        locationPickup = sharedPreferences.getString("location_pickup",null);
        locationReturn = sharedPreferences.getString("location_return", null);
        searchPlace.setText(locationPickup);
        searchPlace2.setText(locationReturn);

        searchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(SearchActivity.this, MapSearch.class);
                startActivity(search);
            }
        });

        Switch switch_location = (Switch)findViewById(R.id.switch_location);
        switch_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    searchPlace2.setVisibility(View.GONE);
                }
                else{
                    searchPlace2.setVisibility(View.VISIBLE);
                }
            }
        });

        c = Calendar.getInstance(TimeZone.getDefault());
        date = c.getTime();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayofmonth = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        month_name = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        anotherDate = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        sdf_time = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

        start_day = (TextView)findViewById(R.id.start_day);
        start_month = (TextView)findViewById(R.id.start_month);
        start_time = (TextView)findViewById(R.id.start_time);

        start_day.setText(String.valueOf(dayofmonth));
        start_month.setText(day_name.format(c.getTime()) + " | " + month_name.format(c.getTime()));
        start_time.setText("Time : " + sdf_time.format(date));

        if((month+1) < 10){
            newMonth = "0" + (month+1);
        }else{
            newMonth = ""+ (month+1);
        }

        if(dayofmonth < 10){
            newDay = "0" + dayofmonth;
        }
        else{
            newDay = "" + dayofmonth;
        }

        dateStart = newMonth  + "/" + newDay + "/" + String.valueOf(year);
        dateEnd = newMonth + "/" + newDay + "/" + String.valueOf(year);
        timeEnd = timeStart = sdf_time.format(date);

        start_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String date = "Date " + String.valueOf(year) + "-" + String.valueOf(month + 1)
                                + "-" + String.valueOf(dayOfMonth) + " " + (String.valueOf(day_name.format(dayOfMonth)) + " | " + String.valueOf(month_name.format(month + 1)));
                        calendar.set(year, month, dayOfMonth);
                        Log.v("sdf date", simpleDateFormat.format(calendar.getTime()));
                        Log.v("sdf_date", month_name.format(calendar.getTime()));
                        Log.v("sdf_date", day_name.format(calendar.getTime()));
                        Log.v("date", date);
                        start_day.setText(String.valueOf(dayOfMonth));
                        start_month.setText(day_name.format(calendar.getTime()) + " | " + month_name.format(calendar.getTime()));

                        int duration = 2;
                        String newMonth, newDay;

                        calendar.setTime(calendar.getTime());
                        calendar.add(Calendar.MONTH, duration);

                        if((month+1) < 10){
                            newMonth = "0" + (month+1);
                        }else{
                            newMonth = ""+ (month+1);
                        }

                        if(dayOfMonth < 10){
                            newDay = "0" + dayOfMonth;
                        }
                        else{
                            newDay = "" + dayOfMonth;
                        }

                        dateStart = newMonth  + "/" + newDay + "/" + String.valueOf(year);
                        Log.v("dateStart", dateStart);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SearchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String newHour, newMinute;

                        if(selectedHour < 10){
                            newHour = "0" + selectedHour;
                        }else{
                            newHour = ""+ selectedHour;
                        }

                        if(selectedMinute < 10) {
                            newMinute = "0" + selectedMinute;
                        }else{
                            newMinute = "" + selectedMinute;
                        }

                        start_time.setText("Time: " + newHour + ":" + newMinute);
                        timeStart = newHour + ":" + newMinute;
                        Log.v("timeStart", timeStart);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        end_day = (TextView)findViewById(R.id.end_day);
        end_month = (TextView)findViewById(R.id.end_month);
        end_time = (TextView)findViewById(R.id.end_time);

        dt = new Date();
        c2 = Calendar.getInstance();
        c2.setTime(dt);
        dt = c2.getTime();

        end_day.setText(String.valueOf(dayofmonth));
        end_month.setText(day_name.format(dt) + " | " + month_name.format(dt));
        end_time.setText("Time : " + sdf_time.format(date));

        end_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String newMonth, newDay;

                        String date = "Date " + String.valueOf(year) + "-" + String.valueOf(month + 1)
                                + "-" + String.valueOf(dayOfMonth) + " " + (String.valueOf(day_name.format(dayOfMonth)) + " | " + String.valueOf(month_name.format(month + 1)));
                        calendar.set(year, month, dayOfMonth);
                        end_day.setText(String.valueOf(dayOfMonth));

                        if((month+1) < 10){
                            newMonth = "0" + (month+1);
                        }else{
                            newMonth = ""+ (month+1);
                        }

                        if(dayOfMonth < 10){
                            newDay = "0" + dayOfMonth;
                        }
                        else{
                            newDay = "" + dayOfMonth;
                        }

                        dateEnd = newMonth+ "/" + newDay + "/" + String.valueOf(year);
                        end_month.setText(day_name.format(calendar.getTime()) + " | " + month_name.format(calendar.getTime()));
                        Log.v("dateEnd", dateEnd);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SearchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String newHour, newMinute;

                        if(selectedHour < 10){
                            newHour = "0" + selectedHour;
                        }else{
                            newHour = ""+ selectedHour;
                        }

                        if(selectedMinute < 10) {
                            newMinute = "0" + selectedMinute;
                        }else{
                            newMinute = "" + selectedMinute;
                        }

                        end_time.setText("Time: " + newHour + ":" + newMinute);
                        timeEnd = newHour + ":" + newMinute;
                        Log.v("timeEnd", timeEnd);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        search = (Button)findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    Date date1 = anotherDate.parse(dateStart);
                    Date date2 = anotherDate.parse(dateEnd);

                    diff = date2.getTime() - date1.getTime();
                    interval = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    Log.v("interval", String.valueOf(interval));
                    if(interval <= 1){
                        interval = 1;
                    }

                }catch(ParseException e){
                    e.printStackTrace();
                }

                if(searchPlace.getText().length() < 1){
                    Snackbar.make(v, "Please choose a location", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    editor.putString("search_dateStart", dateStart);
                    editor.putString("search_timeStart", timeStart);
                    editor.putString("search_dateEnd", dateEnd);
                    editor.putString("search_timeEnd", timeEnd);
                    editor.putLong("search_timeInterval", interval);
                    editor.putString("search_producer", "All");
                    editor.putInt("search_type1", 0);
                    editor.putInt("search_type2", 0);
                    editor.putInt("search_type3", 0);
                    editor.putInt("search_type4", 0);
                    editor.putString("search_transmission", "Both");
                    editor.putInt("search_minVal", 0);
                    editor.putInt("search_maxVal", 1000000);
                    editor.apply();

                    Log.v("interval2", String.valueOf(sharedPreferences.getLong("search_timeInterval", 0)));

                    Intent search = new Intent(SearchActivity.this, SearchCar.class);
                    startActivity(search);
                }
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
//        startActivity(new Intent(SearchActivity.this, SidebarActivity.class));
        startActivity(new Intent(SearchActivity.this, HomeActivity.class));
        finish();
    }
}
