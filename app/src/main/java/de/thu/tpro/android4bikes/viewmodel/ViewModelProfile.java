package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.CouchDBHelper;

public class ViewModelProfile extends ViewModel implements Observer {
    private MutableLiveData<Profile> myProfile;
    private CouchDBHelper couchDBHelper;

    public ViewModelProfile() {
        couchDBHelper = new CouchDBHelper();
    }

    public LiveData<Profile> getMyProfile(){
        if (myProfile == null) {
            myProfile = new MutableLiveData<Profile>();
            myProfile.postValue(couchDBHelper.readMyProfile());
        }
        return myProfile;
    }


    @Override
    public void update(Observable observable, Object o) {
        //ist o liste oder einzelnes Profil?
    }
}
