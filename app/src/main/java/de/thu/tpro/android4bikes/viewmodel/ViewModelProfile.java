package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.Profile;

public class ViewModelProfile extends ViewModel {
    private MutableLiveData<Profile> myProfile;

    public LiveData<Profile> getMyProfile(){
        if (myProfile == null) {
            myProfile = new MutableLiveData<Profile>();
            myProfile.postValue(getInitialProfile());
        }
        return myProfile;
    }

    private Profile getInitialProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));
        return new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
    }
}
