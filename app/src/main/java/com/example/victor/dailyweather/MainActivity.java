package com.example.victor.dailyweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView date;
    private TextView currentTemp;
    private TextView humidity;
    private TextView minMaxTemp;
    private TextView precipChance;
    private TextView currentStats;
    private TextView daySummary;
    private TextView nightSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        date = findViewById(R.id.date);
        currentTemp = findViewById(R.id.currentTemp);
        humidity = findViewById(R.id.humidity);
        minMaxTemp = findViewById(R.id.dailyMinMax);
        precipChance = findViewById(R.id.precipChance);
        currentStats = findViewById(R.id.currentStats);
        daySummary = findViewById(R.id.summary);
        nightSummary = findViewById(R.id.nightSummary);

        // Build the API request URLs and handle them in an async task
        URL twelveHourWeatherURL = NetworkUtils.buildUrlForWeatherTwelveHours();
        URL singleDayWeatherURL = NetworkUtils.buildUrlForWeatherOneDay();
        URL currentWeatherURL = NetworkUtils.buildUrlForCurrentWeather();
        new FetchWeatherDetails().execute(twelveHourWeatherURL, singleDayWeatherURL, currentWeatherURL);

        // Initialize recycler view
//        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, weatherArrayList);
//        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Log.i(TAG, "onCreate singleDayWeatherURL: " + twelveHourWeatherURL);
    }


    // Class to fetch the details from our URL in the background
    private class FetchWeatherDetails extends AsyncTask<URL, Void, ArrayList<String>> {

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
                twelveHourWeatherResults = NetworkUtils.getResponse(twelveHourWeatherURL);
                singleDayWeatherResults = NetworkUtils.getResponse(singleDayWeatherURL);
                currentWeatherResults = NetworkUtils.getResponse(currentWeatherURL);

                params.add(twelveHourWeatherResults);
                params.add(singleDayWeatherResults);
                params.add(currentWeatherResults);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "in background results are: " + twelveHourWeatherResults);
            updateUI(singleDayWeatherResults, currentWeatherResults);
            return params;
        }

        @Override
        protected void onPostExecute(ArrayList<String> weatherJSONs) {
            String twelveHourJSON = weatherJSONs.get(0);
            String singleDayJSON = weatherJSONs.get(1);
            String currentWeatherJSON = weatherJSONs.get(2);

            // Update the RecyclerView list
            if (twelveHourJSON != null && !twelveHourJSON.equals("")) {
                weatherArrayList = parseTwelveHourJSONs(twelveHourJSON);
            }

            // Update the UI at the top
            if (singleDayJSON != null && !singleDayJSON.equals("") && currentWeatherJSON != null && !currentWeatherJSON.equals("")) {
                updateUI(singleDayJSON, currentWeatherJSON);
            }

            super.onPostExecute(weatherJSONs);
        }
    }

    // Updates the UI with current weather info
    private void updateUI (String singleDayWeatherResults, String currentWeatherResults) {
        try {
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

            /// Update the verbose summary for the day
            JSONObject dayObject = dailyForecasts.getJSONObject("Day");
            String dayPrecipChance = dayObject.getString("PrecipitationProbability");
            String daySummaryString = dayObject.getString("LongPhrase");
            daySummary.setText(daySummaryString + " in the day");

            // Update the verbose summary for the night
            JSONObject nightObject = dailyForecasts.getJSONObject("Night");
            String nightPrecipChance = nightObject.getString("PrecipitationProbability");
            String nightSummaryString = nightObject.getString("LongPhrase");
            nightSummary.setText(nightSummaryString + " at night");

            // Update the day's precip chances in the day and at night
            precipChance.setText("Precip " + dayPrecipChance + "%/ " + nightPrecipChance + "%");

            // Update the current temp
            String currentTemperature = currentWeatherJSON.getJSONObject("Temperature").getJSONObject("Imperial").getString("Value");
            currentTemp.setText(currentTemperature + "°F");

            // Update Realfeel and humidity
            String realFeelTemp = currentWeatherJSON.getJSONObject("RealFeelTemperature").getJSONObject("Imperial").getString("Value");
            String humidityPercentage = currentWeatherJSON.getString("RelativeHumidity");
            humidity.setText("Realfeel " + realFeelTemp + "°F / Humidity " + humidityPercentage + "5");

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
        String day = s.substring(9, 11);

        String date = month + " " + day;
        return date;
    }
}
