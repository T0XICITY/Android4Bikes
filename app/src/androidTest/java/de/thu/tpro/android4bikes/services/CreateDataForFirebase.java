package de.thu.tpro.android4bikes.services;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.google.gson.Gson;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.TimeBase;

public class CreateDataForFirebase {
    private static Context context;

    @BeforeClass
    public static void init() {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void generateDataForFirebase() {
        try {
            String jsonString = getJsonFromAssets(context,"cities.json");
            JSONObject jsonObject_cities = new JSONObject(jsonString);
            JSONArray array_cities = jsonObject_cities.getJSONArray("cities");

            List<BikeRack> bikeRacks = new ArrayList<>();
            List<HazardAlert> hazardAlerts = new ArrayList<>();
            List<Track> tracks = new ArrayList<>();

            String route = getJsonFromAssets(context,"testDirections.json");

            for (int i = 0; i < array_cities.length(); i = i+3) {
                if (i+3 >= array_cities.length()) {
                    break;
                }
                int idx_bikerack = i;
                int idx_hazard = i+1;
                int idx_track = i+2;

                JSONObject city_bikeRack = array_cities.getJSONObject(idx_bikerack);
                bikeRacks.add(new BikeRack(
                        new Position(city_bikeRack.getDouble("lat"),city_bikeRack.getDouble("lng")),
                        city_bikeRack.getString("city"),
                        BikeRack.ConstantsCapacity.MEDIUM,
                        false,
                        true,
                        false
                ));

                JSONObject city_hazard = array_cities.getJSONObject(idx_hazard);
                hazardAlerts.add(new HazardAlert(
                        HazardAlert.HazardType.ROADKILL,
                        new Position(city_hazard.getDouble("lat"),city_hazard.getDouble("lng")),
                        TimeBase.getCurrentUnixTimeStamp(),
                        10,
                        true
                        ));

                JSONObject city_track = array_cities.getJSONObject(idx_track);
                tracks.add(new Track(
                        "y729hSy22mYOzRVdcmv8IQ8NcJI3",
                        new Rating(2,5,3),
                        city_track.getString("city"),
                        "description",
                        TimeBase.getCurrentUnixTimeStamp(),
                        i*8.5,
                        DirectionsRoute.fromJson(route),
                        new ArrayList<>(),
                        new Position(city_track.getDouble("lat"),city_track.getDouble("lng")),
                        new Position(city_track.getDouble("lat")+1,city_track.getDouble("lng")+1),
                        true
                        ));
            }

            bikeRacks.forEach(e -> {
                uploadBikeRack(e);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            });
            tracks.forEach(this::uploadTrack);
            hazardAlerts.forEach(this::uploadHazardAlerts);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void uploadHazardAlerts(HazardAlert hazardAlert) {
        try{
            FirebaseConnection con = FirebaseConnection.getInstance();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            JSONObject jsonObject_hazardAlert = new JSONObject(new Gson().toJson(hazardAlert));
            Map map_hazardAlert = new Gson().fromJson(jsonObject_hazardAlert.toString(), Map.class);
            con.getDb().collection(FirebaseConnection.ConstantsFirebase.COLLECTION_HAZARDS.toString())
                    .document(hazardAlert.getFirebaseID())
                    .set(map_hazardAlert) //generate id automatically
                    .addOnSuccessListener(documentReference -> {
                        con.registerDocument(hazardAlert.getFirebaseID(), hazardAlert.getPosition().getGeoPoint(),con.getGeoFireStore_bikeRack());
                        countDownLatch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("HalloWalt", "Error submitting Hazard", e);
                        countDownLatch.countDown();
                    });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadTrack(Track track) {
        try{
            FirebaseConnection con = FirebaseConnection.getInstance();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            JSONObject jsonObject_track = new JSONObject(new Gson().toJson(track));
            Map map_track = new Gson().fromJson(jsonObject_track.toString(), Map.class);
            con.getDb().collection(FirebaseConnection.ConstantsFirebase.COLLECTION_TRACKS.toString())
                    .document(track.getFirebaseID())
                    .set(map_track) //generate id automatically
                    .addOnSuccessListener(documentReference -> {
                        con.registerDocument(track.getFirebaseID(), track.getStartPosition().getGeoPoint(),con.getGeoFireStore_bikeRack());
                        countDownLatch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("HalloWalt", "Error submitting Track", e);
                        countDownLatch.countDown();
                    });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadBikeRack(BikeRack bikeRack){
        try{
            FirebaseConnection con = FirebaseConnection.getInstance();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            JSONObject jsonObject_bikeRack = new JSONObject(new Gson().toJson(bikeRack));
            Map map_bikeRack = new Gson().fromJson(jsonObject_bikeRack.toString(), Map.class);
            jsonObject_bikeRack.getString("firebaseID");
            con.getDb().collection(FirebaseConnection.ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                    .document(bikeRack.getFirebaseID())
                    .set(map_bikeRack) //generate id automatically
                    .addOnSuccessListener(documentReference -> {
                        con.registerDocument(bikeRack.getFirebaseID(), bikeRack.getPosition().getGeoPoint(),con.getGeoFireStore_bikeRack());
                        countDownLatch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("HalloWalt", "Error submitting BikeRack", e);
                        countDownLatch.countDown();
                    });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }
}