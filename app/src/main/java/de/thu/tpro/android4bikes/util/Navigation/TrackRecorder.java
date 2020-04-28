package de.thu.tpro.android4bikes.util.Navigation;

import android.content.Context;
import android.util.Log;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.MapmatchingRequest;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.positiontest.PositionProvider;
import de.thu.tpro.android4bikes.util.GlobalContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackRecorder {
    Context context;
    DirectionsRoute finalroute;
    List<MapmatchingRequest> mapmatchingRequests = new ArrayList<>();
    private List<com.mapbox.geojson.Point> positionsRoute1 = PositionProvider.getMapsTrack1();
    private List<com.mapbox.geojson.Point> positionsRoute2 = PositionProvider.getMapsTrack2();
    private List<com.mapbox.geojson.Point> positionsRoute3 = PositionProvider.getMapsTrack3();
    private com.mapbox.geojson.Point start;
    private com.mapbox.geojson.Point end;
    private Track finalTrack;

    public TrackRecorder() {
        context = GlobalContext.getContext();
    }

    public void start() {
        init();

        MapmatchingRequest request1 = new MapmatchingRequest(positionsRoute1);
        MapmatchingRequest request2 = new MapmatchingRequest(positionsRoute2);
        MapmatchingRequest request3 = new MapmatchingRequest(positionsRoute3);

        mapmatchingRequests.add(request1);
        mapmatchingRequests.add(request2);
        mapmatchingRequests.add(request3);
    }

    public void stop(Track track_without_route) {
        finalTrack = track_without_route;
        generateDirectionRouteAndSaveTrackToFirebase(mapmatchingRequests);
        finalroute = null;
    }

    private void init() {
        mapmatchingRequests = null;
        finalroute = null;
        start = null;
        end = null;
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
                                    start = requests.get(0).getPoints().get(0);
                                    List<com.mapbox.geojson.Point> lastlist = requests.get(requests.size() - 1).getPoints();
                                    end = lastlist.get(lastlist.size() - 1);
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

                                    //TODO add direction info
                                    //finaltrack.setdir;

                                    //Save Track to Firebase
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
}