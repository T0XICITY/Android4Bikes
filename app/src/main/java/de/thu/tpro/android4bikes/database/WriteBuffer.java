package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface WriteBuffer {
    void storeProfile(Profile profile);
    void updateProfile(Profile profile);
    void deleteProfile(Profile profile);
    void submitBikeRack(BikeRack bikeRack);
    void storeTrack(Track track);
    void deleteTrack(Track track);
    void submitHazardAlerts(HazardAlert hazardAlert);
    void addToUtilization(Position position);
}
