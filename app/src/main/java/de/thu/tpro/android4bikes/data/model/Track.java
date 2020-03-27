package de.thu.tpro.android4bikes.data.model;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;

public class Track implements JsonRepresentation {
    private long author;
    private List<Rating> ratings;
    private String name;
    private String description;
    private List<GeoPoint> track;
    private String firebaseID;

    public Track() {
    }

    public Track(long author, List<Rating> ratings, String name, String description, List<GeoPoint> track, String firebaseID) {
        this.author = author;
        this.ratings = ratings;
        this.name = name;
        this.description = description;
        this.track = track;
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

    public List<GeoPoint> getTrack() {
        return track;
    }

    public void setTrack(List<GeoPoint> track) {
        this.track = track;
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }

    @Override
    public Map<String, Object> getMapRepresentation() {
        return null;
    }
}
