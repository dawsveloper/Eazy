package com.eazy.firda.eazy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.DefaultDayViewAdapter;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DateActivity extends AppCompatActivity {

    private static final String TAG = "SampleTimesSquareActivi";
    private CalendarPickerView calendar;
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;
    private final Set<Button> modeButtons = new LinkedHashSet<Button>();

    Date dateStart, dateEnd;
    String s_dateStart, s_dateEnd, s_timeStart, s_timeEnd;
    String[] ds, ds2, de, de2;
    int s_year, s_month, s_day, e_year, e_month, e_day;

    TextView startDay, startTime, endDay, endTime;

    SharedPreferences sp;
    SharedPreferences.Editor edit;

    SimpleDateFormat month_name, month_number, day_name, day_number, year_number, date;

    IndicatorSeekBar start, end;
    long diff, interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setTitle("");

        startDay = (TextView)findViewById(R.id.start_day);
        startTime = (TextView)findViewById(R.id.start_time);
        endDay = (TextView)findViewById(R.id.end_day);
        endTime = (TextView)findViewById(R.id.end_time);

        month_name = new SimpleDateFormat("MMM", Locale.ENGLISH);
        month_number = new SimpleDateFormat("MM", Locale.ENGLISH);
        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);
        year_number = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        date = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sp.edit();

        ds = sp.getString("search_dateStart", null).split("/");
        de = sp.getString("search_dateEnd", null).split("/");

        Date date1 = new Date();
        Date date2 = new Date();

        try{
            date1 = date.parse(sp.getString("search_dateStart", null));
            date2 = date.parse(sp.getString("search_dateEnd", null));
            Log.v("date1", date1.toString());
            Log.v("date2", date2.toString());
        }catch(ParseException e){
            e.printStackTrace();
        }

        startDay.setText(day_name.format(date1) + ", " + ds[1] + " " + month_name.format(date1));
        endDay.setText(day_name.format(date2) + ", " + de[1] + " " + month_name.format(date2));

        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                .withSelectedDate(new Date());
        calendar.setCustomDayView(new DefaultDayViewAdapter());
        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        //today.add(Calendar.DATE, 3);
        dates.add(date1);
        //today.add(Calendar.DATE, 5);
        //dates.add(today.getTime());
        dates.add(date2);
        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        calendar.init(new Date(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.RANGE) //
                .withSelectedDates(dates);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                //Toast.makeText(getBaseContext(), "selected date: " + date.toString(), Toast.LENGTH_SHORT).show();
                if(startDay.getText().toString().equals("Start Date")){
                    startDay.setText(day_name.format(date) + ", " + day_number.format(date) + " " + month_name.format(date));
                    dateStart = date;
                    Log.v("dateStart", dateStart.toString());

                    s_dateStart = month_number.format(dateStart)  + "/" + day_number.format(dateStart) + "/" + year_number.format(dateStart);
                    Log.v("s_dateStart", s_dateStart);
                }
                else{
                    endDay.setText(day_name.format(date) + ", " + day_number.format(date) + " " + month_name.format(date));

                    dateEnd = date;
                    Log.v("dateEnd", dateEnd.toString());

                    diff = dateEnd.getTime() - dateStart.getTime();
                    interval = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    if(interval <= 1){
                        interval = 1;
                    }

                    e_day = date.getDay();
                    e_month = date.getMonth();
                    e_year = date.getYear();
                    s_dateEnd = month_number.format(dateEnd)  + "/" + day_number.format(dateEnd) + "/" + year_number.format(dateEnd);
                    Log.v("s_dateEnd", s_dateEnd);
                    Log.v("interval", String.valueOf(interval));
                }

                Log.v("Selected Date", date.toString());
            }

            @Override
            public void onDateUnselected(Date date) {
                //Toast.makeText(getBaseContext(), "unselected date" + date.toString(), Toast.LENGTH_SHORT).show();
                startDay.setText("Start Date");
                endDay.setText("End Date");
            }
        });

        start = (IndicatorSeekBar)findViewById(R.id.seekStart);
        end = (IndicatorSeekBar)findViewById(R.id.seekEnd);

        start.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {

            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String tickText, boolean fromUserTouch) {
                startTime.setText(tickText);
                s_timeStart = tickText;
                Log.v("s_timeStart", s_timeStart);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        end.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {

            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {
                endTime.setText(textBelowTick);
                s_timeEnd = textBelowTick;
                Log.v("s_dateEnd", s_timeEnd);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.d(TAG, "Selected time in millis: " + calendar.getSelectedDate().toString());
                String toast = "Selected: " + calendar.getSelectedDate().toString();
                //Toast.makeText(DateActivity.this, toast, LENGTH_SHORT).show();

                if(endDay.getText().toString().equals("End Date")){
                    Toast.makeText(getBaseContext(), "Please select date end", Toast.LENGTH_SHORT).show();
                }
                else{
                    edit.putLong("search_timeInterval", interval);
                    edit.putString("search_dateStart", s_dateStart);
                    edit.putString("search_dateEnd", s_dateEnd);
                    if(s_timeStart != null){
                        edit.putString("search_timeStart", s_timeStart);
                    }
                    if(s_timeEnd != null){
                        edit.putString("search_timeEnd", s_timeEnd);
                    }
                    edit.apply();

                    Intent redirect = new Intent(DateActivity.this, DetailCarActivity.class);
                    startActivity(redirect);
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
}
