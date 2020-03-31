package de.thu.tpro.android4bikes.data.model;

import java.util.List;

import de.thu.tpro.android4bikes.util.GeoLocationHelper;

public class Track {
    private String author_googleID;
    private Rating rating;
    private String name;
    private String description;
    private String firebaseID;
    private long creationDate_unixtimestamp;
    private int distance_km;
    private List<Position> coarseGrainedPositions;
    private List<HazardAlert> hazardAlerts;
    private String postcode;
    private boolean isComplete; //TODO: im UI Abfrage, ob Strecke in Bearbeitung auf dem Server ist.

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Track() {
    }

    /**
     * constructor using all parameters
     *
     * @param author_googleID
     * @param rating
     * @param name
     * @param description
     * @param firebaseID
     * @param creationDate_unixtimestamp
     * @param distance_km
     * @param coarseGrainedPositions
     * @param hazardAlerts
     * @param isComplete
     */
    public Track(String author_googleID, Rating rating, String name, String description, String firebaseID, long creationDate_unixtimestamp, int distance_km, List<Position> coarseGrainedPositions, List<HazardAlert> hazardAlerts, boolean isComplete) {
        this.author_googleID = author_googleID;
        this.rating = rating;
        this.name = name;
        this.description = description;
        this.firebaseID = firebaseID;
        this.creationDate_unixtimestamp = creationDate_unixtimestamp;
        this.distance_km = distance_km;
        this.coarseGrainedPositions = coarseGrainedPositions;
        this.hazardAlerts = hazardAlerts;
        this.isComplete = isComplete;

        if (isComplete) {
            this.postcode = GeoLocationHelper.convertPositionToPostcode(coarseGrainedPositions.get(0));
        }
    }

    public List<Position> getCoarseGrainedPositions() {
        return coarseGrainedPositions;
    }

    public void setCoarseGrainedPositions(List<Position> coarseGrainedPositions) {
        this.coarseGrainedPositions = coarseGrainedPositions;
    }

    public long getCreationDate_unixtimestamp() {
        return creationDate_unixtimestamp;
    }

    public void setCreationDate_unixtimestamp(long creationDate_unixtimestamp) {
        this.creationDate_unixtimestamp = creationDate_unixtimestamp;
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

    public String getAuthor_googleID() {
        return author_googleID;
    }

    public void setAuthor_googleID(String author_googleID) {
        this.author_googleID = author_googleID;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
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

    public int getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(int distance_km) {
        this.distance_km = distance_km;
    }

    public List<HazardAlert> getHazardAlerts() {
        return hazardAlerts;
    }

    public void setHazardAlerts(List<HazardAlert> hazardAlerts) {
        this.hazardAlerts = hazardAlerts;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }


    public enum ConstantsTrack {
        AUTHOR_GOOGLEID("author_googleID"),
        RATING("rating"),
        NAME("name"),
        DESCRIPTION("description"),
        COARSEGRAINEDPOSITIONS("coarseGrainedPositions"),
        FIREBASEID("firebaseID"),
        TIMESTAMP("creationDate_unixtimestamp"),
        DISTANCE_KM("distance_km"),
        HAZARD_ALERTS("hazardAlerts"),
        POSTCODE("postcode"),
        IS_COMPLETE("isComplete");


        private String type;

        ConstantsTrack(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
