package com.yeslabapps.friendb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.yeslabapps.friendb.R;

public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(OpeningActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 500);



    }
}