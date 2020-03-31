package de.thu.tpro.android4bikes.database;

import android.util.Log;

import com.couchbase.lite.DataSource;
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
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;

public class CouchDbHelper implements Android4BikesDatabaseHelper {
    private CouchDB couchDB;
    private Gson gson;

    public CouchDbHelper() {
        couchDB = CouchDB.getInstance();
        gson = new Gson();
    }

    @Override
    public BikeRack getBikeRack(Position position) {
        BikeRack bikeRack = null;
        try {
            String jsonString = gson.toJson(position);
            JSONObject json_position = new JSONObject(jsonString);
            MutableDocument mutableDocument_position = convertJSONToMutableDocument(json_position);

            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK)))
                    .where(Expression.property("Position").equalTo(Expression.map(mutableDocument_position.toMap())));

            ResultSet results = couchDB.queryDatabase(query);

            for (Result r : results) {
                JSONObject res = new JSONObject(r.toMap());
                Log.d("HalloWelt", "" + res.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bikeRack;
    }

    @Override
    public Profile getProfile(long firebaseID) {
        Profile profile = null;
        JSONObject jsonObject = null;

        profile = gson.fromJson(jsonObject.toString(), Profile.class);
        return profile;
    }

    @Override
    public Track getTrack(long trackID) {
        Track track = null;

        return track;
    }

    @Override
    public HazardAlert getHazardAlert() {
        HazardAlert hazardAlert = null;

        return hazardAlert;
    }

    /**
     * Saves a position in the local Database
     *
     * @param position is the position which should be stored in the local database
     */
    @Override
    public void savePosition(Position position) {
        try {
            JSONObject json_position = new JSONObject(gson.toJson(position));
            MutableDocument md_position = convertJSONToMutableDocument(json_position);
            couchDB.saveMutableDocumentToDatabase(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION), md_position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all positions which are stored in the local database
     *
     * @return list of all positions
     */
    @Override
    public List<Position> getAllPositions() {
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
     * Deletes all entries of position of the local database
     */
    public void deleteAllPositions() {
        ResultSet results = couchDB.queryDatabase(Queries.getAllPosQuery);
        results.forEach(result -> {
            String id = result.getString(0);
            couchDB.deleteDocumentByID(couchDB.getDatabaseFromName(DatabaseNames.DATABASE_POSITION), id);
        });
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
