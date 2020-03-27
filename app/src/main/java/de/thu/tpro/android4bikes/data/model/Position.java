package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import de.thu.tpro.android4bikes.database.JsonRepresentation;

public class Position implements JsonRepresentation {
    private String firebaseID;

    public Position(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }
}
