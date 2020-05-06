package de.thu.tpro.android4bikes.view.menu.trackList;

import android.util.Log;

import de.thu.tpro.android4bikes.data.model.Track;

public class TrackDistanceTuple implements Comparable<TrackDistanceTuple> {

    private static final String LOG_TAG = "TrackDistanceTuple";

    private Track track;
    private double distanceToUser;
    private SortBy sortBy;
    private boolean sortOrderAscending;

    public TrackDistanceTuple(Track track) {
        this.track = track;
        this.distanceToUser = 0d;
        this.sortBy = SortBy.RANGE; // default: sort by distance to user
        this.sortOrderAscending = true; // default: ascending
    }

    public TrackDistanceTuple(Track track, double distanceToUser) {
        this.track = track;
        this.distanceToUser = distanceToUser;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortOrderAscending(boolean sortOrderAscending) {
        this.sortOrderAscending = sortOrderAscending;
    }

    @Override
    public int compareTo(TrackDistanceTuple other) {
        int result = 0;
        switch (sortBy) {
            case RANGE:
                //Log.d(LOG_TAG, "Comparing by Range");
                result = Double.compare(distanceToUser, other.distanceToUser);
                break;
            case QUALITY:
                //Log.d(LOG_TAG, "Comparing by Quality");
                result = Integer.compare(getTrack().getRating().getRoadquality(),
                        other.getTrack().getRating().getRoadquality());
                break;
            case DIFFICULTY:
                //Log.d(LOG_TAG, "Comparing by Difficulty");
                result = Integer.compare(getTrack().getRating().getDifficulty(),
                        other.getTrack().getRating().getDifficulty());
                break;
            case FUNFACTOR:
                //Log.d(LOG_TAG, "Comparing by Funfactor");
                result = Integer.compare(getTrack().getRating().getFun(),
                        other.getTrack().getRating().getFun());
                break;
            default:
                break;
        }
        //Log.d(LOG_TAG, String.format("Sort tracks %s", sortOrderAscending? "ascending" : "descending"));
        if (sortOrderAscending) {
            // sort ascending
            return result;
        } else {
            // sort descending
            return result * (-1);
        }
    }

    public enum SortBy {
        RANGE,
        QUALITY,
        DIFFICULTY,
        FUNFACTOR
    }
}
