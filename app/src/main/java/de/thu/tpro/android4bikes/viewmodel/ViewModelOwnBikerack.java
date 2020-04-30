package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.database.WriteBuffer;

public class ViewModelOwnBikerack extends ViewModel implements Observer {
    private MutableLiveData<List<BikeRack>> myBikeracks;
    private CouchDBHelper cdbOwn;
    private WriteBuffer writeBuffer;

    public ViewModelOwnBikerack() {
        cdbOwn = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
        cdbOwn.addObserver(this);
        myBikeracks = new MutableLiveData<>();
        cdbOwn.readHazardAlerts();
        writeBuffer = CouchWriteBuffer.getInstance();
    }

    public LiveData<List<BikeRack>> getHazardAlerts(){
        return myBikeracks;
    }

    public void addOwnBikeRack(BikeRack bikeRack){
        writeBuffer.submitBikeRack(bikeRack);
    }

    @Override
    public void update(Observable observable, Object arg) {
        try {
            List<BikeRack> list_loaded_BikeRacks;
            if (arg != null) {
                //cast to general list
                List list = (List) arg;
                if (list.size() > 0 && list.get(0) instanceof BikeRack) {
                    //CouchDB notifies in two cases: new data is available OR synchronisation is in progress
                    list_loaded_BikeRacks = (List<BikeRack>) list;
                    myBikeracks.postValue(list_loaded_BikeRacks);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
