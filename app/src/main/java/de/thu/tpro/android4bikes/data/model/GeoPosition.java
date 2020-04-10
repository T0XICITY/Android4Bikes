package de.thu.tpro.android4bikes.data.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;


public class GeoPosition {
    /**
     * See: https://github.com/imperiumlabs/GeoFirestore-Android
     */
    private CollectionReference collectionReference;
    private GeoFirestore geoFirestore;

    public GeoPosition() {
        collectionReference = FirebaseFirestore.getInstance().collection("mycollection");
        geoFirestore = new GeoFirestore(collectionReference);
    }

    public void setLocation(String id, GeoPoint geoPoint) {
        geoFirestore.setLocation(id, geoPoint, exception -> {
            if (exception == null)
                Log.d("HALLO WELT!", "Location " + id + " : " + geoPoint.toString() + " saved on server successfully!");
            else {
                Log.d("HALLO WELT!", "An error has occurred: " + exception.getMessage());
            }
        });
    }

    public void getLocation(String id) {
        geoFirestore.getLocation("test667", new GeoFirestore.LocationCallback() {
            @Override
            public void onComplete(GeoPoint location, Exception exception) {
                if (exception == null && location != null) {
                    Log.d("HALLO", "The location for this document is" + location.toString());
                } else {
                    Log.d("HALLO WELT!", "An error has occurred: " + exception.getMessage());
                }
            }
        });
    }

    public void geoQuery(GeoPoint geoPoint, int radius) {
        GeoQuery geoQuery = geoFirestore.queryAtLocation(geoPoint, radius);
        Log.d("HALLO WELT!", "An error has occurred: " + geoQuery.toString());

    }

}