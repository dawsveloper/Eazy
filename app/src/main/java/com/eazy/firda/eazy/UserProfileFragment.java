package com.eazy.firda.eazy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.utils.ImageFilePath;
import com.eazy.firda.eazy.utils.Misc;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sauronsoftware.ftp4j.FTPClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    int screen_status = 0;
    int query_result = 0;

    String member_info_url = "http://new.entongproject.com/api/customer/call/profile";
    String member_changePass = "http://new.entongproject.com/api/customer/edit/password";
    String profpict_url = "http://new.entongproject.com/api/customer/upload/profpict";
    String member_id, memberPassword;

    member_detail m_detail = new member_detail();

    TextView profName;
    TextView profPhone;
    TextView profEmail;
    TextView profRate1;
    TextView profRate2;

    EditText curr_pass;
    EditText new_pass;
    EditText confirm_pass;

    ProgressBar progress, progressUpload;

    ScrollView layMain;

    NetworkImageView profPict;
    ImageView profPict2, chgPict, navProfpict;
    CircleImageView profPict3;
    RelativeLayout acc_info, acc_doc, acc_bank, acc_manage, change_pass, logout;

    int GALLERY = 1, CAMERA = 2;

    Bitmap bitmap;
    Intent intentDoc;
    BitmapDrawable drawable;
    String[] pict;
    String pict_path, pict_url, pict_name, photoType, resultPath;

    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();

        layMain = v.findViewById(R.id.layMain);
        layMain.setVisibility(View.INVISIBLE);

        profName = (TextView)v.findViewById(R.id.prof_name);
        profPhone = (TextView)v.findViewById(R.id.prof_phone);
        profEmail = (TextView)v.findViewById(R.id.prof_mail);
        profRate1 = (TextView)v.findViewById(R.id.prof_rate);
        profRate2 = (TextView)v.findViewById(R.id.prof_rate2);

        progress = v.findViewById(R.id.progressBar);
        progressUpload = v.findViewById(R.id.pbUpload);

        member_id = sharedPreferences.getString("user_id", "0");

        if(screen_status == 0){
            m_detail.execute();
        }

//        profPict2 = v.findViewById(R.id.prof_pict);
        profPict3 = v.findViewById(R.id.prof_pict);

        chgPict = v.findViewById(R.id.chgPict);
        navProfpict = getActivity().findViewById(R.id.navProfpict);

        chgPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        acc_info = v.findViewById(R.id.prof_acc_info);

        acc_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("acc_info", "clicked");
                Intent act_info = new Intent(getActivity(), MemberID.class);
                getActivity().startActivity(act_info);
            }
        });

        acc_doc = v.findViewById(R.id.prof_doc);

        acc_doc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                Intent act_doc = new Intent(getActivity(), EditProfile.class);
                getActivity().startActivity(act_doc);
            }
        });

//        acc_bank = (TextView)v.findViewById(R.id.prof_acc_bank);
//
//        acc_bank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log.v("acc bank", "clicked");
//                Intent act_bank = new Intent(getActivity(), MemberBank.class);
//                getActivity().startActivity(act_bank);
//            }
//        });

        acc_manage = v.findViewById(R.id.prof_manage);
        acc_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageCar.class);
                Bundle extras = new Bundle();
                extras.putString("screen", "profile");
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        change_pass = v.findViewById(R.id.prof_change_pass);

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_detail.cancel(true);
                showChangePass();
            }
        });

        logout = v.findViewById(R.id.prof_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Intent out = new Intent(getActivity(), LoginActivity.class);
                startActivity(out);
            }
        });

        return v;
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"
        };
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0 :
                        choosePhotoFromGallery();
                        break;
                    case 1 :
                        takePhotoFromCamera();
                        break;
                }
            }
        });

        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_CANCELED){
            return;
        }
        if(requestCode == GALLERY){
            if(data != null){
                Uri contentURI = data.getData();
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    profPict3.setImageBitmap(bitmap);
                    photoType = "gallery";
                    resultPath = ImageFilePath.getPath(getActivity() ,data.getData());
                }catch (IOException e){
                    e.printStackTrace();
                }

                intentDoc = data;
                drawable = (BitmapDrawable)profPict3.getDrawable();

                if(photoType.equals("camera")){
                    File tmp = new File(resultPath);
                    boolean delResult = tmp.delete();
                    if(delResult)Log.v("source file:", "deleted");
                }
            }
        }
        else if(requestCode == CAMERA){
            bitmap = (Bitmap)data.getExtras().get("data");
            profPict3.setImageBitmap(bitmap);
            photoType = "camera";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String tmp = "/" + timestamp.getTime() + ".jpeg";
            File outFile = new File(Environment.getExternalStorageDirectory(), tmp);
            try{
                FileOutputStream fos = new FileOutputStream(outFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                resultPath = Environment.getExternalStorageDirectory() + tmp;
                Log.v("photo path", resultPath);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }

        pict = copyfile(resultPath, photoType);
        pict_path = pict[0];
        pict_url = pict[1];
        pict_name = pict[2];

        uploadPhoto up = new uploadPhoto();
        up.execute(pict_path, pict_url, pict_name, member_id);
    }

    private String[] copyfile(String path, String type){
        String[] result;
        String url, imgPath, newImgPath, currName, newName, extension, photoId;

        newName = "";

//        bmp = drawable.getBitmap();
//        bos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        bb = bos.toByteArray();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        pict_string = Base64.encodeToString(bb, Base64.DEFAULT);
//        pict_path = ImageFilePath.getPath(getActivity() ,data.getData());
//        Log.v("old image path", pict_path);

        currName = path.substring(path.lastIndexOf("/")+1);
        extension = currName.substring(currName.lastIndexOf(".")+1);
        photoId = Misc.randomString(20);
        newName = "photo_" + photoId + "_" + timestamp.getTime() + "." + extension;
        newImgPath = path.replace(currName, newName);
        Log.v("new image path", newImgPath);
        Misc.copyFile(path, newImgPath);

        url = "http://new.entongproject.com/images/profile/" + newName;
        result = new String[]{newImgPath, url, newName};

        return result;
    }

    private class uploadPhoto extends AsyncTask<String, String, JSONObject>{
        String update_url = "http://new.entongproject.com/api/customer/edit/photo";


        @Override
        protected void onPreExecute(){
            progressUpload.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            while(screen_status == 0){
                FTPClient client = new FTPClient();

                JSONParser jParser = new JSONParser();
                JSONObject response = jParser.getJsonFromUrl(profpict_url, pict_url);
                File doc  = new File(strings[0]);
                try{
                    int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    String[] PERMISSIONS_STORAGE = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        ActivityCompat.requestPermissions(
                                getActivity(), PERMISSIONS_STORAGE, 1
                        );
                    }
                    client.connect(getResources().getString(R.string.ftp_server),21);
                    client.login(getResources().getString(R.string.sftp_user), getResources().getString(R.string.sftp_password));
                    client.setType(FTPClient.TYPE_BINARY);
                    client.changeDirectory("/public_html/angkot/public/images/profile");

                    Misc.MyTransferListener tfl = new Misc().new MyTransferListener();
                    client.upload(doc, tfl);
                    client.disconnect(true);
                    boolean del = doc.delete();
                    if(del){Log.v("status", "image deleted");}
                    else{Log.v("status", "image can't delete");}

                    String data = strings[1] + "|" + strings[2] + "|" + strings[3];
                    Log.v("data", data);
                    response = jParser.getJsonFromUrl(update_url, data);
                }catch(Exception e){
                    e.printStackTrace();
                }

                if (isCancelled())
                    break;

                return response;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response){

            try{

                String url = response.getString("url");
//                        Toast.makeText(getActivity().getParent(), response.getString("result"), Toast.LENGTH_SHORT).show();

//                        Ion.with(navProfpict)
//                                .placeholder(R.drawable.user_def)
//                                .error(R.drawable.ic_close_black_18dp)
//                                .load(strings[1]);

                Ion.with(profPict3)
                        .placeholder(R.drawable.user_def)
                        .error(R.drawable.ic_close_black_18dp)
                        .load(url);

            }catch (JSONException je){
                je.printStackTrace();
            }
            progressUpload.setVisibility(View.INVISIBLE);
        }
    }

    private class member_detail extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog progressDialog;
        //LayoutInflater inflater;
        //ViewGroup container;

        //View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        @Override
        protected void onPreExecute(){

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
//            progressDialog.show();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            while(screen_status == 0){
                JSONParser jParser = new JSONParser();

                Log.v("url", member_changePass);
                Log.v("data", member_id);
                JSONObject response = jParser.getJsonFromUrl(member_info_url, member_id);

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
                Log.v("response", response.toString());
                result = response.getString("result");

                if(result.equals("success")){
                    JSONObject c = response.getJSONObject("member");

                    profName.setText(c.getString("name"));
                    profPhone.setText(c.getString("phone"));
                    profEmail.setText(c.getString("email"));
                    profRate1.setText(String.valueOf(c.getDouble("member_rating")));
                    profRate2.setText(String.valueOf(c.getDouble("provider_rating")));
                    String s_userPict = c.isNull("photo")?"http://new.entongproject.com/images/profile/user_def.png":c.getString("photo");
//                    profPict.setImageUrl(s_userPict, imageLoader);

                    Ion.with(profPict3)
                            .placeholder(R.drawable.user_def)
                            .error(R.drawable.ic_close_black_18dp)
                            .load(s_userPict);
                }
                else{

                    profName.setText("");
                    profPhone.setText("");
                    profEmail.setText("");
                    profRate1.setText("");
                    profRate2.setText("");
                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            progress.setVisibility(View.INVISIBLE);
            layMain.setVisibility(View.VISIBLE);
        }
    }

    private void showChangePass(){

        try{
            query_result = 0;
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater li = this.getLayoutInflater();
            final View prompt = li.inflate(R.layout.popup_change_pass, null);
            curr_pass = (EditText)prompt.findViewById(R.id.curr_pass);
            new_pass = (EditText)prompt.findViewById(R.id.new_pass);
            confirm_pass = (EditText)prompt.findViewById(R.id.confirm_pass);

            alertDialogBuilder.setView(prompt);

            alertDialogBuilder.setTitle("Guest Registration");
            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String c_pass = curr_pass.getText().toString();
                    String n_pass = new_pass.getText().toString();
                    String cn_pass = confirm_pass.getText().toString();
                    //dialog.cancel();
                    if(curr_pass.getText().length() < 1){
                        curr_pass.setError("Complete Data First");
                    }

                    if(new_pass.getText().length() < 1){
                        new_pass.setError("Complete Data First");
                    }

                    if(confirm_pass.getText().length() < 1){
                        confirm_pass.setError("Complete Data First");
                    }

                    if(curr_pass.getText().length() < 1 || new_pass.getText().length() < 1 || confirm_pass.getText().length() < 1){

                    }
                    else{
                        if(!cn_pass.equals(n_pass)){
                            confirm_pass.setError("Confirmation Password is wrong");
                        }
                        else{
                            memberPassword = member_id + ":" +  c_pass + ":" + n_pass;
                            Log.v("pre-guest", memberPassword);
                            changePass chg_pass = new changePass();
                            chg_pass.execute();
                        }
                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            Log.v("screen_status", Integer.toString(screen_status));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class changePass extends AsyncTask<String, String, JSONObject>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONParser jParser = new JSONParser();

            Log.v("url", member_info_url);
            Log.v("car_id", member_id);
            JSONObject response = jParser.getJsonFromUrl(member_changePass, memberPassword);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){

            try{
                String result = response.getString("result");
                if(result.equals("success")){
                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    query_result = 1;
                }
                else{
                    curr_pass.setError(response.getString("message"));
                }
            }catch (JSONException e){

            }
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
