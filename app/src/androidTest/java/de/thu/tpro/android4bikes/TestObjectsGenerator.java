package de.thu.tpro.android4bikes;

import android.content.Context;

import com.mapbox.api.directions.v5.models.DirectionsRoute;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class TestObjectsGenerator {
    /**
     * generates a new instance of the class BikeRack for test purposes
     *
     * @return instance of a bike rack
     */

    private static Random random;
    public static BikeRack generateTHUBikeRack() {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(new Position(48.408880, 9.997507), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }

    public static List<BikeRack> generateRandomBikeRackList() {
        List<BikeRack> list_random_bikeRacks = new ArrayList<>();


        for (int i = 0; i < 200; ++i) {
            BikeRack bikeRack = generateTHUBikeRack();
            bikeRack.setPosition(generateRandomPosition());
            list_random_bikeRacks.add(bikeRack);
        }

        for (int i = 0; i < 200; ++i) {
            BikeRack bikeRack = generateTHUBikeRack();
            bikeRack.setPosition(generateDeterministicRandomPositions(i, bikeRack.getPosition().getLongitude(), bikeRack.getPosition().getLatitude()));
            list_random_bikeRacks.add(bikeRack);
        }

        return list_random_bikeRacks;
    }

    public static Position generateRandomPosition() {
        if (random == null) {
            random = new Random();
        }
        //compare: https://stackoverflow.com/questions/32735998/generating-a-random-lat-long-with-bias-away-from-poles
        double u = random.nextDouble();
        double v = random.nextDouble();

        double latitude = Math.toDegrees(Math.acos(u * 2 - 1)) - 90;
        double longitude = 360 * v - 180;
        return new Position(latitude, longitude);
    }

    public static Position generateDeterministicRandomPositions(int i, double lng, double lat) {
        return new Position(lng + (i / 100.0), lat);
    }

    /**
     * generates a new instance of the class HazardAlert for test purposes
     *
     * @return instance of a hazard alert
     */
    public static HazardAlert generateHazardAlert() {
        HazardAlert hazardAlert_thu = new HazardAlert(
                HazardAlert.HazardType.GENERAL, new Position(48.408880, 9.997507), 5, true
        );
        return hazardAlert_thu;
    }

    /**
     * generates a new instance of the class Track for test purposes
     *
     * @return instance of a track
     */
    public static Track generateTrack() {
        List<Position> positions = new ArrayList<>();
        DirectionsRoute dr = DirectionsRoute.fromJson(getJsonFromAssets(GlobalContext.getContext(),"testDirections.json"));
        positions.add(new Position(48.408880, 9.997507,1587652587));
        positions.add(new Position(48.408980, 9.997807,1587652597));
        Track track = new Track("nullacht15", new Rating(), "Heimweg", "Das ist meine super tolle Strecke", 1585773516, 25,
                dr, new ArrayList<>(), positions.get(0),positions.get(1),true);
        return track;
    }

    private static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static Track generateDifferentTrack(String name) {
        Track track = generateTrack();
        track.setDistance_km(100);
        track.setName(name);
        track.setDescription("Das ist schön. Das ist wunderschön!");
        return track;
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     */
    public static Profile createProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        return new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
    }

    /**
     * generates a new different instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     */
    public static Profile createDifferentProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        return new Profile("Kostas", "Kostidis", "00x15dxxx", 666, 1000, achievements);
    }

    public static Map<Track, Profile> initialize_map_track_profile() {
        List<Track> list_track = initTracklistDummy();

        Map<Track, Profile> map_track_profile = new HashMap<>();

        list_track.forEach(entry -> {
            map_track_profile.put(entry, createAnonProfile());
        });
        return map_track_profile;
    }

    public static Profile createAnonProfile() {
        List<Achievement> list = new ArrayList<>();
        return new Profile("Android", "Biker", "-1", 0x2e8b57, 0, list);
    }

    public static List<Track> initTracklistDummy() {
        List<Track> list_tracks = new ArrayList<Track>();

        //test liste TODO: Backend anbinden
        list_tracks = Arrays.asList(new Track(), new Track(), new Track());

        list_tracks.get(0).setRating(new Rating(1, 1, 1));
        list_tracks.get(1).setRating(new Rating(3, 3, 3));
        list_tracks.get(2).setRating(new Rating(5, 5, 5));

        list_tracks.get(0).setName("Mega Harte Tour");
        list_tracks.get(1).setName("Mega Harte Tour 2: Electric Boogaloo");
        list_tracks.get(2).setName("Mega Harte Tour 3: Götterdämmerung");

        list_tracks.get(0).setDistance_km(15);
        list_tracks.get(1).setDistance_km(30);
        list_tracks.get(2).setDistance_km(7);

        list_tracks.get(0).setStartPosition(new Position(40,9));
        list_tracks.get(1).setStartPosition(new Position(45,8));
        list_tracks.get(2).setStartPosition(new Position(48,7));

        list_tracks.get(0).setDescription("Mega Harte Tour, nur für Mega Harte");
        list_tracks.get(1).setDescription("Fahrradhelm muss dabei sein, ist wirklich hart, die Tour");
        list_tracks.get(2).setDescription("Schreibe lieber noch dein Testament bevor du diese Mega Harte Tour antrittst");

        return list_tracks;
    }
}
