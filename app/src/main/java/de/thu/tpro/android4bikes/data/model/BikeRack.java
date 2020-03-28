package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;

public class BikeRack implements JsonRepresentation {
    public enum ConstantsBikeRack {
        BIKE_RACK_NAME("name"),
        CAPACITY("capacity"),
        IS_EBIKE_STATION("isEBikeStation"),
        IS_EXISTENT("isExistent"),
        IS_COVERED("isCovered");


        private String type;

        ConstantsBikeRack(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

    private String firebaseID;
    private Position position;
    private String name;
    private int capacity;
    private boolean hasBikeCharging;
    private boolean isExistent;
    private boolean isCovered;

    public BikeRack() {
    }

    public BikeRack(String firebaseID, Position position, String name, int capacity, boolean hasBikeCharging, boolean isExistent, boolean isCovered) {
        this.firebaseID = firebaseID;
        this.position = position;
        this.name = name;
        this.capacity = capacity;
        this.hasBikeCharging = hasBikeCharging;
        this.isExistent = isExistent;
        this.isCovered = isCovered;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean hasBikeCharging() {
        return hasBikeCharging;
    }

    public void setHasBikeCharging(boolean hasBikeCharging) {
        this.hasBikeCharging = hasBikeCharging;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }

    public boolean isExistent() {
        return isExistent;
    }

    public void setExistent(boolean existent) {
        isExistent = existent;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }

    @Override
    public Map<String, Object> getMapRepresentation() {
        Map<String,Object> map_bikeRack = new HashMap<>();
        map_bikeRack.put(Position.ConstantsPosition.POSITION.toString(), this.position.getMapRepresentation());
        map_bikeRack.put(ConstantsBikeRack.BIKE_RACK_NAME.toString(), this.name);
        map_bikeRack.put(ConstantsBikeRack.CAPACITY.toString(), this.capacity);
        map_bikeRack.put(ConstantsBikeRack.IS_EBIKE_STATION.toString(), this.hasBikeCharging);
        map_bikeRack.put(ConstantsBikeRack.IS_EXISTENT.toString(), this.isExistent);
        map_bikeRack.put(ConstantsBikeRack.IS_COVERED.toString(), this.isCovered);
        return map_bikeRack;
    }
}
