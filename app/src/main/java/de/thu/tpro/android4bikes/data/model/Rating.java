package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public class Rating implements JsonRepresentation {
    private long author;
    private int difficulty;
    private String comment;
    private int fun;
    private int roadquality;
    private String firebaseID;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Rating() {
    }

    public Rating(long author, int difficulty, String comment, int fun, int roadquality, String firebaseID) {
        this.author = author;
        this.difficulty = difficulty;
        this.comment = comment;
        this.fun = fun;
        this.roadquality = roadquality;
        this.firebaseID = firebaseID;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public long getAuthor() {
        return author;
    }

    public void setAuthor(long author) {
        this.author = author;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
