package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.util.GeoLocationHelper;

public class BikeRack implements JsonRepresentation {
    private String firebaseID;
    private Position position;
    private String postcode;
    private String name;
    private ConstantsCapacity capacity;
    private boolean hasBikeCharging;
    private boolean isExistent;
    private boolean isCovered;
    public BikeRack() {
    }
    /**
     * constructor regarding a bikeRack using all parameters
     *
     * @param firebaseID      firebaseID
     * @param position        position
     * @param name            name of the bikeRack
     * @param capacity        Constant: Small, Medium, Large, Gigantic
     * @param hasBikeCharging Is there any option to charge an e-bike?
     * @param isExistent      is the bikeRack still existing?
     * @param isCovered       is there any kind of roof regarding the bikerack?
     */
    public BikeRack(String firebaseID, Position position, String name, ConstantsCapacity capacity, boolean hasBikeCharging, boolean isExistent, boolean isCovered) {
        this.firebaseID = firebaseID;
        this.position = position;
        this.name = name;
        this.capacity = capacity;
        this.hasBikeCharging = hasBikeCharging;
        this.isExistent = isExistent;
        this.isCovered = isCovered;
        this.postcode = GeoLocationHelper.convertPositionToPostcode(this.position);
    }

    /**
     * constructor regarding a bikeRack using all parameters.
     * It automatically converts an integer number into an associated constant.
     *
     * @param firebaseID      firebaseID
     * @param position        position
     * @param name            name of the bikeRack
     * @param capacity        Int: 0=Small, 1=Medium, 2=Large, else -> Gigantic
     * @param hasBikeCharging Is there any option to charge an e-bike?
     * @param isExistent      is the bikeRack still existing?
     * @param isCovered       is there any kind of roof regarding the bikerack?
     */
    public BikeRack(String firebaseID, Position position, String name, int capacity, boolean hasBikeCharging, boolean isExistent, boolean isCovered) {
        this.firebaseID = firebaseID;
        this.position = position;
        this.name = name;
        this.hasBikeCharging = hasBikeCharging;
        this.isExistent = isExistent;
        this.isCovered = isCovered;

        switch (capacity) {
            case 0:
                this.capacity = ConstantsCapacity.SMALL;
                break;
            case 1:
                this.capacity = ConstantsCapacity.MEDIUM;
                break;
            case 2:
                this.capacity = ConstantsCapacity.LARGE;
                break;
            default:
                this.capacity = ConstantsCapacity.GIGANTIC;
                break;
        }
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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        this.postcode = GeoLocationHelper.convertPositionToPostcode(this.position);
    }

    public boolean hasBikeCharging() {
        return hasBikeCharging;
    }

    public void setHasBikeCharging(boolean hasBikeCharging) {
        this.hasBikeCharging = hasBikeCharging;
    }

    public ConstantsCapacity getCapacity() {
        return capacity;
    }

    public void setCapacity(ConstantsCapacity capacity) {
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
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map_bikeRack = new HashMap<>();
        map_bikeRack.put(Position.ConstantsPosition.POSITION.toString(), this.position.toMap());
        map_bikeRack.put(ConstantsBikeRack.POSTCODE.toString(), this.postcode);
        map_bikeRack.put(ConstantsBikeRack.BIKE_RACK_NAME.toString(), this.name);
        map_bikeRack.put(ConstantsBikeRack.CAPACITY.toString(), this.capacity.toInt()); //enum to int
        map_bikeRack.put(ConstantsBikeRack.IS_EBIKE_STATION.toString(), this.hasBikeCharging);
        map_bikeRack.put(ConstantsBikeRack.IS_EXISTENT.toString(), this.isExistent);
        map_bikeRack.put(ConstantsBikeRack.IS_COVERED.toString(), this.isCovered);
        return map_bikeRack;
    }

    public enum ConstantsBikeRack {
        POSTCODE("postcode"),
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

    public enum ConstantsCapacity {
        SMALL(0),
        MEDIUM(1),
        LARGE(2),
        GIGANTIC(3);

        private int capacity;

        ConstantsCapacity(int type) {
            this.capacity = type;
        }

        public int toInt() {
            return capacity;
        }
    }
}
