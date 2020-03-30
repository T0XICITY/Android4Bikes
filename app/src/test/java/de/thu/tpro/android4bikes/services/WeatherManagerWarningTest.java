package de.thu.tpro.android4bikes.services;

import org.junit.Test;

import java.util.List;

import de.thu.tpro.android4bikes.services.weatherData.warning.DWDwarning;

import static junit.framework.TestCase.*;


public class WeatherManagerWarningTest {

    @Test
    public void testData(){
        WeatherManagerWarning manager = new WeatherManagerWarning();
        List<DWDwarning> dwDwarningList = manager.loadWeatherWarnings();
        assertNotNull(dwDwarningList);
        assertEquals("FROST",dwDwarningList.get(0).getEvent());
    }
}