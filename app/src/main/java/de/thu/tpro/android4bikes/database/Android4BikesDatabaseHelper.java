package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.Android4BikesColor;

public interface Android4BikesDatabaseHelper {
    BikeRack getBikeRack(Position position);

    Profile getProfile(long firebaseID);

    Track getTrack(long trackID);

    Position getAllPositions();

    Android4BikesColor getAndroid4BikeColor();

    HazardAlert getHazardAlert();
}
