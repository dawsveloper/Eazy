package com.eazy.firda.eazy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewSignUp extends AppCompatActivity {

    Button next;
    EditText et_name, et_email, et_phone, et_pass;
    Snackbar sb;
    CoordinatorLayout coordinatorLayout;
    String name, email, phone, pass;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sign_up);
        coordinatorLayout = findViewById(R.id.coordinator);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_pass = findViewById(R.id.et_pass);

        next = findViewById(R.id.btn_next);
        sb = Snackbar.make(coordinatorLayout, "Please complete field", Snackbar.LENGTH_LONG);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_name.length() > 0 || et_email.length() > 0 || et_phone.length() > 0 || et_pass.length() > 0){
                    name = et_name.getText().toString();
                    email = et_email.getText().toString();
                    phone = et_phone.getText().toString();
                    pass = et_pass.getText().toString();

                    editor.putString("reg_name", name);
                    editor.putString("reg_email", email);
                    editor.putString("reg_phone", phone);
                    editor.putString("reg_pass", pass);
                    editor.apply();

                    Intent next = new Intent(NewSignUp.this, NewSignUp2.class);
                    startActivity(next);
                }
                else{
                    sb.show();
                }
            }
        });
    }

    private void redirect(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Data is not save yet");

        alertDialogBuilder
                .setMessage("Your data will be lost. Continue?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        editor.remove("reg_name");
                        editor.remove("reg_email");
                        editor.remove("reg_phone");
                        editor.remove("reg_pass");
                        editor.apply();

                        startActivity(new Intent(NewSignUp.this, LoginActivity.class));
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // your code.
        redirect();
    }
}
