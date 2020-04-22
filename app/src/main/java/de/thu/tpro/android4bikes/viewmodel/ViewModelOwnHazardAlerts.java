package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.database.WriteBuffer;

public class ViewModelOwnHazardAlerts extends ViewModel implements Observer {
    private MutableLiveData<List<HazardAlert>> myHazards;
    private CouchDBHelper cdbOwn;
    private WriteBuffer writeBuffer;

    public ViewModelOwnHazardAlerts() {
        cdbOwn = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
        cdbOwn.addObserver(this);
        myHazards = new MutableLiveData<>();
        cdbOwn.readHazardAlerts();
        writeBuffer = CouchWriteBuffer.getInstance();
    }

    public LiveData<List<HazardAlert>> getHazardAlerts(){
        return myHazards;
    }

    public void addOwnHazard(HazardAlert hazardAlert){
        writeBuffer.submitHazardAlerts(hazardAlert);
    }

    @Override
    public void update(Observable observable, Object arg) {
        try {
            List<HazardAlert> list_loaded_hazardAlerts;
            if (arg != null) {
                //cast to general list
                List list = (List) arg;
                if (list.size() > 0 && list.get(0) instanceof HazardAlert) {
                    //CouchDB notifies in two cases: new data is available OR synchronisation is in progress
                    list_loaded_hazardAlerts = (List<HazardAlert>) list;
                    myHazards.postValue(list_loaded_hazardAlerts);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
