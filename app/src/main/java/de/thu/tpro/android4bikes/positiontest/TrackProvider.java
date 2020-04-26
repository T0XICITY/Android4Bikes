package de.thu.tpro.android4bikes.positiontest;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.TimeBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackProvider {
    public static Track getDummyTrack(){
        Rating rating = new Rating(2, 3, 1, "neunzehnhundert99");
        List<Position> fineGrainedPositions = PositionProvider.getDummyPosition();
        List<HazardAlert> hazardAlerts = new ArrayList<>();
        hazardAlerts.add(new HazardAlert(HazardAlert.HazardType.DAMAGED_ROAD, new Position(48.310792, 9.860655),
                1609459200, 1, "zweitausend20", true));
        return new Track("nullacht15",rating,"Meine Teststrecke","Das ist meine super tolle Teststrecke :)",
                TimeBase.getCurrentUnixTimeStamp(),2,null,hazardAlerts,fineGrainedPositions.get(0),fineGrainedPositions.get(fineGrainedPositions.size()-1),true);
    }
}
