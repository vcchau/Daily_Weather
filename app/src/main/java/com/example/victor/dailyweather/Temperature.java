package com.example.victor.dailyweather;

public class Temperature {
    String date;
    String MinTemp;
    String MaxTemp;
    String link;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinTemp() {
        return MinTemp;
    }

    public void setMinTemp(String minTemp) {
        MinTemp = minTemp;
    }

    public String getMaxTemp() {
        return MaxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        MaxTemp = maxTemp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
