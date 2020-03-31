package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class HazardAlert implements JsonRepresentation {
    private HazardType type;
    private Position position;
    private long expiration_unixtimestamp;
    private int distanceOfInterest;
    private String firebaseID;

    public HazardAlert(HazardType type, Position position, long expiration_unixtimestamp, int distanceOfInterest, String firebaseID) {
        this.type = type;
        this.position = position;
        this.expiration_unixtimestamp = expiration_unixtimestamp;
        this.distanceOfInterest = distanceOfInterest;
        this.firebaseID = firebaseID;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public long getExpiration_unixtimestamp() {
        return expiration_unixtimestamp;
    }

    public void setExpiration_unixtimestamp(long expiration_unixtimestamp) {
        this.expiration_unixtimestamp = expiration_unixtimestamp;
    }

    public String getType() {
        return type.getType();
    }

    public void setType(HazardType type) {
        this.type = type;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
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
