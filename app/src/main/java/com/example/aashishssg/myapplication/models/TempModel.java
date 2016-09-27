package com.example.aashishssg.myapplication.models;

import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by Aashish SSG on 4/20/2016.
 */
public class TempModel {
    private String icon;
    private int temp;
    private float humidity;
    private float pressure;
    private int tempmin;
    private int tempmax;
    private String description;
    private String location;

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getIcon() {return icon;}

    public void setIcon(String icon) {this.icon = icon;}

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public int getTempmin() {
        return tempmin;
    }

    public void setTempmin(int tempmin) {
        this.tempmin = tempmin;
    }

    public int getTempmax() {
        return tempmax;
    }

    public void setTempmax(int tempmax) {
        this.tempmax = tempmax;
    }
}
