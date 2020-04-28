package de.thu.tpro.android4bikes.util;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.thu.tpro.android4bikes.database.CouchDBHelper;


public class GeoFencing extends Observable {
    /**
     * See: https://github.com/imperiumlabs/GeoFirestore-Android
     */

    private GeoFirestore geoFirestore;
    private ArrayList<String> documents_in_area;
    private GeoQuery geoQuery;
    private CouchDBHelper couchDBHelper;
    private ConstantsGeoFencing currentCollection;


    /**
     * Create new Geofence Object at a given point with a certain radius of interest.
     *
     * @param collection Firebase collection path
     */
    public GeoFencing(ConstantsGeoFencing collection) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(collection.toString());
        geoFirestore = new GeoFirestore(collectionReference);
        documents_in_area = new ArrayList<>();

        couchDBHelper = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
        this.currentCollection = collection;

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

    public void setupGeofence(GeoPoint center, double radius_km) {
        geoQuery = geoFirestore.queryAtLocation(center, radius_km);
    }

    public boolean updateRadius(double newRadius) {
        if (geoQuery != null) {
            geoQuery.setRadius(newRadius);
            return true;
        }

        return false;
    }

    public boolean updateCenter(GeoPoint newCenter) {
        if (geoQuery != null) {
            geoQuery.setCenter(newCenter);
            return true;
        }
        return false;
    }


    public boolean startGeoFenceListener() {
        List<Object> list_objects = new ArrayList<>();


        if (geoQuery != null) {
            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                @Override
                public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                    if (!documents_in_area.contains(documentSnapshot.getId())) {
                        documents_in_area.add(documentSnapshot.getId());
                    }
                    //Log.d("HALLO WELT", "DocumentMoved: "+documentSnapshot.getId()+" Geo point: "+ geoPoint);
                }

                @Override
                public void onDocumentExited(DocumentSnapshot documentSnapshot) {
                    documents_in_area.remove(documentSnapshot.getId());
                    //Log.d("HALLO WELT", "DocumentExit: "+documentSnapshot.getId());
                }

                @Override
                public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                    Object object_to_add = null;
                    Map map_object = documentSnapshot.getData();
                    //couchDBHelper.convertMapProfileToProfile(map);
                    switch (currentCollection) {

                        case COLLECTION_TRACKS:
                            break;
                        case COLLECTION_BIKERACKS:

                            break;
                        case COLLECTION_HAZARDS:
                            break;
                    }
                    documents_in_area.add(documentSnapshot.getId());
                    list_objects.add(object_to_add);
                    //Log.d("HALLO WELT", "Document Entered: "+documentSnapshot.getId()+" Geo point: "+ geoPoint);
                }

                @Override
                public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                }

                @Override
                public void onGeoQueryReady() {
                    Log.d("HALLO WELT", "Query ready");
                    documents_in_area.forEach(key -> Log.d("HALLO WELT", key));

                    setChanged();
                    notifyObservers(list_objects);
                }

                @Override
                public void onGeoQueryError(Exception e) {
                    Log.d("HALLO WELT", "Query error" + e.getMessage());
                }
            });
            return true;
        }
        return false;
    }

    public boolean stopGeoFenceListener() {
        if (geoQuery != null) {
            geoQuery.removeAllListeners();
            return true;
        }
        return false;
    }

    public enum ConstantsGeoFencing {
        COLLECTION_TRACKS("tracks"),
        COLLECTION_BIKERACKS("bikeracks"),
        COLLECTION_HAZARDS("hazards"),
        COLLECTION_RADIUS("radiustest");


        private String type;

        ConstantsGeoFencing(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

}