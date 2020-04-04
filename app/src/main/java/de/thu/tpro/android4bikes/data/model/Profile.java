package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import de.thu.tpro.android4bikes.data.achievements.Achievement;

public class Profile {
    @Expose
    @SerializedName("firstName")
    private String firstName;
    @Expose
    @SerializedName("familyName")
    private String familyName;
    @Expose
    @SerializedName("googleID")
    private String googleID;
    @Expose
    @SerializedName("color")
    private int color;
    @Expose
    @SerializedName("overallDistance")
    private int overallDistance;
    @Expose
    @SerializedName("achievements")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return color == profile.color &&
                achievements.equals(profile.achievements) &&
                overallDistance == profile.overallDistance &&
                firstName.equals(profile.firstName) &&
                familyName.equals(profile.familyName) &&
                googleID.equals(profile.googleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, familyName, googleID, color, overallDistance, achievements);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "firstName='" + firstName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", googleID='" + googleID + '\'' +
                ", color=" + color +
                ", overallDistance=" + overallDistance +
                ", achievements=" + achievements +
                '}';
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
