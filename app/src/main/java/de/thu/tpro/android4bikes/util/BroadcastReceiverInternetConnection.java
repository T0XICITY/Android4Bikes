package de.thu.tpro.android4bikes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.util.ObserverMechanism.InternetObservable;
import de.thu.tpro.android4bikes.util.ObserverMechanism.InternetObserver;

/**
 * Class that provides information about the mobile and the wifi connection by using live data.
 */
public class BroadcastReceiverInternetConnection extends BroadcastReceiver implements InternetObservable {

    //TODO: Maybe issues regarding permissions? .INTERNET bzw .ACCESS_NETWORK_STATE?
    //compare: https://developer.android.com/training/basics/network-ops/managing
    //last access: 10.04.2020

    //Singleton pattern:
    private static BroadcastReceiverInternetConnection instance;

    //observer mechanism
    private List<InternetObserver> list_observer;
    private boolean wifiConnected;
    private boolean mobileConnected;

    /**
     * the observation starts automatically after creating an instance of this class.
     */
    private BroadcastReceiverInternetConnection() {
        //observer mechanism:
        list_observer = new ArrayList<>();
        updateConnectedFlags(); //update mobile connection and wifi connection flag
        this.startObserving();
    }

    public static BroadcastReceiverInternetConnection getInstance() {
        if (instance == null) {
            instance = new BroadcastReceiverInternetConnection();
        }
        return instance;
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
        updateConnectedFlags();
        notifyConnectionChange();
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

        if (this.wifiConnected != wifiConnected || this.mobileConnected != mobileConnected) {
            notifyConnectionChange();
        }
        this.mobileConnected = mobileConnected;
        this.wifiConnected = wifiConnected;
    }

    @Override
    public void notifyConnectionChange() {
        for (InternetObserver o : list_observer) {
            o.updatedInternetConnection(this.wifiConnected, this.mobileConnected);
        }
    }

    @Override
    public void addObserver(InternetObserver observer) {
        this.list_observer.add(observer);
    }

    @Override
    public void removeObserver(InternetObserver observer) {
        this.list_observer.remove(observer);
    }

}
