package de.thu.tpro.android4bikes.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;
import de.thu.tpro.android4bikes.services.weather.OpenWeatherManager;
import de.thu.tpro.android4bikes.util.GlobalContext;

/**
 * Class that provides {@link LiveData} regarding {@link OpenWeatherObject}. All operations on WeatherData
 * that are done by the UI have to be done using this class!! Following data
 * can be observed: List<{@link OpenWeatherObject}>
 * <h3>Getting access and observing data</h3>
 * <pre>{@code
 *     public void observeViewModelForChanges(){
 *       ViewModelWeather model_weather = new ViewModelProvider(this).get(ViewModelWeather.class);
 *         model_weather.getCurrentWeather().observe(this, newOpenWeatherObject->{
 *             Toast.makeText(getApplicationContext(),"Weather Icon:"+
 *             newOpenWeatherObject.getForecastList().get(0).getWeather().get(0).getIcon() +
 *             "\nTemperature: "+
 *             newOpenWeatherObject.getForecastList().get(0).getMain().getTemp()
 *             ,Toast.LENGTH_SHORT).show();
 *         });
 *     }
 * }</pre>
 */
public class ViewModelWeather extends ViewModel implements Observer {
    private MutableLiveData<OpenWeatherObject> currentWeather;
    private OpenWeatherManager weatherManager;

    public ViewModelWeather() {
        weatherManager = new OpenWeatherManager();
        weatherManager.addObserver(this);
        currentWeather = new MutableLiveData<>();
        currentWeather.postValue(new Gson().fromJson(getJsonFromAssets(GlobalContext.getContext(),"initialWeather.json"),OpenWeatherObject.class));
        weatherManager.startWeatherSubscription();
    }

    public LiveData<OpenWeatherObject> getCurrentWeather(){
        return currentWeather;
    }

    private static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    @Override
    public void update(Observable o, Object arg) {
        OpenWeatherObject newWeather = weatherManager.getWeather();
        currentWeather.postValue(newWeather);
    }
}
