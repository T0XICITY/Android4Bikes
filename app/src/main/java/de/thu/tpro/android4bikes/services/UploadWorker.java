package de.thu.tpro.android4bikes.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;

import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.Processor;


public class UploadWorker extends Worker {
    private Processor processor;
    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        processor = Processor.getInstance();
    }

    /**
     * @return Result: informs the WorkManager about the success regarding the task
     */
    @NonNull
    @Override
    public Result doWork() {
        //Check if there is a user signed in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (GlobalContext.getContext() != null) { //todo: check why there is sometimes no global context???
                processor.startRunnable(new UploadRunnable());
            }
        }
        return Result.success();
    }
}
