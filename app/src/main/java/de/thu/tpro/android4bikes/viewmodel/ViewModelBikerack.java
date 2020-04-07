package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelBikerack extends ViewModel {
    //Get and set possible
    private MutableLiveData<String> currentName;

    //With LiveData just get possible
    public LiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
            currentName.postValue("Initial Value");
        }
        return currentName;
    }

    public MutableLiveData<String> getCurrentNameMutable() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
            currentName.postValue("Initial Value");
        }
        return currentName;
    }
}
