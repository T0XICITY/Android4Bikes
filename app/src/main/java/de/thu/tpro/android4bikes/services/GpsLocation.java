package de.thu.tpro.android4bikes.services;

import android.location.Location;

public class GpsLocation extends Location {
    private static float totalDistance;

    public GpsLocation(Location location) {
        super(location);
    }

    @Override
    public float getSpeed() {
        float speed = super.getSpeed() * 3.6f;
        return speed;

    }

    @Override
    public float distanceTo(Location loc) {
        float distance = super.distanceTo(loc);
        totalDistance += distance;
        return distance;
    }
}
