package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public class CouchDbHelper {
    private CouchDB couchDB;

    public CouchDbHelper() {
        couchDB = new CouchDB();
    }

    public BikeRack getBikeRack(Position position) {
        return null;
    }

    public Profile getProfile(long firebaseID) {
        return null;
    }

    public Track getTrack(long trackID) {
        return null;
    }

    public HazardAlert getHazardAlert() {
        return null;
    }
}
