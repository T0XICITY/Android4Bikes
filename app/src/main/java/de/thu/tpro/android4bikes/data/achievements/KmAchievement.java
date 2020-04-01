package de.thu.tpro.android4bikes.data.achievements;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KmAchievement)) return false;
        if (!super.equals(o)) return false;
        KmAchievement that = (KmAchievement) o;
        return kmGoal == that.kmGoal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kmGoal);
    }

    @Override
    public String toString() {
        return "KmAchievement{" +
                "kmGoal=" + kmGoal +
                '}';
    }
}
