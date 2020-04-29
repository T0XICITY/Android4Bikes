package de.thu.tpro.android4bikes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.deserialization.AchievementDeserializer;

public class MapToObjectConverter<T> {
    private Gson gson;
    private Class<T> type;
    private Gson gson_achievement;

    public MapToObjectConverter(Class<T> type) {
        this.type = type;
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //create and set deserializer for the inheritance regarding the class "achievement"
        GsonBuilder gsonBuilder_achievement = new GsonBuilder();
        gsonBuilder_achievement.registerTypeAdapter(Achievement.class, new AchievementDeserializer<Achievement>());
        gson_achievement = gsonBuilder_achievement.create();
    }

    public T convertMapToObject(Map map, String db_name) {
        T object = null;
        try {
            if (type.equals(BikeRack.class)) {
                object = (T) convertMapBikeRackToBikeRack(map, db_name);
            } else if (type.equals(HazardAlert.class)) {
                object = (T) convertMapHazardAlertToHazardAlert(map, db_name);
            } else if (type.equals(Track.class)) {
                object = (T) convertMapTrackToTrack(map, db_name);
            } else if (type.equals(Profile.class)) {
                object = (T) convertMapProfileToProfile(map, db_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private Track convertMapTrackToTrack(Map map_track, String db_name) {
        Track track = null;
        try {
            JSONObject jsonObject_track = new JSONObject(map_track);

            if (db_name != null) {
                jsonObject_track = (JSONObject) jsonObject_track.get(db_name);
            }

            String jsonString_Route;
            try {
                jsonString_Route = jsonObject_track.getString(Track.ConstantsTrack.ROUTE.toString());
            } catch (Exception e) {
                e.printStackTrace();
                jsonString_Route = null;
            }
            DirectionsRoute route;
            if (jsonString_Route != null) {
                route = DirectionsRoute.fromJson(jsonString_Route);
                jsonObject_track.remove(Track.ConstantsTrack.ROUTE.toString());
            } else {
                route = null;
            }
            JSONHelper<Track> helper = new JSONHelper<>(Track.class);
            track = helper.convertJSONObjectToObject(jsonObject_track);
            track.setRoute(route);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return track;
    }

    private BikeRack convertMapBikeRackToBikeRack(Map map_bikeRack, String db_name) {
        BikeRack bikeRack = null;
        try {
            JSONObject jsonObject_result = null;
            jsonObject_result = new JSONObject(map_bikeRack);
            if (db_name != null) {
                jsonObject_result = (JSONObject) jsonObject_result.get(db_name);
            }
            bikeRack = gson.fromJson(jsonObject_result.toString(), BikeRack.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bikeRack;
    }

    private HazardAlert convertMapHazardAlertToHazardAlert(Map map_hazardAlert, String db_name) {
        HazardAlert hazardAlert = null;
        try {
            JSONObject jsonObject_result = null;
            //convert result to jsonObject-string
            jsonObject_result = new JSONObject(map_hazardAlert);
            //result document -> "db_haradAlert":{ <object> }
            //because the necessary object in nested, we have to access it by getting it:
            if (db_name != null) {
                jsonObject_result = (JSONObject) jsonObject_result.get(db_name);
            }
            hazardAlert = gson.fromJson(jsonObject_result.toString(), HazardAlert.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hazardAlert;
    }

    /**
     * @param map_profile map representing a profile.
     * @param db_name     required for query results that are delivered by the local db. Otherwise null as value is ok.
     * @return
     */
    private Profile convertMapProfileToProfile(Map map_profile, String db_name) {
        Profile profile = null;
        try {
            JSONObject jsonObject_profile = null;
            JSONArray jsonArray_achievement = null;

            jsonObject_profile = new JSONObject(map_profile);

            if (db_name != null) {
                jsonObject_profile = (JSONObject) jsonObject_profile.get(db_name);
            }

            jsonArray_achievement = jsonObject_profile.getJSONArray(Profile.ConstantsProfile.ACHIEVEMENTS.toString());
            List<Achievement> list_achievements = new ArrayList<>();

            for (int i = 0; i < jsonArray_achievement.length(); ++i) {
                JSONObject jsonObject_achievement = jsonArray_achievement.getJSONObject(i);
                Achievement achievement = gson_achievement.fromJson(jsonObject_achievement.toString(), Achievement.class);
                list_achievements.add(achievement);
            }

            jsonObject_profile.remove(Profile.ConstantsProfile.ACHIEVEMENTS.toString());
            profile = gson.fromJson(jsonObject_profile.toString(), Profile.class);
            profile.setAchievements(list_achievements);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }


}
