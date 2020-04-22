package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.CouchDBHelper;

public class ViewModelOwnProfile extends ViewModel implements Observer {
    private MutableLiveData<Profile> myProfile;
    private CouchDBHelper couchDBHelper;

    public ViewModelOwnProfile() {
        couchDBHelper = new CouchDBHelper();
        myProfile = new MutableLiveData<>();
        couchDBHelper.readMyOwnProfile();
    }

    public LiveData<Profile> getMyProfile(){
        return myProfile;
    }

    public void updateMyProfile(Profile profile) {
        myProfile.postValue(profile);
        couchDBHelper.updateMyOwnProfile(profile);
    }

    public void deleteMyProfile() {
        couchDBHelper.deleteMyOwnProfile();
    }

    @Override
    public void update(Observable observable, Object o) {
        try {
            if (o != null) {
                if (o instanceof Profile) {
                    Profile profile = (Profile) o;
                    myProfile.postValue(profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
