package com.eazy.firda.eazy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GuestMain extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    public Fragment fragment;
    private String[] tabTitle = {"Search", "Booking"};
    private int[] selectedIcons = {
            R.drawable.ic_search_green_500_24dp,
            R.drawable.ic_directions_car_green_500_24dp
    };
    private int[] unselectedIcons = {
            R.drawable.ic_search_grey_600_24dp,
            R.drawable.ic_directions_car_grey_600_24dp
    };

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        getSupportActionBar().setTitle("e@zy");

        fragment = new GuestSearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guess_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){

            editor.remove("logged_in");
            editor.remove("login_as");
            editor.remove("user_id");
            editor.remove("location_pickup");
            editor.remove("location_return");
            editor.remove("user_lat");
            editor.remove("user_lang");
            editor.apply();

            super.onBackPressed();
            startActivity(new Intent(GuestMain.this, LoginActivity.class));
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to Logout", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.find) {
            Intent list = new Intent(GuestMain.this, ListCar.class);
            startActivity(list);
        }
        else if (id == R.id.home){
            fragment = new GuestSearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }
        else if (id == R.id.book){
            fragment = new GuestBooking();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }
        else if(id == R.id.signup){
            Intent regis = new Intent(GuestMain.this, NewSignUp.class);
            startActivity(regis);
        }

        return super.onOptionsItemSelected(item);
    }
}
