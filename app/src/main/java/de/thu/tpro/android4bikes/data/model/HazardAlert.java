package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;
import de.thu.tpro.android4bikes.util.GeoLocationHelper;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class HazardAlert implements JsonRepresentation {
    private HazardType type;
    private Position position;
    private String postcode;
    private long expiryTimestamp;
    private int distanceOfInterest;
    private String firebaseID;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public HazardAlert() {

    }

    public HazardAlert(HazardType type, Position position, long expiryTimestamp, int distanceOfInterest, String firebaseID) {
        this.type = type;
        this.position = position;
        this.expiryTimestamp = expiryTimestamp;
        this.distanceOfInterest = distanceOfInterest;
        this.firebaseID = firebaseID;
        this.postcode = GeoLocationHelper.convertPositionToPostcode(this.position);
    }

    public HazardAlert(Map<String, Object> hazardalertobject) {
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public int getDistanceOfInterest() {
        return distanceOfInterest;
    }

    public void setDistanceOfInterest(int distanceOfInterest) {
        this.distanceOfInterest = distanceOfInterest;
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

    public void setGeoPoint(Position position) {
        this.position = position;
    }

    public String getType() {
        return type.getType();
    }

    public void setType(HazardType type) {
        this.type = type;
    }

    public long getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(long expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }


    @Override
    public JSONObject toJSON() throws InvalidJsonException {
        return null;
    }


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map_Hazards = new HashMap<>();
        map_Hazards.put(ConstantsHazardAlert.TYPE.toString(), type);
        map_Hazards.put(ConstantsHazardAlert.POSITION.toString(), position);
        map_Hazards.put(ConstantsHazardAlert.POSTCODE.toString(), postcode);
        map_Hazards.put(ConstantsHazardAlert.EXPIRYTIMESTAMP.toString(), expiryTimestamp);
        map_Hazards.put(ConstantsHazardAlert.DISTANCEOFINTEREST.toString(), distanceOfInterest);
        map_Hazards.put(ConstantsHazardAlert.FIREBASEID.toString(), firebaseID);
        return map_Hazards;
    }

    public enum ConstantsHazardAlert {
        POSTCODE("postcode"),
        TYPE("type"),
        POSITION("position"),
        EXPIRYTIMESTAMP("expiryTimestamp"),
        DISTANCEOFINTEREST("distanceOfInterest"),
        FIREBASEID("firebaseID");

        private String type;

        ConstantsHazardAlert(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

    public enum HazardType {
        DAMAGED_ROAD(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_DamagedRoad)),
        ICY_ROAD(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_IcyRoad)),
        SLIPPERY_ROAD(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_SlipperyRoad)),
        ROADKILL(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_Roadkill)),
        ROCKFALL(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_Rockfall)),
        GENERAL(""); //todo

        private String type;

        HazardType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
