package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.thu.tpro.android4bikes.R;

public class FragmentRoadsideAssistance extends Fragment {

   // private ViewModelRoadsideAssistance viewModelRoadsideAssistance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     /*   viewModelRoadsideAssistance =
             new ViewModelProviders.of(this).get(ViewModelRoadsideAssistance.class);
        View root = inflater.inflate(R.layout.fragment_road_assistance, container, false);
        final TextView textView = root.findViewById(R.id.tv_institution);
        viewModelRoadsideAssistance.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
*/

        return inflater.inflate(R.layout.fragment_road_assistance, container, false);
    }
}
