package de.thu.tpro.android4bikes.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.util.BluetoothButtonHandler;


public class ViewModelBtBtn extends AndroidViewModel implements Observer {
    private BluetoothButtonHandler handler;
    private Application application;
    private MutableLiveData<Void> btnEvent;

    public ViewModelBtBtn(@NonNull Application application) {
        super(application);
        this.application = application;
        handler = BluetoothButtonHandler.getInstance(application.getApplicationContext());
        btnEvent = new MutableLiveData<>();
        handler.addObserver(this);
    }

    public void resetBtn(){
        handler.removeButton();
    }

    public void detectBtn(){
        handler.detectButton();
    }

    public boolean handleKeyEvent(KeyEvent event){
        boolean b =  handler.handleKeyEvent(event);
        if (b){
            btnEvent.postValue(null);
        }
        return b;
    }

    public MutableLiveData<Void> getBtnEvent() {
        return btnEvent;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof BluetoothButtonHandler){
            Log.d("HalloWelt","MainActivity-UpdateBlueToothButton");
            Toast.makeText(application.getApplicationContext(),"MainActivity-UpdateBlueToothButton",Toast.LENGTH_SHORT).show();
        }
    }
}
