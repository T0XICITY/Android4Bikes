package de.thu.tpro.android4bikes.database;

import org.json.JSONObject;

import java.util.Map;

public interface JsonRepresentation {
    JSONObject getJsonRepresentation();
    Map<String,Object> getMapRepresentation();
}
