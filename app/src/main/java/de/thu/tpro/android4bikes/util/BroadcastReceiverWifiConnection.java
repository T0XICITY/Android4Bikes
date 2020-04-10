package de.thu.tpro.android4bikes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import androidx.lifecycle.MutableLiveData;

public class BroadcastReceiverWifiConnection extends BroadcastReceiver {
    //compare: https://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android
    //last access: 10.04.2020

    private static BroadcastReceiverWifiConnection instance;
    //TODO: Evaluate current update mechanism
    private MutableLiveData<Boolean> connectedToWifi; //LiveData: because class cant't inherit from class Observable

    private BroadcastReceiverWifiConnection() {
        this.connectedToWifi = new MutableLiveData<>();
        connectedToWifi.postValue(isConnectionCurrentlyAvailable());
    }

    public static BroadcastReceiverWifiConnection getInstance() {
        if (instance == null) {
            instance = new BroadcastReceiverWifiConnection();
        }
        return instance;
    }

    public MutableLiveData<Boolean> getConnectedToWifi() {
        return connectedToWifi;
    }

    public void startObservingWifi() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        GlobalContext.getContext().registerReceiver(this, intentFilter);
    }

    public void stopObservingWifi() {
        GlobalContext.getContext().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                connectedToWifi.postValue(true);
            } else {
                connectedToWifi.postValue(false);
            }
        }
    }

    public void forceConnectionUpdate() {
        connectedToWifi.postValue(isConnectionCurrentlyAvailable());
    }

    private boolean isConnectionCurrentlyAvailable() {
        //compare: https://developer.android.com/training/monitoring-device-state/connectivity-status-type
        //last access: 10.04.2020
        boolean isConnected = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) GlobalContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            connectedToWifi.postValue(isConnected);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

}
