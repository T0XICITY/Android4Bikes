package de.thu.tpro.android4bikes.data.model;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class HazardAlert implements JsonRepresentation {
    private HazardType type;
    private GeoPoint geoPoint;
    private Date expiryDate;
    private int distanceOfInterest;
    private String firebaseID;

    public HazardAlert(HazardType type, GeoPoint geoPoint, Date expiryDate, int distanceOfInterest, String firebaseID) {
        this.type = type;
        this.geoPoint = geoPoint;
        this.expiryDate = expiryDate;
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

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getType() {
        return type.getType();
    }

    public void setType(HazardType type) {
        this.type = type;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
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
