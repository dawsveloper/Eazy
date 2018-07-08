package com.eazy.firda.eazy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.utils.ImageFilePath;
import com.eazy.firda.eazy.utils.Misc;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import it.sauronsoftware.ftp4j.FTPClient;

public class NoteOwnerPhotos extends AppCompatActivity {

    ImageView imgDoc, chgDoc;
    Intent intent, intentDoc;
    Bundle extras;
    Bitmap bitmap, bmp;
    BitmapDrawable drawable;
    ByteArrayOutputStream bos;
    byte[] bb;
    FTPClient client;

    String[] note;
    String carId, noteId, note_path, note_url, note_name;
    String imgString, newImgPath, imgPath, currName, newName, extension, photoId;
    int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_owner_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        extras = intent.getExtras();
        carId = extras.getString("car_id");
        client = new FTPClient();

        imgDoc = findViewById(R.id.imgDoc);
        chgDoc = findViewById(R.id.chgDoc);

        call c = new call();
        c.execute(carId);

        chgDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent note = new Intent(NoteOwnerPhotos.this, DetailCarOwner.class);
        Bundle extras = new Bundle();
        extras.putString("car_id", carId);
        note.putExtras(extras);
        startActivity(note);
        finish();
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                //"Capture photo from camera"
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
                    imgDoc.setImageBitmap(bitmap);
//                    uploadImage(contentURI);
                }catch (IOException e){
                    e.printStackTrace();
                }

                intentDoc = data;
                drawable = (BitmapDrawable)imgDoc.getDrawable();
                note = copyfile(data, drawable);
                note_path = note[0];
                note_url = note[1];
                note_name = note[2];

                update up = new update();
                up.execute(note_path, note_url, note_name, noteId, carId);
            }
        }
        else if(requestCode == CAMERA){
            bitmap = (Bitmap)data.getExtras().get("data");
            imgDoc.setImageBitmap(bitmap);
        }
    }

    private class call extends AsyncTask<String, Void, JSONObject>{

        String call_url = "http://new.entongproject.com/api/provider/rental/api_note_car";
        private final ProgressDialog dialog = new ProgressDialog(NoteOwnerPhotos.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading");
            this.dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONParser jParser = new JSONParser();

            JSONObject response = jParser.getJsonFromUrl(call_url, strings[0]);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                String result = jsonObject.getString("result");

                if(result.equals("success")){
                    note_url = jsonObject.getString("url");
                    noteId = jsonObject.getString("id");
                }
                else{
                    note_url = "http://new.entongproject.com/images/profile/doc_default.png";
                    noteId = "nothing";
                }

                Ion.with(imgDoc)
                        .placeholder(R.drawable.document)
                        .error(R.drawable.ic_close_black_18dp)
                        .load(note_url);

            }catch (JSONException e){
                e.printStackTrace();
            }
            this.dialog.dismiss();
        }
    }

    private String[] copyfile(Intent data, BitmapDrawable drawable){
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
        newName = "note_" + photoId + "_" + timestamp.getTime() + "." + extension;
        newImgPath = imgPath.replace(currName, newName);
        Log.v("new image path", newImgPath);
        Misc.copyFile(imgPath, newImgPath);

        url = "http://new.entongproject.com/images/car/" + newName;
        result = new String[]{newImgPath, url, newName};

        return result;
    }

    private class update extends AsyncTask<String, Void, JSONObject>{

        private final ProgressDialog dialog = new ProgressDialog(NoteOwnerPhotos.this);
        String update_url = "http://new.entongproject.com/api/provider/rental/update_note_car";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading");
            this.dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONParser jParser = new JSONParser();
            JSONObject response = null;

            File doc  = new File(strings[0]);
            try{
                int permission = ActivityCompat.checkSelfPermission(NoteOwnerPhotos.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            NoteOwnerPhotos.this, PERMISSIONS_STORAGE, 1
                    );
                }
                client.connect(getResources().getString(R.string.ftp_server),21);
                client.login(getResources().getString(R.string.sftp_user), getResources().getString(R.string.sftp_password));
                client.setType(FTPClient.TYPE_BINARY);
                client.changeDirectory("/public_html/angkot/public/images/car");

                Misc.MyTransferListener tfl = new Misc().new MyTransferListener();
                client.upload(doc, tfl);
                client.disconnect(true);
                boolean del = doc.delete();
                if(del){Log.v("status", "image deleted");}
                else{Log.v("status", "image can't delete");}

                try{

                    String data = strings[1] + "|" + strings[2] + "|" + strings[3] + "|" + strings[4];
                    Log.v("data", data);
                    response = jParser.getJsonFromUrl(update_url, data);
                    Toast.makeText(NoteOwnerPhotos.this, response.getString("result"), Toast.LENGTH_SHORT).show();

                    Ion.with(imgDoc)
                            .placeholder(R.drawable.document)
                            .error(R.drawable.ic_close_black_18dp)
                            .load(strings[1]);

                }catch (JSONException je){
                    je.printStackTrace();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            this.dialog.dismiss();
        }
    }
}
