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
        currentWarnings = new MutableLiveData<>();
        managerWarning.startWeatherTask();
    }

    public LiveData<Set<DWDwarning>> getWeatherWarnings(){
        return currentWarnings;
    }

    @Override
    public void update(Observable o, Object arg) {
        Set<DWDwarning> newWarnings = managerWarning.getDWDwarnings();
        currentWarnings.postValue(newWarnings);
    }
}
