package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import de.thu.tpro.android4bikes.database.Content;

public class Position implements Content {
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
