package de.thu.tpro.android4bikes.geoPosition;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.GeoPoint;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.GeoPosition;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.util.GeoLocationHelper;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertEquals;

public class GeoTesting {
    public GeoPosition geoPosition = new GeoPosition();
    @BeforeClass
    public static void setUp() {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void setlocation() {
        GeoPoint geoPoint = new GeoPoint(48.408880, 9.997507);
        geoPosition.setLocation("test1", geoPoint);
    }
    @Test
    public void getLocation(){
        geoPosition.getLocation("test1");
        //Log.d("OUTP", geoPosition.getLocation("test"));

        GeoPoint geoPoint = new GeoPoint(1,1);
        //assertEquals(2, geoPoint.toString());
    }

}