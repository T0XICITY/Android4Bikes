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

public class CouchDBHelper implements LocalDatabaseHelper {
    private CouchDB couchDB;
    private Gson gson;

    public CouchDBHelper() {
        couchDB = CouchDB.getInstance();
        gson = new Gson();
    }

    @Override
    public void deleteBikeRack(BikeRack bikeRack) {
        //TODO: Review and testing
        this.deleteBikeRack(bikeRack.getFirebaseID());
    }

    @Override
    public void storeTrack(Track track) {

    }

    @Override
    public List<Track> readTracks(String postcode) {
        return null;
    }

    @Override
    public void deleteTrack(String fireBaseID) {

    }

    @Override
    public void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions) {
        //TODO: Review and testing
        try {
            Database db_fineGrainedPositions = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_FINEGRAINEDPOSITIONS); //Get db bikerack

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
            Database db_finegrainedpositions = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_FINEGRAINEDPOSITIONS); //Get db bikerack

            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_finegrainedpositions))
                    .where(Expression.property(HazardAlert.ConstantsHazardAlert.FIREBASEID.toString()).equalTo(Expression.string(firebaseID)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                //convert result to jsonObject-string
                jsonObject_result = new JSONObject(result.toMap());
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
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db bikerack

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
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db bikerack
            HazardAlert hazardAlert = null;

            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(db_hazardAlert))
                    .where(Expression.property(HazardAlert.ConstantsHazardAlert.POSTCODE.toString()).equalTo(Expression.string(postcode)));
            ResultSet results = couchDB.queryDatabase(query);
            for (Result result : results) {
                //convert result to jsonObject-string
                jsonObject_result = new JSONObject(result.toMap());
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
            Database db_hazardAlert = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT); //Get db bikerack
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
        //todo: Nach 50 einträgen alles an firebase und db leeren
        try {
            JSONObject json_position = new JSONObject(gson.toJson(position));
            MutableDocument md_position = convertJSONToMutableDocument(json_position);
            couchDB.saveMutableDocumentToDatabase(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION), md_position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all entries of position of the local database
     */
    @Override
    public void resetUtilization() {
        ResultSet results = couchDB.queryDatabase(Queries.getAllPosQuery);
        results.forEach(result -> {
            String id = result.getString(0);
            couchDB.deleteDocumentByID(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION), id);
        });
    }

    @Override
    public void storeProfile(Profile Profile) {

    }

    @Override
    public Profile readProfile(String firebaseAccountID) {
        return null;
    }

    @Override
    public void updateProfile(Profile profile) {

    }

    @Override
    public void deleteProfile(String googleID) {

    }

    @Override
    public void deleteProfile(Profile profile) {

    }

    @Override
    public void storeBikeRack(BikeRack bikeRack) {
        //TODO: Review and testing
        try {
            Database db_bikerack = couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK); //Get db bikerack

            //convert bikeRack to mutable document
            JSONObject json_bikeRack = new JSONObject(gson.toJson(bikeRack));
            MutableDocument mutableDocument_bikeRack = this.convertJSONToMutableDocument(json_bikeRack);

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