package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.services.GpsLocation;

public class FragmentCreateTrack extends Fragment implements SearchView.OnQueryTextListener, LocationListener {
    private static final String LOG_TAG = "FragmentCreateTrack";

    private ViewModelCreateTrack vm_create_track;
    RecyclerView recyclerView;
    SearchView searchView;
    CreateTrackAdapter adapter;
    ImageButton btn_filter;
    LocationManager locationManager;
//TO DELETE ------------------------------------------
    List<Track> trackList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTracklistDummy();

        vm_create_track = new ViewModelCreateTrack(trackList);
        View view = inflater.inflate(R.layout.fragment_create_track, container, false);
        recyclerView = view.findViewById(R.id.rv_tracks);
        btn_filter = view.findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(v-> openFilterDialog());

        searchView = view.findViewById(R.id.searchView_searchTrack);
        searchView.setOnQueryTextListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLocationManager();
        adapter = new CreateTrackAdapter(getActivity(), vm_create_track.getTrackDistanceList());
        Log.d("FragmentCreateTrack",getActivity()+"");
        recyclerView.setAdapter(adapter);
    }

    private void initTracklistDummy() {
        //test liste TODO: Backend anbinden
        trackList = Arrays.asList(new Track(),new Track(),new Track());

        trackList.get(0).setRating(new Rating(1,1,1,null));
        trackList.get(1).setRating(new Rating(3,3,3,null));
        trackList.get(2).setRating(new Rating(5,5,5,null));

        trackList.get(0).setName("Mega Harte Tour");
        trackList.get(1).setName("Mega Harte Tour 2: Electric Boogaloo");
        trackList.get(2).setName("Mega Harte Tour 3: Götterdämmerung");

        trackList.get(0).setDistance_km(15);
        trackList.get(1).setDistance_km(30);
        trackList.get(2).setDistance_km(7);

        trackList.get(0).setFineGrainedPositions(Arrays.asList(new Position(9.9949,48.4049)));
        trackList.get(1).setFineGrainedPositions(Arrays.asList(new Position(9.9730,48.1773)));
        trackList.get(2).setFineGrainedPositions(Arrays.asList(new Position(10.0015,48.3909)));

        trackList.get(0).setDescription("Mega Harte Tour, nur für Mega Harte");
        trackList.get(1).setDescription("Fahrradhelm muss dabei sein, ist wirklich hart, die Tour");
        trackList.get(2).setDescription("Schreibe lieber noch dein Testament bevor du diese Mega Harte Tour antrittst");
    }

    private void openFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Filter");
        final View filterDialog = getLayoutInflater().inflate(R.layout.dialog_tracklist_filter,null);

        builder.setView(filterDialog);
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {dialogInterface.cancel(); });
        builder.setPositiveButton(R.string.accept, (dialogInterface, i) -> {dialogInterface.cancel();setFilterOptions(filterDialog);});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setFilterOptions(View filterDialog) {
        SeekBar seekBarRange = filterDialog.findViewById(R.id.seekBar_range);
        SeekBar seekBarQuality = filterDialog.findViewById(R.id.seekBar_roadQuality);
        SeekBar seekBarDificulkty = filterDialog.findViewById(R.id.seekBar_dificulty);
        SeekBar seekBarfunfactor = filterDialog.findViewById(R.id.seekBar_funfactor);

        List<TrackDistanceTuple> filteredList =  vm_create_track.filterTrackList(seekBarRange.getProgress(),seekBarQuality.getProgress(),seekBarDificulkty.getProgress(),seekBarfunfactor.getProgress());
        adapter.replaceData(filteredList);
    }

    @SuppressLint("MissingPermission")
    private void initLocationManager() {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,1f,this);
    }


    // ------- OnQueryTextListener Methods -------

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<TrackDistanceTuple> searchedTrackList = vm_create_track.searchTrackList(newText);
        adapter.replaceData(searchedTrackList);
        return true;
    }
    private void insertInformation(){
    }


    // ------- LocationListener Methods --------

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG,"Location change: "+new GpsLocation(location));
        vm_create_track.udpateUserLocation(new GpsLocation(location));
        adapter.replaceData(vm_create_track.getTrackDistanceList());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
