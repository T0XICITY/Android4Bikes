package de.thu.tpro.android4bikes.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;


public class UploadWorker extends Worker {

    private CouchDBHelper cdb_writeBuffer;
    private CouchDBHelper cdb_deleteBuffer;

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        //get access to buffers:
        cdb_writeBuffer = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
        cdb_deleteBuffer = new CouchDBHelper(CouchDBHelper.DBMode.DELETEBUFFER);
    }

    /**
     * @return Result: informs the WorkManager about the success regarding the task
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.d("HalloWelt", "UploadWorker is working " + new Date());
        //Result.success(): Task has benn finished successfully
        //Result.failure(): Task failed
        //Result.retry(): Task should be retried

        //Synchronize profile#######################################################################
        Profile buffered_profile_to_store = readProfileWriteBuffer();
        if (buffered_profile_to_store != null) {
            FirebaseConnection.getInstance().storeProfileToFireStore(buffered_profile_to_store);
        }

        Profile buffered_profile_to_delete = readProfileDeleteBuffer();
        if (buffered_profile_to_delete != null) {
            FirebaseConnection.getInstance().deleteProfileFromFireStore(buffered_profile_to_delete);
        }
        //Synchronize profile#######################################################################


        //Synchronize track#########################################################################
        List<Track> list_tracks_to_store = readTrackWriteBuffer();
        for (Track t : list_tracks_to_store) {
            FirebaseConnection.getInstance().storeTrackInFireStore(t);
        }

        List<Track> list_tracks_to_delete = readTrackDeleteBuffer();
        for (Track t : list_tracks_to_delete) {
            FirebaseConnection.getInstance().deleteTrackFromFireStore(t);
        }
        //Synchronize track#########################################################################

        //Synchronize bikerack######################################################################
        List<BikeRack> list_bikeracks_to_store = readBikeRack();
        for (BikeRack b : list_bikeracks_to_store){
            FirebaseConnection.getInstance().storeBikeRackInFireStore(b);
        }
        //Synchronize bikerack######################################################################

        //Synchronize Utilisation###################################################################
        List<Position> list_positions_to_store = readUtilisation();
        if (list_bikeracks_to_store.size() >= 50){
            FirebaseConnection.getInstance().storeUtilizationToFireStore(list_positions_to_store);
        }
        //Synchronize Utilisation###################################################################

        //HazardAlert###############################################################################
        List<HazardAlert> list_hazardalerts_to_store = readHazardAlertsWriteBuffer();
        for (HazardAlert h : list_hazardalerts_to_store) {
            FirebaseConnection.getInstance().storeHazardAlertInFireStore(h);
        }
        //##########################################################################################

        return Result.success();
    }

    private List<Position> readUtilisation() {
        List<Position> positions = cdb_writeBuffer.getAllPositions();
        return positions;
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     */
    private Profile createProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        return new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
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
        Profile buffered_profile = cdb_writeBuffer.readMyOwnProfile();
        return buffered_profile;
    }

    /**
     * read current version of the own profile that should be uploaded to FireStore
     *
     * @return current version of own profile or null
     */
    private Profile readProfileDeleteBuffer() {
        Profile buffered_profile = cdb_deleteBuffer.readMyOwnProfile();
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

}
