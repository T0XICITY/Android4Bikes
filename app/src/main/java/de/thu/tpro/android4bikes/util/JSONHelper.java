package de.thu.tpro.android4bikes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Helper class to handle the conversions between objects and JSON
 *
 * @param <T> Type of the objects to convert.
 */
public class JSONHelper<T> {

    private Gson gson;
    private Class<T> type;

    public JSONHelper(Class<T> type) {
        this.type = type;
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public JSONObject convertObjectToJSONObject(T object) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(convertObjectToJSONString(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String convertObjectToJSONString(T object) {
        String jsonObjectString = null;
        try {
            jsonObjectString = gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObjectString;
    }

    public T convertJSONObjectToObject(JSONObject jsonObject) {
        T object = null;
        try {
            String jsonObect_String = jsonObject.toString();
            object = convertJSONStringToObject(jsonObect_String);
        } catch (Exception e) {

        }
        return object;
    }

    public T convertJSONStringToObject(String jsonString) {
        T object = null;
        try {
            object = gson.fromJson(jsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

}
