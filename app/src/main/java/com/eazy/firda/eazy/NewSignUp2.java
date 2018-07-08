package com.eazy.firda.eazy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.eazy.firda.eazy.utils.ImageFilePath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class NewSignUp2 extends AppCompatActivity {

    Button next;
    ImageView imgLicense, delLicense;
    RelativeLayout layLicense;
    CoordinatorLayout coordinatorLayout;
    Snackbar sb;

    Bitmap bitmap;
    BitmapDrawable drawable;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    int GALLERY = 1, CAMERA = 2;
    String photoType, LicensePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sign_up2);
        coordinatorLayout = findViewById(R.id.coordinator);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        layLicense = findViewById(R.id.layLicense);
        imgLicense = findViewById(R.id.imgLicense);
        delLicense = findViewById(R.id.delLicense);

        layLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        next = findViewById(R.id.btn_next);
        sb = Snackbar.make(coordinatorLayout, "Please choose photo for upload", Snackbar.LENGTH_LONG);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgLicense.getDrawable() != null){
                    startActivity(new Intent(NewSignUp2.this, NewSignUp3.class));
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

                    imgLicense.setImageBitmap(bitmap);
                    delLicense.setVisibility(View.VISIBLE);
                    drawable = (BitmapDrawable)imgLicense.getDrawable();
                    photoType = "gallery";
                    LicensePath = ImageFilePath.getPath(this ,data.getData());
//                    uploadImage(contentURI);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == CAMERA){
            bitmap = (Bitmap)data.getExtras().get("data");
            imgLicense.setImageBitmap(bitmap);
            delLicense.setVisibility(View.VISIBLE);
            photoType = "camera";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String tmp = "/" + timestamp.getTime() + ".jpeg";
            File outFile = new File(Environment.getExternalStorageDirectory(), tmp);
            try{
                FileOutputStream fos = new FileOutputStream(outFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                LicensePath = Environment.getExternalStorageDirectory() + tmp;
                Log.v("photo path", LicensePath);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }
        editor.putString("pathLicense", LicensePath);
        editor.putString("typeLicense", photoType);
        editor.apply();
    }
}
