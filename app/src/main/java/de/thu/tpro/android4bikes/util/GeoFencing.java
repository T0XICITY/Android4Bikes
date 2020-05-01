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

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Track;
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
    private MapToObjectConverter mapToObjectConverter;


    /**
     * Create new Geofence Object at a given point with a certain radius of interest.
     *
     * @param collection Firebase collection path
     */
    public GeoFencing(ConstantsGeoFencing collection,GeoPoint center, double radius_km) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(collection.toString());
        geoFirestore = new GeoFirestore(collectionReference);
        documents_in_area = new ArrayList<>();

        couchDBHelper = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
        this.currentCollection = collection;

        switch (currentCollection) {
            case COLLECTION_TRACKS:
                mapToObjectConverter = new MapToObjectConverter<>(Track.class);
                break;
            case COLLECTION_BIKERACKS:
                mapToObjectConverter = new MapToObjectConverter<>(BikeRack.class);
                break;
            case COLLECTION_HAZARDS:
                mapToObjectConverter = new MapToObjectConverter<>(HazardAlert.class);
                break;
        }

        setupGeofence(center,radius_km);
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
                    Log.d("HalloWelt", "DocumentMoved: " + documentSnapshot.getId() + " Geo point: " + geoPoint);
                }

                @Override
                public void onDocumentExited(DocumentSnapshot documentSnapshot) {
                    Object object_to_remove = null;
                    Map map_object = documentSnapshot.getData();

                    object_to_remove = mapToObjectConverter.convertMapToObject(map_object, null);

                    documents_in_area.remove(documentSnapshot.getId());
                    if (object_to_remove != null){
                        list_objects.remove(object_to_remove);
                    }
                    //Log.d("HALLO WELT", "DocumentExit: "+documentSnapshot.getId());
                    setChanged();
                    notifyObservers(list_objects);
                }

                @Override
                public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                    Object object_to_add = null;
                    Map map_object = documentSnapshot.getData();

                    object_to_add = mapToObjectConverter.convertMapToObject(map_object, null);

                    documents_in_area.add(documentSnapshot.getId());
                    if (object_to_add != null){
                        list_objects.add(object_to_add);
                    }
                    //Log.d("HALLO WELT", "Document Entered: "+documentSnapshot.getId()+" Geo point: "+ geoPoint);
                    setChanged();
                    notifyObservers(list_objects);
                }

                @Override
                public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                    geoPoint.toString();
                    Log.d("HalloWelt", "onDocumentChanged");
                }

                @Override
                public void onGeoQueryReady() {
                    Log.d("HALLO WELT", "Query ready");
                    documents_in_area.forEach(key -> Log.d("HALLO WELT", key));

                    /*setChanged();
                    notifyObservers(list_objects);*/
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

    public double getRadius() {
        return geoQuery.getRadius();
    }

    public enum ConstantsGeoFencing {
        COLLECTION_TRACKS("tracks"),
        COLLECTION_BIKERACKS("bikeracks"),
        COLLECTION_HAZARDS("hazards"),
        COLLECTION_ROUTE("route");


        private String type;

        ConstantsGeoFencing(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

}