package de.thu.tpro.android4bikes.database;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public class CouchDbHelper implements Android4BikesDatabaseHelper {
    private CouchDB couchDB;

    public CouchDbHelper() {
        couchDB = new CouchDB();
    }


    @Override
    public List<BikeRack> readBikeRack(String postcode) {
        return null;
    }

    @Override
    public void saveBikeRack(BikeRack bikeRack) {

    }

    @Override
    public void updateBikeRack(BikeRack bikeRack) {

    }

    @Override
    public void deleteBikeRack(BikeRack bikeRack) {

    }

    @Override
    public List<HazardAlert> readHazardAlert(String postcode) {
        return null;
    }

    @Override
    public void saveHazardAlert(HazardAlert hazardAlert) {

    }

    @Override
    public void updateHazardAlert(HazardAlert hazardAlert) {

    }

    @Override
    public void deleteHazardAlert(HazardAlert hazardAlert) {

    }

    @Override
    public Profile readProfile(String firebaseAccountID) {
        return null;
    }

    @Override
    public void saveProfile(Profile profile) {

    }

    @Override
    public void updateProfile(Profile profile) {

    }

    @Override
    public void deleteProfile(Profile profile) {

    }

    @Override
    public Track getTrack(long trackID) {
        return null;
    }
    
}
