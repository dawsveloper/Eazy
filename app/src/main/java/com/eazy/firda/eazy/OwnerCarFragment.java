package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.OwnerCarListAdapter;
import com.eazy.firda.eazy.models.Car;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerCarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerCarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String memberId;
    List<Car> cars = new ArrayList<Car>();
    ListView lvcar;
    OwnerCarListAdapter adapter;
    FloatingActionButton addNew;
    TextView addNew2;
    RelativeLayout lay1;
    ScrollView lay2;

    public OwnerCarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerCarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerCarFragment newInstance(String param1, String param2) {
        OwnerCarFragment fragment = new OwnerCarFragment();
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
        View vw = inflater.inflate(R.layout.fragment_owner_car, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sp.edit();
        memberId = sp.getString("user_id", null);

        addNew = vw.findViewById(R.id.fab_add);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(getActivity(), AddCar.class);
                getActivity().startActivity(add);
            }
        });

        lay1 = vw.findViewById(R.id.lay1);
        lay2 = vw.findViewById(R.id.lay2);
        addNew2 = vw.findViewById(R.id.txt_addNew);
        addNew2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(getActivity(), AddCar.class);
                getActivity().startActivity(add);

            }
        });

        lvcar = (ListView)vw.findViewById(R.id.listcar);
        adapter = new OwnerCarListAdapter(getActivity(), cars);
        cars.clear();

        adapter.notifyDataSetChanged();
        lvcar.setAdapter(adapter);

        callCars cc = new callCars();
        cc.execute();

        lvcar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Car car = (Car) parent.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(), DetailCarOwner.class);
                Bundle extras = new Bundle();
                extras.putString("car_id", car.getCar_id());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        return vw;
    }

    private class callCars extends AsyncTask<Void, Void, JSONObject>{
        ProgressDialog progressDialog;
        String callUrl = "http://new.entongproject.com/api/provider/rental/api_list_car";

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser jParser = new JSONParser();

            Log.v("memberId", memberId);

            JSONObject response = jParser.getJsonFromUrl(callUrl, memberId);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {

            try{
                //result = response.getString("result");

                if(response.getString("result").equals("success")){
                    lay1.setVisibility(View.GONE);
                    lay2.setVisibility(View.VISIBLE);

                    JSONArray mCars = response.getJSONArray("cars");
                    for(int i = 0, size = mCars.length(); i < size; i++){
                        JSONObject carDetail = mCars.getJSONObject(i);
                        Log.v("carDetail", carDetail.toString());

                        String imageCar = carDetail.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                : carDetail.getString("photo");
                        Car car = new Car(carDetail.getString("id"), carDetail.getString("car_name"),
                                imageCar, carDetail.getInt("year"), carDetail.getDouble("price"));
                        cars.add(car);
                    }
                }
                else{
                    lay1.setVisibility(View.VISIBLE);
                    lay2.setVisibility(View.GONE);
                    Log.v("Error", response.getString("message"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
