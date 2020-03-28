package de.thu.tpro.android4bikes.database;

import org.json.JSONObject;

import java.util.Map;

import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public interface JsonRepresentation {
    JSONObject getJsonRepresentation() throws InvalidJsonException;
    Map<String,Object> getMapRepresentation();
}
