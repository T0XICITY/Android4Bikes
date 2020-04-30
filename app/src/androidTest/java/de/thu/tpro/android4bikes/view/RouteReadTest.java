package de.thu.tpro.android4bikes.view;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.TestObjectsGenerator;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertNotNull;

public class RouteReadTest {
    private static Context context;

    @BeforeClass
    public static void setUp(){
        context = ApplicationProvider.getApplicationContext();
        GlobalContext.setContext(context);
    }

    @Test
    public void testRead(){
        Track track = TestObjectsGenerator.generateTrack();
        String jsonRoute = track.getRoute().toJson();
        assertNotNull(jsonRoute);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonRoute);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertNotNull(jsonObject);
    }
}