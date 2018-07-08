package com.eazy.firda.eazy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class FilterActivity extends AppCompatActivity {

    int stat_sedan, stat_hatchback, stat_suv, stat_mpv = 0;
    String producer = "All", transmission = "Both";
    int minval = 0;
    int maxval = 1000000;

    Spinner spinProducer;
    ImageView img_sedan, img_hatchback, img_mpv, img_suv;
    ToggleSwitch t_transmission;
    RangeBar rbPrice;
    TextView priceTag;
    Button btn_reset, btn_filter;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setTitle("");

        sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = sp.edit();

        spinProducer = (Spinner)findViewById(R.id.spin_producer);
        spinProducer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                producer = parent.getItemAtPosition(position).toString();
                Log.v("producer", producer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        img_sedan = (ImageView)findViewById(R.id.img_sedan);

        img_sedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat_sedan == 0){
                    img_sedan.setImageResource(R.drawable.sedan_green);
                    stat_sedan = 1;
                }
                else if(stat_sedan == 1){
                    img_sedan.setImageResource(R.drawable.sedan_gray);
                    stat_sedan = 0;
                }
            }
        });

        img_hatchback = (ImageView)findViewById(R.id.img_hatchback);

        img_hatchback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat_hatchback == 0){
                    img_hatchback.setImageResource(R.drawable.hatchback_green);
                    stat_hatchback = 1;
                }
                else if(stat_hatchback == 1){
                    img_hatchback.setImageResource(R.drawable.hatchback_gray);
                    stat_hatchback = 0;
                }
            }
        });

        img_mpv = (ImageView)findViewById(R.id.img_mpv);

        img_mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat_mpv == 0){
                    img_mpv.setImageResource(R.drawable.mpv_green);
                    stat_mpv = 1;
                }
                else if(stat_mpv == 1){
                    img_mpv.setImageResource(R.drawable.mpv_gray);
                    stat_mpv = 0;
                }
            }
        });

        img_suv = (ImageView)findViewById(R.id.img_suv);

        img_suv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat_suv == 0){
                    img_suv.setImageResource(R.drawable.suv_green);
                    stat_suv = 1;
                }
                else if(stat_suv == 1){
                    img_suv.setImageResource(R.drawable.suv_gray);
                    stat_suv = 0;
                }
            }
        });

        t_transmission = (ToggleSwitch) findViewById(R.id.toggle_transmission);
        t_transmission.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(position == 0){
                    transmission = "Both";
                }
                else if(position == 1){
                    transmission = "Auto";
                }
                else if(position == 2){
                    transmission = "Manual";
                }
            }
        });

        rbPrice = (RangeBar) findViewById(R.id.rangebar1);
        rbPrice.setTickCount(1000);
        rbPrice.setTickHeight(0);
        rbPrice.setConnectingLineColor(Color.parseColor("#4CAF50"));
        rbPrice.setThumbColorNormal(Color.parseColor("#4CAF50"));
        rbPrice.setThumbColorPressed(Color.parseColor("#BDBDBD"));

        priceTag = (TextView)findViewById(R.id.tag_pricerange);

        // Sets the display values of the indices
        rbPrice.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                priceTag.setText("IDR " + String.valueOf(leftThumbIndex * 1000) + " - " + String.valueOf(rightThumbIndex * 1000) + "+ per day");
                minval = leftThumbIndex * 1000;
                maxval = rightThumbIndex * 1000;
            }
        });

        btn_reset = (Button)findViewById(R.id.btn_resetfilter);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });


        btn_filter = (Button)findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("search_producer", producer);
                editor.putString("search_transmission", transmission);
                editor.putInt("search_minVal", minval);
                editor.putInt("search_maxVal", maxval);
                editor.apply();

                Intent filter_car = new Intent(FilterActivity.this, SearchCar.class);
                FilterActivity.this.startActivity(filter_car);
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
        startActivity(new Intent(FilterActivity.this, SearchCar.class));
        finish();

    }

    protected void clearFilter(){
        spinProducer.setSelection(0);
        producer = "All";
        img_sedan.setImageResource(R.drawable.sedan_gray);
        stat_sedan = 0;
        img_hatchback.setImageResource(R.drawable.hatchback_gray);
        stat_hatchback = 0;
        img_suv.setImageResource(R.drawable.suv_gray);
        stat_suv = 0;
        img_mpv.setImageResource(R.drawable.mpv_gray);
        stat_mpv = 0;
        minval = 0;
        maxval = 1000000;
        t_transmission.setCheckedTogglePosition(0);
        transmission = "Both";
    }
}
