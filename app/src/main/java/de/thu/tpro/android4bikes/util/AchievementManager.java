package de.thu.tpro.android4bikes.util;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

public class AchievementManager {
    private static AchievementManager instance;
    private CouchDBHelper ownDataDB;
    private AchievementManager(){
        ownDataDB = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
    }

    public static AchievementManager getInstance(){
        if (instance == null){
            instance = new AchievementManager();
        }
        return instance;
    }

    public void checkIfKmAchievementIsReached(){
        FirebaseConnection.getInstance().readAllKmAchievements();
    }

    public void checkIfKmAchievementIsReached(List<KmAchievement> achievements) {
        Profile profile = ownDataDB.readMyOwnProfile();
        int ownOverallKM = profile.getOverallDistance();
        List<Achievement> ownAchievements = profile.getAchievements();
        List<KmAchievement> ownKmAchievements = new ArrayList<>();
        ownAchievements.forEach(entry ->{
            if (entry instanceof KmAchievement){
                ownKmAchievements.add((KmAchievement) entry);
            }
        });
        boolean thereIsSomethingNew = false;
        for (KmAchievement a: achievements) {
            if (!ownKmAchievements.contains(a)){
                if (a.isAchieved(ownOverallKM)){
                    thereIsSomethingNew = true;
                    profile.addAchievement(a);
                }
            }
        }
        if (thereIsSomethingNew){
            CouchWriteBuffer.getInstance().updateProfile(profile);
        }
    }
}
