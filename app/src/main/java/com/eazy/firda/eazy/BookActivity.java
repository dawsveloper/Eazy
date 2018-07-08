package com.eazy.firda.eazy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eazy.firda.eazy.Tasks.JSONParser;
import com.eazy.firda.eazy.utils.GPSTracker;
import com.eazy.firda.eazy.utils.ImageFilePath;
import com.eazy.firda.eazy.utils.Misc;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import it.sauronsoftware.ftp4j.FTPClient;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class BookActivity extends AppCompatActivity {

    String submit_booking= "http://new.entongproject.com/api/customer/book_car";

    TextView bookName, bookPhone, bookMail, bookDateStart,
            bookDateEnd, bookDuration, bookPickUp, bookReturn, bookChgDate, bookChgLocation, totalWallet1, totalWallet2, totalCash;
    Button submit;
    RadioGroup groupPrice;
    RadioGroupPlus groupTotal;
    RadioButton daily, weekly, monthly, cash, wallet;
    LinearLayout layTotal;

    SharedPreferences sp;
    SharedPreferences.Editor edit;
    SimpleDateFormat day_name, day_number, month_name, date, time;
    String search_dateStart, search_dateEnd, price_type, dateStart, dateEnd, locationPickup, locationReturn, member_id, book, car_id, login_as, booking_number;
    String total_type = "";
    String memberUrl = "http://new.entongproject.com/api/customer/call/profile";
    String[] dataLicense, dataIc;
    String licenseType, licenseResPath, icType, icResPath;
    String uploadPath1, uploadPath2, url1, url2, name1, name2;
    FTPClient client;
    String[] ds, de;
    int total = 0;
    int interval, priceDay, priceWeek, priceMonth, tWallet, tCash, point;

    ImageView imgLicense, imgPassport, delLicense, delPassport;
    RelativeLayout layLicense, layPassport;
    LinearLayout layUpload;

    int GALLERY = 1, CAMERA = 2;

    Bitmap bitmap;
    String type = "";

    Calendar c;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.getSupportActionBar().setTitle("");

        bookName = (TextView)findViewById(R.id.book_name);
        bookMail = (TextView)findViewById(R.id.book_email);
        bookPhone = (TextView)findViewById(R.id.book_phone);
        layUpload = findViewById(R.id.layUpload);
        imgLicense = findViewById(R.id.imgLicense);
        imgPassport = findViewById(R.id.imgPassport);
        layLicense = findViewById(R.id.layLicense);
        layPassport = findViewById(R.id.layPassport);
        delLicense = findViewById(R.id.delLicense);
        delPassport = findViewById(R.id.delPassport);
        groupPrice = (RadioGroup)findViewById(R.id.group_payment);
        daily = (RadioButton)findViewById(R.id.book_payday);
        weekly = (RadioButton)findViewById(R.id.book_payweek);
        monthly = (RadioButton)findViewById(R.id.book_paymonth);
        bookDuration = (TextView)findViewById(R.id.book_duration);
        bookDateStart = (TextView)findViewById(R.id.book_dateStart);
        bookDateEnd = (TextView)findViewById(R.id.book_dateEnd);
        bookChgDate = (TextView)findViewById(R.id.book_chgDate);
        bookPickUp = (TextView)findViewById(R.id.book_pickupLocation);
        bookReturn = (TextView)findViewById(R.id.book_returnLocation);
        bookChgLocation = (TextView)findViewById(R.id.book_chgLocation);
        layTotal = (LinearLayout)findViewById(R.id.lay_total);
        groupTotal = (RadioGroupPlus) findViewById(R.id.group_total);
        cash = (RadioButton)findViewById(R.id.total_cash);
        wallet = (RadioButton)findViewById(R.id.total_wallet);
        totalWallet1 = (TextView)findViewById(R.id.tag_total_wallet1);
        totalWallet2 = (TextView)findViewById(R.id.tag_total_wallet2);
        totalCash = (TextView)findViewById(R.id.tag_total_cash);
        submit = (Button)findViewById(R.id.btn_book);

        layTotal.setVisibility(View.GONE);

        month_name = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        day_name = new SimpleDateFormat("EEE", Locale.ENGLISH);
        day_number = new SimpleDateFormat("dd", Locale.ENGLISH);
        date = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        time = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        client = new FTPClient();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        member_id = sp.getString("user_id", null);
        login_as = sp.getString("login_as", null);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        car_id = extras.getString("car_id");
        priceDay = extras.getInt("price_daily");
        priceWeek = extras.getInt("price_weekly");
        priceMonth = extras.getInt("price_monthly");

        search_dateStart = sp.getString("search_dateStart", null);
        search_dateEnd = sp.getString("search_dateEnd", null);

        Date date1 = new Date();
        Date date2 = new Date();

        if(login_as.equals("member")){
           layUpload.setVisibility(View.GONE);
        }
        else if(login_as.equals("guest")){
            layUpload.setVisibility(View.VISIBLE);
        }

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

        if(search_dateStart != null || search_dateEnd != null){
            ds = search_dateStart.split("/");
            de = search_dateEnd.split("/");
            try{
                date1 = date.parse(sp.getString("search_dateStart", null));
                date2 = date.parse(sp.getString("search_dateStart", null));
            }catch(ParseException e){
                e.printStackTrace();
            }
            dateStart = day_name.format(date1) + ", " + ds[1] + " " + month_name.format(date1) + ", " + sp.getString("search_timeStart", null);
            dateEnd = day_name.format(date2) + ", " + de[1] + " " + month_name.format(date2) + ", " + sp.getString("search_timeEnd", null);
            interval = (int)sp.getLong("search_timeInterval", 0);
            locationPickup = sp.getString("location_pickup", null);
            locationReturn = sp.getString("location_return", null);
        }else{
            GPSTracker gps;
            Double lat = 0.0, lng = 0.0;
            String saveDateStart, saveDateEnd, saveTimeStart, saveTimeEnd, newMonths, newDays, newMonthe, newDaye;
            int year, month, dayofmonth, hour, min;
            int year2, month2, dayofmonth2, hour2, min2;
            Date dt, dt2;

            gps = new GPSTracker(getBaseContext(), BookActivity.this);

            if(gps.canGetLocation()){
                lng = gps.getLongitude();
                lat = gps.getLatitude();
            }
            else{
                gps.showSettingsAlert();
            }

            c = Calendar.getInstance(TimeZone.getDefault());
            dt = c.getTime();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            dayofmonth = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            min = c.get(Calendar.MINUTE);
            dateStart = day_name.format(dt) + ", " + day_number.format(dt) + " " + month_name.format(dt)
                    + ", " + time.format(dt);

            if((month+1) < 10){
                newMonths = "0" + (month+1);
            }else{
                newMonths = ""+ (month+1);
            }

            if(dayofmonth < 10){
                newDays = "0" + dayofmonth;
            }
            else{
                newDays = "" + dayofmonth;
            }

            saveDateStart = newMonths  + "/" + newDays + "/" + String.valueOf(year);
            saveTimeStart = time.format(dt);

            c.add(dayofmonth, 3);
            dt2 = c.getTime();
            year2 = c.get(Calendar.YEAR);
            month2 = c.get(Calendar.MONTH);
            dayofmonth2 = c.get(Calendar.DAY_OF_MONTH);
            hour2 = c.get(Calendar.HOUR_OF_DAY);
            min2 = c.get(Calendar.MINUTE);
            dateEnd = day_name.format(dt2) + ", " + day_number.format(dt2) + " " + month_name.format(dt2)
                    + ", " + time.format(dt2);
            interval = 3;

            if((month2+1) < 10){
                newMonthe = "0" + (month2+1);
            }else{
                newMonthe = ""+ (month2+1);
            }

            if(dayofmonth2 < 10){
                newDaye = "0" + dayofmonth2;
            }
            else{
                newDaye = "" + dayofmonth2;
            }

            saveDateEnd = newMonthe  + "/" + newDaye + "/" + String.valueOf(year2);
            saveTimeEnd = time.format(dt2);

            edit.putString("location_pickup", "nearby");
            edit.putString("location_return", "nearby");
            edit.putLong("user_lat", Double.doubleToRawLongBits(lat));
            edit.putLong("user_lang", Double.doubleToRawLongBits(lng));
            edit.putString("search_dateStart", saveDateStart);
            edit.putString("search_timeStart", saveTimeStart);
            edit.putString("search_dateEnd", saveDateEnd);
            edit.putString("search_timeEnd", saveTimeEnd);
            edit.putLong("search_timeInterval", interval);
            edit.putString("search_producer", "All");
            edit.putInt("search_type1", 0);
            edit.putInt("search_type2", 0);
            edit.putInt("search_type3", 0);
            edit.putInt("search_type4", 0);
            edit.putString("search_transmission", "Both");
            edit.putInt("search_minVal", 0);
            edit.putInt("search_maxVal", 1000000);
        }
        
        if(member_id != null){
            detailMember dm = new detailMember();
            dm.execute();
        }

        bookDateStart.setText(dateStart);
        bookDateEnd.setText(dateEnd);
        bookDuration.setText(String.valueOf(interval));
        bookPickUp.setText(locationPickup);
        bookReturn.setText(locationReturn);

        bookDuration.setEnabled(false);

        if(interval % 7 == 0){
            weekly.setVisibility(View.VISIBLE);
        }else{
            weekly.setVisibility(View.GONE);
        }

        if(interval % 30 == 0){
            monthly.setVisibility(View.VISIBLE);
        }else{
            monthly.setVisibility(View.GONE);
        }

        point = sp.getInt("epoint", 0);

        groupPrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.book_payday:
                        price_type = "Daily";
                        tCash = interval * priceDay;
                        tWallet = tCash - (int)((tCash * 0.1));
                        break;
                    case R.id.book_payweek:
                        price_type = "Weekly";
                        tCash = interval * priceWeek;
                        tWallet = tCash - (int)((tCash * 0.1));

                        break;
                    case R.id.book_paymonth:
                        price_type = "Monthly";
                        tCash = interval * priceMonth;
                        tWallet = tCash - (int)((tCash * 0.1));

                        break;
                }

                DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
                symbols.setGroupingSeparator(',');
                DecimalFormat formatter = new DecimalFormat("###,###", symbols);

                layTotal.setVisibility(View.VISIBLE);
                totalCash.setText("RM " + formatter.format(tCash));
                totalWallet1.setText("RM " + formatter.format(tCash));
                totalWallet1.setPaintFlags(totalWallet1.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                totalWallet2.setText("RM " + formatter.format(tWallet));

                if(login_as.equals("member")){
                    if(point < tWallet){
                        wallet.setEnabled(false);
                    }
                    else{
                        wallet.setEnabled(true);
                    }
                }
                else if(login_as.equals("guest")){
                    wallet.setEnabled(false);
                }

                Log.v("price_type", price_type);
            }
        });

       bookChgDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent chgDate = new Intent(BookActivity.this, DateActivity.class);
               startActivity(chgDate);
           }
       });

       bookChgLocation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });

       groupTotal.setOnCheckedChangeListener(new RadioGroupPlus.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroupPlus radioGroupPlus, int i) {
               switch(i){
                   case R.id.total_wallet:
                       total_type = "Wallet";
                       total = tWallet;
                       break;
                   case R.id.total_cash:
                       total_type = "Cash";
                       total = tCash;
                       break;
               }
           }
       });

       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(bookName.getText().length() == 0 || bookMail.getText().length() == 0 || bookPhone.getText().length() == 0){
                   Toast.makeText(getApplicationContext(), "Data is not completed", Toast.LENGTH_SHORT).show();
               }else{
                   if(total_type == ""){
                       Toast.makeText(getApplicationContext(), "Data is not completed", Toast.LENGTH_SHORT).show();
                   }
                   else{
                       if(total == 0){
                           Toast.makeText(getApplicationContext(), "Data is not completed", Toast.LENGTH_SHORT).show();
                       }
                       else{
                           if(login_as.equals("guest")){
                               if(imgLicense.getDrawable() == null || imgPassport.getDrawable() == null){
                                   Toast.makeText(getApplicationContext(), "Please choose license's photo", Toast.LENGTH_SHORT).show();
                               }
                               else{
                                   dataLicense = copyfile(licenseResPath, "license");
                                   uploadPath1 = dataLicense[0];
                                   url1 = dataLicense[1];
                                   name1 = dataLicense[2];

                                   if(licenseType.equals("camera")){
                                       File tmp = new File(licenseResPath);
                                       boolean delLicense = tmp.delete();
                                       if(delLicense)Log.v("source file:", "deleted");
                                   }

                                   dataIc = copyfile(icResPath, "passport");
                                   uploadPath2 = dataIc[0];
                                   url2 = dataIc[1];
                                   name2 = dataIc[2];

                                   if(icType.equals("camera")){
                                       File tmp = new File(icResPath);
                                       boolean delLicense = tmp.delete();
                                       if(delLicense)Log.v("source file:", "deleted");
                                   }

                                   uploadPict up = new uploadPict();
                                   up.execute(uploadPath1, uploadPath2);

                                   booking b = new booking();
                                   b.execute();
                               }
                           }
                           else{
                               url1 = "";
                               name1 = "";

                               url2 = "";
                               name2 = "";

                               booking b = new booking();
                               b.execute();
                           }
                       }
                   }
               }
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
                        licenseType = "gallery";
                        licenseResPath = ImageFilePath.getPath(this ,data.getData());
                    }
                    else if(type.equals("passport")){
                        imgPassport.setImageBitmap(bitmap);
                        delPassport.setVisibility(View.VISIBLE);
                        icType = "gallery";
                        icResPath = ImageFilePath.getPath(this ,data.getData());
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
                licenseType = "camera";
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String tmp = "/" + timestamp.getTime() + ".jpeg";
                File outFile = new File(Environment.getExternalStorageDirectory(), tmp);
                try{
                    FileOutputStream fos = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    licenseResPath = Environment.getExternalStorageDirectory() + tmp;
                    Log.v("photo path", licenseResPath);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e2){
                    e2.printStackTrace();
                }
            }
            else if(type.equals("passport")){
                imgPassport.setImageBitmap(bitmap);
                delPassport.setVisibility(View.VISIBLE);
                icType = "camera";
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String tmp = "/" + timestamp.getTime() + ".jpeg";
                File outFile = new File(Environment.getExternalStorageDirectory(), tmp);
                try{
                    FileOutputStream fos = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    icResPath = Environment.getExternalStorageDirectory() + tmp;
                    Log.v("photo path", icResPath);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e2){
                    e2.printStackTrace();
                }
            }
        }
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

    private class detailMember extends AsyncTask<Void, Void, JSONObject>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(BookActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            JSONParser jsonParser = new JSONParser();
            JSONObject response = jsonParser.getJsonFromUrl(memberUrl, member_id);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try{
                String result = jsonObject.getString("result");

                if(result.equals("success")){
                    JSONObject u = jsonObject.getJSONObject("member");
                    bookName.setText(u.getString("name"));
                    bookPhone.setText(u.getString("phone"));
                    bookMail.setText(u.getString("email"));
                    bookName.setEnabled(false);
                    bookPhone.setEnabled(false);
                    bookMail.setEnabled(false);
                }
                else{
                    bookName.setText("");
                    bookPhone.setText("");
                    bookMail.setText("");
                    bookName.setEnabled(true);
                    bookPhone.setEnabled(true);
                    bookMail.setEnabled(true);
                    Log.v("result", jsonObject.getString("message"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }

    private class uploadPict extends AsyncTask<String, Void, Void>{

        ProgressDialog dialog = new ProgressDialog(BookActivity.this);

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
                int permission = ActivityCompat.checkSelfPermission(BookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            BookActivity.this, PERMISSIONS_STORAGE, 1
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

    private class booking extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(BookActivity.this);
            progressDialog.setMessage("Save Boooking ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

            book = login_as + "|" + car_id + "|" + member_id
                    + "|" + bookName.getText().toString()
                    + "|" + bookPhone.getText().toString()
                    + "|" + bookMail.getText().toString()
                    + "|" + price_type + "|" + interval
                    + "|" + sp.getString("search_dateStart", null) + " "
                    + sp.getString("search_timeStart", null)
                    + "|" + sp.getString("search_dateEnd", null) + " "
                    + sp.getString("search_timeEnd", null)
                    + "|" + total_type
                    + "|" + total
                    + "|" + url1 + "|" + name1
                    + "|" + url2 + "|" + name2;
        }

        @Override
        protected JSONObject doInBackground(String... data){

            JSONParser jParser = new JSONParser();

            Log.v("booking_url", submit_booking);
            Log.v("booking_data", book);
            //Log.v("screen_status", Integer.toString(screen_status2));

            JSONObject mybook = jParser.getJsonFromUrl(submit_booking, book);

            return mybook;
        }

        @Override
        protected void onPostExecute(JSONObject mybook){
            progressDialog.dismiss();
            String result;

            try{
                result = mybook.getString("result");
                Log.v("JSON", mybook.toString());
                Log.v("result", result);

                if(result.equals("success")){
                    booking_number = mybook.getString("booking_number");

                    AlertDialog.Builder builder = new AlertDialog.Builder(BookActivity.this);
                    builder.setTitle("Booking Success");
                    builder.setMessage("This is your booking number \n" + booking_number);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(member_id == null){
                                Intent redirect = new Intent(BookActivity.this, GuestMain.class);
                                startActivity(redirect);
                            }
                            else{
                                Intent redirect = new Intent(BookActivity.this, HomeActivity.class);
                                startActivity(redirect);
                            }
                            finish();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    Toast.makeText(getBaseContext(), "Cannot submit data", Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    private void showBookNumber(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getBaseContext());

        alertDialogBuilder.setTitle("Your booking is proceed");

        alertDialogBuilder
                .setMessage(booking_number)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        if(member_id == null){
                            Intent redirect = new Intent(BookActivity.this, GuestMain.class);
                            startActivity(redirect);
                        }
                        else{
                            Intent redirect = new Intent(BookActivity.this, HomeActivity.class);
                            startActivity(redirect);
                        }
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
