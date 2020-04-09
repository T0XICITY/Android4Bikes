package de.thu.tpro.android4bikes.positiontest;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.TimeBase;

public class TrackProvider {
    public static Track getDummyTrack(){
        Rating rating = new Rating(2, 3, 1, "neunzehnhundert99");
        List<Position> fineGrainedPositions = PositionProvider.getDummyPosition();
        List<HazardAlert> hazardAlerts = new ArrayList<>();
        hazardAlerts.add(new HazardAlert(HazardAlert.HazardType.DAMAGED_ROAD, new Position(9.860655,48.310792),
                1609459200, 1, "zweitausend20"));
        return new Track("nullacht15",rating,"Meine Teststrecke","Das ist meine super tolle Teststrecke :)","siebenundvierzig11",
                TimeBase.getCurrentUnixTimeStamp(),2,fineGrainedPositions,hazardAlerts,true);
    }
}
