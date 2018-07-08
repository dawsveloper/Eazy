package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.CarRecyclerAdapter;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.models.Car;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailOwnerActivity extends AppCompatActivity {
    String carId;
    String url = "http://new.entongproject.com/api/customer/detail_owner";
    NetworkImageView profPict;
    TextView profName, profPhone, profMail, profOwner, profRenter, txtError;
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();
    ProgressBar progressBar;

    RecyclerView rvCar;
    RecyclerView.LayoutManager layoutManager;
    CarRecyclerAdapter rAdapter1;
    List<Car> cars = new ArrayList<Car>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_owner);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        carId = extras.getString("car_id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setTitle("");

        profPict = (NetworkImageView)findViewById(R.id.prof_pict);
        profName = (TextView)findViewById(R.id.prof_name);
        profPhone = (TextView)findViewById(R.id.prof_phone);
        profMail = (TextView)findViewById(R.id.prof_mail);
        profOwner = (TextView)findViewById(R.id.prof_rate);
        profRenter = (TextView)findViewById(R.id.prof_rate2);

        rvCar = findViewById(R.id.rvCar);
        txtError = findViewById(R.id.txtError);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        rvCar.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCar.setLayoutManager(layoutManager);
        rAdapter1 = new CarRecyclerAdapter(this, cars);

        Log.v("carID", carId);

        detailOwner detail = new detailOwner();
        detail.execute();

    }

    private class detailOwner extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DetailOwnerActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            JSONParser jparser = new JSONParser();
            JSONObject response = jparser.getJsonFromUrl(url, carId);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {

            try{
                String result = response.getString("result");

                if(result.equals("success")){
                    JSONObject u = response.getJSONObject("owner");

                    String imgURL = u.isNull("owner_photo")?"http://new.entongproject.com/images/profile/user_def.png":u.getString("owner_photo");

                    profPict.setImageUrl(imgURL, imageLoader);
                    profName.setText(u.getString("name"));
                    profPhone.setText(u.getString("phone"));
                    profMail.setText(u.getString("email"));
                    profOwner.setText(String.valueOf(u.getDouble("rate_owner")));
                    profRenter.setText(String.valueOf(u.getDouble("rate_renter")));
                }
                else{
                    Log.v("result", response.getString("message"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    private class callCars extends AsyncTask<Void, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
