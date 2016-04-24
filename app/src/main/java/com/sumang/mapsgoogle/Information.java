package com.sumang.mapsgoogle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
    }
    public void abc(View view){
        startActivity(new Intent(Information.this, MapsActivity.class));
    }

    public void abc1(View view){
        startActivity(new Intent(Information.this, Info.class));
    }


    }

