package de.thu.tpro.android4bikes.database;

import android.util.Log;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
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

    }

    @Override
    public void storeTrack(Track track) {

    }

    @Override
    public void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions) {

    }

    @Override
    public List<Track> readTracks(String postcode) {
        return null;
    }

    @Override
    public void deleteTrack(String fireBaseID) {

    }

    @Override
    public FineGrainedPositions readFineGrainedPositions(String firebaseID) {
        return null;
    }

    @Override
    public FineGrainedPositions readFineGrainedPositions(Track track) {
        return null;
    }

    @Override
    public void storeHazardAlerts(HazardAlert hazardAlert) {

    }

    @Override
    public List<HazardAlert> readHazardAlerts(String postcode) {
        return null;
    }

    @Override
    public void deleteHazardAlert(String fireBaseID) {

    }

    @Override
    public void deleteHazardAlert(HazardAlert hazardAlert) {

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

    }

    @Override
    public List<BikeRack> readBikeRacks(String postcode) {
        List<BikeRack> bikeRacks = null;
        try {
            throw new JSONException("TEST");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bikeRacks;
    }

    @Override
    public void deleteBikeRack(String fireBaseID) {

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

    @Override
    public Position getLastPosition() {
        return null;
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