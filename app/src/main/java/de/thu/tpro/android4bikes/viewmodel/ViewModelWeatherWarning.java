package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import de.thu.tpro.android4bikes.data.warning.DWDwarning;
import de.thu.tpro.android4bikes.services.weather.WeatherManagerWarning;

public class ViewModelWeatherWarning extends ViewModel implements Observer {
    private MutableLiveData<Set<DWDwarning>> currentWarnings;
    private WeatherManagerWarning managerWarning;

    public ViewModelWeatherWarning(){
        managerWarning = new WeatherManagerWarning();
        managerWarning.addObserver(this);
        managerWarning.startWeatherTask();
    }

    public LiveData<Set<DWDwarning>> getWeatherWarnings(){
        if (currentWarnings == null){
            currentWarnings = new MutableLiveData<>();
        }
        return currentWarnings;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (currentWarnings == null){
            currentWarnings = new MutableLiveData<>();
        }
        Set<DWDwarning> newWarnings = managerWarning.getDWDwarnings();
        currentWarnings.postValue(newWarnings);
    }
}
