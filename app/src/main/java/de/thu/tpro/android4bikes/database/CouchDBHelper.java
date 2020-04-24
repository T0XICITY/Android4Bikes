package de.thu.tpro.android4bikes.database;

import android.util.Log;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.util.JSONHelper;
import de.thu.tpro.android4bikes.util.deserialization.AchievementDeserializer;

/**
 * The purpose of the {@link de.thu.tpro.android4bikes.database.CouchDBHelper} is the provision of the interface
 * between the local couchbase lite database an the classes and objects which are used in this project.
 * It provides methods to store objects in the local database and also to read them.
 * There are also delete and update methods where it is necessary.
 */
public class CouchDBHelper extends Observable implements LocalDatabaseHelper {
    private CouchDB couchDB;
    private Gson gson;
    private Gson gson_achievement;
    private DBMode currentMode;

    public CouchDBHelper() {
        couchDB = CouchDB.getInstance();
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //create and set deserializer for the inheritance regarding the class "achievement"
        GsonBuilder gsonBuilder_achievement = new GsonBuilder();
        gsonBuilder_achievement.registerTypeAdapter(Achievement.class, new AchievementDeserializer<Achievement>());
        gson_achievement = gsonBuilder_achievement.create();

        currentMode = DBMode.OFFLINEDATA;
    }

    public CouchDBHelper(DBMode mode) {
        this();
        currentMode = mode;
    }

    @Override
    public void deleteBikeRack(BikeRack bikeRack) {
        this.deleteBikeRack(bikeRack.getFirebaseID());
    }


    /**
     * stores a list of tracks in the database
     * @param list_tracks list of tracks
     */
    public void storeTracks(List<Track> list_tracks){
        for(Track track : list_tracks){
            storeTrack(track);
        }
    }

    @Override
    public void storeTrack(Track track) {
        Database db_track = null;
        try {
            switch (currentMode) {
                case DELETEBUFFER:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_TRACK);
                    break;
                case WRITEBUFFER:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_TRACK);
                    break;
                case OWNDATA:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_TRACK);
                    break;
                case OFFLINEDATA:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
                    break;
            }

            JSONObject jsonObject_track = new JSONObject(gson.toJson(track));
            jsonObject_track.remove(Track.ConstantsTrack.ROUTE.toString());
            String json_Route;
            if (track.getRoute() != null){
                json_Route = track.getRoute().toJson();
            }else {
                json_Route = null;
            }
            Map map_track = gson.fromJson(jsonObject_track.toString(), Map.class);
            map_track.put(Track.ConstantsTrack.ROUTE.toString(),json_Route);

            MutableDocument mutableDocument_track = new MutableDocument(map_track);
            couchDB.saveMutableDocumentToDatabase(db_track, mutableDocument_track);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (track != null) {
            readTracks();
        }
    }

    //TODO: Remove return values (not necessary!!) and adjust unit tests
    @Override
    public List<Track> readTracks() {
        List<Track> tracks = null;
        Database db_track = null;
        String db_name = null;
        try {
            switch (currentMode) {
                case DELETEBUFFER:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_TRACK);
                    db_name = DatabaseNames.DATABASE_DELETEBUFFER_TRACK.toText();
                    break;
                case WRITEBUFFER:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_TRACK);
                    db_name = DatabaseNames.DATABASE_WRITEBUFFER_TRACK.toText();
                    break;
                case OWNDATA:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_TRACK);
                    db_name = DatabaseNames.DATABASE_OWNDATA_TRACK.toText();
                    break;
                case OFFLINEDATA:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
                    db_name = DatabaseNames.DATABASE_TRACK.toText();
                    break;
            }
            tracks = new ArrayList<>();
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_track));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                Map<String, Object> map_track = result.toMap();
                JSONObject jsonObject_track = new JSONObject(map_track);
                jsonObject_track = (JSONObject) jsonObject_track.get(db_name);
                String jsonString_Route;
                try{
                    jsonString_Route = jsonObject_track.getString(Track.ConstantsTrack.ROUTE.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    jsonString_Route = null;
                }
                DirectionsRoute route;
                if (jsonString_Route != null){
                    route = DirectionsRoute.fromJson(jsonString_Route);
                    jsonObject_track.remove(Track.ConstantsTrack.ROUTE.toString());
                }else {
                    route = null;
                }
                JSONHelper<Track> helper = new JSONHelper<>(Track.class);
                Track track = helper.convertJSONObjectToObject(jsonObject_track);
                track.setRoute(route);
                tracks.add(track);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tracks != null) {
            setChanged();
            notifyObservers(tracks);
        }

        return tracks;
    }

    @Override
    public void deleteTrack(Track track) {
        this.deleteTrack(track.getFirebaseID());
    }

    @Override
    public void deleteTrack(String fireBaseID) {
        Database db_track = null;
        try {
            switch (currentMode) {
                case DELETEBUFFER:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_TRACK);
                    break;
                case WRITEBUFFER:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_TRACK);
                    break;
                case OWNDATA:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_TRACK);
                    break;
                case OFFLINEDATA:
                    db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
                    break;
            }
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                    .from(DataSource.database(db_track))
                    .where(Expression.property(Track.ConstantsTrack.FIREBASEID.toString()).equalTo(Expression.string(fireBaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                couchDB.deleteDocumentByID(db_track, result.getString(CouchDB.AttributeNames.DATABASE_ID.toText()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //notify ViewModel when deletion has been performed
        if (fireBaseID != null) {
            readTracks();
        }
    }

    @Override
    public void storeHazardAlerts(HazardAlert hazardAlert) {
        Database db_hazardAlert = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT);
                    break;
                case OWNDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_HAZARD_ALERT);
                    break;
                case OFFLINEDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);
                    break;
            }
            JSONObject json_hazardAlert = new JSONObject(gson.toJson(hazardAlert));
            Map map_hazardAlert = gson.fromJson(json_hazardAlert.toString(), Map.class);
            MutableDocument mutableDocument_hazardAlert = new MutableDocument(map_hazardAlert);
            //save mutable document representing the hazardAlert to the local db
            couchDB.saveMutableDocumentToDatabase(db_hazardAlert, mutableDocument_hazardAlert);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hazardAlert != null) {
            readHazardAlerts();
        }
    }

    @Override
    public List<HazardAlert> readHazardAlerts() {
        List<HazardAlert> hazardAlerts = null;
        Database db_hazardAlert = null;
        String db_name = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT);
                    db_name = DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT.toText();
                    break;
                case OWNDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_HAZARD_ALERT);
                    db_name = DatabaseNames.DATABASE_OWNDATA_HAZARD_ALERT.toText();
                    break;
                case OFFLINEDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);
                    db_name = DatabaseNames.DATABASE_HAZARD_ALERT.toText();
                    break;
            }
            hazardAlerts = new ArrayList<>();
            HazardAlert hazardAlert = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_hazardAlert));

            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                hazardAlert = convertMapHazardAlertToHazardAlert(result.toMap(), db_name);
                hazardAlerts.add(hazardAlert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hazardAlerts != null) {
            setChanged();
            notifyObservers(hazardAlerts);
        }

        return hazardAlerts;
    }

    @Override
    public void deleteHazardAlert(String fireBaseID) {
        Database db_hazardAlert = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT);
                    break;
                case OWNDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_HAZARD_ALERT);
                    break;
                case OFFLINEDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);
                    break;
            }
            HazardAlert hazardAlert = null;
            JSONObject jsonObject_result = null;
            String mutabledocument_result_id = null;
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                    .from(DataSource.database(db_hazardAlert))
                    .where(Expression.property(HazardAlert.ConstantsHazardAlert.FIREBASEID.toString()).equalTo(Expression.string(fireBaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                mutabledocument_result_id = result.getString(CouchDB.AttributeNames.DATABASE_ID.toText());
                couchDB.deleteDocumentByID(db_hazardAlert, mutabledocument_result_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fireBaseID != null) {
            readHazardAlerts();
        }
    }

    @Override
    public void deleteHazardAlert(HazardAlert hazardAlert) {
        this.deleteHazardAlert(hazardAlert.getFirebaseID());
    }

    @Override
    public void addToUtilization(Position position) {
        try {
            Database db_position = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_POSITION);
            JSONObject json_position = new JSONObject(gson.toJson(position));
            Map result = gson.fromJson(json_position.toString(), Map.class);
            MutableDocument md_position = new MutableDocument(result);
            couchDB.saveMutableDocumentToDatabase(db_position, md_position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetUtilization() {
        try {
            Database utilizationDB = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_POSITION);
            couchDB.clearDB(utilizationDB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeProfile(Profile profile) {
        Database db_profile = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_PROFILE);
                    break;
                case OWNDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE);
                    break;
                case OFFLINEDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
                    break;
                case DELETEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_PROFILE);
                    break;
            }
            JSONObject jsonObject_profile = new JSONObject(gson.toJson(profile));
            Map result = gson.fromJson(jsonObject_profile.toString(), Map.class);
            MutableDocument mutableDocument_profile = new MutableDocument(result);
            couchDB.saveMutableDocumentToDatabase(db_profile, mutableDocument_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile != null) {
            if (currentMode == DBMode.OWNDATA) {
                readMyOwnProfile();
            } else {
                readProfile(profile.getGoogleID());
            }
        }
    }

    @Override
    public void storeProfile(Map map_profile) {
        Database db_profile = null;
        try {
            switch (currentMode) {
                case DELETEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_PROFILE);
                    break;
                case WRITEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_PROFILE);
                    break;
                case OWNDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE);
                    break;
                case OFFLINEDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
                    break;
            }
            MutableDocument mutableDocument_profile = new MutableDocument(map_profile);
            couchDB.saveMutableDocumentToDatabase(db_profile, mutableDocument_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Profile profile = convertMapProfileToProfile(map_profile, null); //todo: Conversion from map to profile

        if (profile != null) {
            if (currentMode == DBMode.OWNDATA) {
                readMyOwnProfile();
            } else {
                readProfile(profile.getGoogleID());
            }
        }
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

    @Override
    public Profile readProfile(String firebaseAccountID) {
        Profile profile = null;
        Database db_profile = null;
        String db_name = null;
        try {
            switch (currentMode) {
                case DELETEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_PROFILE);
                    db_name = DatabaseNames.DATABASE_DELETEBUFFER_PROFILE.toText();
                    break;
                case WRITEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_PROFILE);
                    db_name = DatabaseNames.DATABASE_WRITEBUFFER_PROFILE.toText();
                    break;
                case OWNDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE);
                    db_name = DatabaseNames.DATABASE_OWNDATA_PROFILE.toText();
                    break;
                case OFFLINEDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
                    db_name = DatabaseNames.DATABASE_PROFILE.toText();
                    break;
            }
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_profile))
                    .where(Expression.property(Profile.ConstantsProfile.GOOGLEID.toString()).equalTo(Expression.string(firebaseAccountID)));
            ResultSet results = couchDB.queryDatabase(query);

            for (Result result : results) {
                Map map_result = result.toMap();
                profile = convertMapProfileToProfile(map_result, db_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile != null) {
            setChanged();
            notifyObservers(profile);
        }

        return profile;
    }

    public void storeMyOwnProfile(Profile profile) {
        try {
            Database db_ownprofile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE);
            JSONObject jsonObject_profile = new JSONObject(gson.toJson(profile));
            Map result = gson.fromJson(jsonObject_profile.toString(), Map.class);
            MutableDocument mutableDocument_profile = new MutableDocument(result);
            couchDB.saveMutableDocumentToDatabase(db_ownprofile, mutableDocument_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile != null) {
            readMyOwnProfile();
        }
    }


    public void updateMyOwnProfile(Profile profile) {
        deleteMyOwnProfile();
        storeMyOwnProfile(profile);

        if (profile != null) {
            readMyOwnProfile();
        }
    }

    /**
     * Deletes the own profile.
     */
    public void deleteMyOwnProfile() {
        couchDB.clearDB(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE));
        readMyOwnProfile();
    }

    /**
     * reads the own profile out of the database
     * @return own profile
     */
    public Profile readMyOwnProfile() {
        Profile ownProfile = null;
        try {
            Database db_own_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE);
            Query query_own_profile = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_own_profile));
            ResultSet results = couchDB.queryDatabase(query_own_profile);
            JSONObject jsonObject_profile = null;
            JSONArray jsonArray_achievement = null;
            for (Result result : results) {
                Map map_result = result.toMap();
                jsonObject_profile = new JSONObject(map_result);
                jsonObject_profile = (JSONObject) jsonObject_profile.get(DatabaseNames.DATABASE_OWNDATA_PROFILE.toText());

                jsonArray_achievement = jsonObject_profile.getJSONArray(Profile.ConstantsProfile.ACHIEVEMENTS.toString());
                List<Achievement> list_achievements = new ArrayList<>();

                for (int i = 0; i < jsonArray_achievement.length(); ++i) {
                    JSONObject jsonObject_achievement = jsonArray_achievement.getJSONObject(i);
                    Achievement achievement = gson_achievement.fromJson(jsonObject_achievement.toString(), Achievement.class);
                    list_achievements.add(achievement);
                }
                jsonObject_profile.remove(Profile.ConstantsProfile.ACHIEVEMENTS.toString());
                ownProfile = gson.fromJson(jsonObject_profile.toString(), Profile.class);
                ownProfile.setAchievements(list_achievements);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ownProfile != null) {
            setChanged();
            notifyObservers(ownProfile);
        }

        return ownProfile;
    }

    @Override
    public void updateProfile(Profile profile) {
        deleteProfile(profile.getGoogleID());
        storeProfile(profile);

        if (profile != null) {
            if (currentMode == DBMode.OWNDATA) {
                readMyOwnProfile();
            } else {
                readProfile(profile.getGoogleID());
            }
        }
    }

    @Override
    public void deleteProfile(String googleID) {
        Database db_profile = null;
        try {
            switch (currentMode) {
                case DELETEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_DELETEBUFFER_PROFILE);
                    break;
                case WRITEBUFFER:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_PROFILE);
                    break;
                case OWNDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_PROFILE);
                    break;
                case OFFLINEDATA:
                    db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
                    break;
            }
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                    .from(DataSource.database(db_profile))
                    .where(Expression.property(Profile.ConstantsProfile.GOOGLEID.toString()).equalTo(Expression.string(googleID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                couchDB.deleteDocumentByID(db_profile, result.getString(CouchDB.AttributeNames.DATABASE_ID.toText()));
            }
            Log.d("HalloWelt", "Deleted profile sucessfully from "+currentMode.toText());
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (googleID != null) {
            if (currentMode == DBMode.OWNDATA) {
                readMyOwnProfile();
            } else {
                readProfile(googleID);
            }
        }
    }

    @Override
    public void deleteProfile(Profile profile) {
        this.deleteProfile(profile.getGoogleID());
    }

    @Override
    public void storeBikeRack(BikeRack bikeRack) {
        Database db_bikerack = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);
                    break;
                case OWNDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_BIKERACK);
                    break;
                case OFFLINEDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);
                    break;
            }
            JSONObject json_bikeRack = new JSONObject(gson.toJson(bikeRack));
            Map map_bikeRack = gson.fromJson(json_bikeRack.toString(), Map.class);
            MutableDocument mutableDocument_bikeRack = new MutableDocument(map_bikeRack);
            //save mutable document representing the bikeRack to the local db
            couchDB.saveMutableDocumentToDatabase(db_bikerack, mutableDocument_bikeRack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bikeRack != null) {
            readBikeRacks();
        }
    }

    @Override
    public List<BikeRack> readBikeRacks() {
        List<BikeRack> bikeRacks = null;
        Database db_bikerack = null;
        String db_name = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);
                    db_name = DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK.toText();
                    break;
                case OWNDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_BIKERACK);
                    db_name = DatabaseNames.DATABASE_OWNDATA_BIKERACK.toText();
                    break;
                case OFFLINEDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);
                    db_name = DatabaseNames.DATABASE_BIKERACK.toText();
                    break;
            }
            bikeRacks = new ArrayList<>();
            BikeRack bikeRack = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_bikerack));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                //convert result to jsonObject-string
                bikeRack = convertMapBikeRackToBikeRack(result.toMap(), db_name);
                bikeRacks.add(bikeRack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //notify all observers (e.g. ViewModelTrack)
        if (bikeRacks != null) {
            setChanged();
            notifyObservers(bikeRacks);
        }

        return bikeRacks;
    }

    @Override
    public void deleteBikeRack(String fireBaseID) {
        Database db_bikerack = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);
                    break;
                case OWNDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_BIKERACK);
                    break;
                case OFFLINEDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);
                    break;
            }
            BikeRack bikeRack = null;
            JSONObject jsonObject_result = null;
            String mutabledocument_result_id = null;
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                    .from(DataSource.database(db_bikerack))
                    .where(Expression.property(BikeRack.ConstantsBikeRack.FIREBASEID.toString()).equalTo(Expression.string(fireBaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                mutabledocument_result_id = result.getString(CouchDB.AttributeNames.DATABASE_ID.toText());
                couchDB.deleteDocumentByID(db_bikerack, mutabledocument_result_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fireBaseID != null) {
            readBikeRacks();
        }
    }

    /**
     * Get all positions which are stored in the local database
     *
     * @return list of all positions
     */
    public List<Position> getAllPositions() {
        List<Position> positions = null;
        Database db_position = null;
        String db_name = null;
        try {
            db_position = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_POSITION);
            db_name = DatabaseNames.DATABASE_WRITEBUFFER_POSITION.toText();
            positions = new ArrayList<>();
            ResultSet results = couchDB.readAllDocumentsOfADatabase(db_position);
            for (Result result : results) {
                JSONObject json_result = new JSONObject(result.toMap());
                json_result = json_result.getJSONObject(db_name);
                positions.add(gson.fromJson(json_result.toString(), Position.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    @Override
    public Position getLastPosition() {
        //todo
        return new Position( 9.836149,48.304486);
    }

    @Override
    public void storeBikeRack(Map map_bikeRack) {
        Database db_bikerack = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);
                    break;
                case OWNDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_BIKERACK);
                    break;
                case OFFLINEDATA:
                    db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);
                    break;
            }
            MutableDocument mutableDocument_bikeRack = new MutableDocument(map_bikeRack);
            couchDB.saveMutableDocumentToDatabase(db_bikerack, mutableDocument_bikeRack);
        }catch (Exception e){
            e.printStackTrace();
        }

        BikeRack bikeRack = convertMapBikeRackToBikeRack(map_bikeRack, null);
        if (bikeRack != null) {
            readBikeRacks();
        }
    }

    @Override
    public void storeHazardAlerts(Map map_hazardAlert) {
        Database db_hazardAlert = null;
        try {
            switch (currentMode) {
                case WRITEBUFFER:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT);
                    break;
                case OWNDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_HAZARD_ALERT);
                    break;
                case OFFLINEDATA:
                    db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);
                    break;
            }
            MutableDocument mutableDocument_hazardAlert = new MutableDocument(map_hazardAlert);
            couchDB.saveMutableDocumentToDatabase(db_hazardAlert, mutableDocument_hazardAlert);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HazardAlert hazardAlert = convertMapHazardAlertToHazardAlert(map_hazardAlert, null);
        if (hazardAlert != null) {
            readHazardAlerts();
        }
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
     * Generates the JSON representation out of a mutable document
     *
     * @param mutableDocument specified mutable document
     * @return json representation of a given mutable document
     */
    public JSONObject convertMutableDocumentToJSON(MutableDocument mutableDocument) {
        JSONObject jsonRepresentation = null;
        if (mutableDocument != null) {
            jsonRepresentation = new JSONObject(mutableDocument.toMap());
        }
        return jsonRepresentation;
    }

    /**
     * converts a given json object to a mutable document
     *
     * @param jsonObject json object to convert
     * @return mutable document
     */
    public MutableDocument convertJSONToMutableDocument(JSONObject jsonObject) {

        MutableDocument mutableDocument = null;
        try {
            //generate new mutable document
            mutableDocument = new MutableDocument();

            //map representing all key value pairs of the json object
            Map<String, Object> map_jsonData = new HashMap<>();

            //get iterator regarding all keys of the json object
            Iterator<String> keyIterator = jsonObject.keys();
            String key = null; //store every key
            Object value = null; //store every value

            //get all key value pairs of the json object and store them to the map
            while (keyIterator.hasNext()) {
                key = keyIterator.next();
                value = jsonObject.get(key);
                map_jsonData.put(key, value);
            }
            mutableDocument.setData(map_jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("HalloWelt", "No mutable document could be generated.");
        }
        return mutableDocument;
    }

    public enum DBMode {
        WRITEBUFFER("writebuffer"),
        OWNDATA("owndata"),
        OFFLINEDATA("offlinedata"),
        DELETEBUFFER("deletebuffer");

        private String name;

        DBMode(String type) {
            this.name = type;
        }

        public String toText() {
            return name;
        }
    }
}