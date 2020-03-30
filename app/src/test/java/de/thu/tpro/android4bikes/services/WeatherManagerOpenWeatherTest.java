package de.thu.tpro.android4bikes.services;

import org.junit.Test;

import de.thu.tpro.android4bikes.services.weatherData.openWeather.OpenWeatherObject;

import static org.junit.Assert.*;

public class WeatherManagerOpenWeatherTest {

    @Test
    public void testBasicFunctionality(){
        WeatherManagerOpenWeather manager = new WeatherManagerOpenWeather();
        OpenWeatherObject weatherObject = manager.createOpenWeatherObject(48.3,9.8333);

        assertNotNull(weatherObject);
        assertNotNull(weatherObject.getList());

        //weatherObject.getList().forEach(entry -> System.out.println(entry.getDtTxt() + " : " + entry.getMain().getTemp()));

        assertEquals("2020-03-30 09:00:00", weatherObject.getList().get(0).getDtTxt());
        assertEquals(1.65,(double)weatherObject.getList().get(0).getMain().getTemp(),0.0001);
    }

}