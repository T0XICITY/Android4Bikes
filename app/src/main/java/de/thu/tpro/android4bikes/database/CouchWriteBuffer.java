package de.thu.tpro.android4bikes.database;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

import static de.thu.tpro.android4bikes.database.CouchDBHelper.DBMode;

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
        //Im Hintergrund:
        // Workmanager führt folgendes aus, wenn die Umstände passen:
        // Workmanager liest writebuffer aus und schreibt diese Profiele auf firestore
        // Erfolg: Profil aus wirtebuffer löschen. localDBWriteBuffer.deleteProfile(profile);
        // Misserfolg: Nicht löschen und nochmal wann anders versuchen (retry)
    }

    @Override
    public void updateProfile(Profile profile) {
        storeProfile(profile);
    }

    @Override
    public void deleteProfile(Profile profile) {
        localDBDeleteBuffer.storeProfile(profile);
        //Im Hintergrund:
        // Workmanager führt folgendes aus, wenn die Umstände passen:
        // Workmanager liest deletebuffer aus und löscht diese Profiele vom firestore
        // Erfolg: Profil aus deletebuffer löschen. localDBDeleteBuffer.deleteProfile(profile);
        // Misserfolg: Nicht löschen und nochmal wann anders versuchen (retry)
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
        // nur auf firestore schreiben, wenn >= 50 datensätze
    }
}
