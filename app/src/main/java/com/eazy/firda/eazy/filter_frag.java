package com.eazy.firda.eazy;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.edmodo.rangebar.RangeBar;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;


public class filter_frag extends AAH_FabulousFragment {
    Button btn_close;
    SegmentedButtonGroup carTrans;
    RangeBar rbPrice;
    TextView priceTag;
    ImageView check, refresh;
    Spinner carYear, carType, carFuel, carProducer;

    String transmission, year, type, fuel, producer;
    int minPrice, maxPrice;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public static filter_frag newInstance() {
        filter_frag f = new filter_frag();
        return f;
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_filter_frag, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        editor = sp.edit();

        transmission = "Both";
        year = "All";
        type = "All";
        fuel = "All";
        producer = "All";
        minPrice = 0;
        maxPrice = 1000000;

        carTrans = contentView.findViewById(R.id.carTrans);
        carTrans.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
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

        carYear = contentView.findViewById(R.id.carYear);
        carYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                year = "All";
            }
        });

        carType = contentView.findViewById(R.id.carType);
        carType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = "All";
            }
        });

        carFuel = contentView.findViewById(R.id.carFuel);
        carFuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fuel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fuel = "All";
            }
        });

        carProducer = contentView.findViewById(R.id.carProducer);
        carProducer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                producer = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                producer = "All";
            }
        });

        rbPrice = contentView.findViewById(R.id.carPrice);
        priceTag = contentView.findViewById(R.id.tag_pricerange);
        rbPrice.setTickCount(1000);
        rbPrice.setTickHeight(0);
        rbPrice.setConnectingLineColor(Color.parseColor("#0dd766"));
        rbPrice.setThumbColorNormal(Color.parseColor("#0dd766"));

        rbPrice.setThumbColorPressed(Color.parseColor("#FFFFFF"));      rbPrice.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                priceTag.setText("IDR " + String.valueOf(leftThumbIndex * 1000) + " - " + String.valueOf(rightThumbIndex * 1000) + "+ per day");
                minPrice = leftThumbIndex * 1000;
                maxPrice = rightThumbIndex * 1000;
            }
        });

        check = contentView.findViewById(R.id.check);
        refresh = contentView.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("search_transmission", transmission);
                editor.putString("search_year", year);
                editor.putString("search_type", type);
                editor.putString("search_fuel", fuel);
                editor.putString("search_producer", producer);
                editor.putInt("search_minPrice", minPrice);
                editor.putInt("search_maxPrice", maxPrice);
                editor.apply();

                Intent filter_car = new Intent(getActivity(), ListCar.class);
                getActivity().startActivity(filter_car);
            }
        });

        contentView.findViewById(R.id.lay_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });

        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
//        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

    protected void clearFilter(){
        transmission = "Both";
        year = "All";
        type = "All";
        fuel = "All";
        producer = "All";
        minPrice = 0;
        maxPrice = 1000000;
    }
}
