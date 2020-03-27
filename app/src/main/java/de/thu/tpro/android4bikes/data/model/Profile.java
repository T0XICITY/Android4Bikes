package de.thu.tpro.android4bikes.data.model;

import android.graphics.Color;

import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;

public class Profile {
    private String firstName;
    private String familyName;
    private String firebaseAccountID;
    private int color;
    private int overallDistance;
    private List<Integer> achievements; //TODO better representation

    public Profile() {
    }


    public Profile(String firstName, String familyName, String firebaseAccountID, int color, int overallDistance, List<Integer> achievements) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.firebaseAccountID = firebaseAccountID;
        this.color = color;
        this.overallDistance = overallDistance;
        this.achievements = achievements;
    }

    public int getOverallDistance() {
        return overallDistance;
    }

    public void setOverallDistance(int overallDistance) {
        this.overallDistance = overallDistance;
    }

    public List<Integer> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Integer> achievements) {
        this.achievements = achievements;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
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

    public String getFirebaseAccountID() {
        return firebaseAccountID;
    }

    public void setFirebaseAccountID(String firebaseAccountID) {
        this.firebaseAccountID = firebaseAccountID;
    }
}
