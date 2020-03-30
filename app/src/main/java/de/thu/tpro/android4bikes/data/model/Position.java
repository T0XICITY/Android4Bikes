package de.thu.tpro.android4bikes.data.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;
import de.thu.tpro.android4bikes.exception.InvalidPositionException;

import static de.thu.tpro.android4bikes.data.model.Position.ConstantsPosition.LATITUDE;
import static de.thu.tpro.android4bikes.data.model.Position.ConstantsPosition.LONGITUDE;

public class Position implements JsonRepresentation {

    private String firebaseID;
    private double longitude;
    private double latitude;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     * should be used when there is no position available
     */
    public Position() {
        this.setDefaultLocation();
    }

    /**
     * constructor
     *
     * @param firebaseID
     * @param longitude
     * @param latitude
     */
    public Position(String firebaseID, double longitude, double latitude) {
        this.firebaseID = firebaseID;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Position(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Position(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    /**
     * returns whether the stored positioning data is valid
     *
     * @return is ther any valid positioning data available?
     */
    public boolean isPositioningDataAvailable() {
        //Double-Werte nie auf Gleichheit pruefen:
        return Double.compare(this.longitude, -103.907409) != 0 || Double.compare(this.latitude, -82.463046) != 0;
    }

    /**
     * get JSON representation of a position object
     *
     * @return json representation of a Position object as JSONObject
     */
    @Override
    public JSONObject toJSON() throws InvalidJsonException {
        JSONObject position = null;
        try {
            position = new JSONObject();
            position.put(ConstantsPosition.LATITUDE.toString(), this.latitude);
            position.put(ConstantsPosition.LATITUDE.toString(), this.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new InvalidJsonException();
        }
        return position;
    }

    /**
     * @return map representation of the position object
     * represented by a map. This should be used in combination
     * with FireStore.
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map_position = new HashMap<>();
        map_position.put(LONGITUDE.toString(), longitude);
        map_position.put(LATITUDE.toString(), latitude);
        return map_position;
    }

    /**
     * sets the default location when there is no positioning data available
     * the default location is a place in the antarctic
     */
    private void setDefaultLocation() {
        this.longitude = -103.907409; //Position in der Antarktis. Fuer unbekannte Standorte.
        this.latitude = -82.463046;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) throws InvalidPositionException {
        if (longitude >= -180 && longitude <= 180) {
            this.longitude = longitude;
        } else {
            throw new InvalidPositionException();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) throws InvalidPositionException {
        if (longitude >= 90 && longitude <= 90) {
            this.latitude = latitude;
        } else {
            throw new InvalidPositionException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.longitude, longitude) == 0 &&
                Double.compare(position.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }

    @Override
    public String toString() {
        return "Position{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    public enum ConstantsPosition {
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        POSITION("position");


        private String type;

        ConstantsPosition(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

}
