package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity {

    int screen_status, query_result = 0;
    String memberProfile, member_id;
    String editURL = "http://new.entongproject.com/api/customer/edit/profile";
    String callURL = "http://new.entongproject.com/api/customer/call/profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        member_id = sharedPreferences.getString("user_id", "0");

        final callProfile call = new callProfile();
        final saveProfile save = new saveProfile();

        if(screen_status == 0) call.execute();

        Button saveEdit = (Button) findViewById(R.id.btn_save);
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.cancel(true);
                save.execute();
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

    private class callProfile extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;
        TextView name = (TextView)findViewById(R.id.member_name);
        TextView phone = (TextView)findViewById(R.id.member_phone);
        TextView email = (TextView)findViewById(R.id.member_mail);

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(EditProfile.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            while(screen_status == 0){
                JSONParser jsonParser = new JSONParser();
                JSONObject response = jsonParser.getJsonFromUrl(callURL, member_id);

                if (isCancelled())
                    break;

                return response;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            try{
                String result = response.getString("result");

                if(result.equals("success")){
                    JSONObject m = response.getJSONObject("member");

                    name.setText(m.getString("name"));
                    phone.setText(m.getString("phone"));
                    email.setText(m.getString("email"));
                }
                else{
                    name.setText("");
                    phone.setText("");
                    email.setText("");
                }

                query_result = 1;
                screen_status = 1;

            }catch(JSONException e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }

    private class saveProfile extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;

        TextView name = (TextView)findViewById(R.id.member_name);
        TextView phone = (TextView)findViewById(R.id.member_phone);
        TextView email = (TextView)findViewById(R.id.member_mail);

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(EditProfile.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jsonParser = new JSONParser();

            memberProfile = member_id + ":" +
                    name.getText().toString() + ":" +
                    phone.getText().toString() + ":" + email.getText().toString();

            JSONObject response = jsonParser.getJsonFromUrl(editURL, memberProfile);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){

            try{
                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
            }catch(JSONException e){
                e.printStackTrace();
            }

            progressDialog.dismiss();

//            Intent redirect = new Intent(EditProfile.this, SidebarActivity.class);
            Intent redirect = new Intent(EditProfile.this, HomeActivity.class);
            startActivity(redirect);
        }
    }
}
