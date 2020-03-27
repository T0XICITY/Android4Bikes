package de.thu.tpro.android4bikes.database;

import com.google.firebase.firestore.GeoPoint;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikesDatabaseHelper {
    BikeRack getBikeRack(GeoPoint geoPoint);

    Profile getProfile(long firebaseID);

    Track getTrack(long trackID);

    HazardAlert getHazardAlert();
}
