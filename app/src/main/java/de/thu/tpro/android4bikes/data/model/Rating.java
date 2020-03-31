package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public class Rating implements JsonRepresentation {
    private int difficulty;
    private int fun;
    private int roadquality;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Rating() {
    }

    public Rating(int difficulty, int fun, int roadquality, String firebaseID) {
        this.difficulty = difficulty;
        this.fun = fun;
        this.roadquality = roadquality;

    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getFun() {
        return fun;
    }

    public void setFun(int fun) {
        this.fun = fun;
    }

    public int getRoadquality() {
        return roadquality;
    }

    public void setRoadquality(int roadquality) {
        this.roadquality = roadquality;
    }

    @Override
    public JSONObject toJSON() throws InvalidJsonException {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
