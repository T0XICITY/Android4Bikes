package de.thu.tpro.android4bikes.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;

import de.thu.tpro.android4bikes.data.model.Profile;
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
        Profile p = null;
        FirebaseConnection.getInstance().storeProfileToFireStore(p);


        return Result.success();
    }
}
