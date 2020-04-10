package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.thu.tpro.android4bikes.util.BroadcastReceiverInternetConnection;

public class ViewModelInternetConnection extends ViewModel {
    private MutableLiveData<Boolean> connectedToWifi;
    private MutableLiveData<Boolean> connectedToMobile;
    private BroadcastReceiverInternetConnection broadcastReceiver_mobile_wifi;

    public ViewModelInternetConnection() {
        this.broadcastReceiver_mobile_wifi = BroadcastReceiverInternetConnection.getInstance();
        this.connectedToWifi = broadcastReceiver_mobile_wifi.getConnectedToWifi();
        this.connectedToMobile = broadcastReceiver_mobile_wifi.getConnectedToMobile();
    }

    public LiveData<Boolean> getConnectedToWifi() {
        return connectedToWifi;
    }

    public LiveData<Boolean> getConnectedToMobile() {
        return connectedToMobile;
    }

    public void startObserving() {
        broadcastReceiver_mobile_wifi.startObserving();
    }

    public void stopObserving() {
        broadcastReceiver_mobile_wifi.stopObserving();
    }
}
