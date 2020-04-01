package de.thu.tpro.android4bikes.services.weather.weatherData.openWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clouds {

    @SerializedName("all")
    @Expose
    private Integer all;

    /**
     * No args constructor for use in serialization
     */
    public Clouds() {
    }

    /**
     * @param all
     */
    public Clouds(Integer all) {
        super();
        this.all = all;
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public Clouds withAll(Integer all) {
        this.all = all;
        return this;
    }

    @Override
    public String toString() {
        return "Clouds{" +
                "all=" + all +
                '}';
    }
}
