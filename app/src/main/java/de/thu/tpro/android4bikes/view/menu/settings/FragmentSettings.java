package de.thu.tpro.android4bikes.view.menu.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import de.thu.tpro.android4bikes.R;

public class FragmentSettings extends PreferenceFragmentCompat {

    private ViewModelSettings mViewModel;

    public static FragmentSettings newInstance() {
        return new FragmentSettings();
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);
    }
}
