package com.example.victor.dailyweather;

/*
 * The HourlyWeather class extends the Weather class and includes extra real-time properties
 * like the current temperature, current real feel temperature, UV index, and current cloud cover.
 */
public class HourlyWeather extends Weather {
    private String currentTemp;
    private String currentRealFeelTemp;
    private String UVIndex;
    private String cloudCover;

    public String getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(String relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    private String relativeHumidity;

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getCurrentRealFeelTemp() {
        return currentRealFeelTemp;
    }

    public void setCurrentRealFeelTemp(String currentRealFeelTemp) {
        this.currentRealFeelTemp = currentRealFeelTemp;
    }

    public String getUVIndex() {
        return UVIndex;
    }

    public void setUVIndex(String UVIndex) {
        this.UVIndex = UVIndex;
    }

    public String getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(String cloudCover) {
        this.cloudCover = cloudCover;
    }
}
