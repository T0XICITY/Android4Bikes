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
        assertNotNull(weatherObject.getForecastList());

        weatherObject.getForecastList().forEach(entry -> System.out.println(entry.getDtTxt() + " : " + entry.getMain().getTemp()));

        assertEquals("2020-03-31 12:00:00", weatherObject.getForecastList().get(0).getDtTxt());
        assertEquals(5.55,(double)weatherObject.getForecastList().get(0).getMain().getTemp(),0.0001);
    }

}