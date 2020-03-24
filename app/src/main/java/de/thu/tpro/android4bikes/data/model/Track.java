package de.thu.tpro.android4bikes.data.model;

import java.util.List;

public class Track {
    private long author;
    private List<Rating> ratings;
    private String name;
    private String description;
    private List<Position> track;

    public Track() {
    }

    public Track(long author, List<Rating> ratings, String name, String description, List<Position> track) {
        this.author = author;
        this.ratings = ratings;
        this.name = name;
        this.description = description;
        this.track = track;
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

    public List<Position> getTrack() {
        return track;
    }

    public void setTrack(List<Position> track) {
        this.track = track;
    }
}
