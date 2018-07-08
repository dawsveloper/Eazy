package com.eazy.firda.eazy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.adapter.OwnerCarPhotosAdapter;
import com.eazy.firda.eazy.entity.LinePagerIndicatorDecoration;
import com.eazy.firda.eazy.models.Car;
import com.eazy.firda.eazy.utils.ImageFilePath;
import com.eazy.firda.eazy.utils.Misc;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.kingfisher.easyviewindicator.RecyclerViewIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;


public class CarOwnerPhotos extends AppCompatActivity {

    ImageView carPhoto, delPhoto;
    Button submit;
    RelativeLayout layPhoto, layNull;
    String imagePath, newImagePath, newName, photoId, imageURL, photoType, resultPath;
    int GALLERY = 1, CAMERA = 2;
    Bitmap bitmap;
    BitmapDrawable drawable;
    String carId, imageString, pict_path, pict_url, pict_name;
    String[] pict;
    Intent intent;
    Bundle extras;
    FTPClient client;

    RecyclerView rvPhotos;
    RecyclerView.LayoutManager layoutManager;
    List<Car> cars = new ArrayList<Car>();
    public static OwnerCarPhotosAdapter rAdapter;
    SnapHelper snapHelper = new PagerSnapHelper();
    RecyclerViewIndicator recyclerViewIndicator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_owner_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Car Pictures");

        intent = getIntent();
        extras = intent.getExtras();
        carId = extras.getString("car_id");

        recyclerViewIndicator = findViewById(R.id.circleIndicator);
        rvPhotos = findViewById(R.id.photos);
        rvPhotos.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(layoutManager);
        rAdapter = new OwnerCarPhotosAdapter(this, cars);
        rAdapter.notifyDataSetChanged();
        rvPhotos.setAdapter(rAdapter);
        recyclerViewIndicator.setRecyclerView(rvPhotos);
        snapHelper.attachToRecyclerView(rvPhotos);
        rvPhotos.addItemDecoration(new LinePagerIndicatorDecoration());

        callPict cp = new callPict();
        cp.execute(carId);
        rAdapter.notifyDataSetChanged();
        rvPhotos.setAdapter(rAdapter);

        carPhoto = findViewById(R.id.imgCar);
        delPhoto = findViewById(R.id.delPhoto);
        layPhoto = findViewById(R.id.layPhoto);
        layNull = findViewById(R.id.layNull);

        layPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        delPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carPhoto.setImageBitmap(null);
                delPhoto.setVisibility(View.GONE);
            }
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new FTPClient();
//                ChangePicTask change = new ChangePicTask();
//                change.execute();
                uploadPict up = new uploadPict();
                up.execute(pict_path, pict_url, pict_name, carId);
                callPict cp = new callPict();
                cp.execute(carId);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CarOwnerPhotos.this, DetailCarOwner.class);
        Bundle extras = new Bundle();
        extras.putString("car_id", carId);
        intent.putExtras(extras);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public class callPict extends AsyncTask<String, Void, JSONObject>{

        private final ProgressDialog dialog = new ProgressDialog(CarOwnerPhotos.this);
        String callUrl = "http://new.entongproject.com/api/provider/api_call_car_photos";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cars.clear();
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONParser jParser = new JSONParser();
            JSONObject response = jParser.getJsonFromUrl(callUrl, strings[0]);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                Log.v("json", jsonObject.toString());
                String result = jsonObject.getString("result");

                if(result.equals("success")){
                    layNull.setVisibility(View.GONE);
                    JSONArray mPhotos = jsonObject.getJSONArray("photos");
                    for(int i = 0, size = mPhotos.length(); i < size; i++){
                        JSONObject photo = mPhotos.getJSONObject(i);
                        String url = photo.getString("path");
                        String id = photo.getString("reference_id");
                        Car c = new Car(url, id);
                        cars.add(c);
                    }
                }
                else{
                    Log.e("response", result);
                    layNull.setVisibility(View.VISIBLE);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            rAdapter.notifyDataSetChanged();
            this.dialog.dismiss();
        }
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
                    carPhoto.setImageBitmap(bitmap);
                    drawable = (BitmapDrawable)carPhoto.getDrawable();
                    photoType = "gallery";
                    resultPath = ImageFilePath.getPath(this ,data.getData());
                }catch (IOException e){
                    e.printStackTrace();
                }

                drawable = (BitmapDrawable)carPhoto.getDrawable();

                if(photoType.equals("camera")){
                    File tmp = new File(resultPath);
                    boolean delResult = tmp.delete();
                    if(delResult)Log.v("source file:", "deleted");
                }
            }
        }
        else if(requestCode == CAMERA){
            bitmap = (Bitmap)data.getExtras().get("data");
            carPhoto.setImageBitmap(bitmap);
            delPhoto.setVisibility(View.VISIBLE);
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
        newName = "car_" + photoId + "_" + timestamp.getTime() + "." + extension;
        newImgPath = path.replace(currName, newName);
        Log.v("new image path", newImgPath);
        Misc.copyFile(path, newImgPath);

        url = "http://new.entongproject.com/images/car/" + newName;
        result = new String[]{newImgPath, url, newName};

        return result;
    }

    private String[] copyfile(Intent data, BitmapDrawable drawable){
        String[] result;
        String url, currName, extension, newName, newImgPath;
        Bitmap bmp;
        ByteArrayOutputStream bos;
        byte[] bb;

        bmp = drawable.getBitmap();
        bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bb = bos.toByteArray();
        imageString = Base64.encodeToString(bb, Base64.DEFAULT);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        imagePath = ImageFilePath.getPath(this ,data.getData());
        Log.v("old image path", imagePath);

        currName = imagePath.substring(imagePath.lastIndexOf("/")+1);
        extension = currName.substring(currName.lastIndexOf(".")+1);
        photoId = Misc.randomString(20);
        newName = "car_" + carId + "_" + photoId + "_" + timestamp.getTime() + "_" + "." + extension;
        newImagePath = imagePath.replace(currName, newName);
        Log.v("new image path", newImagePath);
        Misc.copyFile(imagePath, newImagePath);

        url = "http://new.entongproject.com/images/car/" + newName;
        result = new String[]{newImagePath, url, newName};

        return result;
    }

    private class uploadPict extends AsyncTask<String, Void, Void>{

        private final ProgressDialog dialog = new ProgressDialog(CarOwnerPhotos.this);
        String uploadUrl = "http://new.entongproject.com/api/provider/rental/api_upload_car";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                FTPClient client = new FTPClient();
                File img = new File(strings[0]);

                JSONParser jParser = new JSONParser();

                client.connect(getResources().getString(R.string.ftp_server),21);
                client.login(getResources().getString(R.string.sftp_user), getResources().getString(R.string.sftp_password));
                client.setType(FTPClient.TYPE_BINARY);
                client.changeDirectory("/public_html/angkot/public/images/car");

                Misc.MyTransferListener tfl = new Misc().new MyTransferListener();

                client.upload(img, tfl);
                imageURL = "http://new.entongproject.com/images/car/" + newName;
                boolean del = img.delete();
                if(del){Log.v("status", "image deleted");}
                else{Log.v("status", "image can't delete");}

                String data = strings[1] + "|" + strings[2] + "|" + strings[3];
                Log.v("data", data);
                JSONObject response = jParser.getJsonFromUrl(uploadUrl, data);
                Log.v("response", response.toString());
                try{
                    String result = response.getString("result");
                    if(result.equals("success")){
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

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
            carPhoto.setImageBitmap(null);
        }
    }

//    private class EPassportTask extends AsyncTask<String, Integer, String> {
//        String result = "";
//        URL url;
//        HttpURLConnection urlConnection = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showDialog(DIALOG_PROGRESS);
//        }
//
//        protected String doInBackground(String... urls) {
//            try {
//                url = new URL(urls[0]);
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//
//                InputStream in = urlConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(in);
//                int data = reader.read();
//
//                while (data != -1) {
//                    char current = (char) data;
//                    result += current;
//                    data = reader.read();
//                }
//
//                return result;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                JSONObject jsonDatas;
//                jsonDatas = new JSONObject(jsonObject.getString("1"));
//
//                if (lblEPassportType.getText().toString().equals("1")) {
//                    if (!jsonDatas.getString("ImgPassport").equals("null")) {
//                        String strPasporLoc = getResources().getString(R.string.db_server) + getResources().getString(R.string.content_dir_passport_pic) + jsonDatas.getString("ImgPassport");
//                        Picasso.with(getBaseContext()).load(strPasporLoc).into(imgPassportSample);
//                    }
//                } else {
//                    if (!jsonDatas.getString("ImgVisa").equals("null")) {
//                        String strVisaLoc = getResources().getString(R.string.db_server) + getResources().getString(R.string.content_dir_visa_pic) + jsonDatas.getString("ImgVisa");
//                        Picasso.with(getBaseContext()).load(strVisaLoc).into(imgPassportSample);
//                    }
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            } finally {
//                dismissDialog(DIALOG_PROGRESS);
//            }
//        }
//    }

    private class ChangePicTask extends AsyncTask<String, Integer, String> {
        private final ProgressDialog dialog = new ProgressDialog(CarOwnerPhotos.this);
        String result = "0";
        String uploadedFileName = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Uploading");
            this.dialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                JSch ssh = new JSch();
                Session session = ssh.getSession(getResources().getString(R.string.sftp_user), getResources().getString(R.string.ftp_server));
                // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword(getResources().getString(R.string.sftp_password));

                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();

                ChannelSftp sftp = (ChannelSftp) channel;

                String slctdDir = imageString;
                String upldFolder = "car";

                uploadedFileName = "car" + carId + ".jpg";

                sftp.put(slctdDir, getResources().getString(R.string.sftp_base_pic_dir) + upldFolder + "/" + uploadedFileName);
                sftp.chmod(0755, getResources().getString(R.string.sftp_base_pic_dir) + upldFolder + "/" + uploadedFileName);

                result = "1";

                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                System.out.println(e.getMessage().toString());
                e.printStackTrace();
            } catch (SftpException e) {
                System.out.println(e.getMessage().toString());
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            try {
//                if (MainActivity.lblNavUserType.getText().equals("2")) {
//                    staffFuncExt = "staff";
//                }
//
//                if (result.equals("0")) {
//                    Toast.makeText(CarOwnerPhotos.this, "Upload Error", Toast.LENGTH_SHORT).show();
//                } else {
//                    StringBuilder sb = new StringBuilder("");
//                    if (lblEPassportType.getText().toString().equals("1")) {
//                        sb.append(getResources().getString(R.string.db_server_pro) + "updateuserpassportpic" + staffFuncExt + "&user_id=" + MainActivity.lblNavUserId.getText().toString() + "&file_name=paspor/" + uploadedFileName);
//                    } else {
//                        sb.append(getResources().getString(R.string.db_server_pro) + "updateuservisapic" + staffFuncExt + "&user_id=" + MainActivity.lblNavUserId.getText().toString() + "&file_name=visa/" + uploadedFileName);
//                    }
////                    UpdatePicTask updatePicTask = new UpdatePicTask();
////                    updatePicTask.execute(sb.toString());
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            } finally {
//                this.dialog.dismiss();
//            }
            this.dialog.dismiss();
        }
    }

//    private class UpdatePicTask extends AsyncTask<String, Integer, String> {
//        private final ProgressDialog dialog = new ProgressDialog(CarOwnerPhotos.this);
//        String result = "";
//        URL url;
//        HttpURLConnection urlConnection = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            this.dialog.setMessage(getResources().getString(R.string.uploading));
//            this.dialog.show();
//        }
//
//        protected String doInBackground(String... urls) {
//            try {
//                url = new URL(urls[0]);
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//
//                InputStream in = urlConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(in);
//                int data = reader.read();
//
//                while (data != -1) {
//                    char current = (char) data;
//                    result += current;
//                    data = reader.read();
//                }
//
//                return result;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                Toast.makeText(EPassportActivity.this, getResources().getString(R.string.upload_picture_success), Toast.LENGTH_SHORT).show();
//                finish();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            } finally {
//                this.dialog.dismiss();
//            }
//        }
//    }
//
//    public String getRealPathFromURI(Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
//        @SuppressWarnings("deprecation")
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
}
