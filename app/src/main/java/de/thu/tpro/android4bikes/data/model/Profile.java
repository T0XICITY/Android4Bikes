package de.thu.tpro.android4bikes.data.model;

import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;

public class Profile {
    private String firstName;
    private String familyName;
    private String googleID;
    private int color;
    private int overallDistance;
    private List<Achievement> achievements; //TODO better representation

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Profile() {
    }

    public Profile(String firstName, String familyName, String firebaseAccountID, int color, int overallDistance, List<Achievement> achievements) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.googleID = firebaseAccountID;
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

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
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

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public enum ConstantsProfile {
        FIRSTNAME("firstname"),
        FAMILYNAME("familyname"),
        GOOGLEID("googleID"),
        COLOR("color"),
        OVERALLDISTANCE("overalldistance"),
        ACHIEVEMENTS("achievements");


        private String type;

        ConstantsProfile(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
