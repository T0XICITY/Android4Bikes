package de.thu.tpro.android4bikes.services;

import android.util.Log;

import java.util.Date;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

public class UploadRunnable implements Runnable {
    private CouchDBHelper cdb_writeBuffer;
    private CouchDBHelper cdb_deleteBuffer;
    private CouchDBHelper cdb_ownData;

    public UploadRunnable() {
        //get access to buffers:
        cdb_writeBuffer = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
        cdb_deleteBuffer = new CouchDBHelper(CouchDBHelper.DBMode.DELETEBUFFER);
        cdb_ownData = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
    }

    private List<Position> readUtilisation() {
        List<Position> positions = cdb_writeBuffer.getAllPositions();
        return positions;
    }

    /**
     * read BikeRacks that need to be synchronized with the FireStore
     *
     * @return all BikeRacks to synchronize with the FireStore
     */
    private List<BikeRack> readBikeRack() {
        List<BikeRack> bikeRacks = cdb_writeBuffer.readBikeRacks();
        return bikeRacks;
    }

    /**
     * read HazardAlerts that need to be synchronized with the FireStore
     *
     * @return all HazardAlerts to synchronize with the FireStore
     */
    private List<HazardAlert> readHazardAlertsWriteBuffer() {
        List<HazardAlert> hazardAlerts = cdb_writeBuffer.readHazardAlerts();
        return hazardAlerts;
    }


    /**
     * read current version of the own profile that should be uploaded to FireStore
     *
     * @return current version of own profile or null
     */
    private Profile readProfileWriteBuffer() {
        Profile p = cdb_ownData.readMyOwnProfile();
        Profile buffered_profile = null;
        if (p != null) {
            buffered_profile = cdb_writeBuffer.readProfile(p.getGoogleID());
        }
        return buffered_profile;
    }

    /**
     * read current version of the own profile that should be uploaded to FireStore
     *
     * @return current version of own profile or null
     */
    private Profile readProfileDeleteBuffer() {
        Profile p = cdb_ownData.readMyOwnProfile();
        Profile buffered_profile = null;
        if (p != null) {
            buffered_profile = cdb_deleteBuffer.readProfile(p.getGoogleID());
        }
        return buffered_profile;
    }

    /**
     * read tracks that need to be synchronized with the FireStore
     *
     * @return all tracks to synchronize with the FireStore
     */
    private List<Track> readTrackWriteBuffer() {
        List<Track> list_buffered_tracks = cdb_writeBuffer.readTracks();
        return list_buffered_tracks;
    }


    /**
     * read tracks that need to be deleted from the FireStore
     *
     * @return all tracks to synchronize with the FireStore
     */
    private List<Track> readTrackDeleteBuffer() {
        List<Track> list_buffered_tracks = cdb_deleteBuffer.readTracks();
        return list_buffered_tracks;
    }

    @Override
    public void run() {
        //Log.d("HalloWelt", "UploadWorker is working " + new Date());
        //Result.success(): Task has benn finished successfully
        //Result.failure(): Task failed
        //Result.retry(): Task should be retried

        //Synchronize profile#######################################################################
        Profile buffered_profile_to_store = readProfileWriteBuffer();
        if (buffered_profile_to_store != null) {
            FirebaseConnection.getInstance().storeBufferedProfileToFireStore(buffered_profile_to_store);
        }

        Profile buffered_profile_to_delete = readProfileDeleteBuffer();
        if (buffered_profile_to_delete != null) {
            FirebaseConnection.getInstance().deleteBufferedProfileFromFireStore(buffered_profile_to_delete);
        }
        //Synchronize profile#######################################################################


        //Synchronize track#########################################################################
        List<Track> list_tracks_to_store = readTrackWriteBuffer();
        for (Track t : list_tracks_to_store) {
            FirebaseConnection.getInstance().storeBufferedTrackInFireStore(t);
        }

        List<Track> list_tracks_to_delete = readTrackDeleteBuffer();
        for (Track t : list_tracks_to_delete) {
            FirebaseConnection.getInstance().deleteBufferedTrackFromFireStore(t);
        }
        //Synchronize track#########################################################################

        //Synchronize bikerack######################################################################
        List<BikeRack> list_bikeracks_to_store = readBikeRack();
        for (BikeRack b : list_bikeracks_to_store) {
            FirebaseConnection.getInstance().storeBufferedBikeRackInFireStore(b);
        }
        //Synchronize bikerack######################################################################

        //Synchronize Utilisation###################################################################
        List<Position> list_positions_to_store = readUtilisation();
        if (list_positions_to_store.size() >= 50) {
            FirebaseConnection.getInstance().storeBufferedUtilizationToFireStore(list_positions_to_store);
        }
        //Synchronize Utilisation###################################################################

        //HazardAlert###############################################################################
        List<HazardAlert> list_hazardalerts_to_store = readHazardAlertsWriteBuffer();
        for (HazardAlert h : list_hazardalerts_to_store) {
            FirebaseConnection.getInstance().storeBufferedHazardAlertInFireStore(h);
        }
        //##########################################################################################
    }
}
