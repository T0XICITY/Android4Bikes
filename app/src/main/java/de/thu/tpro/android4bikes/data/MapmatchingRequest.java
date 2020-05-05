package de.thu.tpro.android4bikes.data;

import com.mapbox.geojson.Point;

import java.util.List;

public class MapmatchingRequest {
    List<Point> points;

    public MapmatchingRequest(List<Point> points_99MAX) {
        this.points = points_99MAX;
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "MapmatchingRequest{" +
                "points=" + points.toString() +
                '}';
    }
}
