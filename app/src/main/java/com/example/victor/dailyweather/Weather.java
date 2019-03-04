package com.example.victor.dailyweather;

/* Class for storing information about the weather. Contains useful info like the min/max temps,
   chance of precipitation, and other bits of information.
 */
public class Weather {
    private String date;
    private String MinTemp;
    private String MaxTemp;
    private String MinTempRealFeel;
    private String MaxTempRealFeel;
    private String WindSpeed;
    private String WindDirection;
    private String ChanceOfPrecipitation;
    private String link;
    private String currentTemp;
    private String currentRealFeelTemp;
    private String UVIndex;
    private String cloudCover;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    private String summary;

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

    public String getTimeStamp() {
        String date = this.date.substring(11, 16);
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
}
