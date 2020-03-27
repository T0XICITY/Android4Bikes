package de.thu.tpro.android4bikes.data.achievements;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.database.JsonRepresentation;

public abstract class Achievement implements JsonRepresentation {
    String name;
    long exp; //Experience Points
    double significance; //Multiplicator
    int icon= R.color.Amber400Dark;

    public Achievement(String name, long exp, double significance, int icon) {
        this.name = name;
        this.exp = exp;
        this.significance = significance;
        this.icon = icon;
    }

    public long getEP(){
        return (long)(exp*significance);
    }

}
