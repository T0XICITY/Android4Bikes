package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

import static de.thu.tpro.android4bikes.database.CouchDBHelper.DBMode;

//TODO: Review code regarding OwnDB!
public class CouchWriteBuffer implements WriteBuffer {
    private static CouchWriteBuffer instance;
    private CouchDBHelper localDBWriteBuffer;
    private CouchDBHelper localDBDeleteBuffer;
    private CouchDBHelper cdb_OwnDB;


    private CouchWriteBuffer(){
        localDBWriteBuffer = new CouchDBHelper(DBMode.WRITEBUFFER);
        localDBDeleteBuffer = new CouchDBHelper(DBMode.DELETEBUFFER);

        //local changes:
        cdb_OwnDB = new CouchDBHelper(DBMode.OWNDATA);
    }

    public static CouchWriteBuffer getInstance(){
        if (instance == null){
            instance = new CouchWriteBuffer();
        }
        return instance;
    }

    @Override
    public void storeProfile(Profile profile) {
        localDBWriteBuffer.deleteProfile(profile);
        localDBWriteBuffer.storeProfile(profile);

        //apply changes immediately to own profile in local db:
        cdb_OwnDB.deleteMyOwnProfile();
        cdb_OwnDB.storeMyOwnProfile(profile);
    }

    @Override
    public void updateProfile(Profile profile) {
        storeProfile(profile);
    }

    @Override
    public void deleteProfile(Profile profile) {
        localDBDeleteBuffer.storeProfile(profile);

        //delete profile immediately in local db:
        cdb_OwnDB.deleteMyOwnProfile();
    }

    @Override
    public void submitBikeRack(BikeRack bikeRack) {
        localDBWriteBuffer.storeBikeRack(bikeRack);

        //store BikeRack immediately in local db:
        cdb_OwnDB.storeBikeRack(bikeRack);
    }

    @Override
    public void storeTrack(Track track) {
        localDBWriteBuffer.storeTrack(track);

        //store track immediately in local db:
        cdb_OwnDB.storeTrack(track);
    }

    @Override
    public void deleteTrack(Track track) {
        localDBDeleteBuffer.storeTrack(track);

        //delete track immediately in own db:
        cdb_OwnDB.deleteTrack(track);
    }

    @Override
    public void submitHazardAlerts(HazardAlert hazardAlert) {
        localDBWriteBuffer.storeHazardAlerts(hazardAlert);

        //store hazardAlert immediately in own db:
        cdb_OwnDB.storeHazardAlerts(hazardAlert);
    }

    @Override
    public void addToUtilization(Position position) {
        localDBWriteBuffer.addToUtilization(position);
        // nur auf firestore schreiben, wenn >= 50 datensätze
    }
}
