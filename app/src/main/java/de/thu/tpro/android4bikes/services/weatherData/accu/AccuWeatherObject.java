package de.thu.tpro.android4bikes.services.weatherData.accu;

import java.util.List;

public class AccuWeatherObject {
    private List<WeatherData> weatherData;

    public AccuWeatherObject() {
        weatherData = null;
    }

    public AccuWeatherObject(List<WeatherData> weatherData) {
        super();
        this.weatherData = weatherData;
    }

    public List<WeatherData> getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(List<WeatherData> weatherData) {
        this.weatherData = weatherData;
    }

    public AccuWeatherObject withWeatherData(List<WeatherData> weatherData) {
        this.weatherData = weatherData;
        return this;
    }

    @Override
    public String toString() {
        return "AccuWeatherObject{" +
                "weatherData=" + weatherData.toString() +
                '}';
    }
}
