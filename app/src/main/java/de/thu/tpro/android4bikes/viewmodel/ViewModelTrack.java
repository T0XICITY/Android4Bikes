package de.thu.tpro.android4bikes.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ThreadLocalRandom;

import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.FireStoreDatabase;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

public class ViewModelTrack implements Observer {
    //UI -> Datenhaltung
    //SUBMITTRACK: Create Track -> ViewModel ruft FireBase an
    //READTRACK : Liste an Tracks erhalten (PLZ) -> alle tracks in ulm sehen

    private MutableLiveData<List<Track>> list_shownTracks;
    private CouchDBHelper couchDBHelper;
    private FirebaseConnection firebaseConnection;

    public ViewModelTrack() {

        //Deal with the local database
        couchDBHelper = new CouchDBHelper();
        //deal with FireStore:
        firebaseConnection = FirebaseConnection.getInstance();
        //create LiveData-Wrapper:
        list_shownTracks = new MutableLiveData<>();

        //this is observer of FireBase (in the case of failure)
        firebaseConnection.addObserver(this);
        //this is observer of local database (in case of success)
        couchDBHelper.addObserver(this);
    }

    public LiveData<List<Track>> getTracksWithSpecifiedPostcode(String postcode) {
        if (postcode != null && postcode.length() >= 3) {

            //asynchronous task:
            firebaseConnection.readTracksFromFireStoreAndStoreItToLocalDB(postcode);
        }
        return list_shownTracks;
    }


    @Override
    public void update(Observable observable, Object o) {
        try {
            if (o != null) {
                if (observable instanceof FireStoreDatabase) {
                    FirebaseConnection.STATUSCODES statuscode = (FirebaseConnection.STATUSCODES) o;

                    if (statuscode == FirebaseConnection.STATUSCODES.ERROR) {
                        //read from local database + Rueckgabewert wegschmeissen
                        //asynchronous task integration
                    }
                } else if (observable instanceof LocalDatabaseHelper) {
                    //Rueckgabewert in LiveData speichern
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FirebaseTask extends AsyncTask<Void,Void,Void> {
        private ViewModelTrack model;
        public FirebaseTask(ViewModelTrack model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //firebaseconnection. ...
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
