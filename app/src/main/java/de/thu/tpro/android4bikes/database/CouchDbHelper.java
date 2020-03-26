package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public class CouchDbHelper implements Android4BikesDatabaseHelper{
    private CouchDB couchDB;

    public CouchDbHelper() {
        couchDB = new CouchDB();
    }

    @Override
    public BikeRack getBikeRack(Position position) {
        return null;
    }

    @Override
    public Profile getProfile(long firebaseID) {
        return null;
    }

    @Override
    public Track getTrack(long trackID) {
        return null;
    }

    @Override
    public HazardAlert getHazardAlert() {
        return null;
    }
}
