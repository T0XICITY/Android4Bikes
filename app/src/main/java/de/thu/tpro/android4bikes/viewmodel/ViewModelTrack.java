package de.thu.tpro.android4bikes.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.thu.tpro.android4bikes.data.commands.Command;
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

    public void loadTracksWithSpecifiedPostcode(String postcode) {
        if (postcode != null && postcode.length() >= 3) {

            incrementWorkInProgress();

            //asynchronous task:
            firebaseConnection.readTracksFromFireStoreAndStoreItToLocalDB(postcode);
        }
    }

    public LiveData<List<Track>> getTracks(){
        return list_shownTracks;
    }

    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
    }


    private void incrementWorkInProgress() {
        //add information that there is one more request
        if (workInProgress.getValue() != null) {
            int newWorkInProgress = workInProgress.getValue() + 1;
            workInProgress.postValue(newWorkInProgress + 1);
        }
    }

    private void decrementWorkInProgress() {
        if (workInProgress.getValue() != null && workInProgress.getValue() > 0) {
            int newWorkInProgress = workInProgress.getValue() - 1;
            workInProgress.postValue(newWorkInProgress);
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        try {
            if (o != null) {
                if (observable instanceof FireStoreDatabase) {
                    //read from local database + Rueckgabewert wegschmeissen

                    new Thread(() -> {
                        Command command = (Command) o;
                        command.execute();
                    }
                    );

                } else if (observable instanceof LocalDatabaseHelper) {
                    decrementWorkInProgress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
