package de.thu.tpro.android4bikes.services.weather;

import java.util.Observable;
import java.util.Timer;

import de.thu.tpro.android4bikes.services.weather.weatherData.openWeather.OpenWeatherObject;

public class OpenWeatherManager extends Observable {
    private Timer timer;
    private OpenWeatherObject weatherObject;

    public OpenWeatherManager() {
        timer = new Timer();
    }

    public void startWeatherSubscription() {
        WeatherTask weatherTask = new WeatherTask(this);
        timer.schedule(weatherTask, 1000, 1000 * 60 * 15);
    }

    public void stopWeatherSubscription() {
        timer.cancel();
    }

    public void update(OpenWeatherObject weatherObject) {
        this.weatherObject = weatherObject;
        this.setChanged();
        this.notifyObservers();
    }

    public OpenWeatherObject getWeather() {
        return weatherObject;
    }
}
