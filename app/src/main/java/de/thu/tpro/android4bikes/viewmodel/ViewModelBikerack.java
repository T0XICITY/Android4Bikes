package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.util.Processor;

public class ViewModelBikerack extends ViewModel implements Observer {
    private CouchDBHelper localDB;
    private MutableLiveData<List<BikeRack>> list_bikeRacks_shown;
    private MutableLiveData<Integer> workInProgress;
    private Processor processor;

    public ViewModelBikerack() {
        processor = Processor.getInstance();
        localDB = new CouchDBHelper();
        list_bikeRacks_shown = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();
        workInProgress.postValue(0);
        localDB.addObserver(this);
    }

    public LiveData<List<BikeRack>> getList_bikeRacks_shown() {
        return list_bikeRacks_shown;
    }

    public void submitBikeRack(BikeRack bikeRack) {
        processor.startRunnable(() -> {
            //store it to writebuffer
            //localDB.storeHazardAlerts(hazardAlert);
        });
    }

    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
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
                if (list.size() > 0 && list.get(0) instanceof BikeRack) {
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
