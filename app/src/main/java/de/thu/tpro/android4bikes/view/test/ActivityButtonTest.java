package de.thu.tpro.android4bikes.view.test;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.BluetoothButtonHandler;

public class ActivityButtonTest extends AppCompatActivity implements Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_button_test);

        BluetoothButtonHandler.getInstance(getApplicationContext()).addObserver(this);

        // Register new Button? - Call this one
        // BluetoothButtonHandler.getInstance(getApplicationContext()).detectButton();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return BluetoothButtonHandler.getInstance(getApplicationContext()).handleKeyEvent(event);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BluetoothButtonHandler) {
            Toast.makeText(getApplicationContext(), "Gefahr!", Toast.LENGTH_SHORT).show();
        }
    }
}
