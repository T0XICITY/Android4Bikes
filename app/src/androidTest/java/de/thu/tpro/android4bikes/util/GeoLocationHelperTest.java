package de.thu.tpro.android4bikes.util;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.Position;

import static org.junit.Assert.*;

public class GeoLocationHelperTest {
    @BeforeClass
    public static void setUp(){
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void convertPositionToPostcode(){
        Position position_technische_hochschule_ulm = new Position(9.997507,48.408880);
        String postcode_technische_hochschule_ulm_real = "89075";
        String postcode_technische_hochschule_ulm_calculated = GeoLocationHelper.convertPositionToPostcode(position_technische_hochschule_ulm);


        assertEquals(postcode_technische_hochschule_ulm_real, postcode_technische_hochschule_ulm_calculated);
    }
}