package de.thu.tpro.android4bikes.util;

import java.util.HashMap;
import java.util.Map;

import de.thu.tpro.android4bikes.data.model.Profile;

public class FirebaseHelper {
    public static Map<String, Object> convertProfileToMap(Profile profile) {
        Map<String, Object> profile_map = new HashMap<>();
        profile_map.put("firstName", profile.getFirstName());
        profile_map.put("familyName", profile.getFamilyName());
        profile_map.put("firebaseAccountID", profile.getFirebaseAccountID());
        profile_map.put("color", profile.getColor());
        profile_map.put("overallDistance", profile.getOverallDistance());
        profile_map.put("achievements", profile.getAchievements());

        return profile_map;
    }
}
