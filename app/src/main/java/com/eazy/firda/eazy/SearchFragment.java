package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.CarRecyclerAdapter;
import com.eazy.firda.eazy.models.Car;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView rvRating, rvTrip;
    RecyclerView.LayoutManager layoutManager, layoutManager2;

    ScrollView layMain;
    ProgressBar progress;
    TextView totalWallet, totalIncome, tagMyCar, topUp;
    TextView ads;
    LinearLayout laySearch;
    RelativeLayout layNullRated, layNullFeatured;

    Location startPoint, endPoint;

    float distance;
    String data, login_as;
    String callUrl = "http://new.entongproject.com/api/customer/list_fav_car";
    String adsUrl = "http://new.entongproject.com/api/customer/promo";
    String homeUrl = "http://new.entongproject.com/api/customer/featured";

    List<Car> cars = new ArrayList<Car>();
    List<Car> cars2 = new ArrayList<Car>();

    CarRecyclerAdapter rAdapter1, rAdapter2;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    int epoint, income, ownerCars;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

//

        layMain = v.findViewById(R.id.layMain);
        layMain.setVisibility(View.INVISIBLE);
        progress = v.findViewById(R.id.progressBar);
        totalWallet = v.findViewById(R.id.total_wallet);
        totalIncome = v.findViewById(R.id.total_income);
        tagMyCar = v.findViewById(R.id.tagMyCar);
        topUp = v.findViewById(R.id.topUp);
        ads = v.findViewById(R.id.ads);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sp.edit();
        login_as = sp.getString("login_as", null);

        if(login_as.equals("member")){
//            layWallet.setVisibility(View.VISIBLE);

            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setGroupingSeparator(',');
            DecimalFormat formatter = new DecimalFormat("###,###", symbols);

            epoint = sp.getInt("member_epoint", 0);
            totalWallet.setText("RM " + formatter.format((epoint)));

            ownerCars = sp.getInt("member_cars", 0);
            if(ownerCars == 0){
                totalIncome.setText("No cars is listed");
                tagMyCar.setText("List Now");
            }
            else if(ownerCars > 0){
                income = sp.getInt("member_income", 0);
                totalIncome.setText("RM " + formatter.format((income)));
                tagMyCar.setText("My Car");
            }
//            totalWallet.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.textsize));
//            totalIncome.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.textsize));
        }
        else if(login_as.equals("guest")){
//            layWallet.setVisibility(View.GONE);
        }

        topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Currently this service is not available", Toast.LENGTH_SHORT).show();
            }
        });

        tagMyCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageCar.class);
                Bundle extras = new Bundle();
                extras.putString("screen", "home");
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        laySearch = v.findViewById(R.id.laySearch);
        laySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(getActivity(), ListCar.class);
                startActivity(search);
            }
        });

        layNullRated = v.findViewById(R.id.nullRated);
        layNullRated.setVisibility(View.INVISIBLE);

        layNullFeatured = v.findViewById(R.id.nullFeatured);
        layNullFeatured.setVisibility(View.INVISIBLE);

        EditText search = (EditText)v.findViewById(R.id.location);

//        EditText button = (EditText) v.findViewById(R.id.search_location);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent search = new Intent(getActivity(), ListCar.class);
//                startActivity(search);
//            }
//        });


        Double latStart = -7.418233;
        Double lngStrat = 112.678366;

        startPoint = new Location("Point A");
        startPoint.setLatitude(latStart);
        startPoint.setLongitude(lngStrat);

        rvRating = (RecyclerView)v.findViewById(R.id.rvRating);
        rvRating.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvRating.setLayoutManager(layoutManager);
        rAdapter1 = new CarRecyclerAdapter(getActivity(), cars, "rating");

        rvTrip = (RecyclerView) v.findViewById(R.id.rvTrip);
        layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvTrip.setHasFixedSize(true);
        rvTrip.setLayoutManager(layoutManager2);
        rAdapter2 = new CarRecyclerAdapter(getActivity(),cars2, "trips");

        progress.setVisibility(View.VISIBLE);

//        favRate fr = new favRate();
//        fr.execute();
//        favTrip ft = new favTrip();
//        ft.execute();

        featured ft = new featured();
        ft.execute();

        progress.setVisibility(View.INVISIBLE);
        layMain.setVisibility(View.VISIBLE);

        rAdapter1.notifyDataSetChanged();
        rvRating.setAdapter(rAdapter1);
        rAdapter2.notifyDataSetChanged();
        rvTrip.setAdapter(rAdapter2);

        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event

    private class featured extends AsyncTask<Void, Void, JSONObject>{

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected JSONObject doInBackground(Void... voids){

            JSONParser jsonParser = new JSONParser();
            JSONObject response = jsonParser.getJsonFromUrl(homeUrl, "");
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);

            try{

                if(response.getInt("ratingval") == 1){
                    //Load Featured cars
                    JSONArray ftdCars = response.getJSONArray("rating");
                    for(int i = 0, size = ftdCars.length(); i < size; i++){
                        JSONObject car = ftdCars.getJSONObject(i);

                        endPoint = new Location("Point B");
                        String location = "not identified";
                        String city = "not identified";

                        if ((!car.isNull("location_lat") || car.getDouble("location_lat") != 0.0)
                                && (!car.isNull("location_long")|| car.getDouble("location_long") != 0.0)) {
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(car.getDouble("location_lat"),
                                        car.getDouble("location_long"), 1);
                                Address obj = addresses.get(0);
                                city = addresses.get(0).getLocality();
                                location = obj.getAddressLine(0);
                                //location = obj.getFeatureName();
                                endPoint.setLatitude(car.getDouble("location_lat"));
                                endPoint.setLongitude(car.getDouble("location_long"));
                                distance = (startPoint.distanceTo(endPoint))/1000;

                                Log.v("address", addresses.toString());
                                Log.v("obj", obj.toString());

                            }
                            catch(Exception e){
                                Log.e("address", "error", e);
                            }
                        }

                        String imageCar = car.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : car.getString("photo");
                        Car c = new Car(car.getString("id"), car.getString("car_name"),
                                car.getString("owner_name"), imageCar, car.getInt("year"), car.getDouble("price"),
                                car.getDouble("total_rating"), location, city, 0);

                        cars.add(c);
                    }
                    //
                }
                else{
                    rvRating.setVisibility(View.INVISIBLE);
                    layNullRated.setVisibility(View.VISIBLE);
                    Log.v("Error", response.getString("message"));
                }

                if(response.getInt("tripsval") == 1){
                    //Load Cars according to trips
                    JSONArray tripCars = response.getJSONArray("trips");
                    for(int i = 0, size = tripCars.length(); i < size; i++){
                        JSONObject car = tripCars.getJSONObject(i);

                        endPoint = new Location("Point B");
                        String location = "not identified";
                        String city = "not identified";

                        if ((!car.isNull("location_lat") || car.getDouble("location_lat") != 0.0)
                                && (!car.isNull("location_long")|| car.getDouble("location_long") != 0.0)) {
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(car.getDouble("location_lat"),
                                        car.getDouble("location_long"), 1);
                                Address obj = addresses.get(0);
                                city = addresses.get(0).getLocality();
                                location = obj.getAddressLine(0);
                                //location = obj.getFeatureName();
                                endPoint.setLatitude(car.getDouble("location_lat"));
                                endPoint.setLongitude(car.getDouble("location_long"));
                                distance = (startPoint.distanceTo(endPoint))/1000;

                                Log.v("address", addresses.toString());
                                Log.v("obj", obj.toString());

                            }
                            catch(Exception e){
                                Log.e("address", "error", e);
                            }
                        }

                        String imageCar = car.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : car.getString("photo");
                        Car c = new Car(car.getString("id"), car.getString("car_name"),
                                car.getString("owner_name"), imageCar, car.getInt("year"), car.getDouble("price"),
                                car.getDouble("total_rating"), location, city, 0);

                        cars2.add(c);
                    }
                }
                else{

                    rvTrip.setVisibility(View.INVISIBLE);
                    layNullFeatured.setVisibility(View.VISIBLE);
                }

                //load promotion
                if(response.getString("promo").equals("no promo")){
                    ads.setText("No Promotion is Available");
                }else{
                    JSONObject promotion = response.getJSONObject("promo");
                    String msg = promotion.getString("message");
                    ads.setText(msg);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            rAdapter1.notifyDataSetChanged();
            rAdapter2.notifyDataSetChanged();
        }
    }

    private class favRate extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            data = "rating";
            Log.v("url", callUrl);
            Log.v("data", data);
            JSONObject response = jParser.getJsonFromUrl(callUrl, data);
            Log.v("response", response.toString());

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            String result;

            try{
                //result = response.getString("result");

                if(response.getString("result").equals("success")){
                    JSONArray mCars = response.getJSONArray("cars");
                    for(int i = 0, size = mCars.length(); i < size; i++){
                        JSONObject carDetail = mCars.getJSONObject(i);
                        Log.v("carDetail", carDetail.toString());

                        endPoint = new Location("Point B");
                        String location =  "not identified";
                        String city = "not identified";

                        if ((!carDetail.isNull("location_lat") || carDetail.getDouble("location_lat") != 0.0)
                                && (!carDetail.isNull("location_long")|| carDetail.getDouble("location_long") != 0.0)) {
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(carDetail.getDouble("location_lat"),
                                        carDetail.getDouble("location_long"), 1);
                                Address obj = addresses.get(0);
                                city = addresses.get(0).getLocality();
                                location = obj.getAddressLine(0);
                                //location = obj.getFeatureName();
                                endPoint.setLatitude(carDetail.getDouble("location_lat"));
                                endPoint.setLongitude(carDetail.getDouble("location_long"));
                                distance = (startPoint.distanceTo(endPoint))/1000;

                                Log.v("address", addresses.toString());
                                Log.v("obj", obj.toString());

                            }
                            catch(Exception e){
                                Log.e("address", "error", e);
                            }
                        }

                        Log.v("location", location);
                        Log.v("distance", String.valueOf(distance));

                        String imageCar = carDetail.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : carDetail.getString("photo");
                        Car car = new Car(carDetail.getString("id"), carDetail.getString("car_name"),
                                carDetail.getString("owner_name"), imageCar, carDetail.getInt("year"), carDetail.getDouble("price"),
                                carDetail.getDouble("total_rating"), location, city, 0);

                        cars.add(car);
                    }
                }
                else{
                    rvRating.setVisibility(View.INVISIBLE);
                    layNullRated.setVisibility(View.VISIBLE);
                    Log.v("Error", response.getString("message"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            rAdapter1.notifyDataSetChanged();
//            progressDialog.dismiss();
        }
    }

    private class favTrip extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            data = "trips";
            Log.v("url", callUrl);
            Log.v("data", data);
            JSONObject response = jParser.getJsonFromUrl(callUrl, data);
            Log.v("response", response.toString());

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            String result;

            try{
                //result = response.getString("result");

                if(response.getString("result").equals("success")){
                    JSONArray mCars = response.getJSONArray("cars");
                    for(int i = 0, size = mCars.length(); i < size; i++){
                        JSONObject carDetail = mCars.getJSONObject(i);
                        Log.v("carDetail", carDetail.toString());

                        endPoint = new Location("Point B");
                        String location =  "not identified";
                        String city = "not identified";


                        if ((!carDetail.isNull("location_lat") || carDetail.getDouble("location_lat") != 0.0)
                                && (!carDetail.isNull("location_long")|| carDetail.getDouble("location_long") != 0.0)) {
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(carDetail.getDouble("location_lat"),
                                        carDetail.getDouble("location_long"), 1);
                                Address obj = addresses.get(0);
                                city = addresses.get(0).getLocality();
                                location = obj.getAddressLine(0);
                                //location = obj.getFeatureName();
                                endPoint.setLatitude(carDetail.getDouble("location_lat"));
                                endPoint.setLongitude(carDetail.getDouble("location_long"));
                                distance = (startPoint.distanceTo(endPoint))/1000;

                                Log.v("address", addresses.toString());
                                Log.v("obj", obj.toString());

                            }
                            catch(Exception e){
                                Log.e("address", "error", e);
                            }
                        }

                        Log.v("location", location);
                        Log.v("distance", String.valueOf(distance));

                        String imageCar = carDetail.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : carDetail.getString("photo");
                        Car car = new Car(carDetail.getString("id"), carDetail.getString("car_name"),
                                carDetail.getString("owner_name"), imageCar, carDetail.getInt("year"), carDetail.getDouble("price"),
                                carDetail.getDouble("total_rating"), location, city, 0);

                        cars2.add(car);
                    }
                }
                else{
                    rvTrip.setVisibility(View.INVISIBLE);
                    layNullFeatured.setVisibility(View.VISIBLE);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            rAdapter2.notifyDataSetChanged();
//            progressDialog.dismiss();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
