package de.thu.tpro.android4bikes.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.couchbase.lite.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.CouchDB;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.database.WriteBuffer;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;


public class UploadWorker extends Worker {

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
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
        CouchDB couchDB = CouchDB.getInstance();
        Database db = couchDB.getDatabaseFromName(CouchDB.DatabaseNames.DATABASE_WRITEBUFFER_PROFILE);

        Log.d("HalloWelt", "Anz Profile WriteBuffer Initial: " + couchDB.getNumberOfStoredDocuments(db));

        WriteBuffer writeBuffer = CouchWriteBuffer.getInstance();
        Profile p = createProfile();
        writeBuffer.storeProfile(p);


        Log.d("HalloWelt", "Anz Profile WriteBuffer vor FireStore: " + couchDB.getNumberOfStoredDocuments(db));

        FirebaseConnection.getInstance().storeProfileToFireStore(p);


        Log.d("HalloWelt", "Nach dem FireStore-Aufruf");
        Log.d("HalloWelt", "Anz Profile WriteBuffer nach FireStore: " + couchDB.getNumberOfStoredDocuments(db));

        return Result.success();
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

}
