package de.thu.tpro.android4bikes.util;

import android.util.Log;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.thu.tpro.android4bikes.services.UploadWorker;

//TODO: Review
public class WorkManagerHelper {
    public static UUID uploadRequestID;

    /**
     * stops running background service regarding the WorkManager
     */
    public static void stopUploadTaskWithWorkManager() {
        if (uploadRequestID != null) {
            WorkManager.getInstance(GlobalContext.getContext()).cancelWorkById(uploadRequestID);
            uploadRequestID = null;
        }
    }

    /**
     * Defines a task that uploads not synchronized data.
     */
    public static void scheduleUploadTaskWithWorkManager() {

        Log.d("HalloWelt", "Started at: " + new Date());

        //constraints regarding when a task should be scheduled
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Define the request: How often should the task be scheduled
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(UploadWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        uploadRequestID = saveRequest.getId();

        //schedule task
        WorkManager.getInstance(GlobalContext.getContext())
                .enqueue(saveRequest);
    }
}
