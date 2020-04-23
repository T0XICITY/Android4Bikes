package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import de.thu.tpro.android4bikes.util.GeoLocationHelper;
import de.thu.tpro.android4bikes.util.UUIDGenerator;

public class HazardAlert {
    @Expose
    @SerializedName("type")
    private HazardType type;
    @Expose
    @SerializedName("position")
    private Position position;
    @Expose
    @SerializedName("expiryTimestamp")
    private long expiryTimestamp;
    @Expose
    @SerializedName("distanceOfInterest")
    private int distanceOfInterest;
    @Expose
    @SerializedName("firebaseID")
    private String firebaseID;
    @Expose
    @SerializedName("isExistent")
    private boolean isExistent;


    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public HazardAlert() {

    }

    public HazardAlert(HazardType type, Position position, long expiryTimestamp, int distanceOfInterest, String firebaseID, boolean isExistent) {
        this.type = type;
        this.isExistent = isExistent;
        this.position = position;
        this.expiryTimestamp = expiryTimestamp;
        this.distanceOfInterest = distanceOfInterest;
        this.firebaseID = firebaseID;
    }

    /**
     * Constructor generating automatically a UUID as FireBaseID.
     *
     * @param type
     * @param position
     * @param expiryTimestamp
     * @param distanceOfInterest
     * @param isExistent
     */
    public HazardAlert(HazardType type, Position position, long expiryTimestamp, int distanceOfInterest, boolean isExistent) {
        this.type = type;
        this.position = position;
        this.expiryTimestamp = expiryTimestamp;
        this.distanceOfInterest = distanceOfInterest;
        this.isExistent = isExistent;
        this.firebaseID = UUIDGenerator.generateUUID();
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

    public void setGeoPoint(Position position) {
        this.position = position;
    }

    public int getType() {
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

    public boolean isExistent() {
        return isExistent;
    }

    public void setExistent(boolean existent) {
        isExistent = existent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardAlert that = (HazardAlert) o;
        return expiryTimestamp == that.expiryTimestamp &&
                distanceOfInterest == that.distanceOfInterest &&
                isExistent == that.isExistent &&
                type == that.type &&
                Objects.equals(position, that.position) &&
                Objects.equals(firebaseID, that.firebaseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, position, expiryTimestamp, distanceOfInterest, firebaseID, isExistent);
    }

    @Override
    public String toString() {
        return "HazardAlert{" +
                "type=" + type +
                ", position=" + position +
                ", expiryTimestamp=" + expiryTimestamp +
                ", distanceOfInterest=" + distanceOfInterest +
                ", firebaseID='" + firebaseID + '\'' +
                ", isExistent=" + isExistent +
                '}';
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
        DAMAGED_ROAD(6),
        ICY_ROAD(5),
        SLIPPERY_ROAD(4),
        ROADKILL(3),
        ROCKFALL(2),
        GENERAL(1);

        private int type;

        HazardType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
