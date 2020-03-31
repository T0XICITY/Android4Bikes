package de.thu.tpro.android4bikes.services;

import org.junit.Test;

import java.util.List;

import de.thu.tpro.android4bikes.services.weatherData.warning.DWDwarning;

import static junit.framework.TestCase.*;


public class WeatherManagerWarningTest {

    @Test
    public void testData(){
        WeatherManagerWarning manager = new WeatherManagerWarning();
        manager.startWeatherTask();
        try {
            Thread.sleep(2000);
            assertNotNull(manager.getDWDwarnings());
            assertEquals("FROST",manager.getDWDwarnings().iterator().next().getEvent());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}