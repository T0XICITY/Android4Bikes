package de.thu.tpro.android4bikes.database;

import android.util.Log;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

public class CouchDBHelper implements LocalDatabaseHelper {
    private CouchDB couchDB;
    private Gson gson;
    private static int posCounter = 0;

    public CouchDBHelper() {
        couchDB = CouchDB.getInstance();
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Override
    public void deleteBikeRack(BikeRack bikeRack) {
        //TODO: Review and testing
        this.deleteBikeRack(bikeRack.getFirebaseID());
    }

    @Override
    public void storeTrack(Track track) {
        //todo: Review und Test
        try {
            Database db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
            JSONObject json_track = new JSONObject(gson.toJson(track));
            MutableDocument mutableDocument_track = this.convertJSONToMutableDocument(json_track);
            couchDB.saveMutableDocumentToDatabase(db_track, mutableDocument_track);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Track> readTracks(String postcode) {
        List<Track> tracks = new ArrayList<>();
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    @Override
    public void deleteTrack(String fireBaseID) {
        //todo: review und test
        try {
            Database db_track = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
            Query query = QueryBuilder.select(SelectResult.all())
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
    public void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions) {
        //TODO: Review and testing
        try {
            Database db_fineGrainedPositions = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_FINEGRAINEDPOSITIONS); //Get db finegrainedposi

            //convert hazardAlert to mutable document
            JSONObject json_finegrainedPositions = new JSONObject(gson.toJson(fineGrainedPositions));
            MutableDocument mutableDocument_finedgrainedPositions = this.convertJSONToMutableDocument(json_finegrainedPositions);

            //save mutable document representing the hazardAlert to the local db
            couchDB.saveMutableDocumentToDatabase(db_fineGrainedPositions, mutableDocument_finedgrainedPositions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FineGrainedPositions readFineGrainedPositions(String firebaseID) {
        //TODO: Review and testing
        FineGrainedPositions fineGrainedPositions = null;
        try {
            JSONObject jsonObject_result = null;
            Database db_finegrainedpositions = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_FINEGRAINEDPOSITIONS); //Get db finegrainedpositions

            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_finegrainedpositions))
                    .where(Expression.property(HazardAlert.ConstantsHazardAlert.FIREBASEID.toString()).equalTo(Expression.string(firebaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                //convert result to jsonObject-string
                jsonObject_result = new JSONObject(result.toMap());

                //result document -> "db_finefrainedpositions":{ <object> }
                //because the necessary object in nested, we have to access it by getting it:
                jsonObject_result = (JSONObject) jsonObject_result.get(DatabaseNames.DATABASE_FINEGRAINEDPOSITIONS.toText());

                fineGrainedPositions = gson.fromJson(jsonObject_result.toString(), FineGrainedPositions.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fineGrainedPositions;
    }

    @Override
    public FineGrainedPositions readFineGrainedPositions(Track track) {
        //TODO: Review and testing
        return this.readFineGrainedPositions(track.getFirebaseID());
    }

    @Override
    public void storeHazardAlerts(HazardAlert hazardAlert) {
        //TODO: Review and testing
        try {
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db hazardAlerts

            //convert hazardAlert to mutable document
            JSONObject json_hazardAlert = new JSONObject(gson.toJson(hazardAlert));
            MutableDocument mutableDocument_hazardAlerts = this.convertJSONToMutableDocument(json_hazardAlert);

            //save mutable document representing the hazardAlert to the local db
            couchDB.saveMutableDocumentToDatabase(db_hazardAlert, mutableDocument_hazardAlerts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HazardAlert> readHazardAlerts(String postcode) {
        //TODO: Review and testing
        List<HazardAlert> hazardAlerts = new ArrayList<>();
        try {
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
        //TODO: Review and testing
        try {
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db hazardAlerts
            HazardAlert hazardAlert = null;
            JSONObject jsonObject_result = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_hazardAlert))
                    .where(Expression.property(HazardAlert.ConstantsHazardAlert.FIREBASEID.toString()).equalTo(Expression.string(fireBaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                couchDB.deleteDocumentByID(db_hazardAlert, result.getString(CouchDB.AttributeNames.DATABASE_ID.toText()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteHazardAlert(HazardAlert hazardAlert) {
        //TODO: Review and testing
        this.deleteHazardAlert(hazardAlert.getFirebaseID());
    }

    /**
     * Saves a position in the local Database
     *
     * @param position is the position which should be stored in the local database
     */
    @Override
    public void addToUtilization(Position position) {
        //todo: review und test
        try {
            JSONObject json_position = new JSONObject(gson.toJson(position));
            MutableDocument md_position = convertJSONToMutableDocument(json_position);
            couchDB.saveMutableDocumentToDatabase(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION), md_position);
            posCounter++;
            if (posCounter >= 50) {
                List<Position> positions = this.getAllPositions();
                FirebaseConnection.getInstance().storeUtilizationToFireStore(positions); //todo: Is it right this way?
                this.resetUtilization();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all entries of position of the local database
     */
    @Override
    public void resetUtilization() {
        //todo: review und test
        ResultSet results = couchDB.queryDatabase(Queries.getAllPosQuery);
        results.forEach(result -> {
            String id = result.getString(0);
            couchDB.deleteDocumentByID(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION), id);
        });
    }

    @Override
    public void storeProfile(Profile Profile) {
        //todo: review und test
        try {
            Database db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
            JSONObject jsonObject_profile = new JSONObject(gson.toJson(db_profile));
            MutableDocument mutableDocument_profile = this.convertJSONToMutableDocument(jsonObject_profile);
            couchDB.saveMutableDocumentToDatabase(db_profile, mutableDocument_profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Profile readProfile(String firebaseAccountID) {
        //todo: review und test
        Profile profile = null;
        try {
            Database db_profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_profile))
                    .where(Expression.property(Profile.ConstantsProfile.GOOGLEID.toString()).equalTo(Expression.string(firebaseAccountID)));
            ResultSet results = couchDB.queryDatabase(query);
            JSONObject jsonObject_profile = null;
            for (Result result : results) {
                jsonObject_profile = new JSONObject(result.toMap());
                profile = gson.fromJson(jsonObject_profile.get(DatabaseNames.DATABASE_PROFILE.toText()).toString(), Profile.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    @Override
    public void updateProfile(Profile profile) {
        //todo: review und test
        deleteProfile(profile.getGoogleID());
        storeProfile(profile);
    }

    @Override
    public void deleteProfile(String googleID) {
        //todo: review und test
        try {
            Database db_Profile = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);
            Query query = QueryBuilder.select(SelectResult.all())
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
        //todo: review und test
        this.deleteProfile(profile.getGoogleID());
    }

    @Override
    public void storeBikeRack(BikeRack bikeRack) {
        //TODO: Review and testing
        try {
            Database db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK); //Get db bikerack

            //convert bikeRack to mutable document
            JSONObject json_bikeRack = new JSONObject(gson.toJson(bikeRack));
            MutableDocument mutableDocument_bikeRack = this.convertJSONToMutableDocument(json_bikeRack);

            //**************************Work around - should work**************************/
            /**/
            Map result = new Gson().fromJson(json_bikeRack.toString(), Map.class);  /**/
            /**/
            MutableDocument mutableDocument_test = new MutableDocument(result);     /**/
            //*****************************************************************************/

            /*
            Testcode, zum Beweis:
            Map<String,Object> map_bikeRack = new HashMap<>();
            map_bikeRack.put("firebaseId","2344");

            Map<String,Object> map_position = new HashMap<>();
            map_position.put("long",12);
            map_position.put("lat",13);
            map_bikeRack.put("pos",map_position);

            MutableDocument mutableDocument_bikeRack2 = new MutableDocument(map_bikeRack);
            Map<String,Object> result = mutableDocument_bikeRack2.toMap();
            */


            /*
             Map<String,Object> map_bikerack = bikeRack.toMap();
                ->toMap() von BikeRack
                    -"FirebaseID" : "23344"

                    -Mappen von Position
                        ->toMap()von Position
                            -latitude
                            -longitude
                            zurückliefern von: {"latitude":12, "longitude":12}
                        <- Rückkehr aus toMap()

                    -"Position":{"latitude":12, "longitude":12} (Value von "Position" ist eine Map)
                    -"Postcode":"89075"
                    -"name":"gustavbikerack"

                    -Mappen von Capacity
                        ->Aufruf von toInt() von Capacity
                        <-zurückliefern: Zahl von 0 bis 2
                    -"capacity":2

                    -"hasBikeCharging":true,
                    -"isCovered":true,
                    -"isExistent":true,
             MutableDocument mutableDocument_bikeRack = this.convertJSONToMutableDocument(json_bikeRack);
             */

            //save mutable document representing the bikeRack to the local db
            couchDB.saveMutableDocumentToDatabase(db_bikerack, mutableDocument_bikeRack);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BikeRack> readBikeRacks(String postcode) {
        //TODO: Review and testing
        List<BikeRack> bikeRacks = new ArrayList<>();
        try {
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
        //TODO: Review and testing
        try {
            Database db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK); //Get db bikerack
            BikeRack bikeRack = null;
            JSONObject jsonObject_result = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_bikerack))
                    .where(Expression.property(BikeRack.ConstantsBikeRack.FIREBASEID.toString()).equalTo(Expression.string(fireBaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                couchDB.deleteDocumentByID(db_bikerack, result.getString(CouchDB.AttributeNames.DATABASE_ID.toText()));
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
        List<Position> positions = new ArrayList<>();
        ResultSet results = couchDB.queryDatabase(Queries.getAllPosQuery);
        results.forEach(result -> {
            try {
                JSONObject json_result = new JSONObject(result.toMap());
                json_result = json_result.getJSONObject(DatabaseNames.DATABASE_POSITION.toText());
                positions.add(gson.fromJson(json_result.toString(), Position.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return positions;
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

    private static class Queries {
        final static Query getAllPosQuery = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.database(CouchDB.getInstance()
                        .getDatabaseFromName(DatabaseNames.DATABASE_POSITION)));
    }
}