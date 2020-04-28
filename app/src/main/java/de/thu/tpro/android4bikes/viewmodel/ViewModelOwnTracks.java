package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.database.WriteBuffer;
import de.thu.tpro.android4bikes.util.TestObjectsGenerator;

public class ViewModelOwnTracks extends ViewModel implements Observer {
    private MutableLiveData<Map<Track, Profile>> map_tracks_profile_shown;
    private CouchDBHelper cdb_ownDB;
    private WriteBuffer writeBuffer;

    //is there any outstanding operation? Important for ProgressBars in the UI.
    //if there are outstanding operations "workInProgress" is > 0.
    private MutableLiveData<Integer> workInProgress;

    /**
     * Constructor of ViewModelTrack
     * Attention: Tracks could be initially null
     */
    public ViewModelOwnTracks() {

        //create LiveData-Wrapper:
        map_tracks_profile_shown = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();

        //Deal with the local database
        cdb_ownDB = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);

        //create new writeBuffer
        writeBuffer = CouchWriteBuffer.getInstance();

        //this is observer of local database (in case of success)
        cdb_ownDB.addObserver(this);

        //set initial values
        workInProgress.postValue(0);

        //generate dummy data
        this.map_tracks_profile_shown.postValue(TestObjectsGenerator.initialize_map_track_profile());
    }

    /**
     * @return LiveData object regarding a list of tracks
     */
    public LiveData<Map<Track, Profile>> getTracks() {
        return map_tracks_profile_shown;
    }

    /**
     * If there are outstanding database operations this {@link LiveData} object indicates the number
     * of outstanding operations. If it is 0, all {@link LiveData} is up to date!
     *
     * @return LiveData object regarding the progress in this object
     */
    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
    }


    /**
     * store own track
     *
     * @param track
     */
    public void submitTrack(Track track) {
        writeBuffer.storeTrack(track);
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
     *
     * @param observable instance that updates this object
     * @param o          data to deal with in this update
     */
    @Override
    synchronized public void update(Observable observable, Object o) {
        try {
            if (o != null) {
                if (o instanceof List && ((List) o).get(0) instanceof Track) {
                    Map<Track, Profile> map_track_profile = new HashMap<>();
                    List<Track> list_own_tracks = (List<Track>) o;
                    Profile profile_own = cdb_ownDB.readMyOwnProfile();

                    list_own_tracks.forEach(entry -> map_track_profile.put(entry, profile_own));

                    //Update LiveData:
                    this.map_tracks_profile_shown.postValue(map_track_profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
