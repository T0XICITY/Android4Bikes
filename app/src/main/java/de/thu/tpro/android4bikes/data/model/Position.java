package de.thu.tpro.android4bikes.data.model;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import de.thu.tpro.android4bikes.exception.InvalidPositionException;

public class Position {

    @Expose
    @SerializedName("longitude")
    private double longitude;
    @Expose
    @SerializedName("latitude")
    private double latitude;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     * should be used when there is no position available
     */
    public Position() {
        this.setDefaultLocation();
    }

    /**
     * constructor using all fields
     *
     * @param latitude
     * @param longitude
     */
    public Position(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
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
     * sets the default location when there is no positioning data available
     * the default location is a place in the antarctic
     */
    private void setDefaultLocation() {
        this.longitude = -103.907409; //Position in der Antarktis. Fuer unbekannte Standorte.
        this.latitude = -82.463046;
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

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return Double.compare(position.getLongitude(), getLongitude()) == 0 &&
                Double.compare(position.getLatitude(), getLatitude()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLongitude(), getLatitude());
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
