package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import de.thu.tpro.android4bikes.util.GeoLocationHelper;
import de.thu.tpro.android4bikes.util.UUIDGenerator;

public class BikeRack {
    @Expose
    @SerializedName("firebaseID")
    private String firebaseID;
    @Expose
    @SerializedName("position")
    private Position position;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("capacity")
    private ConstantsCapacity capacity;
    @Expose
    @SerializedName("hasBikeCharging")
    private boolean hasBikeCharging;
    @Expose
    @SerializedName("isExistent")
    private boolean isExistent;
    @Expose
    @SerializedName("isCovered")
    private boolean isCovered;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
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

    /**
     * Constructor generating automatically a UUID as FireBaseID.
     *
     * @param position
     * @param name
     * @param capacity
     * @param hasBikeCharging
     * @param isExistent
     * @param isCovered
     */
    public BikeRack(Position position, String name, ConstantsCapacity capacity, boolean hasBikeCharging, boolean isExistent, boolean isCovered) {
        this.position = position;
        this.name = name;
        this.capacity = capacity;
        this.hasBikeCharging = hasBikeCharging;
        this.isExistent = isExistent;
        this.isCovered = isCovered;
        this.firebaseID = UUIDGenerator.generateUUID();
    }

    /**
     * Test-constructor generating automatically a UUID as FireBaseID for voice-control orders.
     *
     * @param position
     *
     */
    public BikeRack(Position position) {
        this.position = position;
        this.name = "BikeRack";
        this.capacity = ConstantsCapacity.MEDIUM;
        this.hasBikeCharging = false;
        this.isExistent = true;
        this.isCovered = false;
        this.firebaseID = UUIDGenerator.generateUUID();
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

    public boolean isHasBikeCharging() {
        return hasBikeCharging;
    }

    public void setHasBikeCharging(boolean hasBikeCharging) {
        this.hasBikeCharging = hasBikeCharging;
    }

    @Override
    public String toString() {
        return "BikeRack{" +
                "firebaseID='" + firebaseID + '\'' +
                ", position=" + position +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", hasBikeCharging=" + hasBikeCharging +
                ", isExistent=" + isExistent +
                ", isCovered=" + isCovered +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BikeRack)) return false;
        BikeRack bikeRack = (BikeRack) o;
        return isHasBikeCharging() == bikeRack.isHasBikeCharging() &&
                isExistent() == bikeRack.isExistent() &&
                isCovered() == bikeRack.isCovered() &&
                getFirebaseID().equals(bikeRack.getFirebaseID()) &&
                getPosition().equals(bikeRack.getPosition()) &&
                getName().equals(bikeRack.getName()) &&
                getCapacity() == bikeRack.getCapacity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirebaseID(), getPosition(), getName(), getCapacity(), isHasBikeCharging(), isExistent(), isCovered());
    }

    public enum ConstantsBikeRack {
        FIREBASEID("firebaseID"),
        POSTCODE("postcode"),
        BIKE_RACK_NAME("name"),
        CAPACITY("capacity"),
        IS_EBIKE_STATION("hasBikeCharging"),
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
