package de.thu.tpro.android4bikes.services.weather;

import java.util.TimerTask;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;

public class WeatherTask extends TimerTask {
    private OpenWeatherManager manager;
    private OpenWeatherHelper helper;
    private LocalDatabaseHelper dbHelper = new CouchDBHelper();
    private OpenWeatherObject weatherObject;

    public WeatherTask(OpenWeatherManager manager) {
        this.manager = manager;
        helper = new OpenWeatherHelper();
    }

    @Override
    public void run() {
        Position position = dbHelper.getLastPosition();
        OpenWeatherObject weather = helper.createOpenWeatherObject(position.getLatitude(), position.getLongitude());
        if (weatherObject == null || !weatherObject.toString().equals(weather.toString())) {
            weatherObject = weather;
            manager.update(weatherObject);
        }
    }
}
