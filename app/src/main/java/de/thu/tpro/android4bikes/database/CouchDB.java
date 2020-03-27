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

import de.thu.tpro.android4bikes.util.GlobalContext;

public class CouchDB implements Andoid4BikeDatabse {
    public static final String DATABASENAME = "MYDB";
    public static final String COUCHBASELITEID = "id";
    private static CouchDB instance;
    private Database database;

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
        if (instance == null) {
            instance = new CouchDB(GlobalContext.getContext());
        }
        return instance;
    }

    /**
     * prepare db for access
     */
    private void prepareCouchDB(Context context) {
        // Get the database (and create it if it doesn’t exist).
        CouchbaseLite.init(context);
        DatabaseConfiguration config = new DatabaseConfiguration();
        this.database = null;
        try {
            this.database = new Database(CouchDB.DATABASENAME, config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d("HalloWelt", "Failure during creation of the database!");
        }
    }

    /**
     * dave a given mutable document to the database
     * a mutable document is a document that allows modifications/updates
     *
     * @param mutableDoc document to save
     */
    public void saveMutableDocumentToDatabase(MutableDocument mutableDoc) {
        // Save it to the database.
        try {
            database.save(mutableDoc, ConcurrencyControl.LAST_WRITE_WINS);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d("HalloWelt", "Failure during saving a document.");
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
    public Document updateSingleValueOfADocument(MutableDocument mutableDoc, String key, String value) {
        MutableDocument document = null;
        // Update a document.
        if (mutableDoc != null) {
            mutableDoc = database.getDocument(mutableDoc.getId()).toMutable();
            mutableDoc.setString(key, value);
            saveMutableDocumentToDatabase(mutableDoc);
        }
        document = database.getDocument(mutableDoc.getId()).toMutable();
        return document;
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public MutableDocument updateSingleValueOfADocument(MutableDocument mutableDoc, String key, int value) {
        MutableDocument document = null;

        // Update a document.
        if (mutableDoc != null) {
            mutableDoc = database.getDocument(mutableDoc.getId()).toMutable();
            mutableDoc.setInt(key, value);
            saveMutableDocumentToDatabase(mutableDoc);
        }
        document = database.getDocument(mutableDoc.getId()).toMutable();
        return document;
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public MutableDocument updateSingleValueOfADocument(MutableDocument mutableDoc, String key, long value) {
        MutableDocument document = null;
        // Update a document.
        if (mutableDoc != null) {
            mutableDoc = database.getDocument(mutableDoc.getId()).toMutable();
            mutableDoc.setLong(key, value);
            saveMutableDocumentToDatabase(mutableDoc);
        }
        document = database.getDocument(mutableDoc.getId()).toMutable();
        return document;
    }

    /**
     * updates a single value regarding a certain document
     *
     * @param mutableDoc document to update
     * @param key        key to update
     * @param value      new value
     * @return updated document
     */
    public MutableDocument updateSingleValueOfADocument(MutableDocument mutableDoc, String key, double value) {
        MutableDocument document = null;
        // Update a document.
        if (mutableDoc != null) {
            mutableDoc = database.getDocument(mutableDoc.getId()).toMutable();
            mutableDoc.setDouble(key, value);
            saveMutableDocumentToDatabase(mutableDoc);
        }
        document = database.getDocument(mutableDoc.getId()).toMutable();
        return document;
    }

    /**
     * read a single document contained by the database
     *
     * @param documentID unique id of the contained document
     * @return read document or null
     */
    public MutableDocument readDocumentByID(String documentID) {
        MutableDocument document = database.getDocument(documentID).toMutable();
        return document;
    }


    /**
     * returns the number of stored elements
     *
     * @return number of stored elements as a long value
     */
    public long getNumberOfStoredDocuments() {
        long numberOfStoredElements = database.getCount();
        return numberOfStoredElements;
    }

    /**
     * get resultset representing all documents stored in the database
     *
     * @return ResultSet
     */
    public ResultSet getAllStoredIds() {
        ResultSet results = null;

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database));
        try {
            results = query.execute();
            Log.d("HalloWelt", query.explain());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(String key, String value) {
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

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(String key, int value) {
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

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(String key, long value) {
        ResultSet result = null;
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property(key).equalTo(Expression.longValue(value)));
        try {
            result = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * query database using a given attribute
     *
     * @param key   attribute used for the query
     * @param value associated value
     */
    public ResultSet queryDatabaseForSingleAttributeValue(String key, double value) {
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

    /**
     * query database using a given attribute
     *
     * @param query database
     * @return ResultSet
     */
    public ResultSet queryDatabase(Query query) {
        ResultSet results = null;
        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * deletes a specified document
     *
     * @param id unique of the document to delete
     */
    public void deleteDocumentByID(String id) {
        Document document = readDocumentByID(id);
        try {
            database.delete(document, ConcurrencyControl.LAST_WRITE_WINS);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d("HalloWelt", "couldn't delete docment");
        }
    }

    /**
     * deletes all documents contained by the db
     */
    public void clearDB() {
        try {
            ResultSet results = getAllStoredIds();
            for (Result res : results) {
                deleteDocumentByID(res.getString(CouchDB.COUCHBASELITEID));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("HalloWelt", "couldn't delete docment");
        }
    }

    /**
     * close db connection
     */
    public void closeDBConnection() {
        try {
            database.close();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.d("HalloWelt", "Database couln't be closed!");
        }
    }
}
