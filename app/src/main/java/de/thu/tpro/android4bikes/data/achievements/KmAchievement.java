package de.thu.tpro.android4bikes.data.achievements;

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
}
