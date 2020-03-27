package de.thu.tpro.android4bikes.data.model;

import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.util.Android4BikesColor;

public class Profile {
    private String firstName;
    private String familyName;
    private String firebaseAccountID;
    private Android4BikesColor color;
    private int overallDistance;
    private Achievement.Level level;
    private long trackID;
    private List<Achievement> achievements;

    public Profile() {
    }


    public Profile(String firstName, String familyName, String firebaseAccountID, Android4BikesColor color, int overallDistance, Achievement.Level level, long trackID, List<Achievement> achievements) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.firebaseAccountID = firebaseAccountID;
        this.color = color;
        this.overallDistance = overallDistance;
        this.level = level;
        this.trackID = trackID;
        this.achievements = achievements;
    }

    public long getTrackID() {
        return trackID;
    }

    public void setTrackID(long trackID) {
        this.trackID = trackID;
    }

    public int getOverallDistance() {
        return overallDistance;
    }

    public void setOverallDistance(int overallDistance) {
        this.overallDistance = overallDistance;
    }

    public Achievement.Level getLevel() {
        return level;
    }

    public void setLevel(Achievement.Level level) {
        this.level = level;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
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

    public String getFirebaseAccountID() {
        return firebaseAccountID;
    }

    public void setFirebaseAccountID(String firebaseAccountID) {
        this.firebaseAccountID = firebaseAccountID;
    }
}
