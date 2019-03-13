package com.example.victor.dailyweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context context;

    // These arraylists store the info as the view recyles them
    // Change to arraylist of weather?
    private ArrayList<Weather> hours;

//    private ArrayList<String> dates = new ArrayList<>();
//    private ArrayList<String> currentTemps = new ArrayList<>();
//    private ArrayList<String> realFeels = new ArrayList<>();
//    private ArrayList<String> windSpeeds = new ArrayList<>();
//    private ArrayList<String> windDirections = new ArrayList<>();
//    private ArrayList<String> precipChances = new ArrayList<>();
//    private ArrayList<String> humidities = new ArrayList<>();
//    private ArrayList<String> UVIndices = new ArrayList<>();
//    private ArrayList<String> summaries = new ArrayList<>();

    public RecyclerViewAdapter(Context context, ArrayList<Weather> hours) {
        this.context = context;
        this.hours = hours;
    }


    //    public RecyclerViewAdapter(ArrayList<String> dates, ArrayList<String> currentTemps, ArrayList<String> realFeels,
//                               ArrayList<String> windSpeeds, ArrayList<String> windDirections, ArrayList<String> precipChances,
//                               ArrayList<String> humidities, ArrayList<String> UVIndices, ArrayList<String> summaries, Context context) {
//        this.dates = dates;
//        this.currentTemps = currentTemps;
//        this.realFeels = realFeels;
//        this.windSpeeds = windSpeeds;
//        this.windDirections = windDirections;
//        this.precipChances = precipChances;
//        this.humidities = humidities;
//        this.UVIndices = UVIndices;
//        this.summaries = summaries;
//        this.context = context;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.i(TAG, "onBindViewholder called");

        viewHolder.dateTextView.setText(hours.get(i).getTimeStamp());
        viewHolder.currentTemp.setText(hours.get(i).getCurrentTemp() + "°F");
        viewHolder.realFeel.setText(hours.get(i).getCurrentRealFeelTemp() + "°F");
        viewHolder.windSpeeds.setText(hours.get(i).getWindDirection() + " " + hours.get(i).getWindSpeed() + " mph");
        viewHolder.precipChance.setText(hours.get(i).getChanceOfPrecipitation() + "%");
        viewHolder.humidity.setText(hours.get(i).getRelativeHumidity() + "%");
        viewHolder.summary.setText(hours.get(i).getSummary());
        viewHolder.UVIndex.setText(hours.get(i).getUVIndex());
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout parentLayout;
        TextView dateTextView;
        TextView currentTemp;
        TextView realFeel;
        TextView windSpeeds;
        TextView precipChance;
        TextView humidity;
        TextView summary;
        TextView UVIndex;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            currentTemp = itemView.findViewById(R.id.currentTemp);
            realFeel = itemView.findViewById(R.id.realFeel);
            windSpeeds = itemView.findViewById(R.id.windSpeed);
            precipChance = itemView.findViewById(R.id.precipChance);
            humidity = itemView.findViewById(R.id.humidity);
            summary = itemView.findViewById(R.id.summary);
            UVIndex = itemView.findViewById(R.id.UVIndex);
        }
    }
}
