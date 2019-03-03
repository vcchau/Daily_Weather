package com.example.victor.dailyweather;

public class Weather {
    String date;
    String MinTemp;
    String MaxTemp;
    String MinTempRealFeel;
    String MaxTempRealFeel;
    String WindSpeed;
    String WindDirection;
    String ChanceOfPrecipitation;
    String link;

    public String getMinTempRealFeel() {
        return MinTempRealFeel;
    }

    public void setMinTempRealFeel(String minTempRealFeel) {
        MinTempRealFeel = minTempRealFeel;
    }

    public String getMaxTempRealFeel() {
        return MaxTempRealFeel;
    }

    public void setMaxTempRealFeel(String maxTempRealFeel) {
        MaxTempRealFeel = maxTempRealFeel;
    }

    public String getWindSpeed() {
        return WindSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        WindSpeed = windSpeed;
    }

    public String getWindDirection() {
        return WindDirection;
    }

    public void setWindDirection(String windDirection) {
        WindDirection = windDirection;
    }

    public String getChanceOfPrecipitation() {
        return ChanceOfPrecipitation;
    }

    public void setChanceOfPrecipitation(String chanceOfPrecipitation) {
        ChanceOfPrecipitation = chanceOfPrecipitation;
    }

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
