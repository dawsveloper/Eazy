package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class DescCarOwner extends AppCompatActivity {

    String carId, f_type, f_reg, textDesc, data;
    int ac;
    Spinner fuelType, fuelReg;
    CheckBox cbAc;
    EditText desc;
    Button add;

    Intent intent;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_car_owner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setTitle("Car Description");

        intent = getIntent();
        extras = intent.getExtras();
        carId = extras.getString("car_id");

        fuelType = findViewById(R.id.carFuel);
        fuelReg = findViewById(R.id.carRegulation);
        cbAc = findViewById(R.id.carAc);
        desc = findViewById(R.id.carDesc);
        add = findViewById(R.id.btnSave);

        call c = new call();
        c.execute();

        fuelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                f_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fuelReg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                f_reg = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbAc.isChecked()){
                    ac = 1;
                }
                else{
                    ac = 0;
                }

                if(desc.length() > 0){
                    textDesc = desc.getText().toString();
                }

                data = carId + "|"
                        + f_type + "|"
                        + f_reg + "|"
                        + ac + "|"
                        + textDesc;

                save s = new save();
                s.execute();
            }
        });
    }

    private class call extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(DescCarOwner.this);
            progressDialog.setMessage("Getting Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            f_type = extras.getString("fuel_type");
            f_reg = extras.getString("fuel_regulation");
            textDesc = extras.getString("desc");
            ac = extras.getInt("ac");

            return null;
        }

        @Override
        protected void onPostExecute(Void jsonObject) {

            if(f_type.length() == 0){
                f_type = "Not Set";
                fuelType.setSelection(0);
            }
            else{
                if(f_type.equals("Pertamax")){
                    fuelType.setSelection(1);
                }
                else if(f_type.equals("Pertalite")){
                    fuelType.setSelection(2);
                }
                else if(f_type.equals("Premium")){
                    fuelType.setSelection(3);
                }
                else if(f_type.equals("Solar")){
                    fuelType.setSelection(4);
                }
            }

            if(f_reg.length() == 0){
                f_reg = "Not Set";
                fuelReg.setSelection(0);
            }
            else{
                if(f_reg.equals("Full to Full")){
                    fuelReg.setSelection(1);
                }
                else if(f_reg.equals("Half to Half")){
                    fuelReg.setSelection(2);
                }
                else if(f_reg.equals("Full to Half")){
                    fuelReg.setSelection(3);
                }
                else if(f_reg.equals("Half to Full")){
                    fuelReg.setSelection(4);
                }
            }

            if(ac == 0){
                cbAc.setChecked(false);
            }else{
                cbAc.setChecked(true);
            }

            if(textDesc.length() > 0){
                desc.setText(textDesc);
            }

            progressDialog.dismiss();
        }
    }

    private class save extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;
        String saveUrl = "http://new.entongproject.com/api/provider/rental/api_save_cardesc";

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(DescCarOwner.this);
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
        Intent intent = new Intent(DescCarOwner.this, DetailCarOwner.class);
        Bundle extras = new Bundle();
        extras.putString("car_id", carId);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }
}
