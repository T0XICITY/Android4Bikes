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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.services.GpsLocation;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnTracks;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;

/**
 * @author Stefanie
 * This fragment contains the view elements and logic regarding tracks
 */
public class FragmentTrackList extends Fragment implements SearchView.OnQueryTextListener,
        LocationListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, Observer<Map<Track, Profile>> {
    private static final String LOG_TAG = "FragmentCreateTrack";

    private boolean showOwnTracksOnly = false;

    private ViewModelTrack viewModelTrack;
    private ViewModelOwnTracks viewModelOwnTrack;

    private TrackListDataBinder dataBinder;
    private RecyclerView recyclerView;
    private TrackListAdapter adapter;
    private TextView tv_trackList;

    // package private to clear focus
    SearchView searchView;

    // Filter dialog elements
    private TextView tv_indicator_range;
    private TextView tv_indicator_quality;
    private TextView tv_indicator_difficulty;
    private TextView tv_indicator_funfactor;
    private SeekBar seekBarRange;
    private SeekBar seekBarQuality;
    private SeekBar seekBarDificulty;
    private SeekBar seekBarFunFactor;

    // Sorting dialog elements
    private RadioGroup rg_sortTracks;
    private RadioGroup rg_orderTracks;

    Track dummy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBinder = new TrackListDataBinder(getResources());

        viewModelTrack = new ViewModelProvider(this).get(ViewModelTrack.class);
        viewModelOwnTrack = new ViewModelProvider(this).get(ViewModelOwnTracks.class);

        if (showOwnTracksOnly)
            viewModelOwnTrack.getTracks().observe(getViewLifecycleOwner(), this::onChanged);
        else
            viewModelTrack.getTracks().observe(getViewLifecycleOwner(), this::onChanged);

        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        recyclerView = view.findViewById(R.id.rv_tracks);
        tv_trackList = view.findViewById(R.id.tv_totalTracksList);

        ImageButton btn_filter = view.findViewById(R.id.btn_filter_tracks);
        btn_filter.setOnClickListener(v -> openFilterDialog());

        ImageButton btn_sort = view.findViewById(R.id.btn_sort_tracks);
        btn_sort.setOnClickListener(v -> openSortingDialog());

        searchView = view.findViewById(R.id.searchView_searchTrack);
        searchView.setOnQueryTextListener(this);

        // make all views != searchView clear focus from the searchview when touched
        recyclerView.setOnClickListener(this::onClick);
        tv_trackList.setOnClickListener(this::onClick);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLocationManager();
        adapter = new TrackListAdapter(this, dataBinder.getTrackDistanceList());

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

    @Override
    public void onChanged(Map<Track, Profile> trackProfileMap) {
        for (Track t : trackProfileMap.keySet()) {
            dataBinder.addTrack(t);
        }
        adapter.replaceData(dataBinder.getTrackDistanceList());
        updateTotalTracksTextView();
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
        searchView.clearFocus();

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
     * opens a MaterialAlertDialog with sorting options
     */
    private void openSortingDialog() {
        searchView.clearFocus();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        // inflaters gonna inflate
        View view = getLayoutInflater().inflate(R.layout.dialog_tracklist_sorting, null);

        // get RadioGroups
        rg_sortTracks = view.findViewById(R.id.radioGroup_sortTracks);
        preselectSorting();
        rg_orderTracks = view.findViewById(R.id.radioGroup_orderTracks);
        preselectOrdering();

        // add custom layout to dialog
        builder.setView(view);

        builder.setPositiveButton(R.string.accept, (dialogInterface, i) -> {
            // determine and apply sorting rules
            TrackDistanceTuple.SortBy sortBy = determineSortBy();
            boolean ascending = determineSortOrder();
            dataBinder.applySortingRules(sortBy, ascending);
            adapter.replaceData(dataBinder.sortTrackDistanceList());
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            submitDummyTrack();
            // Nothing to do - cancel is handled automatically
        });

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

    /**
     * resets all filters (data and view) to defaults
     */
    private void resetFilters() {
        dataBinder.setFilterRange(0);
        dataBinder.setFilterQuality(0);
        dataBinder.setFilterDifficulty(0);
        dataBinder.setFilterFunfactor(0);
        updateFilterSeekbars();
        updateFilterIndicators();
    }


    private TrackDistanceTuple.SortBy determineSortBy() {
        TrackDistanceTuple.SortBy sortBy = TrackDistanceTuple.SortBy.RANGE;
        switch (rg_sortTracks.getCheckedRadioButtonId()) {
            case R.id.radio_sortTracks_Range:
                sortBy = TrackDistanceTuple.SortBy.RANGE;
                break;
            case R.id.radio_sortTracks_Quality:
                sortBy = TrackDistanceTuple.SortBy.QUALITY;
                break;
            case R.id.radio_sortTracks_Difficulty:
                sortBy = TrackDistanceTuple.SortBy.DIFFICULTY;
                break;
            case R.id.radio_sortTracks_Funfactor:
                sortBy = TrackDistanceTuple.SortBy.FUNFACTOR;
            default:
                break;
        }
        return sortBy;
    }

     private boolean determineSortOrder() {
        // true = ascending, false = descending
        return rg_orderTracks.getCheckedRadioButtonId() == R.id.radio_trackOrder_asc;
    }

    /**
     * Pre-selects the radio button in "sort by" group, depending on what was selected before (or default)
     */
    private void preselectSorting() {
        switch (dataBinder.getCheckedSortBy()) {
            case RANGE:
                ((RadioButton) rg_sortTracks.findViewById(R.id.radio_sortTracks_Range)).setChecked(true);
                break;
            case QUALITY:
                ((RadioButton) rg_sortTracks.findViewById(R.id.radio_sortTracks_Quality)).setChecked(true);
                break;
            case DIFFICULTY:
                ((RadioButton) rg_sortTracks.findViewById(R.id.radio_sortTracks_Difficulty)).setChecked(true);
                break;
            case FUNFACTOR:
                ((RadioButton) rg_sortTracks.findViewById(R.id.radio_sortTracks_Funfactor)).setChecked(true);
                break;
        }
    }

    /**
     * Pre-selects the radio button in "order by" group, depending on what was selected before (or default)
     */
    private void preselectOrdering() {
        if (dataBinder.isSortAscending())
            ((RadioButton) rg_orderTracks.findViewById(R.id.radio_trackOrder_asc)).setChecked(true);
        else
            ((RadioButton) rg_orderTracks.findViewById(R.id.radio_trackOrder_desc)).setChecked(true);
    }

    @Override
    public void onClick(View view) {
        if (view != searchView) {
            searchView.clearFocus();
        }
    }

    public void setShowOwnTracksOnly(boolean ownTracksOnly) {
        showOwnTracksOnly = ownTracksOnly;
    }

    public boolean isOwnTracksOnly() {
        return showOwnTracksOnly;
    }

    // TODO delete after testing
    private void submitDummyTrack() {
        dummy = new Track();
        dummy.setName("DummyDummyDumm");
        dummy.setDescription("DummDumm");
        dummy.setDistance_km(999);
        Log.d("TRACK SUBMIT",""+dummy);
        //viewModelTrack.submitTrack(dummy); TODO: SUBMIT ONLY VIA OWN_TRACK.
        viewModelOwnTrack.submitTrack(dummy);
    }
}
