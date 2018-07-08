package com.eazy.firda.eazy;

import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

public class filter_v2 extends AppCompatActivity implements AAH_FabulousFragment.Callbacks{

    FloatingActionButton fab;
    filter_frag dialogFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_v2);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        dialogFrag = filter_frag.newInstance();
        dialogFrag.setParentFab(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
    }

    @Override
    public void onResult(Object result) {
        Log.d("result on activity", result.toString());
        if(result.toString().equalsIgnoreCase("swiped_down")){

        }else{

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }

    }
}
