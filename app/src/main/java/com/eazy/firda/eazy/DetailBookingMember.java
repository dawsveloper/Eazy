package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.application.EazyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailBookingMember extends AppCompatActivity {

    NetworkImageView bookImg;
    TextView bookNumber, bookName, bookDateStart, bookTimeStart, bookDateEnd, bookTimeEnd, bookStatus;

    String book_id;
    String detailUrl = "http://new.entongproject.com/api/customer/book_detail";

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    SimpleDateFormat day_name;
    SimpleDateFormat month_name;
    SimpleDateFormat day_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booking_member);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        book_id = extras.getString("book_id");

        bookImg = (NetworkImageView)findViewById(R.id.book_pic);
        bookNumber = (TextView)findViewById(R.id.book_number);
        bookName = (TextView)findViewById(R.id.book_name);
        bookDateStart = (TextView)findViewById(R.id.book_date_start);
        bookTimeStart = (TextView)findViewById(R.id.book_time_start);
        bookDateEnd = (TextView)findViewById(R.id.book_date_end);
        bookTimeEnd = (TextView)findViewById(R.id.book_time_end);
        bookStatus = (TextView)findViewById(R.id.book_status);

        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        month_name = new SimpleDateFormat("MMM", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);

        getDetail gd = new getDetail();
        gd.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class getDetail extends AsyncTask<Object, String, JSONObject> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(DetailBookingMember.this);
            progressDialog.setMessage("Getting Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONParser jParser = new JSONParser();

            Log.v("url", detailUrl);
            Log.v("car_id", book_id);
            JSONObject response = jParser.getJsonFromUrl(detailUrl, book_id);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){

            try{
                String result = response.getString("result");

                if(result.equals("success")){
                    JSONObject b = response.getJSONObject("booking");

                    String pict = b.isNull("car_photo")? "http://new.entongproject.com/images/car/car_default.png" : b.getString("car_photo");

                    String nameDayStart, numberDayStart, nameMonthStart, nameDayEnd, numberDayEnd, nameMonthEnd, timeStart, timeEnd;
                    nameDayStart = numberDayStart = nameMonthStart = nameDayEnd = numberDayEnd = nameMonthEnd = timeStart = timeEnd = "";
                    String[] stringStart = b.getString("date_start").split(" ");
                    String[] stringEnd = b.getString("date_end").split(" ");
                    try{
                        Date dateStart = new SimpleDateFormat("d/M/yyyy").parse(stringStart[0]);
                        Date dateEnd = new SimpleDateFormat("d/M/yyyy").parse(stringEnd[0]);
                        nameDayStart = day_name.format(dateStart);
                        numberDayStart = day_number.format(dateStart);
                        nameMonthStart = month_name.format(dateStart);

                        nameDayEnd = day_name.format(dateEnd);
                        numberDayEnd = day_number.format(dateEnd);
                        nameMonthEnd = month_name.format(dateEnd);
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                    bookImg.setImageUrl(pict, imageLoader);
                    bookNumber.setText(b.getString("book_number"));
                    bookName.setText(b.getString("car_name"));
                    bookDateStart.setText(nameDayStart + ", " + numberDayStart + " " + nameMonthStart);
                    bookTimeStart.setText(stringStart[1]);
                    bookDateEnd.setText(nameDayEnd + ", " + numberDayEnd + " " + nameMonthEnd);
                    bookTimeEnd.setText(stringEnd[1]);

                    int stats = b.getInt("book_status");
                    if(stats == 0){
                        bookStatus.setText("PENDING");
                        bookStatus.setTextColor(Color.parseColor("#FB8C00"));
                    }
                    else if(stats == 1){
                        bookStatus.setText("CONFIRMED");
                        bookStatus.setTextColor(Color.parseColor("#43A047"));
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }
}
