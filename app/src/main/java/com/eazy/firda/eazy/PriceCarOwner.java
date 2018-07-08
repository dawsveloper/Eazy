package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class PriceCarOwner extends AppCompatActivity {

    Intent intent;
    Bundle extras;
    String carId, data;
    int priceDaily, priceWeekly, priceMonthly;
    ExtendedEditText daily, weekly, monthly;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_car_owner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setTitle("Price List");

        intent = getIntent();
        extras = intent.getExtras();
        carId = extras.getString("car_id");
        priceDaily = extras.getInt("daily");
        priceWeekly = extras.getInt("weekly");
        priceMonthly = extras.getInt("monthly");

        daily = findViewById(R.id.priceDaily);
        weekly = findViewById(R.id.priceWeekly);
        monthly = findViewById(R.id.priceMonthly);

        daily.setText(String.valueOf(priceDaily));
        weekly.setText(String.valueOf(priceWeekly));
        monthly.setText(String.valueOf(priceMonthly));

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = carId + "|"
                        + daily.getText().toString() + "|"
                        + weekly.getText().toString() + "|"
                        + monthly.getText().toString();

                save s = new save();
                s.execute();
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
        Intent intent = new Intent(PriceCarOwner.this, DetailCarOwner.class);
        Bundle extras = new Bundle();
        extras.putString("car_id", carId);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    private class save extends AsyncTask<Void, Void, JSONObject> {

        ProgressDialog progressDialog;
        String saveUrl = "http://new.entongproject.com/api/provider/rental/api_save_carprice";

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(PriceCarOwner.this);
            progressDialog.setMessage("Getting Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            Log.v("carId", carId);
            JSONObject response = jParser.getJsonFromUrl(saveUrl, data);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try{
                String msg = jsonObject.getString("message");

                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
