package de.thu.tpro.android4bikes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.thu.tpro.android4bikes.util.BroadcastReceiverWifiConnection;

public class ViewModelWifiConnection extends ViewModel {
    private MutableLiveData<Boolean> connectedToWifi;
    private BroadcastReceiverWifiConnection wifiConnection;

    public ViewModelWifiConnection() {
        this.wifiConnection = BroadcastReceiverWifiConnection.getInstance();
        this.connectedToWifi = wifiConnection.getConnectedToWifi();
    }

    public LiveData<Boolean> getConnectedToWifi() {
        return connectedToWifi;
    }

    public void startObserving() {
        wifiConnection.startObservingWifi();
    }

    public void stopObserving() {
        wifiConnection.stopObservingWifi();
    }
}
