package de.thu.tpro.android4bikes.util;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.util.ArrayList;


public class GeoFencing {
    /**
     * See: https://github.com/imperiumlabs/GeoFirestore-Android
     */

    GeoFirestore geoFirestore;
    GeoQuery geoQuery;
    ArrayList<String> documents_in_area;

    /**
     * Create new Geofence Object at a given point with a certain radius of interest.
     *
     * @param collection Firebase collection path
     * @param center     center point for area of interest
     * @param radius_km  radius of interest
     */
    public GeoFencing(ConstantsGeoFencing collection, GeoPoint center, double radius_km) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(collection.toString());
        geoFirestore = new GeoFirestore(collectionReference);
        geoQuery = geoFirestore.queryAtLocation(center, radius_km);
        documents_in_area = new ArrayList<>();
    }

    /**
     * Register new Document at Firestore containing Geohash and GeoPoint
     * Only Registered Documents will considered in GeoQuery
     *
     * @param documentID Firestore DocumentID
     * @param geoPoint   Geoposition(Lat,Lon) of the new Element
     */
    public void registerDocument(String documentID, GeoPoint geoPoint) {
        geoFirestore.setLocation(documentID, geoPoint, exception -> {
            if (exception != null) {
                Log.d("HALLO WELT!", "An error has occurred while registering Document: " + exception.getMessage());
            } else {
                Log.d("HALLO WELT!", "Document " + documentID + " : " + geoPoint.toString() + " registered successfully!");
            }
        });
    }

    /**
     * Unregister a Document so it will be not longer considered in GeoQuery
     *
     * @param documentID Firestore Document
     */
    public void unregisterDocument(String documentID) {
        geoFirestore.removeLocation(documentID, new GeoFirestore.CompletionCallback() {
            @Override
            public void onComplete(Exception exception) {
                if (exception != null) {
                    Log.d("HALLO WELT!", "An error has occurred while removing Document: " + exception.getMessage());
                } else {
                    Log.d("HALLO WELT!", "Document removed successfully!");
                }
            }
        });
    }

    public void updateRadius(double newRadius) {
        geoQuery.setRadius(newRadius);
    }

    public void updateCenter(GeoPoint newCenter) {
        geoQuery.setCenter(newCenter);
    }


    public void startGeoFenceListener() {
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoPoint geoPoint) {
                documents_in_area.add(s);
                //Log.d("HALLO WELT", "KeyEntered: "+s+" Geo point: "+ geoPoint);
            }

            @Override
            public void onKeyExited(String s) {
                documents_in_area.remove(s);
                //Log.d("HALLO WELT", "KeyExit: "+s);

            }

            @Override
            public void onKeyMoved(String s, GeoPoint geoPoint) {
                if (!documents_in_area.contains(s)) {
                    documents_in_area.add(s);
                }
                //Log.d("HALLO WELT", "KeyMoved: "+s+" Geo point: "+ geoPoint);
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("HALLO WELT", "Query ready");
                documents_in_area.forEach(key -> Log.d("HALLO WELT", key));
            }

            @Override
            public void onGeoQueryError(Exception e) {
                Log.d("HALLO WELT", "Query error" + e.getMessage());
            }
        });
    }

    public void stopGeoFenceListener() {
        geoQuery.removeAllListeners();
    }

    public enum ConstantsGeoFencing {
        COLLECTION_TRACKS("tracks"),
        COLLECTION_OFFICIAL_BIKERACKS("officialbikeracks"),
        COLLECTION_OFFICIAL_HAZARDS("officialhazards");


        private String type;

        ConstantsGeoFencing(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

}