package de.thu.tpro.android4bikes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.MutableLiveData;

/**
 * Class that provides information about the mobile and the wifi connection by using live data.
 */
public class BroadcastReceiverInternetConnection extends BroadcastReceiver {
    //TODO: Maybe issues regarding permissions? .INTERNET bzw .ACCESS_NETWORK_STATE?
    //compare: https://developer.android.com/training/basics/network-ops/managing
    //last access: 10.04.2020

    //Singleton pattern:
    private static BroadcastReceiverInternetConnection instance;

    //TODO: Evaluate current update mechanism
    private MutableLiveData<Boolean> connectedToWifi; //LiveData: because class cant't inherit from class Observable
    private MutableLiveData<Boolean> connectedToMobile;

    private BroadcastReceiverInternetConnection() {
        this.connectedToMobile = new MutableLiveData<>();
        this.connectedToWifi = new MutableLiveData<>();
        updateConnectedFlags(); //update mobile connection and wifi connection flag
    }

    public static BroadcastReceiverInternetConnection getInstance() {
        if (instance == null) {
            instance = new BroadcastReceiverInternetConnection();
        }
        return instance;
    }

    public MutableLiveData<Boolean> getConnectedToWifi() {
        return connectedToWifi;
    }

    public MutableLiveData<Boolean> getConnectedToMobile() {
        return connectedToMobile;
    }

    /**
     * starts the observation of the mobile and the wifi status.
     */
    public void startObserving() {
        //which kind of intents should inform this object?
        IntentFilter intentFilter = new IntentFilter();


        //TODO: intentFilter Could be deprecated! But is still used in:
        //https://developer.android.com/training/basics/network-ops/managing
        //last access: 10.04.2020
        //subscribe for all intents regarding the internet connection
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ///register this object as receiver
        GlobalContext.getContext().registerReceiver(this, intentFilter);
    }

    /**
     * stops the observation of the mobile and the wifi status.
     */
    public void stopObserving() {
        GlobalContext.getContext().unregisterReceiver(this);
    }

    /**
     * is called every time when there is a change in wifi/mobile connection
     *
     * @param context context
     * @param intent  concrete intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        forceConnectionUpdate();
    }

    /**
     * force update regarding the connection
     */
    public void forceConnectionUpdate() {
        updateConnectedFlags();
    }

    /**
     * updates the liva data objects regarding the mobile and the wifi connection
     */
    public void updateConnectedFlags() {
        //Compare: https://developer.android.com/training/basics/network-ops/managing
        ConnectivityManager connMgr = (ConnectivityManager)
                GlobalContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean wifiConnected = false;
        boolean mobileConnected = false;

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
        connectedToWifi.postValue(wifiConnected);
        connectedToMobile.postValue(mobileConnected);
    }

}
