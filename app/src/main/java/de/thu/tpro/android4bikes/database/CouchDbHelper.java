package de.thu.tpro.android4bikes.database;

import android.util.Log;

import com.couchbase.lite.MutableDocument;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.Android4BikesColor;


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

    @Override
    public Position getPosition() {
        Position position = null;

        return position;
    }

    @Override
    public Android4BikesColor getAndroid4BikeColor() {
        Android4BikesColor android4BikesColor = null;

        return android4BikesColor;
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
