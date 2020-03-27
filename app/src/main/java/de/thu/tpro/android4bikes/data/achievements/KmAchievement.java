package de.thu.tpro.android4bikes.data.achievements;

import org.json.JSONObject;

public class KmAchievement extends Achievement {
    private Level level;
    private int kmGoal;

    public KmAchievement() {
    }

    public KmAchievement(int kmGoal) {
        this.kmGoal = kmGoal;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }
}
