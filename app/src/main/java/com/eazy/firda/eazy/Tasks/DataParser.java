package com.eazy.firda.eazy.Tasks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by firda on 2/25/2018.
 */

public class DataParser {
    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");
            jsonObject = new JSONObject((String) jsonData);
            jsonArray = jsonObject.getJSONArray("cars");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("Places", "Adding places");

            } catch (JSONException e) {
                Log.d("Places", "Error in Adding places");
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String ownerName = "-NA-";
        String carName = "-NA-";
        String latitude = "";
        String longitude = "";
        String car_id = "";
        String price = "";
        String photo = "";

        Log.d("getPlace", "Entered");

        try {
            if (!googlePlaceJson.isNull("owner_name")) {
                ownerName = googlePlaceJson.getString("owner_name");
            }
            if (!googlePlaceJson.isNull("car_name")) {
                carName = googlePlaceJson.getString("car_name");
            }
            latitude = String.valueOf(googlePlaceJson.getDouble("lat"));
            longitude = String.valueOf(googlePlaceJson.getDouble("lng"));
            price = String.valueOf((int)(googlePlaceJson.getDouble("price")));
            car_id = googlePlaceJson.getString("car_id");
            photo = googlePlaceJson.getString("photo");
            googlePlaceMap.put("owner_name", ownerName);
            googlePlaceMap.put("car_name", carName);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("id", car_id);
            googlePlaceMap.put("price", price);
            googlePlaceMap.put("photo", photo);
            Log.d("getPlace", "Putting Places");
        } catch (JSONException e) {
            Log.d("getPlace", "Error");
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}