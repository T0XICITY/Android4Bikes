package de.thu.tpro.android4bikes.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;

import java.util.Observable;

/**
 * {@link BluetoothButtonHandler} provides the necessary functionality for integration of an BluetoothButton.
 * Objects which want to get knowledge about a buttonEvent have to implement the interface {@link java.util.Observer}
 * and register themselves
 */
public class BluetoothButtonHandler extends Observable {
    private static class Contract {
        public final static String SP_BUTTON = "SP_Button";
        public final static String DEVICEID = "deviceID";
        public final static int keyCode = 24;
    }

    private static BluetoothButtonHandler instance;
    private SharedPreferences sharedPreferences;
    private boolean bDetect;
    private int deviceID;

    /**
     * Private constructor which allows the singleton pattern
     *
     * @param context {@link android.content.Context}
     */
    private BluetoothButtonHandler(Context context) {
        sharedPreferences = context.getSharedPreferences(Contract.SP_BUTTON, Context.MODE_PRIVATE);
        deviceID = sharedPreferences.getInt(Contract.DEVICEID, -1);
        bDetect = false;
    }

    /**
     * @param context Caller have to provide the {@link android.content.Context}
     * @return Instance of the {@link BluetoothButtonHandler}
     */
    public static BluetoothButtonHandler getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothButtonHandler(context);
        }
        return instance;
    }

    /**
     * Method to detect and deposit the button
     */
    public void detectButton() {
        bDetect = true;
    }

    /**
     * Method which handles the keyEvent in order to notify Observers for the keyEvent
     *
     * @param keyEvent is the keyEvent which is provieded by {@link android.view.KeyEvent.Callback#onKeyDown(int, KeyEvent)}
     * @return True if the keyEvent was handles by this Handler and it was the right value
     */
    public boolean handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == Contract.keyCode) {
            if (bDetect) {
                deviceID = keyEvent.getDeviceId();
                sharedPreferences.edit().putInt(Contract.DEVICEID, deviceID).apply();
                bDetect = false;
                return true;
            } else if (keyEvent.getDeviceId() == deviceID) {
                setChanged();
                notifyObservers();
                return true;
            }
        }
        return false;
    }
}
