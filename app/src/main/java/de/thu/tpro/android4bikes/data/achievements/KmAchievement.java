package de.thu.tpro.android4bikes.data.achievements;

import de.thu.tpro.android4bikes.data.Achievement;

public class KmAchievement extends Achievement {
    private Level level;
    private int kmGoal;

    public KmAchievement() {
    }

    public KmAchievement(Level level, int kmGoal) {
        this.level = level;
        this.kmGoal = kmGoal;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getKmGoal() {
        return kmGoal;
    }

    public void setKmGoal(int kmGoal) {
        this.kmGoal = kmGoal;
    }
}
