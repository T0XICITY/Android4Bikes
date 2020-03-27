package de.thu.tpro.android4bikes.data.model;

public class BikeRack {
    private Position position;
    private String name;
    private int capacity;
    private boolean hasBikeCharging;
    private boolean isCovered;
    private String firebaseID;

    public BikeRack() {
    }

    public BikeRack(Position position, String name, int capacity, boolean hasBikeCharging, boolean isCovered, String firebaseID) {
        this.position = position;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
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
}
