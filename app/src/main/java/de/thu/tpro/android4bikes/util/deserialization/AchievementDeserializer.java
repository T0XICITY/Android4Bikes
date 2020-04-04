package de.thu.tpro.android4bikes.util.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

/**
 * AchievementDeserializer is used to deserialize {@link com.google.gson.JsonElement} which is of type {@link de.thu.tpro.android4bikes.data.achievements.Achievement}.
 * It is necessary to have an own implementation of that because of the inheritance of  Achievements.
 * By default it is not possible to deserialize with inheritance. This implementation makes it possible.
 */
public class AchievementDeserializer<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive classNamePrimitive = (JsonPrimitive) jsonObject.get("classname");
        String className = classNamePrimitive.getAsString();

        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
        return context.deserialize(jsonObject, clazz);
    }
}
