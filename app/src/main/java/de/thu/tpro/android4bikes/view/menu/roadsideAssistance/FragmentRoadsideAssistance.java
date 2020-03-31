package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.thu.tpro.android4bikes.R;

public class FragmentRoadsideAssistance extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_road_assistance, container, false);
    }
}
