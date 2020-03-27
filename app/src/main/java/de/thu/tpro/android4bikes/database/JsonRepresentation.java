package de.thu.tpro.android4bikes.database;

import org.json.JSONObject;

import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public interface JsonRepresentation {
    JSONObject getJsonRepresentation() throws InvalidJsonException;
}
