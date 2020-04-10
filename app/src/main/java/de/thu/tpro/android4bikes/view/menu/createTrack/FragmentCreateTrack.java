package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.Distribution;

import java.util.Arrays;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Track;

public class FragmentCreateTrack extends Fragment {
    private ViewModelCreateTrack vm_create_track;
    RecyclerView recyclerView;
//TO DELETE ------------------------------------------
    List<Track> trackList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //test liste TODO: Backend anbinden
        trackList = Arrays.asList(new Track(),new Track(),new Track());
        trackList.get(0).setName("Mega Harte Tour");
        trackList.get(1).setName("Mega Harte Tour 2: Electric Boogaloo");
        trackList.get(2).setName("Mega Harte Tour 3: Götterdämmerung");

        vm_create_track = new ViewModelCreateTrack(trackList);
        View view = inflater.inflate(R.layout.fragment_create_track, container, false);
        recyclerView = view.findViewById(R.id.rv_tracks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CreateTrackAdapter adapter = new CreateTrackAdapter(getActivity(),trackList);
        Log.d("FragmentCreateTrack",getActivity()+"");
        recyclerView.setAdapter(adapter);
    }

    private void insertInformation(){

    }
}
