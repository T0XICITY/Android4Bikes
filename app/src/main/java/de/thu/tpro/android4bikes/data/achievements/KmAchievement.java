package de.thu.tpro.android4bikes.data.achievements;

import org.json.JSONObject;

import java.util.Map;

public class KmAchievement extends Achievement {
    private int kmGoal;

    public KmAchievement(String name, long exp, double significance, int icon, int kmGoal) {
        super(name, exp, significance, icon);
        this.kmGoal = kmGoal;
    }

    public boolean isAchieved(int overallkm) {
        if (overallkm>kmGoal){
               return true;
        }
        return false;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }

    @Override
    public Map<String, Object> getMapRepresentation() {
        return null;
    }
}
