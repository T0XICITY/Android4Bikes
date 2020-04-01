package de.thu.tpro.android4bikes.data.achievements;

import java.util.Objects;

import de.thu.tpro.android4bikes.R;

public abstract class Achievement {
    String name;
    long exp; //Experience Points
    double significance = 1.0d; //Multiplicator
    int icon = R.color.Amber400Dark;

    public Achievement() {

    }

    public Achievement(String name, long exp, double significance, int icon) {
        this.name = name;
        this.exp = exp;
        this.significance = significance;
        this.icon = icon;
    }

    public long getExp() {
        return (int) (exp * significance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;
        Achievement that = (Achievement) o;
        return getExp() == that.getExp() &&
                Double.compare(that.significance, significance) == 0 &&
                icon == that.icon &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getExp(), significance, icon);
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "name='" + name + '\'' +
                ", exp=" + exp +
                ", significance=" + significance +
                ", icon=" + icon +
                '}';
    }
}
