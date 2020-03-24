package de.thu.tpro.android4bikes.data.model;

import java.util.List;

import de.thu.tpro.android4bikes.data.Achievement;
import de.thu.tpro.android4bikes.util.Android4BikesColor;

public class Profile {
    private String firstName;
    private String familyName;
    private long firebaseID;
    private Android4BikesColor color;
    private int overallDistance;
    private Achievement.Level level;
    private List<Achievement> achievements;

    public Profile() {
    }

    public Profile(String firstName, String familyName, long firebaseID, Android4BikesColor color, int overallDistance, Achievement.Level level, List<Achievement> achievements) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.firebaseID = firebaseID;
        this.color = color;
        this.overallDistance = overallDistance;
        this.level = level;
        this.achievements = achievements;
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

    public long getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(long firebaseID) {
        this.firebaseID = firebaseID;
    }
}
