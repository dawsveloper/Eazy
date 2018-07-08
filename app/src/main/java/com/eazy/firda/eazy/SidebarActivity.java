package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.CarPagerAdapter;
import com.eazy.firda.eazy.application.EazyApplication;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class SidebarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;
    public Fragment fragment, last_fragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String memberId;

    DecimalFormatSymbols symbols;
    DecimalFormat formatter;

    TextView navWallet, navUsername, navEmail;
    NetworkImageView navProfpict;
    ImageView navProfpict2;
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    ArrayList<String> url;
    private CarPagerAdapter adapter;
    String login_as;

    ViewPager vpAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        login_as = sharedPreferences.getString("login_as", null);

        fragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sidebar_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        url = new ArrayList<String>();

        //vpAds = (ViewPager)findViewById(R.id.vpAds);
        adapter = new CarPagerAdapter(this, url);

        url.add("http://new.entongproject.com/images/profile/user_def.png");
        url.add("http://new.entongproject.com/images/profile/user_def.png");
        url.add("http://new.entongproject.com/images/profile/user_def.png");

        adapter.notifyDataSetChanged();
        //vpAds.setAdapter(adapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navUsername = (TextView)headerView.findViewById(R.id.navUsername);
        navEmail = (TextView)headerView.findViewById(R.id.navEmail);
        navWallet = (TextView)headerView.findViewById(R.id.epoint);
//        navProfpict = headerView.findViewById(R.id.navProfpict);
        navProfpict2 = headerView.findViewById(R.id.navProfpict);

        String imgUrl = sharedPreferences.getString("member_photo", null);
        Log.v("imgUrl", imgUrl);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);

        Ion.with(navProfpict2)
                .placeholder(R.drawable.ic_rotate_right_grey_200_24dp)
                .error(R.drawable.ic_close_black_18dp)
                .load(imgUrl);


        memberId = sharedPreferences.getString("user_id", null);

//        navProfpict.setImageUrl(sharedPreferences.getString("member_photo", null), imageLoader);

        if(login_as.equals("member")){
            checkWallet cw = new checkWallet();
            cw.execute();
        }

        navigationView.setNavigationItemSelectedListener(this);
        this.getSupportActionBar().setTitle("");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sidebar_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(doubleBackToExitPressedOnce){
//                super.onBackPressed();
//                finish();
//                return;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.book) {
            fragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }
        else if (id == R.id.car){
            fragment = new BookingFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.profile) {

            fragment = new UserProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        }
        else if(id == R.id.search){
            fragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }
        else if (id == R.id.car) {
            fragment = new BookingFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        }
        else if(id == R.id.listCar){
            fragment = new OwnerCarFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_sidebar, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }
        else if (id == R.id.logout) {
            editor.remove("logged_in");
            editor.remove("login_as");
            editor.remove("user_id");
            editor.remove("member_name");
            editor.remove("member_email");
            editor.remove("location_pickup");
            editor.remove("location_return");
            editor.remove("user_lat");
            editor.remove("user_lang");
            editor.remove("epoint");
            editor.apply();

            Log.v("logged", String.valueOf(sharedPreferences.getBoolean("logged_in", false)));

            Intent out = new Intent(SidebarActivity.this, LoginActivity.class);
            startActivity(out);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.sidebar_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class checkWallet extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;
        String wallet_url = "http://new.entongproject.com/api/customer/check_wallet";

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SidebarActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jsonParser = new JSONParser();
            Log.v("walleturl", wallet_url);
            Log.v("memberid", memberId);
            JSONObject response = jsonParser.getJsonFromUrl(wallet_url, memberId);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            progressDialog.dismiss();
            try{
                String result = response.getString("result");

                if(result.equals("success")){
                    symbols = DecimalFormatSymbols.getInstance();
                    symbols.setGroupingSeparator(',');
                    formatter = new DecimalFormat("###,###", symbols);

                    navWallet.setText("RM " + formatter.format(response.getInt("epoint")));
                    navUsername.setText(response.getString("name"));
                    navEmail.setText(response.getString("email"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
