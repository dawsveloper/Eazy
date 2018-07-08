package com.eazy.firda.eazy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.utils.ImageFilePath;
import com.eazy.firda.eazy.utils.Misc;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import it.sauronsoftware.ftp4j.FTPClient;

public class SignUp extends AppCompatActivity {

    EditText name, email, phone, password;
    Button submit;
    String regisUrl="http://new.entongproject.com/api/customer/signup";
    SharedPreferences sp;
    String[] license, ic;
    Intent dataLicense, dataIc;
    File fileLicense, fileIc;
    String stats, type, newImgPath, licenseUrl, licensePath, licenseName, icUrl, icPath, icName;
    String imgString, imgPath, currName, newName, extension, photoId, photoType1, photoType2;
    ImageView imgLicense, imgPassport, delLicense, delPassport;
    RelativeLayout layLicense, layPassport;
    CheckBox cbPrivacy;
    TextView tvPrivacy;

    int GALLERY = 1, CAMERA = 2;

    Bitmap bitmap, bmp;
    BitmapDrawable drawable;
    ByteArrayOutputStream bos;
    byte[] bb;
    FTPClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = (EditText)findViewById(R.id.regis_name);
        email = (EditText)findViewById(R.id.regis_email);
        phone = (EditText)findViewById(R.id.regis_phone);
        password = (EditText)findViewById(R.id.regis_pass);
        imgLicense = findViewById(R.id.imgLicense);
        imgPassport = findViewById(R.id.imgPassport);
        layLicense = findViewById(R.id.layLicense);
        layPassport = findViewById(R.id.layPassport);
        delLicense = findViewById(R.id.delLicense);
        delPassport = findViewById(R.id.delPassport);
        cbPrivacy = findViewById(R.id.cbPrivacy);
        tvPrivacy = findViewById(R.id.tvPrivacy);

        type = "";
        client = new FTPClient();

        layLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "license";
                showPictureDialog();
            }
        });

        layPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "passport";
                showPictureDialog();
            }
        });

        delLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgLicense.setImageBitmap(null);
                delLicense.setVisibility(View.GONE);
            }
        });

        delPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPassport.setImageBitmap(null);
                delPassport.setVisibility(View.GONE);
            }
        });

        sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        stats = sp.getString("login_as", null);

        submit = (Button)findViewById(R.id.regis_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbPrivacy.isChecked()){

                    if(photoType1.equals("gallery")){
                        license = copyfile(dataLicense, "license", drawable);
                        licensePath = license[0];
                        licenseUrl = license[1];
                        ic = copyfile(dataIc, "passport", drawable);
                        icPath = ic[0];
                        icUrl = ic[1];
                    }
                    else if(photoType1.equals("camera")){

                    }

                    uploadPict upload = new uploadPict();
                    upload.execute(licensePath, icPath);
                    regis reg = new regis();
                    reg.execute();
                }
                else{
                    Toast.makeText(getBaseContext(), "You have to agree with privayc policy to register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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

        if(resultCode == RESULT_CANCELED){
            return;
        }
        if(requestCode == GALLERY){
            if(data != null){
                Uri contentURI = data.getData();
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    if(type.equals("license")){
                        imgLicense.setImageBitmap(bitmap);
                        delLicense.setVisibility(View.VISIBLE);
                        drawable = (BitmapDrawable)imgLicense.getDrawable();
                        dataLicense = data;
                        photoType1 = "gallery";
                    }
                    else if(type.equals("passport")){
                        imgPassport.setImageBitmap(bitmap);
                        delPassport.setVisibility(View.VISIBLE);
                        drawable = (BitmapDrawable)imgPassport.getDrawable();
                        dataIc = data;
                        photoType2 = "gallery";
                    }
//                    uploadImage(contentURI);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == CAMERA){
            bitmap = (Bitmap)data.getExtras().get("data");

            if(type.equals("license")){
                imgLicense.setImageBitmap(bitmap);
                delLicense.setVisibility(View.VISIBLE);
//                drawable = (BitmapDrawable)imgLicense.getDrawable();
//                dataLicense = data;
                fileLicense = fileCamera(bitmap, "license");
                photoType1 = "camera";

            }
            else if(type.equals("passport")){
                imgPassport.setImageBitmap(bitmap);
                delPassport.setVisibility(View.VISIBLE);
//                drawable = (BitmapDrawable)imgPassport.getDrawable();
//                dataIc = data;
                fileIc = fileCamera(bitmap, "Ic");
                photoType2 = "camera";
                test t = new test();
                t.execute();
            }
        }
    }

    private String[] copyfile(Intent data, String type, BitmapDrawable drawable){
        String[] result;
        String url;

        bmp = drawable.getBitmap();
        bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bb = bos.toByteArray();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        imgString = Base64.encodeToString(bb, Base64.DEFAULT);
        imgPath = ImageFilePath.getPath(this ,data.getData());
        Log.v("old image path", imgPath);

        currName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        extension = currName.substring(currName.lastIndexOf(".")+1);
        photoId = Misc.randomString(20);
        if(type.equals("license")){
            newName = "license_" + photoId + "_" + timestamp.getTime() + "." + extension;
            licenseName = newName;
        }
        else if(type.equals("passport")){
            newName = "passsport_" + photoId + "_" + timestamp.getTime() + "." + extension;
            icName = newName;
        }
        newImgPath = imgPath.replace(currName, newName);
        Log.v("new image path", newImgPath);
        Misc.copyFile(imgPath, newImgPath);

        url = "http://new.entongproject.com/images/car/" + newName;
        result = new String[]{newImgPath, url};

        return result;
    }

    private File fileCamera(Bitmap bmp, String type){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path;
        Uri tempUri;
        Cursor cursor;
        File fileCamera;
        int idx;

        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        Log.v("photo path", path);
        tempUri = Uri.parse(path);
        cursor = getContentResolver().query(tempUri, null, null, null, null);
        cursor.moveToFirst();
        idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        fileCamera = new File(cursor.getString(idx));
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

        return fileCamera;
    }

    private class test extends AsyncTask<Void, Void, Void>{

        private final ProgressDialog dialog = new ProgressDialog(SignUp.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                int permission = ActivityCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            SignUp.this, PERMISSIONS_STORAGE, 1
                    );
                }
                client.connect(getResources().getString(R.string.ftp_server),21);
                client.login(getResources().getString(R.string.sftp_user), getResources().getString(R.string.sftp_password));
                client.setType(FTPClient.TYPE_BINARY);
                client.changeDirectory("/public_html/angkot/public/images/profile");

                Misc.MyTransferListener tfl = new Misc().new MyTransferListener();
                client.upload(fileIc, tfl);
                //client.upload(imgIc, tfl);
            }
            catch(Exception ex){
                ex.printStackTrace();
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.dialog.dismiss();
        }
    }

    private class uploadPict extends AsyncTask<String, Void, Void>{

        private final ProgressDialog dialog = new ProgressDialog(SignUp.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... files) {

            File imgLicense = null, imgIc = null;

            if(photoType1.equals("gallery")){
                imgLicense  = new File(files[0]);
            }
            else if(photoType1.equals("camera")){
                imgLicense = fileLicense;
            }

            if(photoType2.equals("gallery")){
                imgIc = new File(files[1]);
            }
            else if(photoType2.equals("camera")){
                imgIc = fileIc;
            }

            try {

                int permission = ActivityCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            SignUp.this, PERMISSIONS_STORAGE, 1
                    );
                }
                client.connect(getResources().getString(R.string.ftp_server),21);
                client.login(getResources().getString(R.string.sftp_user), getResources().getString(R.string.sftp_password));
                client.setType(FTPClient.TYPE_BINARY);
                client.changeDirectory("/public_html/angkot/public/images/profile");

                Misc.MyTransferListener tfl = new Misc().new MyTransferListener();
                client.upload(imgLicense, tfl);
                client.upload(imgIc, tfl);
                boolean delLicense = imgLicense.delete();
                boolean delIc = imgIc.delete();
                if(delLicense){Log.v("status", "image deleted");}
                else{Log.v("status", "image can't delete");}
                if(delIc){Log.v("status", "image deleted");}
                else{Log.v("status", "image can't delete");}

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.dialog.dismiss();
        }
    }

//    private void uploadImage(final Uri filePath) {
//        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadurl, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                try {
//                    String jsonString = new String(response.data,
//                            HttpHeaderParser.parseCharset(response.headers));
//                    JSONObject jsonObject  = new JSONObject(jsonString);
//                }
//                catch (Exception e){
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        }) {
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                // file name could found file base or direct access from real path
//                // for now just get bitmap data from ImageView
//                params.put("image", new DataPart("profilepict.jpeg", getImage(bitmap), "image/jpeg"));
//
//                return params;
//            }
//        };
//
//        EazyApplication.getInstance().addToRequestQueue(multipartRequest);
//    }

    private class regis extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;
        String regName, regEmail, regPhone, regPass, data;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            regName = name.getText().toString();
            regEmail = email.getText().toString();
            regPhone = phone.getText().toString();
            regPass = password.getText().toString();

            data = regName + "|" + regEmail + "|" + regPhone + "|" + regPass
                    + "|" + licenseUrl + "|" + licenseName
                    + "|" + icUrl + "|" + icName;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            JSONParser jparser = new JSONParser();
            JSONObject response = jparser.getJsonFromUrl(regisUrl, data);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            try{
                String result = response.getString("result");

                Toast.makeText(getBaseContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                if(result.equals("success")){
                    //if(stats.equals(null)){
                        Intent redirect = new Intent(SignUp.this, LoginActivity.class);
                        startActivity(redirect);
                    //}
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
