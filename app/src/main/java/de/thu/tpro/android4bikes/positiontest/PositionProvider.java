package de.thu.tpro.android4bikes.positiontest;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.model.Position;

public class PositionProvider {

    public static Map<String, GeoPoint> get50kmRadiusPositionstest() {
        /**
         * Points in 50km radius
         */
        HashMap<String, GeoPoint> geoPositions_radius_50km = new HashMap<>();
        geoPositions_radius_50km.put("1km", new GeoPoint(48.409141, 9.978735));
        geoPositions_radius_50km.put("2km", new GeoPoint(48.408834, 9.965260));
        geoPositions_radius_50km.put("3km", new GeoPoint(48.408724, 9.951749));
        geoPositions_radius_50km.put("4km", new GeoPoint(48.408427, 9.938229));
        geoPositions_radius_50km.put("5km", new GeoPoint(48.408113, 9.924512));
        geoPositions_radius_50km.put("6km", new GeoPoint(48.407987, 9.911078));
        geoPositions_radius_50km.put("7km", new GeoPoint(48.407673, 9.897550));
        geoPositions_radius_50km.put("8km", new GeoPoint(48.407422, 9.883928));
        geoPositions_radius_50km.put("9km", new GeoPoint(48.407234, 9.870305));
        geoPositions_radius_50km.put("10km", new GeoPoint(48.407297, 9.856967));
        geoPositions_radius_50km.put("15km", new GeoPoint(48.406388, 9.789102));
        geoPositions_radius_50km.put("20km", new GeoPoint(48.405339, 9.721493));
        geoPositions_radius_50km.put("30km", new GeoPoint(48.403406, 9.585960));
        geoPositions_radius_50km.put("50km", new GeoPoint(48.401590, 9.314984));
        return geoPositions_radius_50km;
    }

    public static List<Position> getDummyPosition(){
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(48.30141, 9.842189));
        positions.add(new Position(48.301404, 9.842255));
        positions.add(new Position(48.301397, 9.842298));
        positions.add(new Position(48.301395, 9.842322));
        positions.add(new Position(48.301392, 9.842334));
        positions.add(new Position(48.30139, 9.842338));
        positions.add(new Position(48.30139, 9.842339));
        positions.add(new Position(48.30139, 9.84234));
        positions.add(new Position(48.301391, 9.84234));
        positions.add(new Position(48.301391, 9.84234));
        positions.add(new Position(48.301391, 9.84234));
        positions.add(new Position(48.301392, 9.84234));
        positions.add(new Position(48.301392, 9.842339));
        positions.add(new Position(48.301391, 9.842339));
        positions.add(new Position(48.301391, 9.842339));
        positions.add(new Position(48.30139, 9.842339));
        positions.add(new Position(48.30139, 9.842339));
        positions.add(new Position(48.301389, 9.84234));
        positions.add(new Position(48.301389, 9.84234));
        positions.add(new Position(48.301389, 9.84234));
        positions.add(new Position(48.301388, 9.842341));
        positions.add(new Position(48.301388, 9.842341));
        positions.add(new Position(48.301388, 9.842341));
        positions.add(new Position(48.301387, 9.842341));
        positions.add(new Position(48.301387, 9.842342));
        positions.add(new Position(48.301386, 9.842343));
        positions.add(new Position(48.301386, 9.842344));
        positions.add(new Position(48.301386, 9.842345));
        positions.add(new Position(48.301386, 9.842346));
        positions.add(new Position(48.301386, 9.842347));
        positions.add(new Position(48.301385, 9.842347));
        positions.add(new Position(48.301385, 9.842348));
        positions.add(new Position(48.301385, 9.842348));
        positions.add(new Position(48.301385, 9.842348));
        positions.add(new Position(48.301386, 9.842349));
        positions.add(new Position(48.301386, 9.842348));
        positions.add(new Position(48.301386, 9.842348));
        positions.add(new Position(48.301386, 9.842348));
        positions.add(new Position(48.301386, 9.842347));
        positions.add(new Position(48.301386, 9.842348));
        positions.add(new Position(48.301387, 9.842348));
        positions.add(new Position(48.301387, 9.842348));
        positions.add(new Position(48.301388, 9.842348));
        positions.add(new Position(48.301388, 9.842348));
        positions.add(new Position(48.301388, 9.842348));
        positions.add(new Position(48.301388, 9.842347));
        positions.add(new Position(48.301387, 9.842346));
        positions.add(new Position(48.301387, 9.842346));
        positions.add(new Position(48.301387, 9.842346));
        positions.add(new Position(48.301387, 9.842346));
        positions.add(new Position(48.301387, 9.842346));
        positions.add(new Position(48.301388, 9.842346));
        positions.add(new Position(48.301388, 9.842346));
        positions.add(new Position(48.301388, 9.842346));
        positions.add(new Position(48.301388, 9.842346));
        positions.add(new Position(48.301387, 9.842352));
        positions.add(new Position(48.301379, 9.842378));
        positions.add(new Position(48.301359, 9.842423));
        positions.add(new Position(48.301335, 9.842485));
        positions.add(new Position(48.301318, 9.84256));
        positions.add(new Position(48.301317, 9.842656));
        positions.add(new Position(48.301336, 9.842775));
        positions.add(new Position(48.301372, 9.842905));
        positions.add(new Position(48.301419, 9.843039));
        positions.add(new Position(48.301474, 9.843186));
        positions.add(new Position(48.301537, 9.843344));
        positions.add(new Position(48.301606, 9.843519));
        positions.add(new Position(48.30168, 9.843704));
        positions.add(new Position(48.301756, 9.843892));
        positions.add(new Position(48.301838, 9.844088));
        positions.add(new Position(48.301925, 9.844294));
        positions.add(new Position(48.302019, 9.844508));
        positions.add(new Position(48.302121, 9.844731));
        positions.add(new Position(48.302228, 9.844962));
        positions.add(new Position(48.302343, 9.8452));
        positions.add(new Position(48.302461, 9.845442));
        positions.add(new Position(48.302581, 9.845679));
        positions.add(new Position(48.302704, 9.845916));
        positions.add(new Position(48.302833, 9.846155));
        positions.add(new Position(48.302968, 9.846398));
        positions.add(new Position(48.303112, 9.846643));
        positions.add(new Position(48.303261, 9.846891));
        positions.add(new Position(48.303415, 9.847143));
        positions.add(new Position(48.303573, 9.847401));
        positions.add(new Position(48.303732, 9.847663));
        positions.add(new Position(48.303891, 9.847926));
        positions.add(new Position(48.30405, 9.848193));
        positions.add(new Position(48.304207, 9.848464));
        positions.add(new Position(48.304362, 9.848739));
        positions.add(new Position(48.304516, 9.849019));
        positions.add(new Position(48.30467, 9.849303));
        positions.add(new Position(48.304821, 9.849591));
        positions.add(new Position(48.304972, 9.849881));
        positions.add(new Position(48.305119, 9.850171));
        positions.add(new Position(48.305265, 9.850461));
        positions.add(new Position(48.30541, 9.85075));
        positions.add(new Position(48.305553, 9.851038));
        positions.add(new Position(48.305695, 9.851324));
        positions.add(new Position(48.305836, 9.85161));
        positions.add(new Position(48.305977, 9.851893));
        positions.add(new Position(48.306118, 9.852176));
        positions.add(new Position(48.306258, 9.852457));
        positions.add(new Position(48.306397, 9.852736));
        positions.add(new Position(48.306536, 9.853014));
        positions.add(new Position(48.306674, 9.853293));
        positions.add(new Position(48.306812, 9.853569));
        positions.add(new Position(48.306949, 9.853846));
        positions.add(new Position(48.307086, 9.854122));
        positions.add(new Position(48.307222, 9.854398));
        positions.add(new Position(48.307358, 9.854674));
        positions.add(new Position(48.307493, 9.854951));
        positions.add(new Position(48.307625, 9.85523));
        positions.add(new Position(48.307754, 9.855512));
        positions.add(new Position(48.307881, 9.855795));
        positions.add(new Position(48.308007, 9.856082));
        positions.add(new Position(48.308131, 9.856371));
        positions.add(new Position(48.308251, 9.856661));
        positions.add(new Position(48.308372, 9.856953));
        positions.add(new Position(48.308493, 9.857245));
        positions.add(new Position(48.308613, 9.857537));
        positions.add(new Position(48.308735, 9.857829));
        positions.add(new Position(48.308862, 9.858115));
        positions.add(new Position(48.308996, 9.858393));
        positions.add(new Position(48.30914, 9.858661));
        positions.add(new Position(48.30914, 9.858661));
        positions.add(new Position(48.309295, 9.858917));
        positions.add(new Position(48.309455, 9.85916));
        positions.add(new Position(48.30962, 9.859389));
        positions.add(new Position(48.30979, 9.859601));
        positions.add(new Position(48.309962, 9.8598));
        positions.add(new Position(48.310134, 9.859987));
        positions.add(new Position(48.310305, 9.860166));
        positions.add(new Position(48.310472, 9.860335));
        positions.add(new Position(48.310635, 9.860498));
        positions.add(new Position(48.310792, 9.860655));
        positions.add(new Position(48.310943, 9.860805));
        positions.add(new Position(48.311084, 9.860945));
        positions.add(new Position(48.311215, 9.861073));
        positions.add(new Position(48.311336, 9.861189));
        positions.add(new Position(48.311445, 9.861293));
        positions.add(new Position(48.311545, 9.861388));
        positions.add(new Position(48.311637, 9.861476));
        positions.add(new Position(48.311722, 9.861555));
        positions.add(new Position(48.311797, 9.861628));
        positions.add(new Position(48.311867, 9.861695));
        positions.add(new Position(48.311935, 9.861759));
        positions.add(new Position(48.312003, 9.861822));
        positions.add(new Position(48.312072, 9.861888));
        positions.add(new Position(48.312144, 9.861954));
        positions.add(new Position(48.312215, 9.862021));
        positions.add(new Position(48.312288, 9.86209));
        positions.add(new Position(48.312359, 9.862164));
        positions.add(new Position(48.31243, 9.862244));
        positions.add(new Position(48.312503, 9.862333));
        positions.add(new Position(48.312581, 9.86243));
        positions.add(new Position(48.31266, 9.862531));
        positions.add(new Position(48.312736, 9.862636));
        positions.add(new Position(48.31281, 9.862737));
        positions.add(new Position(48.312885, 9.862837));
        positions.add(new Position(48.312958, 9.862936));
        positions.add(new Position(48.313028, 9.863033));
        positions.add(new Position(48.313092, 9.863124));
        positions.add(new Position(48.313154, 9.863212));
        positions.add(new Position(48.313218, 9.863303));
        positions.add(new Position(48.313286, 9.863399));
        positions.add(new Position(48.31336, 9.863503));
        positions.add(new Position(48.313436, 9.863611));
        positions.add(new Position(48.313514, 9.863722));
        positions.add(new Position(48.313599, 9.863841));
        positions.add(new Position(48.30141, 9.842189));
        positions.add(new Position(48.301387, 9.842348));
        positions.add(new Position(48.313687, 9.863973));
        positions.add(new Position(48.313778, 9.864114));
        positions.add(new Position(48.313874, 9.864266));
        positions.add(new Position(48.313966, 9.864425));
        positions.add(new Position(48.314055, 9.864585));
        positions.add(new Position(48.314142, 9.864751));
        positions.add(new Position(48.314227, 9.864927));
        positions.add(new Position(48.314311, 9.865112));
        positions.add(new Position(48.314394, 9.865305));
        positions.add(new Position(48.314471, 9.865506));
        return positions;
    }
}
