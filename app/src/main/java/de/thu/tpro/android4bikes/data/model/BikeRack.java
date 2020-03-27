package de.thu.tpro.android4bikes.data.model;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONObject;

import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;

public class BikeRack implements JsonRepresentation {
    private GeoPoint geoPoint;
    private String name;
    private int capacity;
    private boolean hasBikeCharging;
    private boolean isCovered;
    private String firebaseID;

    public BikeRack() {
    }

    public BikeRack(GeoPoint geoPoint, String name, int capacity, boolean hasBikeCharging, boolean isCovered, String firebaseID) {
        this.geoPoint = geoPoint;
        this.name = name;
        this.capacity = capacity;
        this.hasBikeCharging = hasBikeCharging;
        this.isCovered = isCovered;
        this.firebaseID = firebaseID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean hasBikeCharging() {
        return hasBikeCharging;
    }

    public void setHasBikeCharging(boolean haseBikeCharging) {
        this.hasBikeCharging = haseBikeCharging;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }

    @Override
    public Map<String, Object> getMapRepresentation() {
        return null;
    }
}
