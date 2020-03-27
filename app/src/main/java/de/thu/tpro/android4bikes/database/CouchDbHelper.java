package de.thu.tpro.android4bikes.database;

import com.google.gson.Gson;

import org.json.JSONObject;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.Android4BikesColor;


public class CouchDbHelper implements Android4BikesDatabaseHelper {
    private CouchDB couchDB;
    private Gson gson;

    public CouchDbHelper() {
        couchDB = CouchDB.getInstance();
        gson = new Gson();
    }

    @Override
    public BikeRack getBikeRack(Position position) {
        BikeRack bikeRack = null;

        return bikeRack;
    }

    @Override
    public Profile getProfile(long firebaseID) {
        Profile profile = null;
        JSONObject jsonObject=null;

        profile = gson.fromJson(jsonObject.toString(),Profile.class);
        return profile;
    }

    @Override
    public Track getTrack(long trackID) {
        Track track = null;

        return track;
    }

    @Override
    public HazardAlert getHazardAlert() {
        HazardAlert hazardAlert = null;

        return hazardAlert;
    }

    @Override
    public Position getPosition() {
        Position position = null;

        return position;
    }

    @Override
    public Android4BikesColor getAndroid4BikeColor() {
        Android4BikesColor android4BikesColor = null;

        return android4BikesColor;
    }
}
