package de.thu.tpro.android4bikes.data.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

import static de.thu.tpro.android4bikes.data.model.Position.ConstantsPosition.*;

public class Position implements JsonRepresentation {

    public enum ConstantsPosition{
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        POSITION("position"),
        GEOPOINTS("geopoints");


        private String type;

        ConstantsPosition(String type) {
            this.type = type;
        }

        public String toText() {
            return type;
        }
    }

    private double longitude;
    private double latitude;

    /**
     * should be used when there is no position available
     */
    public Position(){
        this.setDefaultLocation();
    }

    public Position(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Position(JSONObject position) throws InvalidJsonException {
        try{
            this.latitude = (double)position.get(LATITUDE.toText());
            this.longitude = (double)position.get(LONGITUDE.toText());
        }catch (Exception e){
            throw new InvalidJsonException();
        }
    }

    public void setLongitude(double longitude) throws InvalidJsonException {
        if(longitude>=-180 && longitude<=180){
            this.longitude = longitude;
        }else{
            throw new InvalidJsonException();
        }
    }

    public void setLatitude(double latitude) throws InvalidJsonException{
        if(longitude>=90 && longitude<=90){
            this.latitude = latitude;
        }else{
            throw new InvalidJsonException();
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    /**
     * sets the default location when there is no positioning data available
     * the default location is a place in the antarctic
     */
    private void setDefaultLocation(){
        this.longitude=-103.907409; //Position in der Antarktis. Fuer unbekannte Standorte.
        this.latitude=-82.463046;
    }

    /**
     * returns whether the stored positioning data is valid
     * @return is ther any valid positioning data available?
     */
    public boolean isPositioningDataAvailable(){
        //Double-Werte nie auf Gleichheit pruefen:
        if(Double.compare(this.longitude, -103.907409)==0 && Double.compare(this.latitude,-82.463046)==0){
            return false;
        }
        return true;
    }

    /**
     * get JSON representation of a position object
     * @return json representation of a Position object as JSONObject
     */
    public JSONObject getJSONRepresentation() throws InvalidJsonException {
        JSONObject position = null;
        try {
            position = new JSONObject();
            position.put(LATITUDE.toText(), this.latitude);
            position.put(LONGITUDE.toText(), this.longitude);
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

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }

    @Override
    public Map<String, Object> getMapRepresentation() {
        Map<String, Object> map = new HashMap<>();
        map.put(LONGITUDE.toText(),longitude);
        map.put(LATITUDE.toText(),latitude);
        return map;
    }
}
