package com.eazy.firda.eazy;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;


import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;

import com.eazy.firda.eazy.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class MapSearch extends AppCompatActivity {

    private static ArrayAdapter<String> adapter;
    private static String[] itemArrayList = new String[]{};
    private static HashSet<String> set = new HashSet<String>();
    private static String apikey = "AIzaSyC5uHROOrPPPbyCN40nIEx90laVue8pjYY";

    Location location;
    GPSTracker gps;

    Double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

        LinearLayout btn_nearby = (LinearLayout)findViewById(R.id.btn_nearby);
        btn_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(getBaseContext(), MapSearch.this);

                if(gps.canGetLocation()){
                    lng = gps.getLongitude();
                    lat = gps.getLatitude();
                }
                else{
                    gps.showSettingsAlert();
                }

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MapSearch.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("location_pickup", "nearby");
                editor.putString("location_return", "nearby");
                editor.putLong("user_lat", Double.doubleToRawLongBits(lat));
                editor.putLong("user_lang", Double.doubleToRawLongBits(lng));
                editor.apply();

                Log.v("location", lat + " : " + lng);

                Intent redirect = new Intent(MapSearch.this, SearchActivity.class);
                startActivity(redirect);
            }
        });
    }

    @Override
    protected void onDestroy(){
        set.clear();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_location).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        // autocomplete
        SearchView.SearchAutoComplete searchAutoComplete =
                (SearchView.SearchAutoComplete)  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setDropDownBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.abc_popup_background_mtrl_mult));

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, itemArrayList);

        searchAutoComplete.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.replaceAll(" ", "\\+");
                if(!set.contains(newText)) {
                    set.add(newText);
                    // download places
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute
                            ("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + newText +
                                    "&types=geocode&language=en&key=" + apikey);
                }
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.i("click","select");
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String input = adapter.getItem(position);
                DownloadTask2 downloadTask = new DownloadTask2();
                downloadTask.execute("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input +
                        "&types=geocode&language=en&key=" + apikey);

                return true;
            }
        });

        return true;
    }

    private class DownloadTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = params[0];

                URL url = new URL(BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    jsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    jsonStr = null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("DOWNLOAD", "Error ", e);
                jsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DOWNLOAD", "Error closing stream", e);
                    }
                }
            }


            String[] rlt = null;
            if(jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("predictions");

                    rlt = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = (JSONObject) jsonArray.get(i);
                        rlt[i] = obj.getString("description");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return rlt;
        }

        @Override
        protected void onPostExecute(String[] rlt) {
            if(rlt != null) {
                adapter.addAll(rlt);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class DownloadTask2 extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = params[0].replaceAll(" ","+");

                URL url = new URL(BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    jsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    jsonStr = null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("DOWNLOAD", "Error ", e);
                jsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DOWNLOAD", "Error closing stream", e);
                    }
                }
            }

            String[] rlt = null;
            if(jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("predictions");

                    rlt = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = (JSONObject) jsonArray.get(i);
                        rlt[i] = obj.getString("place_id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return rlt;
        }

        @Override
        protected void onPostExecute(String[] rlt) {
            if(rlt != null) {
                //Intent intent = new Intent(getApplication(), DetailActivity.class);
                //intent.putExtra(Intent.EXTRA_TEXT, rlt[0]);
                //startActivity(intent);
                Log.i("result", rlt[0].toString());
                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+
                        rlt[0]+"&key=" + apikey;
                DownloadTask3 downloadTask3 = new DownloadTask3();
                downloadTask3.execute(url);
            }
        }
    }

    private class DownloadTask3 extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = params[0].replaceAll(" ","+");

                URL url = new URL(BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    jsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    jsonStr = null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("DOWNLOAD", "Error ", e);
                jsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DOWNLOAD", "Error closing stream", e);
                    }
                }
            }

            String[] rlt = new String[5];
            if(jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("result");
                    JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                    rlt[0] = jsonObject.getString("icon");
                    rlt[1] = location.getString("lat");
                    rlt[2] = location.getString("lng");
                    rlt[3] = jsonObject.getString("scope");
                    rlt[4] = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return rlt;
        }

        @Override
        protected void onPostExecute(String[] rlt) {
            if(rlt != null) {

                Log.i("result", rlt[0] + ":" + rlt[1] + ":" + rlt[2] + ":" + rlt[3] + ":" + rlt[4]);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MapSearch.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("location_pickup", rlt[4]);
                editor.putString("location_return", rlt[4]);
                editor.putLong("user_lat", Double.doubleToRawLongBits(Double.parseDouble(rlt[1])));
                editor.putLong("user_lang", Double.doubleToRawLongBits(Double.parseDouble(rlt[2])));
                editor.apply();

                Intent redirect = new Intent(MapSearch.this, SearchActivity.class);
                startActivity(redirect);

//                TextView lat = (TextView)findViewById(R.id.lat);
//                lat.setText("Location lat = " + rlt[1]);
//
//                TextView lng = (TextView)findViewById(R.id.lng);
//                lng.setText("Location lng = " + rlt[2]);
//
//                TextView scope = (TextView)findViewById(R.id.scope);
//                scope.setText("Scope = " + rlt[3]);
//
//                TextView name = (TextView)findViewById(R.id.name);
//                name.setText(rlt[4]);
            }
        }
    }
}
