package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

public class ViewModelHazardAlert extends ViewModel {
    private FirebaseConnection firebaseConnection;
    private LocalDatabaseHelper localDB;
    private MutableLiveData<List<HazardAlert>> hazardAlerts;

    public ViewModelHazardAlert(){
        firebaseConnection = FirebaseConnection.getInstance();
        localDB = new CouchDBHelper();
        hazardAlerts = new MutableLiveData<>();
    }

    public LiveData<List<HazardAlert>> getHazardAlerts(){
        return hazardAlerts;
    }

    public void submitHazardAlert(HazardAlert hazardAlert){
        firebaseConnection.submitHazardAlertToFireStore(hazardAlert);
    }
}

//UI -> Datenhaltung
//SUBMITTRACK: Create Track -> ViewModel ruft FireBase an
//READTRACK: Liste an Tracks erhalten (PLZ) -> alle tracks in ulm sehen

