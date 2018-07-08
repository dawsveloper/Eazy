package com.eazy.firda.eazy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Boolean firstLaunch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        firstLaunch = sharedPreferences.getBoolean("firstLaunch", true);
        if(firstLaunch){
            editor.putBoolean("logged_in", false);
            editor.putBoolean("firstLaunch", false);
        }
        editor.putString("location", "");
        editor.putLong("user_lat", Double.doubleToRawLongBits(Double.parseDouble("0.0")));
        editor.putLong("user_lang", Double.doubleToRawLongBits(Double.parseDouble("0.0")));
        editor.apply();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, 3000L); //3000 L = 3 detik
    }
}
