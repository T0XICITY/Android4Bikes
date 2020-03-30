package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public class CouchDbHelperLocal implements Android4BikesLocalDatabaseHelper {
    private CouchDB couchDB;

    public CouchDbHelperLocal() {
        couchDB = new CouchDB();
    }


    @Override
    public void deleteBikeRack(BikeRack bikeRack) {

    }

    @Override
    public void storeTrack(Track track) {

    }

    @Override
    public void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions) {

    }

    @Override
    public List<Track> readTracks(String postcode) {
        return null;
    }

    @Override
    public void deleteTrack(String fireBaseID) {

    }

    @Override
    public FineGrainedPositions readFineGrainedPositions(String firebaseID) {
        return null;
    }

    @Override
    public FineGrainedPositions readFineGrainedPositions(Track track) {
        return null;
    }

    @Override
    public void storeHazardAlerts(HazardAlert hazardAlert) {

    }

    @Override
    public List<HazardAlert> readHazardAlerts(String postcode) {
        return null;
    }

    @Override
    public void deleteHazardAlert(String fireBaseID) {

    }


    @Override
    public void deleteHazardAlert(HazardAlert hazardAlert) {

    }

    @Override
    public void addToUtilization(Position position) {

    }

    @Override
    public void resetUtilization() {

    }

    @Override
    public void storeProfile(Profile Profile) {

    }

    @Override
    public Profile readProfile(String firebaseAccountID) {
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
    public void storeBikeRack(BikeRack bikeRack) {

    }

    @Override
    public List<BikeRack> readBikeRacks(String postcode) {
        return null;
    }

    @Override
    public void deleteBikeRack(String fireBaseID) {

    }


}
