package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.util.Processor;

/**
 * Class that provides {@link LiveData} regarding {@link HazardAlert}s. All operations on HazardAlert data
 * that are done by the UI have to be done using this class!! Following data can be observed:
 * {@link List}<{@link HazardAlert}> and an integer variable showing whether there is work in progress.
 * Getting access and observing tracking data:
 * <pre>{@code
 *     public void observeViewModelListForChanges(){
 *       ViewModelHazardAlert model_hazardAlert = new ViewModelProvider(this).get(ViewModelHazardAlert.class);
 *         model_hazardAlert.getHazardAlerts().observe(this, newHazardAlertList->{
 *             newHazardAlertList.get(0); //get the first element in the list
 *         });
 *     }
 * }</pre>
 */
public class ViewModelHazardAlert extends ViewModel implements Observer {
    private CouchDBHelper localDB;
    private MutableLiveData<List<HazardAlert>> hazardAlerts;
    private MutableLiveData<Integer> workInProgress;
    private Processor processor;

    public ViewModelHazardAlert(){
        processor = Processor.getInstance();
        localDB = new CouchDBHelper();
        hazardAlerts = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();
        workInProgress.postValue(0);
        localDB.addObserver(this);
    }

    public LiveData<List<HazardAlert>> getHazardAlerts(){
        return hazardAlerts;
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
            List<HazardAlert> list_loaded_hazardAlerts;
            if (arg != null) {
                //cast to general list
                List list = (List) arg;
                if (list.size() > 0 && list.get(0) instanceof HazardAlert) {
                    //CouchDB notifies in two cases: new data is available OR synchronisation is in progress
                    list_loaded_hazardAlerts = (List<HazardAlert>) list;
                    hazardAlerts.postValue(list_loaded_hazardAlerts);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
