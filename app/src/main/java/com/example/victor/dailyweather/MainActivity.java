package com.example.victor.dailyweather;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
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
    private String locationKey;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView locationView;
    private TextView date;
    private TextView currentTemp;
    private TextView humidity;
    private TextView minMaxTemp;
    private TextView precipChance;
    private TextView summary;

//    private TextView currentStats;


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
        locationView = findViewById(R.id.location);
        date = findViewById(R.id.date);
        currentTemp = findViewById(R.id.currentTemp);
        humidity = findViewById(R.id.humidity);
        minMaxTemp = findViewById(R.id.dailyMinMax);
        precipChance = findViewById(R.id.precipChance);
        summary = findViewById(R.id.summary);

//        currentStats = findViewById(R.id.currentStats);

//        URLs[0] = NetworkUtils.buildUrlForWeatherTwelveHours(cityKey);
//        URLs[1] = NetworkUtils.buildUrlForWeatherOneDay(cityKey);
//        URLs[2] = NetworkUtils.buildUrlForCurrentWeather(cityKey);

        // Update the weather on app startup
//        new FetchForecastDetails().execute(URLs[0], URLs[1], URLs[2]);


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

            case R.id.get_location:
                displayDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayDialog() {
        // Create an alert to prompt user for input
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Enter a city and state");

        // Create an EditText whenever the alert dialog is created
        final EditText editText = new EditText(MainActivity.this);
        editText.setPadding(30, 30, 30, 30);
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the city from user and search API for location key
                String city = editText.getText().toString().trim();
                Log.i(TAG, "City is: " + city);
                Log.i(TAG, "Value is : " + (!city.equals(null) && !city.equals("") && !city.equals(" ")));

                if (!city.equals(null) && !city.equals("") && !city.equals(" ")) {
                    URL locationURL = NetworkUtils.buildUrlForCity(city);
                    Log.i(TAG,"City is: " + city);
                    Log.i(TAG, "Location URL is: " + locationURL);

                    // Attempt to grab the location key from the URL generated
                    new FetchLocationKey().execute(locationURL);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "There was an error fetching your city. Please re-enter your city.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    displayDialog();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", null);

        AlertDialog a = alertDialog.create();
        a.show();
        a.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#49e5dd"));
        a.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1a191c"));
    }

    private void setLocation (String location) {
        locationView.setText(location);
    }

    // Class to grab the location key
    private class FetchLocationKey extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL locationURL = urls[0];
            String locationKeyData = null;

            try {
                locationKeyData = NetworkUtils.getResponse(locationURL);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return locationKeyData;
        }

        @Override
        protected void onPostExecute(String locationKeyData) {
            if (locationKeyData != null && locationKeyData != "") {
                // Grab the location string from parseLocation
                final String[] location = new String[1];
                location[0] = parseLocation(locationKeyData);

                if (location != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLocation(location[0]);
                        }
                    });
                }
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "There was an error fetching your city. Please re-enter your city.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            super.onPostExecute(locationKeyData);
        }
    }

    private String parseLocation (String locationKeyData) {
        String location = "";
        try {
            Log.i(TAG, "Location key data: " + locationKeyData);
            JSONArray locationArray = new JSONArray(locationKeyData);
            JSONObject locationObject = locationArray.getJSONObject(0);

            String city = locationObject.getString("LocalizedName");
            String state = locationObject.getJSONObject("AdministrativeArea").getString("LocalizedName");
            location = city + ", " + state;

            this.locationKey = locationObject.getString("Key");
            Log.i(TAG, "Location key is: " + locationKeyData);

            URLs[0] = NetworkUtils.buildUrlForWeatherTwelveHours(locationKey);
            URLs[1] = NetworkUtils.buildUrlForWeatherOneDay(locationKey);
            URLs[2] = NetworkUtils.buildUrlForCurrentWeather(locationKey);

            // Update the weather on app startup
            new FetchForecastDetails().execute(URLs[0], URLs[1], URLs[2]);

        }
        catch (JSONException e) {
            e.printStackTrace();
            // Give the user a warning message and re-initialize text prompt
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "There was an error fetching your city. Please re-enter your city.",
                            Toast.LENGTH_LONG).show();
                }
            });
            displayDialog();
        }
        return location;
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
            minMaxTemp.setText(dailyMax + "°F / " + dailyMin + "°F");

            // Update the precip chances for day and night
            JSONObject dayObject = dailyForecasts.getJSONObject("Day");
            String dayPrecipChance = dayObject.getString("PrecipitationProbability");
            JSONObject nightObject = dailyForecasts.getJSONObject("Night");
            String nightPrecipChance = nightObject.getString("PrecipitationProbability");

            // Update UV Index and current MPH
            JSONObject currentWind = currentWeatherJSON.getJSONObject("Wind");
            String currentWindSpeed = currentWind.getJSONObject("Speed").getJSONObject("Imperial").getString("Value");
            String currentWindDirection = currentWind.getJSONObject("Direction").getString("English");

            // Update the day's precip chances in the day and at night
            precipChance.setText("Precip: " + dayPrecipChance + "% / " + nightPrecipChance + "% / " + currentWindSpeed + " mph " + currentWindDirection);

            String summaryString = currentWeatherJSON.getString("WeatherText");
            summary.setText(summaryString);



            // Update the current temp
            String currentTemperature = currentWeatherJSON.getJSONObject("Temperature").getJSONObject("Imperial").getString("Value");
            currentTemp.setText(currentTemperature + "°F");

            // Update Realfeel and humidity
            String realFeelTemp = currentWeatherJSON.getJSONObject("RealFeelTemperature").getJSONObject("Imperial").getString("Value");
            String humidityPercentage = currentWeatherJSON.getString("RelativeHumidity");
            humidity.setText("Realfeel " + realFeelTemp + "°F / Humidity " + humidityPercentage + "%");




//            String UVIndex = currentWeatherJSON.getString("UVIndex");
//            currentStats.setText(currentWindSpeed + " mph " + currentWindDirection + " / UV Index: " + UVIndex);

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

                    weatherArrayList.add(hourlyWeather);
                }

                if (weatherArrayList != null) {
                    Log.i(TAG, "Date from ");

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
