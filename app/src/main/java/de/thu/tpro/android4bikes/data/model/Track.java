package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import java.util.List;
import java.util.Objects;

import de.thu.tpro.android4bikes.util.GeoLocationHelper;
import de.thu.tpro.android4bikes.util.UUIDGenerator;

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
    private double distance_km;
    @Expose
    @SerializedName("route")
    private DirectionsRoute route;
    @Expose
    @SerializedName("hazardAlerts")
    private List<HazardAlert> hazardAlerts;
    @Expose
    @SerializedName("startPosition")
    private Position startPosition;
    @Expose
    @SerializedName("endPosition")
    private Position endPosition;
    @Expose
    @SerializedName("isComplete")
    private boolean isComplete; //TODO: im UI Abfrage, ob Strecke in Bearbeitung auf dem Server ist.

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Track() {
    }

    public Track(String author_googleID, Rating rating, String name, String description, long creationDate_unixtimestamp, double distance_km, DirectionsRoute route, List<HazardAlert> hazardAlerts, Position startPosition, Position endPosition, boolean isComplete) {
        this.author_googleID = author_googleID;
        this.rating = rating;
        this.name = name;
        this.description = description;
        this.creationDate_unixtimestamp = creationDate_unixtimestamp;
        this.distance_km = distance_km;
        this.route = route;
        this.hazardAlerts = hazardAlerts;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.isComplete = isComplete;
        this.firebaseID = UUIDGenerator.generateUUID();
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Position endPosition) {
        this.endPosition = endPosition;
    }

    public void setDistance_km(double distance_km) {
        this.distance_km = distance_km;
    }

    public DirectionsRoute getRoute() {
        return route;
    }

    public void setRoute(DirectionsRoute route) {
        this.route = route;
    }

    public long getCreationDate_unixtimestamp() {
        return creationDate_unixtimestamp;
    }

    public void setCreationDate_unixtimestamp(long creationDate_unixtimestamp) {
        this.creationDate_unixtimestamp = creationDate_unixtimestamp;
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

    public double getDistance_km() {
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return creationDate_unixtimestamp == track.creationDate_unixtimestamp &&
                Double.compare(track.distance_km, distance_km) == 0 &&
                isComplete == track.isComplete &&
                Objects.equals(author_googleID, track.author_googleID) &&
                Objects.equals(rating, track.rating) &&
                Objects.equals(name, track.name) &&
                Objects.equals(description, track.description) &&
                Objects.equals(firebaseID, track.firebaseID) &&
                Objects.equals(route, track.route) &&
                Objects.equals(hazardAlerts, track.hazardAlerts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author_googleID, rating, name, description, firebaseID, creationDate_unixtimestamp, distance_km, route, hazardAlerts, isComplete);
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
                ", route=" + route +
                ", hazardAlerts=" + hazardAlerts +
                ", isComplete=" + isComplete +
                '}';
    }

    public enum ConstantsTrack {
        AUTHOR_GOOGLEID("author_googleID"),
        RATING("rating"),
        NAME("name"),
        DESCRIPTION("description"),
        ROUTE("route"),
        FIREBASEID("firebaseID"),
        TIMESTAMP("creationDate_unixtimestamp"),
        DISTANCE_KM("distance_km"),
        HAZARD_ALERTS("hazardAlerts"),
        STARTPOINT("startPosition"),
        ENDPOINT("endPosition"),
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
