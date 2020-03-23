package de.thu.tpro.android4bikes.data.model;

public class Profil {
    private String firstName;
    private String familyName;
    private long firebaseID;

    public Profil() {
    }

    public Profil(String firstName, String familyName, long firebaseID) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.firebaseID = firebaseID;
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
