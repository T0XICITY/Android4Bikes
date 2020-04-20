package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

import static de.thu.tpro.android4bikes.database.CouchDBHelper.*;

public class CouchWriteBuffer implements WriteBuffer {
    private static CouchWriteBuffer instance;
    private CouchDBHelper localDBWriteBuffer;
    private CouchDBHelper localDBDeleteBuffer;


    private CouchWriteBuffer(){
        localDBWriteBuffer = new CouchDBHelper(DBMode.WRITEBUFFER);
        localDBDeleteBuffer = new CouchDBHelper(DBMode.DELETEBUFFER);
    }

    public static CouchWriteBuffer getInstance(){
        if (instance == null){
            instance = new CouchWriteBuffer();
        }
        return instance;
    }

    @Override
    public void storeProfile(Profile profile) {
        localDBWriteBuffer.storeProfile(profile);
    }

    @Override
    public void updateProfile(Profile profile) {
        localDBWriteBuffer.updateProfile(profile);
    }

    @Override
    public void deleteProfile(Profile profile) {
        localDBDeleteBuffer.storeProfile(profile);
    }

    @Override
    public void submitBikeRack(BikeRack bikeRack) {
        localDBWriteBuffer.storeBikeRack(bikeRack);
    }

    @Override
    public void storeTrack(Track track) {
        localDBWriteBuffer.storeTrack(track);
    }

    @Override
    public void deleteTrack(Track track) {
        localDBDeleteBuffer.storeTrack(track);
    }

    @Override
    public void submitHazardAlerts(HazardAlert hazardAlert) {
        localDBWriteBuffer.storeHazardAlerts(hazardAlert);
    }

    @Override
    public void addToUtilization(Position position) {
        localDBWriteBuffer.addToUtilization(position);
    }
}
