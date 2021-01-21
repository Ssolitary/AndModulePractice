package com.finddreams.modulepractice;

import android.content.Intent;
import android.os.Bundle;

import com.finddreams.module_home.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.home_activity);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
