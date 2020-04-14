package de.thu.tpro.android4bikes.view.menu.trackList;

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
import android.widget.TextView;

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

/**
 * @author Stefanie
 * This fragment contains the view elements and logic regarding tracks
 */
public class FragmentTrackList extends Fragment implements SearchView.OnQueryTextListener, LocationListener, SeekBar.OnSeekBarChangeListener {
    private static final String LOG_TAG = "FragmentCreateTrack";

    private TrackListDataBinder dataBinder;
    private RecyclerView recyclerView;
    private TrackListAdapter adapter;
    private TextView tv_trackList;
    private TextView tv_indicator_range;
    private TextView tv_indicator_quality;
    private TextView tv_indicator_difficulty;
    private TextView tv_indicator_funfactor;
    private SeekBar seekBarRange;
    private SeekBar seekBarQuality;
    private SeekBar seekBarDificulty;
    private SeekBar seekBarFunFactor;

    //TODO: delete when backend is connected to view
    private List<Track> trackList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTracklistDummy();

        dataBinder = new TrackListDataBinder(getResources(), trackList);
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        recyclerView = view.findViewById(R.id.rv_tracks);
        tv_trackList = view.findViewById(R.id.tv_totalTracksList);
        ImageButton btn_filter = view.findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(v -> openFilterDialog());

        SearchView searchView = view.findViewById(R.id.searchView_searchTrack);
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
        adapter = new TrackListAdapter(getActivity(), dataBinder.getTrackDistanceList());
        Log.d("FragmentCreateTrack", getActivity() + "");
        recyclerView.setAdapter(adapter);
        updateTotalTracksTextView();
    }

    @SuppressLint("MissingPermission")
    private void initLocationManager() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this);
    }


    // ------- OnQueryTextListener Methods -------

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<TrackDistanceTuple> searchedTrackList = dataBinder.searchTrackList(newText);
        adapter.replaceData(searchedTrackList);
        updateTotalTracksTextView();
        return true;
    }

    private void insertInformation() {
    }

    // ------- LocationListener Methods --------

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Location change: " + new GpsLocation(location));
        dataBinder.udpateUserLocation(new GpsLocation(location));
        adapter.replaceData(dataBinder.getTrackDistanceList());
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

    // ------- OnSeekbarChangeListener methods -------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar_range:
                dataBinder.setFilterRange(progress);
                break;
            case R.id.seekBar_roadQuality:
                dataBinder.setFilterQuality(progress);
                break;
            case R.id.seekBar_dificulty:
                dataBinder.setFilterDifficulty(progress);
                break;
            case R.id.seekBar_funfactor:
                dataBinder.setFilterFunfactor(progress);
                break;
            default:
                break;
        }
        updateFilterIndicators();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    // ------- private methods -------

    /**
     * opens a MaterialAlertDialog to show filter options to the user
     */
    private void openFilterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        // inflaters gonna inflate
        final View filterDialog = getLayoutInflater().inflate(R.layout.dialog_tracklist_filter, null);

        // Retrieve all seekbars from dialog and set listener
        seekBarRange = filterDialog.findViewById(R.id.seekBar_range);
        seekBarRange.setOnSeekBarChangeListener(this);
        seekBarQuality = filterDialog.findViewById(R.id.seekBar_roadQuality);
        seekBarQuality.setOnSeekBarChangeListener(this);
        seekBarDificulty = filterDialog.findViewById(R.id.seekBar_dificulty);
        seekBarDificulty.setOnSeekBarChangeListener(this);
        seekBarFunFactor = filterDialog.findViewById(R.id.seekBar_funfactor);
        seekBarFunFactor.setOnSeekBarChangeListener(this);
        // update seekbar to show actual values (if filtered before)
        updateFilterSeekbars();

        // retrieve all indicator textviews from the dialog
        tv_indicator_range = filterDialog.findViewById(R.id.tv_indicator_range);
        tv_indicator_quality = filterDialog.findViewById(R.id.tv_indicator_quality);
        tv_indicator_difficulty = filterDialog.findViewById(R.id.tv_indicator_dificulty);
        tv_indicator_funfactor = filterDialog.findViewById(R.id.tv_indicator_funfactor);
        // update textviews to show actual values
        updateFilterIndicators();

        // add custom layout to dialog
        builder.setView(filterDialog);

        // set dialog buttons and listeners
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            // nothing to do here -> cancel happens automatically
        });
        builder.setNeutralButton(R.string.reset, ((dialogInterface, i) -> {
            resetFilters();
            applyFilters();
        }));
        builder.setPositiveButton(R.string.accept, (dialogInterface, i) -> {
            applyFilters();
        });

        builder.setTitle("Filter");

        // show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Updates the displayed text of indicator TextViews in the filter Dialog
     */
    private void updateFilterIndicators() {
        tv_indicator_range.setText(dataBinder.getFilterTextRange());
        tv_indicator_quality.setText(dataBinder.getFilterTextQuality());
        tv_indicator_difficulty.setText(dataBinder.getFilterTextDifficulty());
        tv_indicator_funfactor.setText(dataBinder.getFilterTextFunfactor());
    }

    /**
     * Updates the Seekbar progress in the filter dialog
     */
    private void updateFilterSeekbars() {
        seekBarRange.setProgress(dataBinder.getFilterRange());
        seekBarQuality.setProgress(dataBinder.getFilterQuality());
        seekBarDificulty.setProgress(dataBinder.getFilterDifficulty());
        seekBarFunFactor.setProgress(dataBinder.getFilterFunfactor());
    }

    /**
     * Updates the total number of tracks in the fragment header
     */
    private void updateTotalTracksTextView() {
        tv_trackList.setText(String.format(getResources().getString(R.string.total_track_List),
                adapter.getItemCount()));
    }

    /**
     * filters the tracklist by the given criteria from the seekbars and updates the recyclerView
     */
    private void applyFilters() {
        List<TrackDistanceTuple> filteredList = dataBinder.filterTrackList();
        adapter.replaceData(filteredList);
        updateTotalTracksTextView();
    }

    private void resetFilters() {
        dataBinder.setFilterRange(0);
        dataBinder.setFilterQuality(0);
        dataBinder.setFilterDifficulty(0);
        dataBinder.setFilterFunfactor(0);
        updateFilterSeekbars();
        updateFilterIndicators();
    }


    // TODO: delete after backend is connected to view
    private void initTracklistDummy() {
        //test liste TODO: Backend anbinden
        trackList = Arrays.asList(new Track(), new Track(), new Track());

        trackList.get(0).setRating(new Rating(1, 1, 1, null));
        trackList.get(1).setRating(new Rating(3, 3, 3, null));
        trackList.get(2).setRating(new Rating(5, 5, 5, null));

        trackList.get(0).setName("Mega Harte Tour");
        trackList.get(1).setName("Mega Harte Tour 2: Electric Boogaloo");
        trackList.get(2).setName("Mega Harte Tour 3: Götterdämmerung");

        trackList.get(0).setDistance_km(15);
        trackList.get(1).setDistance_km(30);
        trackList.get(2).setDistance_km(7);

        trackList.get(0).setFineGrainedPositions(Arrays.asList(new Position(48.4049,9.9949)));
        trackList.get(1).setFineGrainedPositions(Arrays.asList(new Position(48.1773, 9.9730)));
        trackList.get(2).setFineGrainedPositions(Arrays.asList(new Position(48.3909, 10.0015)));

        trackList.get(0).setDescription("Mega Harte Tour, nur für Mega Harte");
        trackList.get(1).setDescription("Fahrradhelm muss dabei sein, ist wirklich hart, die Tour");
        trackList.get(2).setDescription("Schreibe lieber noch dein Testament bevor du diese Mega Harte Tour antrittst");
    }
}
