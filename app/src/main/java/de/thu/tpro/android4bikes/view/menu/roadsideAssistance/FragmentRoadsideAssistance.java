package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.thu.tpro.android4bikes.R;

/**
 * @author Elias, Stefanie
 */
public class FragmentRoadsideAssistance extends Fragment {

    private RecyclerView rv_road_assistance;
    private ViewModelRoadsideAssistance vm_RoadsideAssistance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO -> Code korrigieren, da bald deprecated
        vm_RoadsideAssistance = new ViewModelRoadsideAssistance(getResources());

        View view = inflater.inflate(R.layout.fragment_road_assistance, container, false);
        rv_road_assistance = view.findViewById(R.id.lv_road_assistance);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRoadAssistanceList();
    }

    /**
     * showing Listview with RoadsideAssistance entries
     */
    private void initRoadAssistanceList() {
        RoadsideAssistanceListAdapter roadsideAssistanceListAdapter = new RoadsideAssistanceListAdapter(getActivity(),
                vm_RoadsideAssistance.getEntries(), vm_RoadsideAssistance.getTelnummer());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        rv_road_assistance.setAdapter(roadsideAssistanceListAdapter);
        rv_road_assistance.setLayoutManager(layoutManager);
    }
}
