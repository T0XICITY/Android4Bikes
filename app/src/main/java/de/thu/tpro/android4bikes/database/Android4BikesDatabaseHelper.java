package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikesDatabaseHelper {
    BikeRack getBikeRack(Position position);

    Profile getProfile(long firebaseID);

    Track getTrack(long trackID);

    List<Position> getAllPositions();

    HazardAlert getHazardAlert();

    void savePosition(Position position);
}
