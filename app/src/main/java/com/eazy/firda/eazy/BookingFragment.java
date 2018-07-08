package com.eazy.firda.eazy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.BookListAdapter;
import com.eazy.firda.eazy.models.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Book> books = new ArrayList<Book>();
    private ListView lvbook;
    private BookListAdapter adapter;
    private String url = "http://new.entongproject.com/api/customer/book_list";

    int screen_status = 0;
    int status = 2;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String member_id;

    SimpleDateFormat day_name;
    SimpleDateFormat month_name;
    SimpleDateFormat day_number;

    LinearLayout layMain;
    RelativeLayout bookingNull;
    ProgressBar progressBar;
    TextView startBooking;

    public BookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingFragment newInstance(String param1, String param2) {
        BookingFragment fragment = new BookingFragment();
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
        View v = inflater.inflate(R.layout.fragment_booking, container, false);
        layMain = v.findViewById(R.id.layMain);
        layMain.setVisibility(View.INVISIBLE);
        bookingNull = v.findViewById(R.id.bookingNull);
        bookingNull.setVisibility(View.INVISIBLE);
        progressBar = v.findViewById(R.id.progressBar);

        startBooking = v.findViewById(R.id.startBooking);
        startBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(getActivity(), ListCar.class);
                startActivity(search);
            }
        });
        adapter = new BookListAdapter(getActivity(), books);

        lvbook = (ListView)v.findViewById(R.id.lv_book);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();

        member_id =  sharedPreferences.getString("user_id", "0");

        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        month_name = new SimpleDateFormat("MMM", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);

        //getBooks();
//
//        getBook gb = new getBook();
//        gb.execute();

        final getBook jparse = new getBook();

        if(screen_status == 0){
            jparse.execute();
            //new JSONParse().execute();
        }


        status = 1;
        getBook gb = new getBook();
        gb.execute();

        SegmentedButtonGroup sbg = (SegmentedButtonGroup)v.findViewById(R.id.toggle_booktype);

        sbg.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                books.clear();
                if(position == 0){
                    status = 0;
                }
                else if(position == 1){
                    status = 1;
                }
                else if(position == 2){
                    status = 2;
                }
                getBook gb = new getBook();
                gb.execute();
//                cardetail cd = new cardetail();
//                cd.execute();
            }
        });

        adapter.notifyDataSetChanged();
        lvbook.setAdapter(adapter);

        lvbook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book)parent.getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), DetailBookingMember.class);
                Bundle extras = new Bundle();
                extras.putString("book_id", book.getId());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        return v;
    }


    private class getBook extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Getting Data ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
//            progressDialog.show();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... args){

            while(screen_status == 0){
                JSONParser jParser = new JSONParser();

                Log.v("url", url);
                Log.v("car_id", member_id +":"+status);
                JSONObject response = jParser.getJsonFromUrl(url, member_id + "#" + status);

                if (isCancelled())
                    break;

                return response;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response){
//            progressDialog.dismiss();
            String result;
            try{
                result = response.getString("result");
                Log.v("JSON", response.toString());
                Log.v("result", result);

                    if(result.equals("success")){
                        final JSONArray aBook = response.getJSONArray("books");
                        Log.v("array", String.valueOf(aBook.length()));

                        if(aBook.length() == 0){
                            bookingNull.setVisibility(View.VISIBLE);
                            layMain.setVisibility(View.INVISIBLE);
                        }
                        else{
                            bookingNull.setVisibility(View.INVISIBLE);
                            layMain.setVisibility(View.VISIBLE);
                            for (int i = 0, size = aBook.length(); i < size; i++){

                                JSONObject bookDetail = aBook.getJSONObject(i);
                                Log.v("bookDetail", bookDetail.toString());

                                String location =  "not identified";

                                if ((!bookDetail.isNull("location_lat") || bookDetail.getDouble("location_lat") != 0.0)
                                        && (!bookDetail.isNull("location_long")|| bookDetail.getDouble("location_long") != 0.0)) {
                                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(bookDetail.getDouble("location_lat"),
                                                bookDetail.getDouble("location_long"), 1);
                                        Address obj = addresses.get(0);
                                        location = obj.getAddressLine(0);
                                        //location = obj.getFeatureName();

                                        Log.v("address", addresses.toString());
                                        Log.v("obj", obj.toString());

                                    }
                                    catch(Exception e){
                                        Log.e("address", "error", e);
                                    }
                                }

                                String imageCar = bookDetail.isNull("photo")? "http://new.entongproject.com/images/car/car_default.png"
                                        : bookDetail.getString("photo");

                                String book_stats = "";
                                if(bookDetail.getInt("book_status") == 0){
                                    book_stats = "PENDING";
                                }
                                else if(bookDetail.getInt("book_status") == 1){
                                    book_stats = "CONFIRMED";
                                }
                                else if(bookDetail.getInt("book_status") == 2){
                                    book_stats = "REJECTED";
                                }
                                else if(bookDetail.getInt("book_status") == 3){
                                    book_stats = "COMPLETED";
                                }

                                String day_start, number_start, month_start, day_end, number_end, month_end;
                                day_start = number_start = month_start = day_end = number_end = month_end = "";
                                String[] string_start = bookDetail.getString("date_start").split(" ");
                                String[] string_end = bookDetail.getString("date_end").split(" ");
                                try {
                                    Date date = new SimpleDateFormat("M/d/yyyy").parse(string_start[0]);
                                    Date date2 = new SimpleDateFormat("M/d/yyyy").parse(string_end[0]);
                                    Log.v("date", date.toString());
                                    day_start = day_name.format(date);
                                    number_start = day_number.format(date);
                                    month_start = month_name.format(date);

                                    day_end = day_name.format(date2);
                                    number_end = day_number.format(date2);
                                    month_end = month_name.format(date2);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                String date = day_start +", " + number_start + " " + month_start + " - " + day_end +", " + number_end + " " + month_end;
                                Book book = new Book(bookDetail.getString("book_id"),
                                        bookDetail.getString("car_name"), book_stats, date, location);
                                //Book book = new Book(book_stats, imageCar, day_start +", " + number_start + " " + month_start
                                //      , day_end +", " + number_end + " " + month_end, location);

                                books.add(book);
                            }
                        }
                    }
                    else{
                        bookingNull.setVisibility(View.VISIBLE);
                        layMain.setVisibility(View.INVISIBLE);
                    }
            }catch(JSONException e){
                e.printStackTrace();
            }

            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            layMain.setVisibility(View.VISIBLE);
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
