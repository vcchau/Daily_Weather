package com.example.victor.dailyweather;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class NetworkUtils extends  MainActivity{


    private final static String TAG = "NetworkUtils";

    // API key param
    private final static String PARAM_API_KEY = "apikey";
    private final static String API_KEY = API_Key.getApi_key();

    // Whether to show more details in JSON response
    private final static String PARAM_EXTRA_DETAILS = "details";
    private final static String EXTRA_PARAMS = "true";

    // Whether to display values in metric or imperial
    private final static String METRIC_PARAMS = "metric";
    private final static String METRIC_VALUES = "false";

    /* ALL COMMENTED OUT STRINGS ARE CURRENTLY UNAUTHORIZED IN THE FREE API PACKAGE
     * All string are missing location keys, we grab those by entering our city
     */

    // Base url for 1 Day of Daily Forecasts
    private final static String ONE_DAY_BASE_REQUEST_URL =
            "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";

    // Base url for 5 Days of Daily Forecasts
    private final static String FIVE_DAY_BASE_REQUEST_URL =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";

    // Base url for 1 hour of Daily Forecasts
    private final static String ONE_HOUR_BASE_REQUEST_URL =
            "http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";

    // Base url for 12 hours of Daily Forecasts
    private final static String TWELVE_HOUR_BASE_REQUEST_URL =
            "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/";

    // Base url to search for location key
    private final static String LOCATION_SEARCH_BASE_URL =
            "http://dataservice.accuweather.com/locations/v1/cities/search";

    // Base url to search for current conditions
    private final static String CURRENT_CONDITIONS_BASE_URL =
            "http://dataservice.accuweather.com/currentconditions/v1/";

//    // Base url for 24 hours of Daily Forecasts
//    private final static String TWENTY_FOUR_HOUR_BASE_REQUEST_URL =
//            "http://dataservice.accuweather.com/forecasts/v1/hourly/24hour/";

//    // Base url for 72 hours of Daily Forecasts
//    private final static String SEVENTY_TWO_HOUR_BASE_REQUEST_URL =
//            "http://dataservice.accuweather.com/forecasts/v1/hourly/72hour/";

//    // Base url for 120 hours of Daily Forecasts
//    private final static String ONE_TWENTY_HOUR_BASE_REQUEST_URL =
//            "http://dataservice.accuweather.com/forecasts/v1/hourly/120hour/";



    // Static cache of popular cities
    private final static HashMap<String, String> locationKeys = new HashMap<String, String>()
    {{
        put("austin", "351193");
        put("houston", "351197");
    }};

    // Get info for twelve hours
    public static URL buildUrlForWeatherTwelveHours() {
        String requestURL = TWELVE_HOUR_BASE_REQUEST_URL + locationKeys.get("austin");

        // Use a uri to create our request url
        Uri buildUri = Uri.parse(requestURL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRA_DETAILS, EXTRA_PARAMS)
                .appendQueryParameter(METRIC_PARAMS, METRIC_VALUES)
                .build();

        // Attempt to create the url
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // Get current weather info
    public static URL buildUrlForCurrentWeather() {
        String requestURL = CURRENT_CONDITIONS_BASE_URL + locationKeys.get("austin");

        // Use a uri to create our request url
        Uri buildUri = Uri.parse(requestURL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRA_DETAILS, EXTRA_PARAMS)
                .appendQueryParameter(METRIC_PARAMS, METRIC_VALUES)
                .build();

        // Attempt to create the url
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    // Get info for the day
    public static URL buildUrlForWeatherOneDay() {
        String requestURL = ONE_DAY_BASE_REQUEST_URL + locationKeys.get("austin");

        // Use a uri to create our request url
        Uri buildUri = Uri.parse(requestURL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRA_DETAILS, EXTRA_PARAMS)
                .appendQueryParameter(METRIC_PARAMS, METRIC_VALUES)
                .build();

        // Attempt to create the url
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            }
            else {
                return null;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            String error = "error";
            return error;
        }
        finally {
            urlConnection.disconnect();
        }
    }

}

