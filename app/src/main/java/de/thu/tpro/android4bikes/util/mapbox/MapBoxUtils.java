package de.thu.tpro.android4bikes.util.mapbox;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.not;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapBoxUtils {

    public static HashMap<MapBoxSymbols, Drawable> generateMarkerPool(MainActivity parent){
        HashMap<MapBoxSymbols, Drawable> markerPool = new HashMap<>();
        markerPool.put(MapBoxSymbols.BIKERACK, parent.getDrawable(R.drawable.ic_material_bikerack_24dp));
        markerPool.put(MapBoxSymbols.HAZARDALERT_GENERAL, parent.getDrawable(R.drawable.ic_material_hazard));
        markerPool.put(MapBoxSymbols.TRACK, parent.getDrawable(R.drawable.ic_flag_green_24dp));
        markerPool.put(MapBoxSymbols.TRACK_FINISH, parent.getDrawable(R.drawable.flag_finish_green_24dp));
        return markerPool;
    }

    public static void initMarkerSymbols(MapboxMap mapboxMap, HashMap<MapBoxSymbols, Drawable> markers) {
        //Check if marker HashMap is empty
        if (!markers.isEmpty()) {
            //Register Symbols in Mapbox
            markers.forEach((type, icon) -> mapboxMap.getStyle().addImage(type.toString(), icon));
        }
    }

    public static SymbolOptions createMarker(double latitude, double longitude, MapBoxSymbols type) {
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(type.toString());
    }

    public static void showRouteWithCamera(com.mapbox.geojson.Point start, com.mapbox.geojson.Point end, MapboxMap mapboxMap) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(start.latitude(), start.longitude())) // Northeast
                .include(new LatLng(end.latitude(), end.longitude())) // Southwest
                .build();
        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
    }

    public static void addGeoJsonSource(@NonNull Style loadedMapStyle, FeatureCollection featureCollection, String ID, boolean withCluster) {
        //Check if Source is initialized
        GeoJsonSource mapStyleSource = loadedMapStyle.getSourceAs(ID);
        if (mapStyleSource != null) {
            mapStyleSource.setGeoJson(featureCollection);
        } else {
            if (withCluster) {
                if (ID.equals(GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString()) || ID.equals(GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString())) {
                    GeoJsonSource geoJsonSource = new GeoJsonSource(ID, featureCollection, new GeoJsonOptions()
                            .withCluster(true)
                            .withClusterRadius(50)
                            //.withClusterMaxZoom(10)
                            .withMinZoom(13)
                    );
                    loadedMapStyle.addSource(geoJsonSource);
                } else {
                    GeoJsonSource geoJsonSource = new GeoJsonSource(ID, featureCollection, new GeoJsonOptions()
                            .withCluster(true)
                            .withClusterRadius(50)
                            //.withClusterMaxZoom(10)
                            .withMaxZoom(13)
                            .withMinZoom(4)
                    );
                    loadedMapStyle.addSource(geoJsonSource);
                }
            } else {
                GeoJsonSource geoJsonSource = new GeoJsonSource(ID, featureCollection);
                loadedMapStyle.addSource(geoJsonSource);
            }

        }

    }

    public static void addGeoJsonSource(@NonNull Style loadedMapStyle, Geometry geometry, String ID) {
        //Check if Source is initialized
        GeoJsonSource mapStyleSource = loadedMapStyle.getSourceAs(ID);
        if (mapStyleSource != null) {
            mapStyleSource.setGeoJson(geometry);
        } else {
            GeoJsonSource geoJsonSource = new GeoJsonSource(ID, FeatureCollection.fromFeatures(new Feature[]{
                    Feature.fromGeometry(geometry)}));
            loadedMapStyle.addSource(geoJsonSource);
        }
    }

    public static void createRouteLayer(@NonNull Style loadedMapStyle, String ID) {
        LineLayer routeLayer = new LineLayer("Tracks_" + ID, ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(ContextCompat.getColor(GlobalContext.getContext(), R.color.Green800Primary))
        );
        loadedMapStyle.addLayer(routeLayer);

    }

    public static void createFinishLayer(@NonNull Style loadedMapStyle, String ID) {
        // Add the Finish SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer("Finish_" + ID, ID).withProperties(
                iconImage(MapBoxSymbols.TRACK_FINISH.toString()),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -9f})));
    }

    /**
     * Create unclustered SymbolLayer for small zoom level
     *
     * @param loadedMapStyle
     * @param sourceID
     */
    public static void createUnclusteredSymbolLayer(@NonNull Style loadedMapStyle, String sourceID, MapBoxSymbols type) {
        //Create Symbol Layer for unclustered data points
        SymbolLayer unclustered = new SymbolLayer("unclustered_" + sourceID, sourceID);

        unclustered.setProperties(
                iconImage(type.toString())
        );
        // Add a filter to the cluster layer that hides the circles based on "point_count"
        unclustered.setFilter(all(not(has("point_count"))));
        loadedMapStyle.addLayer(unclustered);
    }

    /**
     * Create clustered CircleLayer for bigger zoom level
     *
     * @param loadedMapStyle
     * @param sourceID
     */
    public static void createClusteredCircleOverlay(@NonNull Style loadedMapStyle, String sourceID, MapBoxSymbols type) {
        int color = R.color.mapbox_blue; //default
        switch (type) {
            case BIKERACK:
                color = R.color.Blue800Primary;
                break;
            case HAZARDALERT_GENERAL:
                color = R.color.Amber800Light;
                break;
            case TRACK:
                color = R.color.Green800Primary;
                break;
        }
        //Add clusters' circles
        CircleLayer circles = new CircleLayer("clustered_" + sourceID, sourceID);
        circles.setProperties(
                circleOpacity(0.6f),
                circleColor(ContextCompat.getColor(GlobalContext.getContext(), color)),
                circleRadius(15f)
        );

        Expression pointCount = toNumber(get("point_count"));

        // Add a filter to the cluster layer that hides the circles based on "point_count"
        circles.setFilter(
                all(has("point_count"),
                        gte(pointCount, literal(0)
                        )
                ));

        loadedMapStyle.addLayer(circles);

        //Add the count labels
        SymbolLayer count = new SymbolLayer("count_" + sourceID, sourceID);
        count.setProperties(
                textField(Expression.toString(get("point_count"))),
                textSize(12f),
                textColor(Color.WHITE),
                textIgnorePlacement(true),
                textAllowOverlap(false)
        );
        loadedMapStyle.addLayer(count);
    }

    /**
     * Create Markers from Track List
     *
     * @param loadedMapStyle
     * @param tracks
     */
    public synchronized static void updateTrackOverlay(@NonNull Style loadedMapStyle, List<Track> tracks) {
        List<Feature> list_feature = new ArrayList<>();
        //Generate Markers from ArrayList
        for (Track track : tracks) {
            //getStringProperty
            SymbolOptions marker = createMarker(track.getStartPosition().getLatitude(), track.getStartPosition().getLongitude(), MapBoxSymbols.TRACK);
            Feature feature = Feature.fromGeometry(marker.getGeometry());
            feature.addStringProperty("ID", track.getFirebaseID());
            //TODO add info for Popup
            list_feature.add(feature);
        }
        //Create FeatureCollection from Feature List
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
        //Create unclustered symbol layer
        addGeoJsonSource(loadedMapStyle, featureCollection, GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS.toString(), true);
    }

    /**
     * Create Markers from BikeRack List
     *
     * @param loadedMapStyle
     * @param bikeRacks
     */
    public synchronized static void updateBikeRackOverlay(@NonNull Style loadedMapStyle, List<BikeRack> bikeRacks) {
        List<Feature> list_feature = new ArrayList<>();
        //Generate Markers from ArrayList
        for (BikeRack bikeRack : bikeRacks) {
            SymbolOptions marker = createMarker(bikeRack.getPosition().getLatitude(), bikeRack.getPosition().getLongitude(), MapBoxSymbols.BIKERACK);
            Feature feature = Feature.fromGeometry(marker.getGeometry());
            feature.addStringProperty("ID", bikeRack.getFirebaseID());
            //TODO add info for Popup
            list_feature.add(feature);
        }
        //Create FeatureCollection from Feature List
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
        for (Feature singleFeature : featureCollection.features()) {
            singleFeature.addBooleanProperty("selected", false);
        }
        //Create unclustered symbol layer
        addGeoJsonSource(loadedMapStyle, featureCollection, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString(), true);
    }

    /**
     * Create markers from HazardAlert list
     *
     * @param loadedMapStyle
     * @param hazardAlerts
     */
    public synchronized static void updateHazardAlertOverlay(@NonNull Style loadedMapStyle, List<HazardAlert> hazardAlerts) {
        // Create Markers from data and set the 'cluster' option to true.
        MapBoxSymbols type = MapBoxSymbols.HAZARDALERT_GENERAL;
        List<Feature> list_feature = new ArrayList<>();
        //Generate Markers from ArrayList
        for (HazardAlert hazardAlert : hazardAlerts) {
            SymbolOptions marker = createMarker(hazardAlert.getPosition().getLatitude(), hazardAlert.getPosition().getLongitude(), type);
            Feature feature = Feature.fromGeometry(marker.getGeometry());
            feature.addStringProperty("ID", hazardAlert.getFirebaseID());
            //TODO add info for Popup
            list_feature.add(feature);
        }
        //Create FeatureCollection from Feature List
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
        for (Feature singleFeature : featureCollection.features()) {
            singleFeature.addBooleanProperty("selected", false);
        }
        addGeoJsonSource(loadedMapStyle, featureCollection, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString(), true);
    }

    public static boolean removeTrackFromMap(@NonNull Style loadedMapStyle, boolean routeLayer_created, ViewModelTrack vm_Tracks) {
        vm_Tracks.setNavigationTrack(null);
        loadedMapStyle.removeSource(GeoFencing.ConstantsGeoFencing.COLLECTION_ROUTE.toString());
        loadedMapStyle.removeSource(GeoFencing.ConstantsGeoFencing.FINISH_FLAG.toString());
        if (routeLayer_created) {
            loadedMapStyle.removeLayer("Tracks_" + GeoFencing.ConstantsGeoFencing.COLLECTION_ROUTE.toString());
            loadedMapStyle.removeLayer("Finish_" + GeoFencing.ConstantsGeoFencing.FINISH_FLAG.toString());
            routeLayer_created = false;
        }
        return routeLayer_created;
    }

    public static boolean addRouteToMap(@NonNull Style loadedMapStyle, Track track, boolean routeLayer_created) {
        if (!routeLayer_created) {
            createRouteLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_ROUTE.toString());
            createFinishLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.FINISH_FLAG.toString());
            routeLayer_created = true;
        }
        addGeoJsonSource(loadedMapStyle, LineString.fromPolyline(track.getRoute().geometry(), PRECISION_6),
                GeoFencing.ConstantsGeoFencing.COLLECTION_ROUTE.toString());
        addGeoJsonSource(loadedMapStyle, track.getEndPosition().getAsPoint(), GeoFencing.ConstantsGeoFencing.FINISH_FLAG.toString());
        return routeLayer_created;
    }

    public enum MapBoxSymbols {
        BIKERACK("BIKERACK"),
        HAZARDALERT_GENERAL("HAZARDALERT_GENERAL"),
        TRACK("TRACK"),
        TRACK_FINISH("TRACK_FINISH"),
        INFO("INFO");

        private String type;

        MapBoxSymbols(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
