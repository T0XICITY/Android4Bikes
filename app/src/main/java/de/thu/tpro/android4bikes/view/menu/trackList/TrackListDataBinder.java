package de.thu.tpro.android4bikes.view.menu.trackList;

import android.content.res.Resources;
import android.location.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.services.GpsLocation;

/**
 * @author Stefanie
 * contains all fields and logic for data that will be displayed in {@link FragmentTrackList}
 */
public class TrackListDataBinder {

    private static final String LOG_TAG = "ViewModelCreateTrack";
    private static final int[] rangeIncrements = {99999,1,5,10,15}; // TODO auslagern um konfiguriertbar zu machen

    private final Resources res;

    private int filter_range;
    private int filter_quality;
    private int filter_difficulty;
    private int filter_funfactor;
    private List<TrackDistanceTuple> trackDistanceList;
    private GpsLocation currentLocation;

    //TODO: Backend anbinden
    public TrackListDataBinder(Resources res, List<Track> trackList) {
        this.res = res;
        trackDistanceList = new ArrayList<>();
        for (Track track : trackList) {
            trackDistanceList.add(new TrackDistanceTuple(track));
        }
    }

    /**
     * @return list of tracks the view model is working with
     */
    public List<TrackDistanceTuple> getTrackDistanceList() {
        return trackDistanceList;
    }

    /**
     * https://stackoverflow.com/questions/33793948/searchview-with-recyclerview/35144362#35144362
     * Searches all tracks (name and description) for the given term
     *
     * @param searchTerm the search term to filter the list
     * @return filtered list
     */
    public List<TrackDistanceTuple> searchTrackList(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        final List<TrackDistanceTuple> filteredTrackList = new ArrayList<>();
        for (TrackDistanceTuple tuple : trackDistanceList) {
            Track track = tuple.getTrack();
            final String title = track.getName().toLowerCase();
            if (title.contains(searchTerm)) {
                filteredTrackList.add(tuple);
                continue;
            }
            final String description = track.getDescription();
            if (description != null && description.toLowerCase().contains(searchTerm)) {
                filteredTrackList.add(tuple);
            }
        }
        return filteredTrackList;
    }

    /**
     * filters the track list by the filters already set in the databinder
     * @return list of all tracks that match the given criteria
     */
    public List<TrackDistanceTuple> filterTrackList(){
        final List<TrackDistanceTuple> filteredTrackList = new ArrayList<>();

        for (TrackDistanceTuple tuple : trackDistanceList) {
            Track track = tuple.getTrack();
            // get actual filter range from range increments array
            int range = rangeIncrements[filter_range];

            if (tuple.getDistanceToUser() <= range
                    && track.getRating().getRoadquality() >= filter_quality
                    && track.getRating().getDifficulty() >= filter_difficulty
                    && track.getRating().getFun() >= filter_funfactor){
                filteredTrackList.add(tuple);
            }

        }
        // sort list
        return filteredTrackList;
    }

    /**
     * updates the current user location and re-calculates the distances to track starting points
     * @param userLocation current location of the user
     */
    public void udpateUserLocation(GpsLocation userLocation) {
        currentLocation = userLocation;
        calculateAllDistances();
    }

    /**
     * Calculates the distance between a track starting point and the user's current location
     * @param trackListIndex index of the track to calculate distance to in the trackList
     */
    private void calculateDistance(int trackListIndex){
        // abort calculation if user's location is unknown
        if (currentLocation==null)
            return;

        TrackDistanceTuple currentTuple = trackDistanceList.get(trackListIndex);

        Position startPosition = currentTuple.getTrack().getFineGrainedPositions().get(0);
        Location trackLocation = new Location("");

        trackLocation.setLatitude(startPosition.getLatitude());
        trackLocation.setLongitude(startPosition.getLongitude());

        // set distance to track in kilometres
        trackDistanceList.get(trackListIndex)
                .setDistanceToUser(currentLocation.distanceTo(trackLocation)  / 1000);
    }

    /**
     * calculates the distance between all tracks and the user's current location
     */
    private void calculateAllDistances() {
        for (int i = 0; i < trackDistanceList.size(); i++){
            calculateDistance(i);
        }
        Collections.sort(trackDistanceList);
    }

    public void setFilterRange(int filterRange) {
        this.filter_range = filterRange;
    }

    public void setFilterQuality(int filter_quality) {
        this.filter_quality = filter_quality;
    }

    public void setFilterDifficulty(int filterDifficulty) {
        this.filter_difficulty = filterDifficulty;
    }

    public void setFilterFunfactor(int filterFunfactor) {
        this.filter_funfactor = filterFunfactor;
    }

    /**
     * Get filter values for seekBar_range progress
     * @return values from 0 to 4
     */
    public int getFilterRange() {
        return filter_range;
    }

    /**
     * Get filter values for seekBar_quality progress
     * @return values from 0 to 4
     */
    public int getFilterQuality() {
        return filter_quality;
    }

    /**
     * Get filter values for seekBar_difficulty progress
     * @return values from 0 to 4
     */
    public int getFilterDifficulty() {
        return filter_difficulty;
    }

    /**
     * Get filter values for seekBar_funfactor progress
     * @return values from 0 to 4
     */
    public int getFilterFunfactor() {
        return filter_funfactor;
    }

    /**
     * Gets the display text for the range filter indicator
     * @return "All", "1", "5", "10", "15"
     */
    public String getFilterTextRange() {
        if (filter_range > 0)
            return String.format(res.getString(R.string.range), rangeIncrements[filter_range]);
        else
            return res.getString(R.string.unfiltered);
    }

    /**
     * Gets the display text for the road quality indicator
     * @return "All", "2 Stars", "3 Stars", "4 Stars", "5 Stars",
     */
    public String getFilterTextQuality() {
        if (filter_quality > 0)
            return String.format(res.getString(R.string.filter_rating),
                    Integer.toString(filter_quality+1));
        else
            return res.getString(R.string.unfiltered);
    }

    /**
     * Gets the display text for the difficulty indicator
     * @return "All", "2 Stars", "3 Stars", "4 Stars", "5 Stars",
     */
    public String getFilterTextDifficulty() {
        if (filter_difficulty > 0)
            return String.format(res.getString(R.string.filter_rating),
                    Integer.toString(filter_difficulty+1));
        else
            return res.getString(R.string.unfiltered);
    }

    /**
     * Gets the display text for the fun level indicator
     * @return "All", "2 Stars", "3 Stars", "4 Stars", "5 Stars",
     */
    public String getFilterTextFunfactor() {
        if (filter_funfactor > 0)
            return String.format(res.getString(R.string.filter_rating),
                    Integer.toString(filter_funfactor+1));
        else
            return res.getString(R.string.unfiltered);
    }
}
