package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;
import de.thu.tpro.android4bikes.services.weather.OpenWeatherManager;

public class ViewModelWeather extends ViewModel implements Observer {
    private MutableLiveData<OpenWeatherObject> currentWeather;
    private OpenWeatherManager weatherManager;

    public ViewModelWeather() {
        weatherManager = new OpenWeatherManager();
        weatherManager.addObserver(this);
        weatherManager.startWeatherSubscription();
    }

    public LiveData<OpenWeatherObject> getCurrentWeather(){
        if (currentWeather == null) {
            currentWeather = new MutableLiveData<OpenWeatherObject>();
        }
        return currentWeather;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (currentWeather == null){
            currentWeather = new MutableLiveData<OpenWeatherObject>();
        }
        OpenWeatherObject newWeather = weatherManager.getWeather();
        currentWeather.postValue(newWeather);
    }
}
