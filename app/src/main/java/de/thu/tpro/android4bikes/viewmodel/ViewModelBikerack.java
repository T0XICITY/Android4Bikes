package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.Processor;

/**
 * Class that provides {@link LiveData} regarding {@link BikeRack}s. All operations on HazardAlert data
 * that are done by the UI have to be done using this class!! Following data can be observed:
 * {@link List}<{@link BikeRack}> and an integer variable showing whether there is work in progress.
 * Getting access and observing tracking data:
 * <pre>{@code
 *      public void observeViewModelListBikeRacksForChanges(){
 *          ViewModelBikerack model_bikeRack = new ViewModelProvider(this).get(ViewModelBikerack.class);
 *          model_bikeRack.getList_bikeRacks_shown().observe(this, newBikeRackList->{
 *              newBikeRackList.get(0); //get the first element in the list
 *          });
 *      }
 * }</pre>
 */
public class ViewModelBikerack extends ViewModel implements Observer {
    private CouchDBHelper localDB;
    private MutableLiveData<List<BikeRack>> list_bikeRacks_shown;
    private MutableLiveData<Integer> workInProgress;
    private Processor processor;

    //GeoFencing
    private GeoFencing geoFencing_bikeRacks;


    public ViewModelBikerack() {
        processor = Processor.getInstance();
        localDB = new CouchDBHelper();
        list_bikeRacks_shown = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();
        workInProgress.postValue(0);
        localDB.addObserver(this);


        //Initialize GeoFences
        if (PositionTracker.getLastPosition() != null){
            geoFencing_bikeRacks = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS,PositionTracker.getLastPosition().getGeoPoint(),200);
        }else {
            geoFencing_bikeRacks = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS,new Position(48.408751,9.997498).getGeoPoint(),200);
        }

        geoFencing_bikeRacks.addObserver(this);
    }

    public LiveData<List<BikeRack>> getList_bikeRacks_shown() {
        return list_bikeRacks_shown;
    }

    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
    }

    public GeoFencing getGeoFencing_bikeRacks() {
        return geoFencing_bikeRacks;
    }

    public void incrementWorkInProgress() {
        workInProgress.postValue(workInProgress.getValue() + 1);
    }

    public void decrementWorkInProgress() {
        workInProgress.postValue(workInProgress.getValue() - 1);
    }

    @Override
    public void update(Observable observable, Object arg) {
        try {
            List<BikeRack> list_loaded_bikeRacks;
            if (arg != null) {
                //cast to general list
                List list = (List) arg;
                if (list.size() > 0 && ((List) arg).size() > 0 && list.get(0) instanceof BikeRack) {
                    //CouchDB notifies in two cases: new data is available OR synchronisation is in progress
                    list_loaded_bikeRacks = (List<BikeRack>) list;
                    list_bikeRacks_shown.postValue(list_loaded_bikeRacks);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
