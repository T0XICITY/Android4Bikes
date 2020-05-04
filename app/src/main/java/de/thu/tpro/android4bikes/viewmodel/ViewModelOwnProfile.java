package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.CouchDBHelper;

/**
 * Class that provides {@link LiveData} regarding {@link Profile}s. All operations on Own Profile data
 * that are done by the UI have to be done using this class!! Following data
 * can be observed: {@link Profile}>
 * <h3>Getting access and observing tracking data</h3>
 * <pre>{@code
 *     public void observeViewModelForChanges(){
 *           ViewModelOwnProfile model_profile = new ViewModelProvider(this).get(ViewModelOwnProfile.class);
 *           model_profile.getMyProfile().observe(this, ownProfile->{
 *              if(ownProfile != null){
 *                  Toast.makeText(getApplicationcontext(),"Name: "+ownProfile.getFirstName(),Toast.LENGTH_SHORT).show();
 *              }
 *           });
 *     }
 * }</pre>
 */
public class ViewModelOwnProfile extends ViewModel implements Observer {
    private MutableLiveData<Profile> myProfile;
    private CouchDBHelper couchDBHelper;

    public ViewModelOwnProfile() {
        couchDBHelper = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
        myProfile = new MutableLiveData<>();
        couchDBHelper.addObserver(this);
        myProfile.postValue(couchDBHelper.readMyOwnProfile());
    }

    public LiveData<Profile> getMyProfile(){
        return myProfile;
    }

    public void updateMyProfile(Profile profile) {
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
