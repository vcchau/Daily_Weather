package com.example.victor.dailyweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    final URL[] URLs = new URL[3];

    private android.support.v7.widget.Toolbar toolbar;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView date;
    private TextView currentTemp;
    private TextView humidity;
    private TextView minMaxTemp;
    private TextView precipChance;
    private TextView currentStats;
    private TextView summary;


//    private TextView daySummary;
//    private TextView nightSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up our toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize all of the textViews for the main UI
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        date = findViewById(R.id.date);
        currentTemp = findViewById(R.id.currentTemp);
        humidity = findViewById(R.id.humidity);
        minMaxTemp = findViewById(R.id.dailyMinMax);
        precipChance = findViewById(R.id.precipChance);
        currentStats = findViewById(R.id.currentStats);
        summary = findViewById(R.id.summary);

        URL twelveHourWeatherURL = NetworkUtils.buildUrlForWeatherTwelveHours();
        URL singleDayWeatherURL = NetworkUtils.buildUrlForWeatherOneDay();
        URL currentWeatherURL = NetworkUtils.buildUrlForCurrentWeather();
        URLs[0] = twelveHourWeatherURL;
        URLs[1] = singleDayWeatherURL;
        URLs[2] = currentWeatherURL;

        new FetchForecastDetails().execute(URLs[0], URLs[1], URLs[2]);


//        daySummary = findViewById(R.id.daySummary);
//        nightSummary = findViewById(R.id.nightSummary);

        // Build the API request URLs and handle them in an async task

        // Initialize recycler view
//        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, weatherArrayList);
//        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Log.i(TAG, "onCreate singleDayWeatherURL: " + twelveHourWeatherURL);
    }

    // Create our action bar buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Refresh the weather info when the user hits the refresh button
            case R.id.action_refreshWeather:
                // We use a new fetch details here because each 'execute' can only be executed once
                new FetchForecastDetails().execute(URLs[0], URLs[1], URLs[2]);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Class to fetch the details from our URL in the background
    private class FetchForecastDetails extends AsyncTask<URL, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(URL... urls) {
            URL twelveHourWeatherURL = urls[0];
            URL singleDayWeatherURL = urls[1];
            URL currentWeatherURL = urls[2];
            String twelveHourWeatherResults = null;
            String singleDayWeatherResults = null;
            String currentWeatherResults = null;
            ArrayList<String> params = new ArrayList<>();

            try {
                // Grab the responses from the API request URLs
                twelveHourWeatherResults = NetworkUtils.getResponse(twelveHourWeatherURL);
                singleDayWeatherResults = NetworkUtils.getResponse(singleDayWeatherURL);
                currentWeatherResults = NetworkUtils.getResponse(currentWeatherURL);

                // Add the JSON strings to the params list
                params.add(twelveHourWeatherResults);
                params.add(singleDayWeatherResults);
                params.add(currentWeatherResults);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
//            Log.i(TAG, "in background results are: " + twelveHourWeatherResults)

//            updateUI(singleDayWeatherResults, currentWeatherResults); *** moved to onPostExecute ***
            return params;
        }

        @Override
        protected void onPostExecute(ArrayList<String> weatherJSONs) {
            String twelveHourJSON = weatherJSONs.get(0);
            final String singleDayJSON = weatherJSONs.get(1);
            final String currentWeatherJSON = weatherJSONs.get(2);

            // We reached an unknown error
            // Only error we run into right now is running out of API requests; used for debugging/logging
            if (twelveHourJSON.equals("error") || singleDayJSON.equals("error") || currentWeatherJSON.equals("error")) {
                // Updating the UI needs to be ran in the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "You have run out of API requests for today.",
                                        Toast.LENGTH_LONG).show();
                    }
                });
            }

            // Update the RecyclerView list
            if (twelveHourJSON != null && !twelveHourJSON.equals("")) {
                weatherArrayList = parseTwelveHourJSONs(twelveHourJSON);
            }

            // Update the UI at the top
            if (singleDayJSON != null && !singleDayJSON.equals("") && currentWeatherJSON != null && !currentWeatherJSON.equals("")) {
                // Updating the UI needs to be ran in the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        updateUI(singleDayJSON, currentWeatherJSON);
                    }
                });

            }

            super.onPostExecute(weatherJSONs);
        }
    }

    // Updates the UI with current weather info
    private void updateUI (String singleDayWeatherResults, String currentWeatherResults) {
        try {
            Log.i(TAG, "single day : " + singleDayWeatherResults);
            Log.i(TAG, "current weather : " + currentWeatherResults);
            // Convert currentWeatherResults from form of JSON Array to JSON Object
            JSONArray currentWeatherJSONArray = new JSONArray(currentWeatherResults);
            JSONObject currentWeatherJSON = currentWeatherJSONArray.getJSONObject(0);

            // Must grab the JSONArray "DailyForecasts" and convert to JSONObject to parse data
            JSONObject singleDayJSON = new JSONObject(singleDayWeatherResults);
            JSONArray dailyForecastArray = singleDayJSON.getJSONArray("DailyForecasts");
            JSONObject dailyForecasts = dailyForecastArray.getJSONObject(0);

            // Update current day
            String currentDate = getDate(dailyForecasts.getString("Date"));
            date.setText(currentDate);

            // Update the min/max temps for the day
            JSONObject temperature = dailyForecasts.getJSONObject("Temperature");
            String dailyMin = temperature.getJSONObject("Minimum").getString("Value");
            String dailyMax = temperature.getJSONObject("Maximum").getString("Value");
            minMaxTemp.setText(dailyMax + "°F/ " + dailyMin + "°F");

            // Update the precip chances for day and night
            JSONObject dayObject = dailyForecasts.getJSONObject("Day");
            String dayPrecipChance = dayObject.getString("PrecipitationProbability");
            JSONObject nightObject = dailyForecasts.getJSONObject("Night");
            String nightPrecipChance = nightObject.getString("PrecipitationProbability");

            String summaryString = currentWeatherJSON.getString("WeatherText");
            summary.setText(summaryString);

//            String daySummaryString = dayObject.getString("LongPhrase");
//            daySummary.setText(daySummaryString + " in the day");
//
//            String nightSummaryString = nightObject.getString("LongPhrase");
//            nightSummary.setText(nightSummaryString + " at night");

            // Update the day's precip chances in the day and at night
            precipChance.setText("Precip: " + dayPrecipChance + "% / " + nightPrecipChance + "%");

            // Update the current temp
            String currentTemperature = currentWeatherJSON.getJSONObject("Temperature").getJSONObject("Imperial").getString("Value");
            currentTemp.setText(currentTemperature + "°F");

            // Update Realfeel and humidity
            String realFeelTemp = currentWeatherJSON.getJSONObject("RealFeelTemperature").getJSONObject("Imperial").getString("Value");
            String humidityPercentage = currentWeatherJSON.getString("RelativeHumidity");
            humidity.setText("Realfeel " + realFeelTemp + "°F / Humidity " + humidityPercentage + "%");

            // Update UV Index and current MPH
            JSONObject currentWind = currentWeatherJSON.getJSONObject("Wind");
            String currentWindSpeed = currentWind.getJSONObject("Speed").getJSONObject("Imperial").getString("Value");
            String currentWindDirection = currentWind.getJSONObject("Direction").getString("English");
            String UVIndex = currentWeatherJSON.getString("UVIndex");
            currentStats.setText(currentWindSpeed + " mph " + currentWindDirection + " / UV Index: " + UVIndex);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Handles the JSON for the twelve hour forecast and posts info to the RecyclerView
    private ArrayList<Weather> parseTwelveHourJSONs(String weatherSearchResults) {
        if (weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if (weatherSearchResults != null) {
            try {
                Log.i(TAG, "weather search results: " + weatherSearchResults);
                // Create array of 12 single-hour JSONs
                JSONArray jsonArray = new JSONArray(weatherSearchResults);

                // Grab data from each individual JSON
                for (int j = 0; j < jsonArray.length(); ++j) {
                    JSONObject hourObject = jsonArray.getJSONObject(j);

                    Weather hourlyWeather = new Weather();

                    // Set the date
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

                    // Set the summary
                    String summary = hourObject.getString("IconPhrase");
                    hourlyWeather.setSummary(summary);

                    //Set the UV Index
                    String UVIndex = hourObject.getString("UVIndex");
                    hourlyWeather.setUVIndex(UVIndex);

//                    Log.i(TAG, "UV index: " + hourlyWeather.getUVIndex());

                    // Logging info for debugging purposes
//                    Log.i(TAG, "Min temp: " + hourlyWeather.getMinTemp() + " Max temp: " + hourlyWeather.getMaxTemp());
//                    Log.i(TAG, "Min RealFeel: " + hourlyWeather.getMinTempRealFeel() + " Max RealFeel: " + hourlyWeather.getMaxTempRealFeel());
//                    Log.i(TAG, "Precip chance: " + hourlyWeather.getChanceOfPrecipitation() + "%");
//                    Log.i(TAG, "Wind speeds: " + hourlyWeather.getWindSpeed() + "mph coming from the " + hourlyWeather.getWindDirection());


//                    Log.i(TAG, "Date from " + i + date);
                // Retrieve the JSON Array called 'DailyForecasts'
//                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                // Create our weather objects and populate with data from the JSON object
                // Eventually add in for loop to parse data from the 12 hour responses
                // Can leave out now since we're only using 1-day forecast
                // may eventually add in a 5-day forecast option

                    weatherArrayList.add(hourlyWeather);
                }

                if (weatherArrayList != null) {
                    Log.i(TAG, "Date from ");
//                    WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherArrayList);
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, weatherArrayList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    public String getTimeStamp(String s) {
        String date = s.substring(11, 16);
        Boolean PM = false;
        int hour = Integer.parseInt(date.substring(0, 2));
        if (hour > 12) {
            hour %= 12;
            PM = true;
        }
        // Noon (12:00 PM)
        else if (hour == 12) {
            PM = true;
        }
        // Midnight (12:00 AM)
        else if (hour == 0) {
            hour = 12;
        }

        String time = Integer.toString(hour) + date.substring(2);

        if (PM) {
            time += " PM";
        }
        else {
            time += " AM";
        }

        return time;
    }

    // Get the date in format of MM/DD
    public String getDate (String s) {
        String month = months[Integer.parseInt(s.substring(5, 7))];
        String day = s.substring(8, 10);
        // Days 01-09
        if (day.charAt(0) == '0') {
            day = day.substring(1);
        }

        String date = month + " " + day;
        return date;
    }
}
