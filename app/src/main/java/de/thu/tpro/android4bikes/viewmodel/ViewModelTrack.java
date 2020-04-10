package de.thu.tpro.android4bikes.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.commands.Command;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.FireStoreDatabase;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.util.TimeBase;

/**
 * Class that provides {@link LiveData} regarding {@link Track}s. All operations on track data
 * that are done by the UI have to be done using this class!! Following data
 * can be observed: List<{@link Track}> and an integer variable showing whether there is work in progress.
 * <h3>Getting access and observing tracking data</h3>
 * <pre>{@code
 *     public void observeViewModelListForChanges(){
 *       ViewModelTrack model_track = new ViewModelProvider(this).get(ViewModelTrack.class);
 *         model_track.getTracks().observe(this, newTrackList->{
 *             newTrackList.get(0); //get the first element in the list
 *         });
 *     }
 * }</pre>
 *
 */
public class ViewModelTrack extends ViewModel implements Observer {
    //UI -> Datenhaltung
    //SUBMITTRACK: Create Track -> ViewModel ruft FireBase an
    //READTRACK : Liste an Tracks erhalten (PLZ) -> alle tracks in ulm sehen

    private MutableLiveData<List<Track>> list_shownTracks;
    private CouchDBHelper couchDBHelper;
    private FirebaseConnection firebaseConnection;

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

    }

    /**
     * 1. load existing tracks from database
     * 2. Perform request to get new data from FireStore and to store it in the local database
     * 3.1 FireStore operation successful?
     * Local database sends updates list to this class.
     * 3.2 FireStore operation not successful?
     * No more actions are taken!
     *
     * @param postcode postcode of the needed tracks
     */
    public void loadTracksWithSpecifiedPostcode(String postcode) {
        if (postcode != null && postcode.length() >= 3) {
            incrementWorkInProgress();

            //read tracks from local database
            couchDBHelper.readTracks(postcode);

            //asynchronous task:
            firebaseConnection.readTracksFromFireStoreAndStoreItToLocalDB(postcode);
        }
    }

    /**
     *
     * @return LiveData object regarding a list of tracks
     */
    public LiveData<List<Track>> getTracks(){
        return list_shownTracks;
    }

    /**
     *  If there are outstanding database operations this {@link LiveData} object indicates the number
     *  of outstanding operations. If it is 0, all {@link LiveData} is up to date!
     * @return LiveData object regarding the progress in this object
     */
    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
    }


    /**
     * increment number of outstanding database operations
     */
    private void incrementWorkInProgress() {
        //add information that there is one more request
        try {
            if (workInProgress.getValue() != null) {
                int newWorkInProgress = workInProgress.getValue() + 1;
                workInProgress.postValue(newWorkInProgress + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * decrement number of outstanding database operations
     */
    private void decrementWorkInProgress() {
        try {
            if (workInProgress.getValue() != null && workInProgress.getValue() > 0) {
                int newWorkInProgress = workInProgress.getValue() - 1;
                workInProgress.postValue(newWorkInProgress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * called by the local db in case of success. called by FireStore in case of failure.
     * @param observable instance that updates this object
     * @param o data to deal with in this update
     */
    @Override
    public void update(Observable observable, Object o) {
        try {
            if (o != null) {
                if (observable instanceof FireStoreDatabase) {
                    //read from local database + Rueckgabewert wegschmeissen

                    if (o instanceof Command) {
                        //TODO: INTRODUCE CLASS "PROCESSOR"
                        new Thread(() -> {
                            Command command = (Command) o;
                            command.execute();
                        });
                    } else {
                        //if something went wrong and there is no alternative action to be taken
                        //there is no more work in progress
                        decrementWorkInProgress();
                    }


                } else if (observable instanceof LocalDatabaseHelper) {
                    List<Track> list_loaded_tracks = (List<Track>) o;

                    //update list of shown tracks:
                    list_shownTracks.postValue(list_loaded_tracks);

                    //every time the local database updates this class,
                    //an operation was performed successfully
                    decrementWorkInProgress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
