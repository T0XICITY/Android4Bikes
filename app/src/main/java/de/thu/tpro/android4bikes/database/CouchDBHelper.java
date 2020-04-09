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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
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

    public CouchDBHelper() {
        couchDB = CouchDB.getInstance();
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //create and set deserializer for the inheritance regarding the class "achievement"
        GsonBuilder gsonBuilder_achievement = new GsonBuilder();
        gsonBuilder_achievement.registerTypeAdapter(Achievement.class, new AchievementDeserializer<Achievement>());
        gson_achievement = gsonBuilder_achievement.create();
    }

    @Override
    public void deleteBikeRack(BikeRack bikeRack) {
        this.deleteBikeRack(bikeRack.getFirebaseID());
    }

    @Override
    public void storeTrack(Track track) {
        try {
            Database db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
            JSONObject json_track = new JSONObject(gson.toJson(track));
            Map result = gson.fromJson(json_track.toString(), Map.class);
            MutableDocument mutableDocument_track = new MutableDocument(result);
            couchDB.saveMutableDocumentToDatabase(db_track, mutableDocument_track);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Track> readTracks(String postcode) {
        List<Track> tracks = null;
        try {
            tracks = new ArrayList<>();
            Database db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_track))
                    .where(Expression.property(Track.ConstantsTrack.POSTCODE.toString()).equalTo(Expression.string(postcode)));
            ResultSet results = couchDB.queryDatabase(query);
            JSONObject jsonObject_result = null;
            Track track_result = null;
            for (Result result : results) {
                jsonObject_result = new JSONObject(result.toMap());
                track_result = gson.fromJson(jsonObject_result.get(DatabaseNames.DATABASE_TRACK.toText()).toString(), Track.class);
                tracks.add(track_result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    @Override
    public void deleteTrack(String fireBaseID) {
        try {
            Database db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
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
    }

    @Override
    public void storeHazardAlerts(HazardAlert hazardAlert) {
        try {
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db hazardAlerts
            JSONObject json_hazardAlert = new JSONObject(gson.toJson(hazardAlert));
            Map map_hazardAlert = gson.fromJson(json_hazardAlert.toString(), Map.class);
            MutableDocument mutableDocument_hazardAlert = new MutableDocument(map_hazardAlert);
            //save mutable document representing the hazardAlert to the local db
            couchDB.saveMutableDocumentToDatabase(db_hazardAlert, mutableDocument_hazardAlert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HazardAlert> readHazardAlerts(String postcode) {
        List<HazardAlert> hazardAlerts = null;
        try {
            hazardAlerts = new ArrayList<>();
            JSONObject jsonObject_result = null;
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db hazardAlerts
            HazardAlert hazardAlert = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_hazardAlert))
                    .where(Expression.property(HazardAlert.ConstantsHazardAlert.POSTCODE.toString()).equalTo(Expression.string(postcode)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                //convert result to jsonObject-string
                jsonObject_result = new JSONObject(result.toMap());
                //result document -> "db_haradAlert":{ <object> }
                //because the necessary object in nested, we have to access it by getting it:
                jsonObject_result = (JSONObject) jsonObject_result.get(DatabaseNames.DATABASE_HAZARD_ALERT.toText());
                hazardAlert = gson.fromJson(jsonObject_result.toString(), HazardAlert.class);
                hazardAlerts.add(hazardAlert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hazardAlerts;
    }

    @Override
    public void deleteHazardAlert(String fireBaseID) {
        try {
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db hazardAlerts
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
    }

    @Override
    public void deleteHazardAlert(HazardAlert hazardAlert) {
        this.deleteHazardAlert(hazardAlert.getFirebaseID());
    }

    @Override
    public void addToUtilization(Position position) {
        try {
            Database db_position = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION);
            JSONObject json_position = new JSONObject(gson.toJson(position));
            Map result = gson.fromJson(json_position.toString(), Map.class);
            MutableDocument md_position = new MutableDocument(result);
            couchDB.saveMutableDocumentToDatabase(db_position, md_position);
            if (couchDB.getNumberOfStoredDocuments(db_position) >= 50) {
                List<Position> positions = this.getAllPositions();
                FirebaseConnection.getInstance().storeUtilizationToFireStore(positions);
                this.resetUtilization();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetUtilization() {
        Database utilizationDB = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION);
        couchDB.clearDB(utilizationDB);
    }

    @Override
    public void storeProfile(Profile profile) {
        try {
            Database db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
            JSONObject jsonObject_profile = new JSONObject(gson.toJson(profile));
            Map result = gson.fromJson(jsonObject_profile.toString(), Map.class);
            MutableDocument mutableDocument_profile = new MutableDocument(result);
            couchDB.saveMutableDocumentToDatabase(db_profile, mutableDocument_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeProfile(Map map_profile) {
        try {
            MutableDocument mutableDocument_profile = new MutableDocument(map_profile);
            couchDB.saveMutableDocumentToDatabase(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE), mutableDocument_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Profile readProfile(String firebaseAccountID) {
        Profile profile = null;
        try {
            Database db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_profile))
                    .where(Expression.property(Profile.ConstantsProfile.GOOGLEID.toString()).equalTo(Expression.string(firebaseAccountID)));
            ResultSet results = couchDB.queryDatabase(query);
            JSONObject jsonObject_profile = null;
            JSONArray jsonArray_achievement = null;
            for (Result result : results) {
                Map map_result = result.toMap();
                jsonObject_profile = new JSONObject(map_result);
                jsonObject_profile = (JSONObject) jsonObject_profile.get(DatabaseNames.DATABASE_PROFILE.toText());

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }


    public Profile readMyProfile() {
        //TODO!!!!! AUSIMPLEMENTIEREN
        //eigenes Profile: { attribute... isMyProfile:"true"}
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));
        return new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
    }

    @Override
    public void updateProfile(Profile profile) {
        deleteProfile(profile.getGoogleID());
        storeProfile(profile);
    }

    @Override
    public void deleteProfile(String googleID) {
        try {
            Database db_Profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                    .from(DataSource.database(db_Profile))
                    .where(Expression.property(Profile.ConstantsProfile.GOOGLEID.toString()).equalTo(Expression.string(googleID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                couchDB.deleteDocumentByID(db_Profile, result.getString(CouchDB.AttributeNames.DATABASE_ID.toText()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProfile(Profile profile) {
        this.deleteProfile(profile.getGoogleID());
    }

    @Override
    public void storeBikeRack(BikeRack bikeRack) {
        try {
            Database db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK); //Get db bikerack
            JSONObject json_bikeRack = new JSONObject(gson.toJson(bikeRack));
            Map map_bikeRack = gson.fromJson(json_bikeRack.toString(), Map.class);
            MutableDocument mutableDocument_bikeRack = new MutableDocument(map_bikeRack);
            //save mutable document representing the bikeRack to the local db
            couchDB.saveMutableDocumentToDatabase(db_bikerack, mutableDocument_bikeRack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BikeRack> readBikeRacks(String postcode) {
        List<BikeRack> bikeRacks = null;
        try {
            bikeRacks = new ArrayList<>();
            JSONObject jsonObject_result = null;
            Database db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK); //Get db bikerack
            BikeRack bikeRack = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_bikerack))
                    .where(Expression.property(BikeRack.ConstantsBikeRack.POSTCODE.toString()).equalTo(Expression.string(postcode)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                //convert result to jsonObject-string
                jsonObject_result = new JSONObject(result.toMap());
                jsonObject_result = (JSONObject) jsonObject_result.get(DatabaseNames.DATABASE_BIKERACK.toText());
                bikeRack = gson.fromJson(jsonObject_result.toString(), BikeRack.class);
                bikeRacks.add(bikeRack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bikeRacks;
    }

    @Override
    public void deleteBikeRack(String fireBaseID) {
        try {
            Database db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK); //Get db bikerack
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
    }

    /**
     * Get all positions which are stored in the local database
     *
     * @return list of all positions
     */
    private List<Position> getAllPositions() {
        List<Position> positions = null;
        try {
            positions = new ArrayList<>();
            ResultSet results = couchDB.readAllDocumentsOfADatabase(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION));
            for (Result result : results) {
                JSONObject json_result = new JSONObject(result.toMap());
                json_result = json_result.getJSONObject(DatabaseNames.DATABASE_POSITION.toText());
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
        int i = ThreadLocalRandom.current().nextInt(1,10);
        int k = ThreadLocalRandom.current().nextInt(1,10);
        return new Position( 9.836149+i,48.304486+k);
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
}