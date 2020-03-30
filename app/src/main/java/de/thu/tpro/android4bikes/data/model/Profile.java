package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public class Profile implements JsonRepresentation {
    private String firstName;
    private String familyName;
    private String googleID;
    private int color;
    private int overallDistance;
    private List<Achievement> achievements; //TODO better representation
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
    public JSONObject toJSON() {
        return new JSONObject(this.toMap());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("firstname", firstName);
        map.put("familyname", familyName);
        map.put("firebaseaccountid", firebaseAccountID);
        map.put("color", color);
        map.put("overalldistance", overallDistance);
        List<Map<String, Object>> list_achievements = new ArrayList<>();
        for (Achievement a : achievements) {
            list_achievements.add(a.toMap());
        }
        map.put("achievements", list_achievements);
        return map;
    }

    public enum ConstantsProfile {
        FIRSTNAME("firstname"),
        FAMILYNAME("familyname"),
        FIREBASEACCOUNTID("firebaseaccountid"),
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
