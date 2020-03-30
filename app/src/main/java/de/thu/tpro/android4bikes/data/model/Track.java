package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public class Track implements JsonRepresentation {
    private long author;
    private List<Rating> ratings;
    private String name;
    private String description;
    private String firebaseID;
    private List<Position> coarseGrainedPositions;

    public Track() {
    }

    public Track(long author, List<Rating> ratings, String name, String description, String firebaseID, List<Position> track) {
        this.author = author;
        this.ratings = ratings;
        this.name = name;
        this.description = description;
        this.firebaseID = firebaseID;
        this.coarseGrainedPositions = track;
    }

    public List<Position> getTrack() {
        return coarseGrainedPositions;
    }

    public void setTrack(List<Position> track) {
        this.coarseGrainedPositions = track;
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

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        List<Map<String, Object>> trackpositions = new ArrayList<>();
        for (Position pos : coarseGrainedPositions) {
            trackpositions.add(pos.toMap());
        }

        map.put(ConstantsTrack.AUTHOR.toString(), author);
        map.put(ConstantsTrack.TRACK.toString(), trackpositions);

        return map;
    }

    public enum ConstantsTrack {
        AUTHOR("author"),
        RATINGS("ratings"),
        NAME("name"),
        DESCRIPTION("description"),
        TRACK("track"),
        FIREBASEID("firebaseid");


        private String type;

        ConstantsTrack(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
