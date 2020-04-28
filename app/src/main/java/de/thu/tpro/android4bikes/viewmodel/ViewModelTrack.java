package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.Processor;
import de.thu.tpro.android4bikes.util.TestObjectsGenerator;

/**
 * Class that provides {@link LiveData} regarding {@link Track}s. All operations on track data
 * that are done by the UI have to be done using this class!! Following data
 * can be observed: List<{@link Track}> and an integer variable showing whether there is work in progress.
 * <h3>Getting access and observing tracking data</h3>
 * <pre>{@code
 *     public void observeViewModelListForChanges(){
 *         ViewModelTrack model_track = new ViewModelProvider(this).get(ViewModelTrack.class);
 *           model_track.getTracks().observe(this, newTrackMap->{
 *               if(newTrackMap != null){
 *                   //iterate over all provided tracks:
 *                   Set<Track> tracks = newTrackMap.keySet();
 *
 *                   for(Track iterated_track : tracks){
 *                       Track singleTrack_01 = iterated_track; //get a single track
 *                       Profile profile_track_01 = newTrackMap.get(singleTrack_01); //provides the associated profile regarding a track
 *                       Toast.makeText(getApplicationContext(), singleTrack_01.toString()+" : "+profile_track_01.toString(), Toast.LENGTH_LONG).show();
 *                   }
 *               }
 *           });
 *     }
 * }</pre>
 */
public class ViewModelTrack extends ViewModel implements Observer {
    private MutableLiveData<Map<Track, Profile>> map_tracks_profile_shown;
    private CouchDBHelper couchDBHelper;
    private FirebaseConnection firebaseConnection;
    private Processor processor;


    private GeoFencing geoFencing_tracks;

    //is there any outstanding operation? Important for ProgressBars in the UI.
    //if there are outstanding operations "workInProgress" is > 0.
    private MutableLiveData<Integer> workInProgress;

    /**
     * Constructor of ViewModelTrack
     * Attention: Tracks could be initially null
     */
    public ViewModelTrack() {
        this.processor = Processor.getInstance();

        //create LiveData-Wrapper:
        map_tracks_profile_shown = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();

        //Deal with the local database
        couchDBHelper = new CouchDBHelper();

        firebaseConnection = FirebaseConnection.getInstance();

        //this is observer of local database (in case of success)
        couchDBHelper.addObserver(this);
        firebaseConnection.addObserver(this);

        //set initial values
        workInProgress.postValue(0);

        //generate dummy data
        this.map_tracks_profile_shown.postValue(TestObjectsGenerator.initialize_map_track_profile());


        //initialize GeoFencing
        geoFencing_tracks = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS);
        geoFencing_tracks.addObserver(this);
    }

    /**
     * 1. load existing tracks from database
     * 2. Perform request to get new data from FireStore and to store it in the local database
     * 3.1 FireStore operation successful?
     * Local database sends updates list to this class.
     * 3.2 FireStore operation not successful?
     * No more actions are taken!
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
    public GeoFencing getGeoFencing_tracks() {
        return geoFencing_tracks;
    }

    /**
     *
     * @return LiveData object regarding a list of tracks
     */
    public LiveData<Map<Track, Profile>> getTracks() {
        return map_tracks_profile_shown;
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
            if (o != null) {
                if (o instanceof List && ((List) o).size() > 0 && ((List) o).get(0) instanceof Track) {
                    //CouchDB notifies in two cases: new data is available OR synchronisation is in progress
                    firebaseConnection.readProfilesBasedOnTracks((List<Track>) o);
                }
                //is the map the right map (Map<Track,Profile>) and not empty?
                else if (o instanceof Map && !((Map) o).keySet().isEmpty() && ((Map) o).keySet().iterator().next() instanceof Track) {
                    //post map provided by FireBaseConnection
                    map_tracks_profile_shown.postValue((Map<Track, Profile>) o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
