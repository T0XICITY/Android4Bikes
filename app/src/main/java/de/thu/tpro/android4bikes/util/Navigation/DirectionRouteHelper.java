package de.thu.tpro.android4bikes.util.Navigation;

import android.util.Log;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.utils.PolylineUtils;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;

public class DirectionRouteHelper {
    public static DirectionsRoute appendRoute(DirectionsRoute route1, DirectionsRoute route2) {
        DirectionsRoute finalRoute = DirectionsRoute.builder()
                .distance(route1.distance() + route2.distance())
                .duration(route1.duration() + route2.duration())
                .geometry(mergeGeometry(route1.geometry(), route2.geometry()))
                .weightName(route1.weightName())
                .legs(mergeLegs(route1.legs(), route2.legs()))
                .routeOptions(mergeRouteOptions(route1.routeOptions(), route2.routeOptions()))
                .voiceLanguage(route1.voiceLanguage())
                .build();
        return finalRoute;
    }

    private static List<RouteLeg> mergeLegs(List<RouteLeg> routeLegs1, List<RouteLeg> routeLegs2) {
        List<RouteLeg> finalRouteLegs = new ArrayList<>();
        //for (int i = 0; i < (routeLegs1.size()+routeLegs2.size()); i++) {
        RouteLeg routeLeg = RouteLeg.builder()
                .distance(routeLegs1.get(0).distance() + routeLegs2.get(0).distance())
                .duration(routeLegs1.get(0).duration() + routeLegs2.get(0).duration())
                .summary(routeLegs1.get(0).summary() + routeLegs2.get(0).summary())
                .steps(mergeLegSteps(routeLegs1.get(0).steps(), routeLegs2.get(0).steps()))
                .build();
        finalRouteLegs.add(routeLeg);
        //}

        return finalRouteLegs;
    }

    private static String mergeGeometry(String geometry1, String geometry2) {
        List<com.mapbox.geojson.Point> finalRoutePoints = new ArrayList<>();
        //Decode Geometry to List of Points and merge
        finalRoutePoints.addAll(PolylineUtils.decode(geometry1, 6));
        finalRoutePoints.addAll(PolylineUtils.decode(geometry2, 6));

        //Endcode List of Points back to Geometry
        String geometry = PolylineUtils.encode(finalRoutePoints, 6);
        return geometry;
    }

    private static List<com.mapbox.geojson.Point> mergeCoordinates(List<com.mapbox.geojson.Point> coordinates1, List<com.mapbox.geojson.Point> coordinates2) {
        List<com.mapbox.geojson.Point> finalCoordinates = new ArrayList<>();
        finalCoordinates.addAll(coordinates1);
        finalCoordinates.addAll(coordinates2);
        return finalCoordinates;
    }

    private static List<LegStep> mergeLegSteps(List<LegStep> legSteps1, List<LegStep> legSteps2) {
        List<LegStep> finalLegSteps = new ArrayList<>();
        finalLegSteps.addAll(legSteps1);
        finalLegSteps.addAll(legSteps2);
        return finalLegSteps;
    }

    private static RouteOptions mergeRouteOptions(RouteOptions routeOptions1, RouteOptions routeOptions2) {
        Log.d("HELLO", "Waypoints: " + calculateWaypointindices(routeOptions1.waypointIndices(), routeOptions2.waypointIndices()));
        RouteOptions routeOptions = RouteOptions.builder()
                .baseUrl(routeOptions1.baseUrl())
                .user(routeOptions1.user())
                .profile(routeOptions1.profile())
                .coordinates(mergeCoordinates(routeOptions1.coordinates(), routeOptions2.coordinates()))
                .language(routeOptions1.language())
                .geometries(routeOptions1.geometries())
                .steps(routeOptions1.steps())
                .voiceInstructions(routeOptions1.voiceInstructions())
                .bannerInstructions(routeOptions1.bannerInstructions())
                .voiceUnits(routeOptions1.voiceUnits())
                .accessToken(routeOptions1.accessToken())
                .requestUuid(routeOptions1.requestUuid())
                //.waypointIndices("0;8")
                .waypointIndices(calculateWaypointindices(routeOptions1.waypointIndices(), routeOptions2.waypointIndices()))
                .build();

        return routeOptions;
    }

    private static String calculateWaypointindices(String waypointindices1, String waypointindices2) {
        int start = 0;
        int maxIndiceRoute1 = Integer.parseInt(waypointindices1.substring(waypointindices1.length() - 1));
        int maxIndiceRoute2 = Integer.parseInt(waypointindices2.substring(waypointindices2.length() - 1));
        int end = maxIndiceRoute1 + maxIndiceRoute2;
        return start + ";" + (end + 1);
    }

    public List<com.mapbox.geojson.Point> convertPositionListToPointList(List<Position> finegrainedPositions) {
        List<com.mapbox.geojson.Point> points = new ArrayList<>();
        finegrainedPositions.forEach(position -> points.add(com.mapbox.geojson.Point.fromLngLat(position.getLongitude(), position.getLatitude())));
        return points;
    }

    public List<Position> convertPointListToPositionList(List<com.mapbox.geojson.Point> finegrainedPositions) {
        List<Position> points = new ArrayList<>();
        finegrainedPositions.forEach(position -> points.add(new Position(position.latitude(), position.longitude())));
        return points;
    }
}
