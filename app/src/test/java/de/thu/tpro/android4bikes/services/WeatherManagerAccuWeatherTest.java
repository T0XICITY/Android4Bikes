package de.thu.tpro.android4bikes.services;

import org.junit.Test;

import de.thu.tpro.android4bikes.services.weatherData.accu.AccuWeatherObject;

import static org.junit.Assert.*;

public class WeatherManagerAccuWeatherTest {

    @Test
    public void testBasicFunctionality(){
        WeatherManagerAccuWeather weatherManager = new WeatherManagerAccuWeather();
        AccuWeatherObject weatherObject = weatherManager.createAccuWeatherObject(48.3,9.8333);

        assertNotNull(weatherObject);
        assertNotNull(weatherObject.getWeatherData());

        weatherObject.getWeatherData().forEach(entry -> System.out.println(entry.getDateTime()+" - "+entry.getTemperature().getValue()));

        assertEquals("2020-03-30T10:00:00+02:00",weatherObject.getWeatherData().get(0).getDateTime());
        assertEquals(-0.2,weatherObject.getWeatherData().get(0).getTemperature().getValue(),0.0001);

    }
}