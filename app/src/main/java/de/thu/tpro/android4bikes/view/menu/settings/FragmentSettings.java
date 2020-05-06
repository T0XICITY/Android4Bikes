package de.thu.tpro.android4bikes.view.menu.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.BluetoothButtonHandler;
import de.thu.tpro.android4bikes.view.MainActivity;

public class FragmentSettings extends PreferenceFragmentCompat {

    private ViewModelSettings mViewModel;

    public static FragmentSettings newInstance() {
        return new FragmentSettings();
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        findPreference("bluetooth_active").setOnPreferenceClickListener(preference -> {
            //Log.d("HalloWelt","Detect");
            BluetoothButtonHandler.getInstance(getActivity().getApplicationContext()).detectButton();
            return true;
        });
        findPreference("bluetooth_reset").setOnPreferenceClickListener(preference -> {
            //Log.d("HalloWelt","Reset");
            BluetoothButtonHandler.getInstance(getActivity().getApplicationContext()).removeButton();
            return true;
        });

    }
}
