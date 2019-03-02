package com.example.victor.dailyweather;

import android.net.Network;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL weatherURL = NetworkUtils.buildUrlForWeatherTwelveFourHour();
        Log.i(TAG, "onCreate weatherURL: " + weatherURL);
    }
}
