package com.eazy.firda.eazy;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.eazy.firda.eazy.application.EazyApplication;
import com.squareup.timessquare.CalendarPickerView;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();
    private static final String TAG = "SampleTimesSquareActivi";
    private CalendarPickerView calendar;
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;
    private final Set<Button> modeButtons = new LinkedHashSet<Button>();
    Fragment fragment;

    TextView startDay, startTime, endDay, endTime;

    SimpleDateFormat month_name, day_name, day_number;

    IndicatorSeekBar start, end;

    BottomNavigationView navView;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragment = null;
        fragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        navView = findViewById(R.id.navMenu);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
//                        Toast.makeText(HomeActivity.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                        fragment = new SearchFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                        break;
                    case R.id.action_history:
//                        Toast.makeText(HomeActivity.this, "History Clicked", Toast.LENGTH_SHORT).show();
                        fragment = new BookingFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                        break;
                    case R.id.action_myacc:
//                        Toast.makeText(HomeActivity.this, "My Acc Clicked", Toast.LENGTH_SHORT).show();
                        fragment = new UserProfileFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_content, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(doubleBackToExitPressedOnce){
//                super.onBackPressed();
//                finish();
//                return;
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
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
}
