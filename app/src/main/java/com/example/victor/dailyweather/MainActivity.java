package com.example.victor.dailyweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);

        URL singleDayWeatherURL = NetworkUtils.buildUrlForWeatherSingleDay();
        new FetchWeatherDetails().execute(singleDayWeatherURL);
        Log.i(TAG, "onCreate singleDayWeatherURL: " + singleDayWeatherURL);
    }

    // Class to fetch the details from our URL in the background
    private class FetchWeatherDetails extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL singleDayWeatherURL = urls[0];
            String weatherSearchResults = null;

            try {
                weatherSearchResults = NetworkUtils.getResponse(singleDayWeatherURL);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "in background results are: " + weatherSearchResults);
            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {
            if (weatherSearchResults != null && !weatherSearchResults.equals("")) {
                weatherArrayList = parseSingleDayJSON(weatherSearchResults);
            }
            super.onPostExecute(weatherSearchResults);
        }
    }

    // Currently set to handle request from single day forecast, need to update for 12 hour forecasts
    // Which is comprised of 12 separate json objects
    private ArrayList<Weather> parseSingleDayJSON(String weatherSearchResults) {
        if (weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if (weatherSearchResults != null) {
            try {
                Log.i(TAG, "Type of weatherSearchResults is " + weatherSearchResults.getClass().getSimpleName());
                Log.i(TAG, "weatherSearchResults is " + weatherSearchResults);

                JSONObject rootObject = new JSONObject(weatherSearchResults);

                // Retrieve the JSON Array called 'DailyForecasts'
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                // Create our weather objects and populate with data from the JSON object
                // Eventually add in for loop to parse data from the 12 hour responses
                // Can leave out now since we're only using 1-day forecast
                // may eventually add in a 5-day forecast option
                for (int i = 0; i < results.length(); ++i) {
                    Weather weather = new Weather();

                    JSONObject resultsObj = results.getJSONObject(i);

                    String date = resultsObj.getString("Date");
                    weather.setDate(date);

                    // Grab the temperature object so we can grab min and max temps
                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    String minTemp = temperatureObj.getJSONObject("Minimum").getString("Value");
                    String maxTemp = temperatureObj.getJSONObject("Maximum").getString("Value");
                    weather.setMinTemp(minTemp);
                    weather.setMaxTemp(maxTemp);

                    // Grab the RealFeelTemperature object to grab min and max real feel temps
                    JSONObject realFeelObj = resultsObj.getJSONObject("RealFeelTemperature");
                    String minRealFeel = realFeelObj.getJSONObject("Minimum").getString("Value");
                    String maxRealFeel = realFeelObj.getJSONObject("Maximum").getString("Value");
                    weather.setMinTempRealFeel(minRealFeel);
                    weather.setMaxTempRealFeel(maxRealFeel);

                    // Grab data for precip chance, wind speeds and wind direction
                    JSONObject dayObj = resultsObj.getJSONObject("Day");
                    String chanceOfPrecipitation = dayObj.getString("PrecipitationProbability");
                    JSONObject windObj = dayObj.getJSONObject("Wind");
                    String windSpeeds = windObj.getJSONObject("Speed").getString("Value");
                    String windDirection = windObj.getJSONObject("Direction").getString("English");
                    weather.setChanceOfPrecipitation(chanceOfPrecipitation);
                    weather.setWindSpeed(windSpeeds);
                    weather.setWindDirection(windDirection);

                    // Logging info for debugging purposes
                    Log.i(TAG, "Min temp: " + weather.getMinTemp() + " Max temp: " + weather.getMaxTemp());
                    Log.i(TAG, "Min RealFeel: " + weather.getMinTempRealFeel() + " Max RealFeel: " + weather.getMaxTempRealFeel());
                    Log.i(TAG, "Precip chance: " + weather.getChanceOfPrecipitation() + "%");
                    Log.i(TAG, "Wind speeds: " + weather.getWindSpeed() + "mph coming from the " + weather.getWindDirection());

                    weatherArrayList.add(weather);
//                    Log.i(TAG, "Date from " + i + date);
                }

                if (weatherArrayList != null) {
                    WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherArrayList);
                    listView.setAdapter(weatherAdapter);
                }

                return weatherArrayList;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Should never reach here
        return null;
    }
}
