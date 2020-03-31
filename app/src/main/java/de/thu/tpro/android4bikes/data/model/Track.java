package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.util.GeoLocationHelper;

public class Track implements JsonRepresentation {
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


    public Track(Map<String, Object> map_track) {
        this.hazardAlerts = new ArrayList<>();
        this.coarseGrainedPositions = new ArrayList<>();
        this.author_googleID = String.valueOf(map_track.get(ConstantsTrack.AUTHOR_GOOGLEID.toString()));
        this.rating = new Rating((Map<String, Object>) map_track.get(ConstantsTrack.RATING.toString()));
        this.name = String.valueOf(map_track.get(ConstantsTrack.NAME.toString()));
        this.description = String.valueOf(map_track.get(ConstantsTrack.DESCRIPTION.toString()));
        List<Object> list_coarsedGrainedPositions = (List<Object>) map_track.get(ConstantsTrack.HAZARD_ALERTS.toString());
        for (Object coarsedGrainedPosition : list_coarsedGrainedPositions) {
            this.coarseGrainedPositions.add(new Position((Map<String, Object>) coarsedGrainedPosition));
        }
        this.firebaseID = String.valueOf(map_track.get(ConstantsTrack.FIREBASEID.toString()));
        this.creationDate_unixtimestamp = (long) map_track.get(ConstantsTrack.TIMESTAMP.toString());
        this.distance_km = (int) map_track.get(ConstantsTrack.DISTANCE_KM.toString());
        List<Object> list_hazardalertobjects = (List<Object>) map_track.get(ConstantsTrack.HAZARD_ALERTS.toString());
        for (Object hazardalertobject : list_hazardalertobjects) {
            this.hazardAlerts.add(new HazardAlert((Map<String, Object>) hazardalertobject));
        }
        this.postcode = String.valueOf(map_track.get(ConstantsTrack.POSTCODE.toString()));
        this.isComplete = (boolean) map_track.get(ConstantsTrack.IS_COMPLETE.toString());
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

    @Override
    public JSONObject toJSON() {
        return new JSONObject(this.toMap());
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map_track = new HashMap<>();

        List<Map<String, Object>> list_positionscoarsegrained = new ArrayList<>();
        for (Position pos : this.coarseGrainedPositions) {
            list_positionscoarsegrained.add(pos.toMap());
        }

        List<Map<String, Object>> list_hazardalerts = new ArrayList<>();
        for (HazardAlert hazardAlert : this.hazardAlerts) {
            list_hazardalerts.add(hazardAlert.toMap());
        }

        map_track.put(ConstantsTrack.AUTHOR_GOOGLEID.toString(), this.author_googleID);
        map_track.put(ConstantsTrack.RATING.toString(), this.rating.toMap());
        map_track.put(ConstantsTrack.NAME.toString(), this.name);
        map_track.put(ConstantsTrack.DESCRIPTION.toString(), this.description);
        map_track.put(ConstantsTrack.COARSEGRAINEDPOSITIONS.toString(), list_positionscoarsegrained);
        map_track.put(ConstantsTrack.FIREBASEID.toString(), this.firebaseID);
        map_track.put(ConstantsTrack.TIMESTAMP.toString(), this.creationDate_unixtimestamp);
        map_track.put(ConstantsTrack.DISTANCE_KM.toString(), this.distance_km);
        map_track.put(ConstantsTrack.HAZARD_ALERTS.toString(), list_hazardalerts);
        map_track.put(ConstantsTrack.POSTCODE.toString(), this.postcode);
        map_track.put(ConstantsTrack.IS_COMPLETE.toString(), this.isComplete);
        return map_track;
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
