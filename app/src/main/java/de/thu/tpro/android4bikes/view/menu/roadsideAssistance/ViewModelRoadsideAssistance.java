package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ViewModelRoadsideAssistance {

    private MutableLiveData<String> mText;

    public ViewModelRoadsideAssistance() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }


}
