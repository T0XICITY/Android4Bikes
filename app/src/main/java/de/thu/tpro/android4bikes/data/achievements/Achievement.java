package de.thu.tpro.android4bikes.data.achievements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import de.thu.tpro.android4bikes.R;

public abstract class Achievement {
    @Expose
    @SerializedName("name")
    protected String name;
    @Expose
    @SerializedName("exp")
    protected long exp; //Experience Points
    @Expose
    @SerializedName("significance")
    protected double significance = 1.0d; //Multiplicator
    @Expose
    @SerializedName("icon")
    protected int icon = R.color.Amber400Dark;
    @Expose
    @SerializedName("classname")
    private String classname;

    public Achievement() {
        this.classname = getClass().getName();
    }

    public Achievement(String name, long exp, double significance, int icon) {
        this(); //call parameterless constructor
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
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return exp == that.exp &&
                Double.compare(that.significance, significance) == 0 &&
                icon == that.icon &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, exp, significance, icon);
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
