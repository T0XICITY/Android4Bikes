package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import de.thu.tpro.android4bikes.util.GeoLocationHelper;

public class Track {
    @Expose
    @SerializedName("author_googleID")
    private String author_googleID;
    @Expose
    @SerializedName("rating")
    private Rating rating;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("description")
    private String description;
    @Expose
    @SerializedName("firebaseID")
    private String firebaseID;
    @Expose
    @SerializedName("creationDate_unixtimestamp")
    private long creationDate_unixtimestamp;
    @Expose
    @SerializedName("distance_km")
    private int distance_km;
    @Expose
    @SerializedName("coarseGrainedPositions")
    private List<Position> coarseGrainedPositions;
    @Expose
    @SerializedName("hazardAlerts")
    private List<HazardAlert> hazardAlerts;
    @Expose
    @SerializedName("postcode")
    private String postcode;
    @Expose
    @SerializedName("isComplete")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return getCreationDate_unixtimestamp() == track.getCreationDate_unixtimestamp() &&
                getDistance_km() == track.getDistance_km() &&
                isComplete() == track.isComplete() &&
                getAuthor_googleID().equals(track.getAuthor_googleID()) &&
                getRating().equals(track.getRating()) &&
                getName().equals(track.getName()) &&
                getDescription().equals(track.getDescription()) &&
                getFirebaseID().equals(track.getFirebaseID()) &&
                getCoarseGrainedPositions().equals(track.getCoarseGrainedPositions()) &&
                getHazardAlerts().equals(track.getHazardAlerts()) &&
                getPostcode().equals(track.getPostcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthor_googleID(), getRating(), getName(), getDescription(), getFirebaseID(), getCreationDate_unixtimestamp(), getDistance_km(), getCoarseGrainedPositions(), getHazardAlerts(), getPostcode(), isComplete());
    }

    @Override
    public String toString() {
        return "Track{" +
                "author_googleID='" + author_googleID + '\'' +
                ", rating=" + rating +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", firebaseID='" + firebaseID + '\'' +
                ", creationDate_unixtimestamp=" + creationDate_unixtimestamp +
                ", distance_km=" + distance_km +
                ", coarseGrainedPositions=" + coarseGrainedPositions +
                ", hazardAlerts=" + hazardAlerts +
                ", postcode='" + postcode + '\'' +
                ", isComplete=" + isComplete +
                '}';
    }
}
