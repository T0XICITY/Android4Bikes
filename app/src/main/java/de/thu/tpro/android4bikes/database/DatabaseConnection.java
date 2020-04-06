package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public class DatabaseConnection implements Database {
    private static DatabaseConnection instance;

    private DatabaseConnection() {

    }

    public static DatabaseConnection getInstance(){
        if (instance == null){
            instance = new DatabaseConnection();
        }
        return instance;
    }


    @Override
    public void storeProfile(Profile profile) {

    }

    @Override
    public void readProfile(String googleID) {

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
    public void readBikeRacks(String postcode) {

    }

    @Override
    public void submitTrack(Track track) {

    }

    @Override
    public void readTracks(String postcode) {

    }

    @Override
    public void submitHazardAlerts(HazardAlert hazardAlert) {

    }

    @Override
    public void readHazardAlerts(String postcode) {

    }

    @Override
    public void addToUtilization(Position position) {

    }

    @Override
    public void getLastPosition() {

    }
}
