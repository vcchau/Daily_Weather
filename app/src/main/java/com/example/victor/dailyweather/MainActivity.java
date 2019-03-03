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
                weatherArrayList = parseTwelveHourJSONs(weatherSearchResults);
            }
            super.onPostExecute(weatherSearchResults);
        }
    }

    // Currently set to handle request from single day forecast, need to update for 12 hour forecasts
    // Which is comprised of 12 separate json objects
    private ArrayList<Weather> parseTwelveHourJSONs(String weatherSearchResults) {
        if (weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if (weatherSearchResults != null) {
            try {
                Log.i(TAG, "Type of weatherSearchResults is " + weatherSearchResults.getClass().getSimpleName());
                Log.i(TAG, "weatherSearchResults is " + weatherSearchResults);

//                JSONObject rootObject = new JSONObject(weatherSearchResults);

                // Create array of 12 single-hour JSONs
                JSONArray jsonArray = new JSONArray(weatherSearchResults);

                // Grab data from each individual JSON
                for (int j = 0; j < jsonArray.length(); ++j) {
                    JSONObject hourObject = jsonArray.getJSONObject(j);

                    Weather hourlyWeather = new Weather();
                    
                    String date = hourObject.getString("DateTime");
                    hourlyWeather.setDate(date);

                    // Set the current temperature
                    JSONObject temperatureObj = hourObject.getJSONObject("Temperature");
                    String currentTemp = temperatureObj.getString("Value");
                    hourlyWeather.setCurrentTemp(currentTemp);

                    // Set the real feel temperature
                    JSONObject realFeelObj = hourObject.getJSONObject("RealFeelTemperature");
                    String realFeelTemp = realFeelObj.getString("Value");
                    hourlyWeather.setCurrentRealFeelTemp(realFeelTemp);

                    // Set the precipitation chance
                    String chanceOfPrecipitation = hourObject.getString("PrecipitationProbability");
                    hourlyWeather.setChanceOfPrecipitation(chanceOfPrecipitation);

                    // Set the wind speeds and direction
                    JSONObject windObj = hourObject.getJSONObject("Wind");
                    String windSpeeds = windObj.getJSONObject("Speed").getString("Value");
                    String windDirection = windObj.getJSONObject("Direction").getString("English");
                    hourlyWeather.setWindSpeed(windSpeeds);
                    hourlyWeather.setWindDirection(windDirection);

                    // Set the relative humidity
                    String relativeHumidity = hourObject.getString("RelativeHumidity");
                    hourlyWeather.setRelativeHumidity(relativeHumidity);

                    // Set the link
                    String link = hourObject.getString("Link");
                    hourlyWeather.setLink(link);

                    // Logging info for debugging purposes
//                    Log.i(TAG, "Min temp: " + hourlyWeather.getMinTemp() + " Max temp: " + hourlyWeather.getMaxTemp());
//                    Log.i(TAG, "Min RealFeel: " + hourlyWeather.getMinTempRealFeel() + " Max RealFeel: " + hourlyWeather.getMaxTempRealFeel());
//                    Log.i(TAG, "Precip chance: " + hourlyWeather.getChanceOfPrecipitation() + "%");
//                    Log.i(TAG, "Wind speeds: " + hourlyWeather.getWindSpeed() + "mph coming from the " + hourlyWeather.getWindDirection());

                    weatherArrayList.add(hourlyWeather);
                    
//                    Log.i(TAG, "Date from " + i + date);
                // Retrieve the JSON Array called 'DailyForecasts'
//                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                // Create our weather objects and populate with data from the JSON object
                // Eventually add in for loop to parse data from the 12 hour responses
                // Can leave out now since we're only using 1-day forecast
                // may eventually add in a 5-day forecast option

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
