package de.thu.tpro.android4bikes.database;


import android.content.Context;
import android.util.Log;

import com.couchbase.lite.ConcurrencyControl;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.lang.reflect.Field;
import java.util.HashMap;

import de.thu.tpro.android4bikes.util.GlobalContext;

/**
 * {@link de.thu.tpro.android4bikes.database.CouchDB} is the representation regarding the couchbase lite database.
 * With an instance of this class it's possible to perform the basic operations on the local database.
 */
public class CouchDB {
    private static CouchDB instance;

    private Database database_achievement;
    private Database database_bikerack; //for storing positions of a certain user when he is offline
    private Database database_hazardalert;
    private Database database_position;
    private Database database_profile;
    private Database database_rating;
    private Database database_track;
    private Database database_writeBuffer_bikerack;
    private Database database_writeBuffer_hazardalert;
    private Database database_writeBuffer_position;
    private Database database_writeBuffer_profile;
    private Database database_writeBuffer_track;
    private Database database_ownData_bikerack;
    private Database database_ownData_hazardalert;
    private Database database_ownData_position;
    private Database database_ownData_profile;
    private Database database_ownData_track;

    private Database database_deleteBuffer_profile;
    private Database database_deleteBuffer_track;

    /**
     * constructor
     *
     * @param context necessary for preparing the database
     */
    private CouchDB(Context context) {
        prepareCouchDB(context);
    }

    /**
     * realisation of the singleton pattern
     *
     * @return instance of the class 'CouchBaseLiteDBHelper'
     */
    public static CouchDB getInstance() {
        synchronized (GlobalContext.getContext()) {
            if (instance == null) {
                instance = new CouchDB(GlobalContext.getContext());
            }
            return instance;
        }
    }

    /**
     * prepare db for access. Create all necessary databases.
     */
    private void prepareCouchDB(Context context) {
        // Get the database (and create it if it doesn’t exist).
        CouchbaseLite.init(context);
        DatabaseConfiguration config = new DatabaseConfiguration();
        this.database_achievement = null;
        this.database_bikerack = null;
        this.database_hazardalert = null;
        this.database_position = null;
        this.database_profile = null;
        this.database_rating = null;
        this.database_track = null;

        this.database_writeBuffer_bikerack = null;
        this.database_writeBuffer_hazardalert = null;
        this.database_writeBuffer_position = null;
        this.database_writeBuffer_profile = null;
        this.database_writeBuffer_track = null;

        this.database_ownData_bikerack = null;
        this.database_ownData_hazardalert = null;
        this.database_ownData_position = null;
        this.database_ownData_profile = null;
        this.database_ownData_track = null;

        this.database_deleteBuffer_profile = null;
        this.database_deleteBuffer_track = null;


        try {
            this.database_achievement = new Database(DatabaseNames.DATABASE_ACHIEVEMENT.toText(), config);
            this.database_bikerack = new Database(DatabaseNames.DATABASE_BIKERACK.toText(), config);
            this.database_hazardalert = new Database(DatabaseNames.DATABASE_HAZARD_ALERT.toText(), config);
            this.database_profile = new Database(DatabaseNames.DATABASE_PROFILE.toText(), config);
            this.database_rating = new Database(DatabaseNames.DATABASE_RATING.toText(), config);
            this.database_track = new Database(DatabaseNames.DATABASE_TRACK.toText(), config);

            this.database_writeBuffer_bikerack = new Database(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK.toText(), config);
            this.database_writeBuffer_hazardalert = new Database(DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT.toText(), config);
            this.database_writeBuffer_position = new Database(DatabaseNames.DATABASE_WRITEBUFFER_POSITION.toText(), config);
            this.database_writeBuffer_profile = new Database(DatabaseNames.DATABASE_WRITEBUFFER_PROFILE.toText(), config);
            this.database_writeBuffer_track = new Database(DatabaseNames.DATABASE_WRITEBUFFER_TRACK.toText(), config);

            this.database_ownData_bikerack = new Database(DatabaseNames.DATABASE_OWNDATA_BIKERACK.toText(), config);
            this.database_ownData_hazardalert = new Database(DatabaseNames.DATABASE_OWNDATA_HAZARD_ALERT.toText(), config);
            this.database_ownData_profile = new Database(DatabaseNames.DATABASE_OWNDATA_PROFILE.toText(), config);
            this.database_ownData_track = new Database(DatabaseNames.DATABASE_OWNDATA_TRACK.toText(), config);

            this.database_deleteBuffer_track = new Database(DatabaseNames.DATABASE_DELETEBUFFER_TRACK.toText(), config);
            this.database_deleteBuffer_profile = new Database(DatabaseNames.DATABASE_DELETEBUFFER_PROFILE.toText(), config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.e("HalloWelt", "Failure during creation of the database!");
        }
    }

    /**
     * get a specified database object
     *
     * @param databaseName name of the database
     * @return database object
     */
    public Database getDatabaseFromName(DatabaseNames databaseName) {
        Database db = null;
        switch (databaseName) {
            case DATABASE_ACHIEVEMENT:
                db = database_achievement;
                break;
            case DATABASE_BIKERACK:
                db = database_bikerack;
                break;
            case DATABASE_HAZARD_ALERT:
                db = database_hazardalert;
                break;
            case DATABASE_PROFILE:
                db = database_profile;
                break;
            case DATABASE_RATING:
                db = database_rating;
                break;
            case DATABASE_TRACK:
                db = database_track;
                break;
            case DATABASE_WRITEBUFFER_BIKERACK:
                db = database_writeBuffer_bikerack;
                break;
            case DATABASE_WRITEBUFFER_HAZARD_ALERT:
                db = database_writeBuffer_hazardalert;
                break;
            case DATABASE_WRITEBUFFER_POSITION:
                db = database_writeBuffer_position;
                break;
            case DATABASE_WRITEBUFFER_PROFILE:
                db = database_writeBuffer_profile;
                break;
            case DATABASE_WRITEBUFFER_TRACK:
                db = database_writeBuffer_track;
                break;
            case DATABASE_OWNDATA_BIKERACK:
                db = database_ownData_bikerack;
                break;
            case DATABASE_OWNDATA_HAZARD_ALERT:
                db = database_ownData_hazardalert;
                break;
            case DATABASE_OWNDATA_PROFILE:
                db = database_ownData_profile;
                break;
            case DATABASE_OWNDATA_TRACK:
                db = database_ownData_track;
                break;
            case DATABASE_DELETEBUFFER_PROFILE:
                db = database_deleteBuffer_profile;
                break;
            case DATABASE_DELETEBUFFER_TRACK:
                db = database_deleteBuffer_track;
                break;
        }
        return db;
    }

    /**
     * save a given mutable document to the database
     * a mutable document is a document that allows modifications/updates
     *
     * @param mutableDoc document to save
     */
    public void saveMutableDocumentToDatabase(Database database, MutableDocument mutableDoc) {
        synchronized (this) {
            // Save it to the database.
            try {
                database.save(mutableDoc, ConcurrencyControl.LAST_WRITE_WINS);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during saving a document.");
            }
        }
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public Document updateSingleValueOfADocument(Database database, MutableDocument mutableDoc, String key, String value) {
        synchronized (this) {
            MutableDocument document = null;
            try {
                // Update a document.
                if (mutableDoc != null) {
                    mutableDoc = database.getDocument(mutableDoc.getId()).toMutable(); //read doc from db
                    deleteDocumentByID(database, mutableDoc.getId()); //delete document from db
                    mutableDoc.setString(key, value); //update value
                    saveMutableDocumentToDatabase(database, mutableDoc); //save updated doc to db
                }
                document = database.getDocument(mutableDoc.getId()).toMutable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during updating a document (string value).");
            }
            return document;
        }
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public Document updateSingleValueOfADocument(Database database, MutableDocument mutableDoc, String key, int value) {
        synchronized (this) {
            MutableDocument document = null;
            try {
                // Update a document.
                if (mutableDoc != null) {
                    mutableDoc = database.getDocument(mutableDoc.getId()).toMutable(); //read doc from db
                    deleteDocumentByID(database, mutableDoc.getId()); //delete document from db
                    mutableDoc.setInt(key, value); //update value
                    saveMutableDocumentToDatabase(database, mutableDoc); //save updated doc to db
                }
                document = database.getDocument(mutableDoc.getId()).toMutable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during updating a document (int value).");
            }
            return document;
        }
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public Document updateSingleValueOfADocument(Database database, MutableDocument mutableDoc, String key, long value) {
        synchronized (this) {
            MutableDocument document = null;
            try {
                // Update a document.
                if (mutableDoc != null) {
                    mutableDoc = database.getDocument(mutableDoc.getId()).toMutable(); //read doc from db
                    deleteDocumentByID(database, mutableDoc.getId()); //delete document from db
                    mutableDoc.setLong(key, value); //update value
                    saveMutableDocumentToDatabase(database, mutableDoc); //save updated doc to db
                }
                document = database.getDocument(mutableDoc.getId()).toMutable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during updating a document (long value).");
            }
            return document;
        }
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public Document updateSingleValueOfADocument(Database database, MutableDocument mutableDoc, String key, double value) {
        synchronized (this) {
            MutableDocument document = null;
            try {
                // Update a document.
                if (mutableDoc != null) {
                    mutableDoc = database.getDocument(mutableDoc.getId()).toMutable(); //read doc from db
                    deleteDocumentByID(database, mutableDoc.getId()); //delete document from db
                    mutableDoc.setDouble(key, value); //update value
                    saveMutableDocumentToDatabase(database, mutableDoc); //save updated doc to db
                }
                document = database.getDocument(mutableDoc.getId()).toMutable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during updating a document (double value).");
            }
            return document;
        }
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public Document updateSingleValueOfADocument(Database database, MutableDocument mutableDoc, String key, MutableDocument value) {
        synchronized (this) {
            MutableDocument document = null;
            try {
                // Update a document.
                if (mutableDoc != null) {
                    mutableDoc = database.getDocument(mutableDoc.getId()).toMutable(); //read doc from db
                    deleteDocumentByID(database, mutableDoc.getId()); //delete document from db
                    mutableDoc.setValue(key, mutableDoc); //update value
                    saveMutableDocumentToDatabase(database, mutableDoc); //save updated doc to db
                }
                document = database.getDocument(mutableDoc.getId()).toMutable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during updating a document (subdocument).");
            }
            return document;
        }
    }

    /**
     * updates all values based
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param newValues  Mutable document containing all new key value pairs
     * @return updated document
     */
    public Document updateAllValuesOfADocument(Database database, MutableDocument mutableDoc, String key, HashMap<String, Object> newValues) {
        synchronized (this) {
            MutableDocument document = null;
            try {
                // Update a document.
                if (mutableDoc != null) {
                    mutableDoc = database.getDocument(mutableDoc.getId()).toMutable(); //read doc from db
                    deleteDocumentByID(database, mutableDoc.getId()); //delete document from db
                    mutableDoc.setData(newValues); //update value
                    saveMutableDocumentToDatabase(database, mutableDoc); //save updated doc to db
                }
                document = database.getDocument(mutableDoc.getId()).toMutable();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HalloWelt", "Failure during updating a complete document.");
            }
            return document;
        }
    }


    /**
     * read a single document contained by the database
     *
     * @param documentID unique id of the contained document
     * @return read document or null
     */
    public MutableDocument readDocumentByID(Database database, String documentID) {
        synchronized (this) {
            MutableDocument document = database.getDocument(documentID).toMutable();
            return document;
        }
    }

    /**
     * returns the number of stored elements
     *
     * @return number of stored elements as a long value
     */
    public long getNumberOfStoredDocuments(Database database) {
        synchronized (this) {
            long numberOfStoredElements = database.getCount();
            return numberOfStoredElements;
        }
    }

    /**
     * get resultset representing all documents stored in the database
     *
     * @return ResultSet
     */
    public ResultSet getAllStoredIds(Database database) {
        synchronized (this) {
            ResultSet results = null;
            try {
                Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                        .from(DataSource.database(database));
                results = query.execute();
                //Log.d("HalloWelt", query.explain());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(Database database, String key, String value) {
        synchronized (this) {
            ResultSet result = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(Expression.property(key).equalTo(Expression.string(value)));
            try {
                result = query.execute();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(Database database, String key, int value) {
        synchronized (this) {
            ResultSet result = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(Expression.property(key).equalTo(Expression.intValue(value)));
            try {
                result = query.execute();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(Database database, String key, long value) {
        synchronized (this) {
            ResultSet result = null;
            try {
                Query query = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(database))
                        .where(Expression.property(key).equalTo(Expression.longValue(value)));
                result = query.execute();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * @param database Database to query
     * @param key      Key that shoul be considered
     * @param regex    regular expression regarding the value
     * @return ResultSet
     */
    public ResultSet queryDatabaseForRegularExpression(Database database, String key, String regex) {
        synchronized (this) {
            ResultSet result = null;
            try {
                Query query = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(database))
                        .where(Expression.property(key).regex(Expression.string(regex)));
                result = query.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(Database database, String key, double value) {
        synchronized (this) {
            ResultSet results = null;
            Query query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(Expression.property(key).equalTo(Expression.doubleValue(value)));
            try {
                results = query.execute();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    /**
     * query database using a given attribute
     *
     * @param query database
     * @return ResultSet
     */
    public ResultSet queryDatabase(Query query) {
        synchronized (this) {
            ResultSet results = null;
            try {
                results = query.execute();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    /**
     * deletes a specified document
     *
     * @param id unique of the document to delete
     */
    public void deleteDocumentByID(Database database, String id) {
        synchronized (this) {
            Document document = readDocumentByID(database, id);
            try {
                database.delete(document, ConcurrencyControl.LAST_WRITE_WINS);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                ////Log.d("HalloWelt", "couldn't delete docment");
            }
        }
    }

    /**
     * deletes all documents contained by the db
     */
    public void clearDB(Database database) {
        synchronized (this) {
            try {
                ResultSet results = getAllStoredIds(database);
                for (Result res : results) {
                    deleteDocumentByID(database, res.getString(AttributeNames.DATABASE_ID.toText()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                ////Log.d("HalloWelt", "couldn't delete docment");
            }
        }
    }

    /**
     * returns all documents that are stored in a specified database
     *
     * @param database
     * @return
     */
    public ResultSet readAllDocumentsOfADatabase(Database database) {
        synchronized (this) {
            ResultSet results = null;
            try {
                Query query = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(database));
                results = query.execute();
                ////Log.d("HalloWelt", query.explain());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    /**
     * close db connection
     */
    public void closeDBConnection(Database database) {
        synchronized (this) {
            try {
                database.close();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                ////Log.d("HalloWelt", "Database couln't be closed!");
            }
        }
    }

    /**
     * Method which returns a Document
     *
     * @param id       is the ID of the wanted document
     * @param database is the Database where the wanted document is stored
     */
    public Document getDocument(DatabaseNames database, String id) {
        synchronized (this) {
            return getDatabaseFromName(database).getDocument(id);
        }
    }

    /**
     * reset the content of each existing database
     */
    public void clearAllDatabases() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(Database.class)) {
                    clearDB((Database) field.get(this));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public enum DatabaseNames {
        //Databases:
        DATABASE_ACHIEVEMENT("achievementdb"),
        DATABASE_BIKERACK("bikerackdb"),
        DATABASE_HAZARD_ALERT("hazardalertsdb"),
        DATABASE_PROFILE("profiledb"),
        DATABASE_RATING("ratingdb"),
        DATABASE_TRACK("trackdb"),
        DATABASE_WRITEBUFFER_BIKERACK("writebufferbikerackdb"),
        DATABASE_WRITEBUFFER_HAZARD_ALERT("writebufferhazardalertsdb"),
        DATABASE_WRITEBUFFER_POSITION("writebufferpositiondb"),
        DATABASE_WRITEBUFFER_PROFILE("writebufferprofiledb"),
        DATABASE_WRITEBUFFER_TRACK("writebuffertrackdb"),
        DATABASE_OWNDATA_BIKERACK("owndatabikerackdb"),
        DATABASE_OWNDATA_HAZARD_ALERT("owndatahazardalertsdb"),
        DATABASE_OWNDATA_PROFILE("owndataprofiledb"),
        DATABASE_OWNDATA_TRACK("owndatatrackdb"),
        DATABASE_DELETEBUFFER_PROFILE("deletebufferprofiledb"),
        DATABASE_DELETEBUFFER_TRACK("deletebuffertrackdb");

        private String name;

        DatabaseNames(String type) {
            this.name = type;
        }

        public String toText() {
            return name;
        }
    }

    public enum AttributeNames {
        //Databases:
        DATABASE_ID("id");
        private String name;

        AttributeNames(String type) {
            this.name = type;
        }

        public String toText() {
            return name;
        }
    }
}
