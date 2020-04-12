package de.thu.tpro.android4bikes.data.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;

import java.util.concurrent.atomic.AtomicInteger;


public class GeoPosition {
    /**
     * See: https://github.com/imperiumlabs/GeoFirestore-Android
     */
    private CollectionReference collectionReference;
    private GeoFirestore geoFirestore;

    public GeoPosition(String collectionpath) {
        collectionReference = FirebaseFirestore.getInstance().collection(collectionpath);
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

    public void getLocationQuery(GeoPoint geoPoint, double radius) {

        geoFirestore.getAtLocation(geoPoint, radius, (list, e) -> {
            if (e != null) {
                Log.d("HALLO WELT", "Error: ");
                e.printStackTrace();
            } else {
                list.forEach(document -> Log.d("HALLO WELT", document.toString()));
            }

        });
    }

    public void geoQuery(GeoPoint geoPoint, double radius_km) {
        GeoQuery geoQuery = geoFirestore.queryAtLocation(geoPoint, radius_km);
        Log.d("HALLO WELT", "Km: " + geoQuery.getRadius());
        Log.d("HALLO WELT", "Meilen?: " + geoQuery.getRadius() * 1.609d);
        AtomicInteger count = new AtomicInteger(1);
        for (Query query : geoQuery.getQueries()) {
            query.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("HALLO WELT", "Query: " + count.getAndIncrement() + "\tElemente:" + task.getResult().getDocuments().size());


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("HALLO WELT", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("HALLO WELT", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

}