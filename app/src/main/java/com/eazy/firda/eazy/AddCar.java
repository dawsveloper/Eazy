package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class AddCar extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String memberId, trans, data, producer, year, body, size;

    EditText carName, carDaily, carWeekly, carMonthly;
    Spinner carProducer, carYear, carBody, carSize;
    RadioGroup transGroup;
    RadioButton transAuto, transManual;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.getSupportActionBar().setTitle("My Car Profile");

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        memberId = sp.getString("user_id", null);

        producer = "All";
        year = "Select Car Year";
        body = "Select Car Body";
        size = "Select Car Capacity";
        trans = "";

        carName = findViewById(R.id.carName);
        carDaily = findViewById(R.id.carDailyRate);
        carWeekly = findViewById(R.id.carWeeklyRate);
        carMonthly = findViewById(R.id.carMonthlyRate);
        carProducer = findViewById(R.id.carProducer);
        carYear = findViewById(R.id.carYear);
        carBody = findViewById(R.id.carBody);
        carSize = findViewById(R.id.carSize);
        transGroup = findViewById(R.id.group_trans);
        transAuto = findViewById(R.id.trans_auto);
        transManual = findViewById(R.id.trans_manual);

        carProducer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                producer = parent.getItemAtPosition(position).toString();
                Log.v("producer", producer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = parent.getItemAtPosition(position).toString();
                Log.v("year", year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carBody.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                body = parent.getItemAtPosition(position).toString();
                Log.v("body", body);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size = parent.getItemAtPosition(position).toString();
                Log.v("size",size);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        transGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.trans_auto:
                        trans = "Auto";
                        break;
                    case R.id.trans_manual:
                        trans = "Manual";
                        break;
                }
            }
        });

        add = findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carName.length() == 0 || producer.equals("All") || year.equals("Select Car Year")
                        || body.equals("Select Car Body") || size.equals("Select Car Size")
                        || trans.equals("") || carDaily.length() == 0 || carWeekly.length() == 0
                        || carMonthly.length() == 0){
                    Toast.makeText(getBaseContext(), "Please complete data first", Toast.LENGTH_SHORT).show();
                }
                else{
                    data = memberId + "|"
                            + carName.getText().toString() + "|"
                            + producer + "|"
                            + year + "|"
                            + trans + "|"
                            + body + "|"
                            + Integer.parseInt(size) + "|"
                            + Integer.parseInt(carDaily.getText().toString()) + "|"
                            + Integer.parseInt(carWeekly.getText().toString()) + "|"
                            + Integer.parseInt(carMonthly.getText().toString());

                    saveData sd = new saveData();
                    sd.execute();
                }

            }
        });
    }

    private class saveData extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;
        String saveUrl = "http://new.entongproject.com/api/provider/rental/api_add_new_car";
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AddCar.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            Log.v("data", data);
            JSONParser jsonParser = new JSONParser();
            JSONObject response = jsonParser.getJsonFromUrl(saveUrl, data);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try{
                String msg = jsonObject.getString("message");
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            progressDialog.dismiss();

//            Intent redirect = new Intent(AddCar.this, SidebarActivity.class);
            Intent redirect = new Intent(AddCar.this, HomeActivity.class);
            startActivity(redirect);
        }
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
//        startActivity(new Intent(AddCar.this, SidebarActivity.class));
        Intent intent = new Intent(AddCar.this, ManageCar.class);
        Bundle extras = new Bundle();
        extras.putString("screen", "add");
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }
}
