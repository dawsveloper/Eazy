package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.OwnerCarListAdapter;
import com.eazy.firda.eazy.models.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageCar extends AppCompatActivity {

    CoordinatorLayout layMain;
    ProgressBar progressBar;
    FloatingActionButton addNew;
    TextView addNew2;
    RelativeLayout lay1;
    ScrollView lay2;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    OwnerCarListAdapter adapter;

    String memberId, screen;
    int status;

    List<Car> cars = new ArrayList<Car>();
    ListView lvcar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_car);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Cars");

        layMain = findViewById(R.id.layMain);
        progressBar = findViewById(R.id.progressBar);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        memberId = sp.getString("user_id", null);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        screen = extras.getString("screen");

        addNew = findViewById(R.id.fab_add);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(ManageCar.this, AddCar.class);
                startActivity(add);
            }
        });

        lay1 = findViewById(R.id.lay1);
        lay2 = findViewById(R.id.lay2);
        addNew2 = findViewById(R.id.txt_addNew);
        addNew2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(ManageCar.this, AddCar.class);
                startActivity(add);
            }
        });


        lvcar = (ListView)findViewById(R.id.listcar);
        adapter = new OwnerCarListAdapter(ManageCar.this, cars);
        cars.clear();

        adapter.notifyDataSetChanged();
        lvcar.setAdapter(adapter);

        callCars cc = new callCars();
        cc.execute();

        lvcar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Car car = (Car) parent.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), DetailCarOwner.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", car.getCar_id());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(screen.equals("home")){
            startActivity(new Intent(ManageCar.this, HomeActivity.class));
        }
        else if(screen.equals("profile")){
            startActivity(new Intent(ManageCar.this, HomeActivity.class));
        }
        else{
            startActivity(new Intent(ManageCar.this, HomeActivity.class));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class callCars extends AsyncTask<Void, Void, JSONObject> {
        ProgressDialog progressDialog;
        String callUrl = "http://new.entongproject.com/api/provider/rental/api_list_car";

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(ManageCar.this);
            progressDialog.setMessage("");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            layMain.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
//            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            Log.v("memberId", memberId);

            JSONObject response = jParser.getJsonFromUrl(callUrl, memberId);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {

            try{
                //result = response.getString("result");

                if(response.getString("result").equals("success")){
                    lay1.setVisibility(View.GONE);
                    lay2.setVisibility(View.VISIBLE);

                    JSONArray mCars = response.getJSONArray("cars");
                    for(int i = 0, size = mCars.length(); i < size; i++){
                        JSONObject carDetail = mCars.getJSONObject(i);
                        Log.v("carDetail", carDetail.toString());
                        String location = "Not set";

                        if((carDetail.getDouble("location_long")!= 0) && (carDetail.getDouble("location_lat") != 0)){
                            Geocoder geocoder = new Geocoder(ManageCar.this, Locale.getDefault());
                            try{
                                List<Address>addresses = geocoder.getFromLocation(carDetail.getDouble("location_lat")
                                        , carDetail.getDouble("location_long"), 1);
                                Address obj = addresses.get(0);
                                location = obj.getAddressLine(0);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        String imageCar = carDetail.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : carDetail.getString("photo");
                        int status = carDetail.getInt("car_status");
                        Car car = new Car(carDetail.getString("id"), carDetail.getString("car_name"),
                                imageCar, carDetail.getInt("year"), carDetail.getDouble("price"), status);
                        cars.add(car);
                    }
                }
                else{
                    lay1.setVisibility(View.VISIBLE);
                    lay2.setVisibility(View.GONE);
                    Log.v("Error", response.getString("message"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
//            progressDialog.dismiss();
            layMain.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
