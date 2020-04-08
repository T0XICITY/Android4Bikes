package de.thu.tpro.android4bikes.viewmodel;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.FireStoreDatabase;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.util.TimeBase;


public class ViewModelTrack extends ViewModel implements Observer {
    //UI -> Datenhaltung
    //SUBMITTRACK: Create Track -> ViewModel ruft FireBase an
    //READTRACK : Liste an Tracks erhalten (PLZ) -> alle tracks in ulm sehen

    private MutableLiveData<List<Track>> list_shownTracks;
    private CouchDBHelper couchDBHelper;
    private FirebaseConnection firebaseConnection;
    private ExecutorService executorService;

    //is there any outstanding operation? Important for ProgressBars in the UI.
    //if there are outstanding operations "workInProgress" is > 0.
    private MutableLiveData<Integer> workInProgress;


    private Runnable doSthImportant = () -> {
        try {
            //as soon as this task is startetd there is work in progress
            /*if(workInProgress.getValue()!=null){
                int newProgress = workInProgress.getValue() + 1;
                workInProgress.postValue(newProgress);
            }*/


            Log.d("HalloWelt", "Current time :: " + TimeBase.getDateFromMilliSecondsAsString(TimeBase.getCurrentUnixTimeStamp()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public ViewModelTrack() {
        TimeBase.setDateRepresentation(TimeBase.DateRepresentation.HOUR_MINUTE_SECOND);

        //create LiveData-Wrapper:
        list_shownTracks = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();

        //set initial value of work in progress
        workInProgress.postValue(0);

        //Deal with the local database
        couchDBHelper = new CouchDBHelper();
        //deal with FireStore:
        firebaseConnection = FirebaseConnection.getInstance();


        //this is observer of FireBase (in the case of failure)
        firebaseConnection.addObserver(this);
        //this is observer of local database (in case of success)
        couchDBHelper.addObserver(this);

        //ExecutorService for executing work in background (e.g. database access)
        executorService = Executors.newFixedThreadPool(4);

    }

    public LiveData<List<Track>> getTracksWithSpecifiedPostcode(String postcode) {
        if (postcode != null && postcode.length() >= 3) {

            //asynchronous task:
            firebaseConnection.readTracksFromFireStoreAndStoreItToLocalDB(postcode);
        }
        return list_shownTracks;
    }

    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
    }

    public void doSth() {

        new FirebaseTask(this).execute();

        /*Future<String> result = executorService.submit(doSthImportant, "DONE");

        while(result.isDone() == false) {
            try {
               Log.d("HalloWelt","The method return value : " + result.get());
                break;
            }catch (Exception e) {
                e.printStackTrace();
            }

            //Sleep for 1 second
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }*/

/*
        executorService.s
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if(workInProgress.getValue()!=null){
                int newProgress = workInProgress.getValue() - 1;
                workInProgress.postValue(newProgress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public void update(Observable observable, Object o) {
        try {
            if (o != null) {
                if (observable instanceof FireStoreDatabase) {
                    FirebaseConnection.STATUSCODES statuscode = (FirebaseConnection.STATUSCODES) o;

                    if (statuscode == FirebaseConnection.STATUSCODES.ERROR) {
                        //read from local database + Rueckgabewert wegschmeissen
                        //asynchronous task integration
                    }
                } else if (observable instanceof LocalDatabaseHelper) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FirebaseTask extends AsyncTask<Void,Void,Void> {
        private ViewModelTrack model;
        public FirebaseTask(ViewModelTrack model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("HalloWelt", "doSth() started!!!!\t" + TimeBase.getDateFromMilliSecondsAsString(TimeBase.getCurrentUnixTimeStamp()));
                Thread r = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("HalloWelt", "Thread fertig: " + TimeBase.getDateFromMilliSecondsAsString(TimeBase.getCurrentUnixTimeStamp()));
                    }
                });
                r.start();
                r.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("HalloWelt", "ONPOST:\t" + TimeBase.getDateFromMilliSecondsAsString(TimeBase.getCurrentUnixTimeStamp()));
            super.onPostExecute(aVoid);
        }
    }

}
