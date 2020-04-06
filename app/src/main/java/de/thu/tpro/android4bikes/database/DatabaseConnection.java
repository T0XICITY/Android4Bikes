package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public class DatabaseConnection implements Database {
    @Override
    public void storeProfile(Profile profile) {

    }

    @Override
    public Profile readProfile(String googleID) {
        return null;
    }

    @Override
    public void updateProfile(Profile profile) {

    }

    @Override
    public void deleteProfile(String googleID) {

    }

    @Override
    public void deleteProfile(Profile profile) {

    }

    @Override
    public void submitBikeRack(BikeRack bikeRack) {

    }

    @Override
    public List<BikeRack> readBikeRacks(String postcode) {
        return null;
    }

    @Override
    public void submitTrack(Track track) {

    }

    @Override
    public List<Track> readTracks(String postcode) {
        return null;
    }

    @Override
    public void submitHazardAlerts(HazardAlert hazardAlert) {

    }

    @Override
    public List<HazardAlert> readHazardAlerts(String postcode) {
        return null;
    }

    @Override
    public void addToUtilization(Position position) {

    }

    @Override
    public Position getLastPosition() {
        return null;
    }
}
