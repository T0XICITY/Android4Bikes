package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Database {
    void storeProfile(Profile profile);

    Profile readProfile(String googleID);

    void updateProfile(Profile profile);

    void deleteProfile(String googleID);

    void deleteProfile(Profile profile);

    void submitBikeRack(BikeRack bikeRack);

    List<BikeRack> readBikeRacks(String postcode);

    void submitTrack(Track track);

    List<Track> readTracks(String postcode); //isTrackComplete ???!!!

    void submitHazardAlerts(HazardAlert hazardAlert);

    List<HazardAlert> readHazardAlerts(String postcode);

    void addToUtilization(Position position);

    Position getLastPosition();
}
