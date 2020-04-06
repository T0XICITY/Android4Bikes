package de.thu.tpro.android4bikes.data.achievements;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KmAchievement extends Achievement {
    private int kmGoal;

    public KmAchievement() {

    }

    public KmAchievement(String name, long exp, double significance, int icon, int kmGoal) {
        super(name, exp, significance, icon);
        this.kmGoal = kmGoal;
    }

    public boolean isAchieved(int overallkm) {
        return overallkm > kmGoal;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("exp", exp);
        map.put("significance", significance);
        map.put("icon", icon);
        map.put("kmgoal", kmGoal);
        return map;
    }
}
