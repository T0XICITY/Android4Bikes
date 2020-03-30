package de.thu.tpro.android4bikes.database;

import org.json.JSONObject;

import java.util.Map;

import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public interface JsonRepresentation {
    public JSONObject toJSON() throws InvalidJsonException;
    public Map<String, Object> toMap();
}
