package com.eazy.firda.eazy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.utils.ImageFilePath;
import com.eazy.firda.eazy.utils.Misc;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import it.sauronsoftware.ftp4j.FTPClient;

public class NewSignUp3 extends AppCompatActivity {

    Button next;
    ImageView imgIc, delIc;
    RelativeLayout layIc;
    CoordinatorLayout coordinatorLayout;
    Snackbar sb;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Bitmap bitmap;
    BitmapDrawable drawable;
    FTPClient client;

    int GALLERY = 1, CAMERA = 2;
    String IcPath, photoType, sharedLicense, sharedIc, licenseType, icType;
    String[] dataLicense, dataIc;
    String uploadPath1, uploadPath2, url1, url2, licenseName, icName;
    String reg_name, reg_email, reg_phone, reg_pass, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sign_up3);
        coordinatorLayout = findViewById(R.id.coordinator);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        reg_name = sp.getString("reg_name", null);
        reg_email = sp.getString("reg_email", null);
        reg_phone = sp.getString("reg_phone", null);
        reg_pass = sp.getString("reg_pass", null);

        client = new FTPClient();

        layIc = findViewById(R.id.layPassport);
        imgIc = findViewById(R.id.imgPassport);
        delIc = findViewById(R.id.delPassport);

        layIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        delIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgIc.setImageBitmap(null);
                delIc.setVisibility(View.GONE);
            }
        });

        next = findViewById(R.id.btn_submit);
        sb = Snackbar.make(coordinatorLayout, "Please choose photo for upload", Snackbar.LENGTH_LONG);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgIc.getDrawable() != null){

                    sharedLicense = sp.getString("pathLicense", null);
                    licenseType = sp.getString("typeLicense", null);
                    if(sharedLicense != null){
                        dataLicense = copyfile(sharedLicense, "license");
                        uploadPath1 = dataLicense[0];
                        url1 = dataLicense[1];
                        licenseName = dataLicense[2];
                        Log.v("path license", uploadPath1);
                        Log.v("url license", url1);

                        if(licenseType.equals("camera")){
                            File tmp = new File(sharedLicense);
                            boolean delLicense = tmp.delete();
                            if(delLicense)Log.v("source file:", "deleted");
                        }
                    }
                    else{
                        Log.v("path license", "null");
                    }

                    sharedIc = sp.getString("pathIc", null);
                    icType = sp.getString("typeIc", null);
                    if(sharedIc != null){
                        dataIc = copyfile(sharedIc, "passport");
                        uploadPath2 = dataIc[0];
                        url2 = dataIc[1];
                        icName = dataIc[2];
                        Log.v("path ic", uploadPath2);
                        Log.v("url ic", url2);

                        if(icType.equals("camera")){
                            File tmp = new File(sharedIc);
                            boolean delIc = tmp.delete();
                            if(delIc)Log.v("source file:", "deleted");
                        }
                    }
                    else{
                        Log.v("path IC", "null");
                    }

                    uploadPict up = new uploadPict();
                    up.execute(uploadPath1, uploadPath2);
                    data = reg_name + "|" + reg_email + "|" + reg_phone + "|" + reg_pass
                                    + "|" + url1 + "|" + licenseName
                                    + "|" + url2 + "|" + icName;

                    regis reg = new regis();
                    reg.execute(data);
                }
                else{
                    sb.show();
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
                    imgIc.setImageBitmap(bitmap);
                    delIc.setVisibility(View.VISIBLE);
                    drawable = (BitmapDrawable)imgIc.getDrawable();
                    photoType = "gallery";
                    IcPath = ImageFilePath.getPath(this ,data.getData());
//                    uploadImage(contentURI);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == CAMERA){
            bitmap = (Bitmap)data.getExtras().get("data");
            imgIc.setImageBitmap(bitmap);
            delIc.setVisibility(View.VISIBLE);
            photoType = "camera";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String tmp = "/" + timestamp.getTime() + ".jpeg";
            File outFile = new File(Environment.getExternalStorageDirectory(), tmp);
            try{
                FileOutputStream fos = new FileOutputStream(outFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                IcPath = Environment.getExternalStorageDirectory() + tmp;
                Log.v("photo path", IcPath);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }
        editor.putString("pathIc", IcPath);
        editor.putString("typeIc", photoType);
        editor.apply();
    }

    private String[] copyfile(String path, String type){
        String[] result;
        String url, imgPath, newImgPath, currName, newName, extension, photoId;

        newName = "";

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        imgString = Base64.encodeToString(bb, Base64.DEFAULT);
//        imgPath = ImageFilePath.getPath(this ,data.getData());
        Log.v("old image path", path);

        currName = path.substring(path.lastIndexOf("/")+1);
        extension = currName.substring(currName.lastIndexOf(".")+1);
        photoId = Misc.randomString(20);
        if(type.equals("license")){
            newName = "license_" + photoId + "_" + timestamp.getTime() + "." + extension;
        }
        else if(type.equals("passport")){
            newName = "passsport_" + photoId + "_" + timestamp.getTime() + "." + extension;
        }
        newImgPath = path.replace(currName, newName);
        Log.v("new image path", newImgPath);
        Misc.copyFile(path, newImgPath);

        url = "http://new.entongproject.com/images/profile/" + newName;
        result = new String[]{newImgPath, url, newName};

        return result;
    }

    private class uploadPict extends AsyncTask<String, Void, Void>{

        ProgressDialog dialog = new ProgressDialog(NewSignUp3.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading...");
            this.dialog.dismiss();
        }

        @Override
        protected Void doInBackground(String... strings) {
            File imgLicense = new File(strings[0]);
            File imgIc = new File(strings[1]);

            try{
                int permission = ActivityCompat.checkSelfPermission(NewSignUp3.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            NewSignUp3.this, PERMISSIONS_STORAGE, 1
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

            }catch (Exception e){
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

    private class regis extends AsyncTask<String, Void, JSONObject>{

        ProgressDialog dialog = new ProgressDialog(NewSignUp3.this);
        String regisUrl="http://new.entongproject.com/api/customer/signup";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Submitting...");
            this.dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            JSONParser jparser = new JSONParser();
            JSONObject response = jparser.getJsonFromUrl(regisUrl, strings[0]);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            try{
                String result = response.getString("result");

                Toast.makeText(getBaseContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                if(result.equals("success")){
                    //if(stats.equals(null)){
                    Toast.makeText(getBaseContext(), "Please verify your account in your email", Toast.LENGTH_SHORT).show();
                    Intent redirect = new Intent(NewSignUp3.this, LoginActivity.class);
                    startActivity(redirect);
                    //}
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.dialog.dismiss();
        }
    }
}
