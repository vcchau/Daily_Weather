package com.example.victor.dailyweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherAdapter extends ArrayAdapter<Weather> {

    public WeatherAdapter(@NonNull Context context, ArrayList<Weather> weatherArrayList) {
        super(context, 0, weatherArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather weather = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Grab the textviews by their IDs
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView currentTemp = convertView.findViewById(R.id.currentTemp);
        TextView realFeel = convertView.findViewById(R.id.realFeel);
        TextView windSpeeds = convertView.findViewById(R.id.windSpeed);
        TextView UVIndex = convertView.findViewById(R.id.UVIndex);
        TextView precipChance = convertView.findViewById(R.id.precipChance);
        TextView humidity = convertView.findViewById(R.id.humidity);
        TextView summary = convertView.findViewById(R.id.summary);

//        TextView windDirection = convertView.findViewById(R.id.windDirection);

//        TextView link = convertView.findViewById(R.id.link);

        // Display the information in the list
        dateTextView.setText(weather.getTimeStamp());
        currentTemp.setText(weather.getCurrentTemp() + "°F");
        realFeel.setText(weather.getCurrentRealFeelTemp() + "°F");
        windSpeeds.setText(weather.getWindDirection() + " " + weather.getWindSpeed() + "mph");
        UVIndex.setText(weather.getUVIndex());
        precipChance.setText(weather.getChanceOfPrecipitation() + "%");
        humidity.setText(weather.getRelativeHumidity() + "%");
        summary.setText(weather.getSummary());


//        windDirection.setText(weather.getWindDirection());


//        link.setText(weather.getLink());

        return convertView;
    }


}
