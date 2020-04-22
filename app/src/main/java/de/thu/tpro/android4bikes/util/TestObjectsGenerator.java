package de.thu.tpro.android4bikes.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;

public class TestObjectsGenerator {
    /**
     * generates a new instance of the class BikeRack for test purposes
     *
     * @return instance of a bike rack
     */
    public static BikeRack generateTHUBikeRack() {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(48.408880, 9.997507), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }

    /**
     * generates a new instance of the class HazardAlert for test purposes
     *
     * @return instance of a hazard alert
     */
    public static HazardAlert generateHazardAlert() {
        HazardAlert hazardAlert_thu = new HazardAlert(
                HazardAlert.HazardType.GENERAL, new Position(48.408880, 9.997507), 120000, 5, true
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
        positions.add(new Position(48.408880, 9.997507));
        Track track = new Track("nullacht15", new Rating(), "Heimweg", "Das ist meine super tolle Strecke", 1585773516, 25,
                positions, new ArrayList<>(), true);
        return track;
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

        list_tracks.get(0).setRating(new Rating(1, 1, 1, null));
        list_tracks.get(1).setRating(new Rating(3, 3, 3, null));
        list_tracks.get(2).setRating(new Rating(5, 5, 5, null));

        list_tracks.get(0).setName("Mega Harte Tour");
        list_tracks.get(1).setName("Mega Harte Tour 2: Electric Boogaloo");
        list_tracks.get(2).setName("Mega Harte Tour 3: Götterdämmerung");

        list_tracks.get(0).setDistance_km(15);
        list_tracks.get(1).setDistance_km(30);
        list_tracks.get(2).setDistance_km(7);

        list_tracks.get(0).setFineGrainedPositions(Arrays.asList(new Position(48.4049, 9.9949)));
        list_tracks.get(1).setFineGrainedPositions(Arrays.asList(new Position(48.1773, 9.9730)));
        list_tracks.get(2).setFineGrainedPositions(Arrays.asList(new Position(48.3909, 10.0015)));

        list_tracks.get(0).setDescription("Mega Harte Tour, nur für Mega Harte");
        list_tracks.get(1).setDescription("Fahrradhelm muss dabei sein, ist wirklich hart, die Tour");
        list_tracks.get(2).setDescription("Schreibe lieber noch dein Testament bevor du diese Mega Harte Tour antrittst");

        list_tracks.get(0).setPostcode("89073");
        list_tracks.get(1).setPostcode("88477");
        list_tracks.get(2).setPostcode("89231");
        return list_tracks;
    }
}
