package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;

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
 */
public class ViewModelTrack extends ViewModel implements Observer {
    private MutableLiveData<List<Track>> list_shownTracks;
    private CouchDBHelper couchDBHelper;


    //is there any outstanding operation? Important for ProgressBars in the UI.
    //if there are outstanding operations "workInProgress" is > 0.
    private MutableLiveData<Integer> workInProgress;

    public ViewModelTrack() {

        //create LiveData-Wrapper:
        list_shownTracks = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();

        //set initial value of work in progress
        workInProgress.postValue(0);

        //Deal with the local database
        couchDBHelper = new CouchDBHelper();

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
    /*
    public void loadTracksWithSpecifiedPostcode(String postcode) {
        if (postcode != null && postcode.length() >= 3) {

            //reading from local database (asynchronously)
            processor.startRunnable(() -> {
                incrementWorkInProgress();
                //read tracks from local database
                couchDBHelper.readTracks(postcode);
            });


            //try to access data from firestore
            processor.startRunnable(() -> {
                incrementWorkInProgress();
                //asynchronous task:
                firebaseConnection.readTracksFromFireStoreAndStoreItToLocalDB(postcode);
            });

        }
    }*/

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
     * increment number of outstanding operations
     */
    public void incrementWorkInProgress() {
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
     * decrement number of outstanding operations
     */
    public void decrementWorkInProgress() {
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
    //TODO: Review race condition
    @Override
    synchronized public void update(Observable observable, Object o) {
        try {
            List<Track> list_loaded_tracks = null;
            if (o != null) {
                //cast to general list
                List list = (List) o;

                //cast to List<Track>, if o is a Track-List
                if (list.size() > 0 && list.get(0) instanceof Track) {
                    //CouchDB notifies in two cases: new data is available OR synchronisation is in progress
                    list_loaded_tracks = (List<Track>) list;
                    //update list of shown tracks:
                    list_shownTracks.postValue(list_loaded_tracks);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
