package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    private SharedPreferences prefs;
    private String mail, pass;
    private int process = 0;
    String url_login = "http://new.entongproject.com/api/customer/login";
    boolean doubleBackToExitPressedOnce;
    boolean logged;
    String login_as;

    EditText et_mail, et_pass;

    JsonObject json = new JsonObject();
    JsonObject response = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(LoginActivity.this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final Button user_login = (Button)findViewById(R.id.login_btn);

//        if(sharedPreferences.getBoolean("logged_in", false)){
//            Intent redirect = new Intent(LoginActivity.this, SidebarActivity.class);
//            startActivity(redirect);
//        }

        et_mail = (EditText)findViewById(R.id.login_email);
        et_pass = (EditText)findViewById(R.id.login_pass);

        login_as = sharedPreferences.getString("login_as", null);
//        if(login_as != null){
//            Log.v("loginas", login_as);
//            Intent redirect = new Intent(LoginActivity.this, SidebarActivity.class);
//            startActivity(redirect);
//        }

        user_login.setEnabled(false);

        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int sdk = android.os.Build.VERSION.SDK_INT;

                if(et_mail.getText().length() > 0 && et_pass.getText().length() > 0){

                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                            && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
                        // your code here - is between 15-21
                        user_login.setBackgroundColor(Color.parseColor("#B376AC9D"));

                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // your code here - is api 21
                        user_login.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B376AC9D")));
                    }

                    user_login.setEnabled(true);
                }
                else{
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                            && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
                        // your code here - is between 15-21
                        user_login.setBackgroundColor(Color.parseColor("#636867"));

                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // your code here - is api 21
                        user_login.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#636867")));
                    }
                    user_login.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        user_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = et_mail.getText().toString();
                pass = et_pass.getText().toString();

                json.addProperty("email", mail);
                json.addProperty("password", pass);
                Log.v("json", json.toString());


                checkAuth mAuth = new checkAuth();
                mAuth.execute();
            }
        });

        final TextView regis = (TextView)findViewById(R.id.login_register);

        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regis = new Intent(LoginActivity.this, NewSignUp.class);
                startActivity(regis);
//                Intent home = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(home);
            }
        });

        final TextView guest = (TextView)findViewById(R.id.login_guest);

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("text", "clicked");
                editor.putBoolean("logged_in", false);
                editor.putString("login_as", "guest");
                editor.putString("user_id", null);
                editor.putString("search_dateStart", null);
                editor.putString("search_dateEnd", null);
                editor.apply();

                Intent redirect = new Intent(LoginActivity.this, GuestMain.class);
                startActivity(redirect);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
//
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private class checkAuth extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog;

        String user;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            user  = mail + ":" + pass;
            Log.v("user", user);
        }

        @Override
        protected Void doInBackground(String...data){

            JSONParser asyncJSON = new JSONParser();

            Log.v("url", url_login);
            Log.v("user", json.toString());
//            Log.v("respon", asyncJSON.toString());

//            JSONObject respon = asyncJSON.getJsonFromUrl(url_login, user);
//            Log.v("respon", respon.toString());

            Ion.with(getBaseContext())
                    .load("POST",url_login)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(e == null){
                                Log.v("result", result.toString());
                                try{
                                    String msg = result.get("result").getAsString();
                                    if(msg.equals("success")){
                                        String imgURL = "";
//                                    String imgURL = respon.get("photo")?"http://new.entongproject.com/images/profile/user_def.png":respon.getString("photo");
                                        if(result.get("photo").isJsonNull()){
                                            imgURL = "http://new.entongproject.com/images/profile/user_def.png";
                                        }
                                        else{
                                            imgURL = result.get("photo").getAsString();
                                        }
                                        editor.putBoolean("logged_in", true);
                                        editor.putString("login_as", "member");
                                        editor.putString("user_id", result.get("user_id").getAsString());
                                        editor.putString("member_name", result.get("username").getAsString());
                                        editor.putString("member_email", result.get("email").getAsString());
                                        editor.putString("member_photo", imgURL);
                                        editor.putInt("member_epoint", result.get("epoint").getAsInt());
                                        editor.putInt("member_cars", result.get("cars").getAsInt());
                                        if(result.get("cars").getAsInt() > 0){
                                            editor.putInt("member_income", result.get("total_income").getAsInt());
                                        }
                                        editor.putString("search_dateStart", null);
                                        editor.putString("search_dateEnd", null);
                                        editor.apply();

                                        Log.v("login_status", sharedPreferences.getString("login_status", "0"));
                                        Log.v("user_id",  sharedPreferences.getString("user_id", null));
//                                        Intent redirect = new Intent(LoginActivity.this, SidebarActivity.class);
                                        Intent redirect = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(redirect);
                                    }
                                    else if(msg.equals("error")){
                                        String error = result.get("message").getAsString();
                                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                                        process = 1;
                                    }
                                }catch(UnsupportedOperationException err){
                                    err.printStackTrace();
                                }
                            }
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            progressDialog.dismiss();
//            String result;
//            try{
//                result = respon.get("result").toString();
//
//                Log.v("JSON", respon.getAsString());
//
//                if(result.equals("success")){
//                    String imgURL = "";
//                    String imgURL = respon.get("photo")?"http://new.entongproject.com/images/profile/user_def.png":respon.getString("photo");
//                    if(respon.get("photo") == null){
//                        imgURL = "http://new.entongproject.com/images/profile/user_def.png";
//                    }
//                    else{
//                        imgURL = respon.get("photo").toString();
//                    }
//                    editor.putBoolean("logged_in", true);
//                    editor.putString("login_as", "member");
//                    editor.putString("user_id", respon.get("user_id").toString());
//                    editor.putString("member_name", respon.get("username").toString());
//                    editor.putString("member_email", respon.get("email").toString());
//                    editor.putString("member_photo", imgURL);
//                    editor.apply();
//
//                    Log.v("login_status", sharedPreferences.getString("login_status", "0"));
//                    Log.v("user_id",  sharedPreferences.getString("user_id", null));
//                    Intent redirect = new Intent(LoginActivity.this, SidebarActivity.class);
////                    Intent redirect = new Intent(LoginActivity.this, MainActivity.class);
////                    startActivity(redirect);
//
//
//                }
//                else if(result.equals("error")){
//                    tv_error.setText("Login combination is wrong");
//                    process = 1;
//                }
//            }catch(JsonIOException e){
//                e.printStackTrace();
//            }
        }
    }
}

