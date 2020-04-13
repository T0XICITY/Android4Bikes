package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.commands.Command;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.FireStoreDatabase;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

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
    private FirebaseConnection fireStoreDatabase;
    private CouchDBHelper localDB;
    private MutableLiveData<List<HazardAlert>> hazardAlerts;
    private MutableLiveData<Integer> workInProgress;

    public ViewModelHazardAlert(){
        fireStoreDatabase = FirebaseConnection.getInstance();
        localDB = new CouchDBHelper();
        hazardAlerts = new MutableLiveData<>();
        workInProgress = new MutableLiveData<>();
        workInProgress.postValue(0);
        fireStoreDatabase.addObserver(this);
        localDB.addObserver(this);
    }

    public LiveData<List<HazardAlert>> getHazardAlerts(){
        return hazardAlerts;
    }

    public void submitHazardAlert(HazardAlert hazardAlert){
        fireStoreDatabase.submitHazardAlertToFireStore(hazardAlert);
    }

    public void loadHazardAlertsWithSpecifiedPostcode(String postcode) {
        if (postcode != null) {
            incrementWorkInProgress();

            //read tracks from local database
            localDB.readHazardAlerts(postcode);

            //asynchronous task:
            fireStoreDatabase.readHazardAlertsFromFireStoreAndStoreItToLocalDB(postcode);
        }
    }

    private void incrementWorkInProgress() {
        workInProgress.postValue(workInProgress.getValue() + 1);
    }

    private void decrementWorkInProgress() {
        workInProgress.postValue(workInProgress.getValue() - 1);
    }

    public void loadHazardAlertsWithSpecifiedGeoHash(String geoHash) {

    }

    public LiveData<Integer> getWorkInProgress() {
        return workInProgress;
    }


    @Override
    public void update(Observable observable, Object arg) {
        try {
            if (arg != null) {
                if (observable instanceof FireStoreDatabase) {
                    if (arg instanceof Command) {
                        new Thread(() -> {
                            Command command = (Command) arg;
                            command.execute();
                        });
                    } else {
                        decrementWorkInProgress();
                    }
                } else if (observable instanceof LocalDatabaseHelper) {
                    List<HazardAlert> list_loaded_hazardAlerts = (List<HazardAlert>) arg;
                    hazardAlerts.postValue(list_loaded_hazardAlerts);
                    decrementWorkInProgress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
