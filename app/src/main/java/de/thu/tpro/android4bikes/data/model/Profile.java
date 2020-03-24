package de.thu.tpro.android4bikes.data.model;

import de.thu.tpro.android4bikes.util.Android4BikesColor;

public class Profile {
    private String firstName;
    private String familyName;
    private long firebaseID;
    private Android4BikesColor color;

    public Profile() {
    }

    public Profile(String firstName, String familyName, long firebaseID, Android4BikesColor color) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.firebaseID = firebaseID;
        this.color = color;
    }

    public Android4BikesColor getColor() {
        return color;
    }

    public void setColor(Android4BikesColor color) {
        this.color = color;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public long getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(long firebaseID) {
        this.firebaseID = firebaseID;
    }
}
