package de.thu.tpro.android4bikes.util;

import java.util.UUID;

public class UUIDGenerator {
    //compare:
    //https://www.baeldung.com/java-uuid
    public static String generateUUID() {
        String uuid_string = null;
        try {
            UUID uuid = UUID.randomUUID();
            uuid_string = uuid.toString();
            //add some unique data (MAC-adress, timestamp, random number, email address)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid_string;
    }
}
