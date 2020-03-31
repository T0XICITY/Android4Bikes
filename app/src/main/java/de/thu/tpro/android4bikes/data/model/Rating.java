package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.HashMap;
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

    public Rating(Map<String, Object> map_rating) {
        this.difficulty = (int) map_rating.get(ConstantsRating.DIFFICULTY.toString());
        this.fun = (int) map_rating.get(ConstantsRating.FUN.toString());
        this.roadquality = (int) map_rating.get(ConstantsRating.ROADQUALITY.toString());
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
        return new JSONObject(this.toMap());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map_rating = new HashMap<>();

        map_rating.put(ConstantsRating.DIFFICULTY.toString(), this.difficulty);
        map_rating.put(ConstantsRating.FUN.toString(), this.fun);
        map_rating.put(ConstantsRating.ROADQUALITY.toString(), this.roadquality);

        return map_rating;
    }

    public enum ConstantsRating {
        DIFFICULTY("difficulty"),
        FUN("fun"),
        ROADQUALITY("roadquality");

        private String type;

        ConstantsRating(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
