package com.eazy.firda.eazy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.CarImageSlider;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.entity.LinePagerIndicatorDecoration;
import com.eazy.firda.eazy.models.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailCarOwner extends AppCompatActivity {

    String carId, f_type, f_reg, desc;
    int ac, priceDaily, priceWeekly, priceMonthly, status;
    double lat, lng;
//    NetworkImageView carImg;
    CheckBox cmbImg, cmbDesc, cmbLocation, cmbPrice, cmbNote;
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();
    RelativeLayout layImg;
    LinearLayout layDesc, layLocation, layPrice, layNote;

    RecyclerView rvCar;
    RecyclerView.LayoutManager layoutManager;
    List<Car> cars = new ArrayList<Car>();
    CarImageSlider rAdapter;
    SnapHelper snapHelper = new PagerSnapHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car_owner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        carId = extras.getString("car_id");


//        carImg = findViewById(R.id.carImg);
        cmbImg = findViewById(R.id.cbImg);
        cmbDesc = findViewById(R.id.cbInfo);
        cmbLocation = findViewById(R.id.cbLocation);
        cmbPrice = findViewById(R.id.cbPrice);
        cmbNote = findViewById(R.id.cbNote);

        layImg = findViewById(R.id.layImg);
        layDesc = findViewById(R.id.carDesc);
        layLocation = findViewById(R.id.carLocation);
        layPrice = findViewById(R.id.carPrice);
        layNote = findViewById(R.id.carNote);

        rvCar = findViewById(R.id.rvCar);
        rvCar.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCar.setLayoutManager(layoutManager);
        rAdapter = new CarImageSlider(this, cars);
        rAdapter.notifyDataSetChanged();
        rvCar.setAdapter(rAdapter);
        snapHelper.attachToRecyclerView(rvCar);
        rvCar.addItemDecoration(new LinePagerIndicatorDecoration());

        carDetail cd = new carDetail();
        cd.execute();

        int permission = ActivityCompat.checkSelfPermission(DetailCarOwner.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    DetailCarOwner.this, PERMISSIONS_STORAGE, 1
            );
        }

        layImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getBaseContext(), "This service currently is not available yet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailCarOwner.this, CarOwnerPhotos.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", carId);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        layDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCarOwner.this, DescCarOwner.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", carId);
                extras.putString("fuel_type", f_type);
                extras.putString("fuel_regulation", f_reg);
                extras.putString("desc", desc);
                extras.putInt("ac", ac);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        layLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getBaseContext(), "This service currently is not available yet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailCarOwner.this, CarOwnerLocation.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", carId);
                extras.putDouble("lat", lat);
                extras.putDouble("lng", lng);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        layPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCarOwner.this, PriceCarOwner.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", carId);
                extras.putInt("daily", priceDaily);
                extras.putInt("weekly", priceWeekly);
                extras.putInt("monthly", priceMonthly);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        layNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getBaseContext(), "This service currently is not available yet", Toast.LENGTH_SHORT).show();
                Intent note = new Intent(DetailCarOwner.this, NoteOwnerPhotos.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", carId);
                note.putExtras(extras);
                startActivity(note);
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

        Intent intent = new Intent(DetailCarOwner.this, ManageCar.class);
        Bundle extras = new Bundle();
        extras.putString("screen", "detail");
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    private class carDetail extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;
        String car_detail_url = "http://new.entongproject.com/api/provider/rental/api_detail_car";

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(DetailCarOwner.this);
            progressDialog.setMessage("Getting Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            Log.v("carId", carId);
            JSONObject response = jParser.getJsonFromUrl(car_detail_url, carId);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {

            try{
                JSONObject c = response.getJSONObject("car");

                String s_carPict = c.isNull("car_photo")? "http://new.entongproject.com/images/car/car_default.png"
                        : c.getString("car_photo");
                int statImg = c.isNull("car_photo")?0:1;

                if(statImg == 0){cmbImg.setChecked(false);}
                else if(statImg == 1){cmbImg.setChecked(true);}

                if(c.isNull("note_grand")){cmbNote.setChecked(false);}
                else{cmbNote.setChecked(true);}

                if(c.getDouble("price") == 0 || c.getDouble("price_week") == 0 || c.getDouble("price_month") == 0){
                    cmbPrice.setChecked(false);
                }
                else{
                    cmbPrice.setChecked(true);
                }

                if(c.getInt("size") == 0 || c.isNull("fuel_type")
                        || c.isNull("fuel_regulation") || c.getInt("air_conditioner") == 0 || c.isNull("description")){
                    cmbDesc.setChecked(false);
                }else{
                    cmbDesc.setChecked(true);
                }

                if(c.getDouble("location_lat") == 0 || c.getDouble("location_long") == 0){
                    cmbLocation.setChecked(false);
                }
                else{
                    cmbLocation.setChecked(true);
                }

                f_type = c.getString("fuel_type");
                f_reg = c.getString("fuel_regulation");
                ac = c.getInt("air_conditioner");
                desc = c.getString("description");
                priceDaily = (int)c.getDouble("price");
                priceWeekly = (int)c.getDouble("price_week");
                priceMonthly = (int)c.getDouble("price_month");
                lat = c.getDouble("location_lat");
                lng = c.getDouble("location_long");

//                carImg.setImageUrl(s_carPict, imageLoader);

                JSONArray mPhoto = response.getJSONArray("photo");
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
                getSupportActionBar().setTitle(c.getString("car_name"));

            }catch(JSONException e){

            }
            rAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }
}
