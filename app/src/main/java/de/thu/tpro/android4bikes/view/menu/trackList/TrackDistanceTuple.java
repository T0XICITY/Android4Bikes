package de.thu.tpro.android4bikes.view.menu.trackList;

import de.thu.tpro.android4bikes.data.model.Track;

public class TrackDistanceTuple implements Comparable<TrackDistanceTuple> {

    private Track track;
    private double distanceToUser;

    public TrackDistanceTuple(Track track) {
        this.track = track;
        this.distanceToUser = 0d;
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

    /**
     * Compares another TrackDistanceTuple to the current one by comparing the distances to the user
     * @param other
     * @return 0 when equal, 1 when other has lesser distance, -1 when other has bigger distance
     */
    @Override
    public int compareTo(TrackDistanceTuple other) {
        if (distanceToUser > other.distanceToUser)
            return 1;
        if (distanceToUser < other.distanceToUser)
            return -1;
        return 0;
    }
}
