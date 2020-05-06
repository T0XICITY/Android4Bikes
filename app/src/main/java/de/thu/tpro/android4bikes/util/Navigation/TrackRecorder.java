package de.thu.tpro.android4bikes.util.Navigation;

import android.content.Context;
import android.util.Log;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.MapmatchingRequest;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.positiontest.PositionProvider;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackRecorder implements Observer {
    Context context;
    DirectionsRoute finalroute;
    List<MapmatchingRequest> list_mapmatchingRequests = new ArrayList<>();
    private List<com.mapbox.geojson.Point> positionsRoute1 = PositionProvider.getMapsTrack1();
    private List<com.mapbox.geojson.Point> positionsRoute2 = PositionProvider.getMapsTrack2();
    private List<com.mapbox.geojson.Point> positionsRoute3 = PositionProvider.getMapsTrack3();
    private Track finalTrack;
    private Observable observable_position;

    public TrackRecorder() {
        context = GlobalContext.getContext();
    }

    public void start(MainActivity mainActivity) {
        init();
        //Generate Point at Location every 5sec
        observable_position = PositionTracker.LocationChangeListeningActivityLocationCallback.getInstance(mainActivity);
        observable_position.addObserver(this);
    }

    public void stop() {
        observable_position.deleteObserver(this);
    }

    public void save(String author, Rating rating, String name, String description) {
        finalTrack = new Track(author, rating, name, description, 0, null, null, null, null, true);
        generateDirectionRouteAndSaveTrackToFirebase(list_mapmatchingRequests);
        finalroute = null;
    }

    private void init() {
        list_mapmatchingRequests = new ArrayList<>();
        list_mapmatchingRequests.add(new MapmatchingRequest(new ArrayList<>()));
        finalroute = null;
    }

    private void generateDirectionRouteAndSaveTrackToFirebase(List<MapmatchingRequest> mapmatchingRequests) {
        List<MapmatchingRequest> requests = mapmatchingRequests;
        MapboxMapMatching.builder()
                .accessToken(context.getString(R.string.access_token))
                .coordinates(requests.get(0).getPoints())
                .steps(true)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .tidy(true)
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                .waypointIndices(0, requests.get(0).getPoints().size() - 1)
                .build()
                .enqueueCall(new Callback<MapMatchingResponse>() {

                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {

                        if (response.isSuccessful()) {
                            Log.d("HELLO", "SIZE: " + response.body().matchings().size());
                            if (response.body().matchings().size() > 0) {
                                DirectionsRoute route_new = response.body().matchings().get(0).toDirectionRoute();


                                if (finalroute != null) {
                                    //Append of Route
                                    finalroute = DirectionRouteHelper.appendRoute(finalroute, route_new);
                                } else {
                                    //First Init of Route
                                    List<com.mapbox.geojson.Point> lastlist = requests.get(requests.size() - 1).getPoints();
                                    finalroute = route_new;
                                }
                                //Slice first index
                                requests.remove(0);

                                //Terminate Condition
                                if (requests.size() > 0) {
                                    //Continue with Track Appending
                                    generateDirectionRouteAndSaveTrackToFirebase(requests);
                                } else {
                                    //Finished with Track Appending
                                    //Save Track to Firebase
                                    finalTrack.setDistance_km(finalroute.distance() / 1000.0);
                                    finalTrack.setRoute(finalroute);
                                    finalTrack.setStartPosition(new Position(finalroute.legs().get(0).steps().get(0).maneuver().location().latitude(), finalroute.legs().get(0).steps().get(0).maneuver().location().longitude()));
                                    finalTrack.setEndPosition(new Position(finalroute.legs().get(0).steps().get(finalroute.legs().get(0).steps().size() - 1).maneuver().location().latitude(), finalroute.legs().get(0).steps().get(finalroute.legs().get(0).steps().size() - 1).maneuver().location().longitude()));
                                    //TODO set Distance and remove from Constuctor
                                    CouchWriteBuffer.getInstance().storeTrack(finalTrack);
                                }
                            }

                        } else {
                            Log.d("HELLO", "Response empty");

                        }
                    }

                    @Override
                    public void onFailure(Call<MapMatchingResponse> call, Throwable throwable) {

                    }
                });
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof PositionTracker.LocationChangeListeningActivityLocationCallback) {
            if (o instanceof Map && !((Map) o).keySet().isEmpty()) {
                Map map = (Map) o;
                if (map.get(PositionTracker.CONSTANTS.POSITION.toText()) != null) {
                    Position last_position = (Position) map.get(PositionTracker.CONSTANTS.POSITION.toText());
                    if (last_position.isValid()) {
                        addPositionToMapMatchingRequest(last_position);
                    }
                }
            }
        }
    }

    private void addPositionToMapMatchingRequest(Position position) {
        if (!list_mapmatchingRequests.isEmpty()) {
            MapmatchingRequest mapmatchingRequest = list_mapmatchingRequests.get(list_mapmatchingRequests.size() - 1);
            if (mapmatchingRequest.getPoints().size() >= 99) {
                list_mapmatchingRequests.add(new MapmatchingRequest(new ArrayList<>()));
                mapmatchingRequest = list_mapmatchingRequests.get(list_mapmatchingRequests.size() - 1);
            }
            mapmatchingRequest.getPoints().add(position.getAsPoint());

        }
        list_mapmatchingRequests.forEach(mapmatchingRequest1 -> Log.d("HalloWelTrack", mapmatchingRequest1.toString()));
    }
}
