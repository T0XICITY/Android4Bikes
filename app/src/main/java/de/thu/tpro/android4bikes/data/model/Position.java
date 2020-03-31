package de.thu.tpro.android4bikes.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;
import de.thu.tpro.android4bikes.exception.InvalidPositionException;

public class Position implements JsonRepresentation {
    //Constants------------------------------------------------
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String POSITION = "position";
    private String firebaseID;
    //Constants------------------------------------------------
    private double longitude;
    private double latitude;

    /**
     * should be used when there is no position available
     */
    public Position() {
        this.setDefaultLocation();
    }

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

    /**
     * sets the default location when there is no positioning data available
     * the default location is a place in the antarctic
     */
    private void setDefaultLocation() {
        this.longitude = -103.907409; //Position in der Antarktis. Fuer unbekannte Standorte.
        this.latitude = -82.463046;
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
    public JSONObject getJsonRepresentation() throws InvalidJsonException {
        JSONObject position = null;
        try {
            position = new JSONObject();
            position.put(Position.LATITUDE, this.latitude);
            position.put(Position.LONGITUDE, this.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new InvalidJsonException();
        }
        return position;
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

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }
}
