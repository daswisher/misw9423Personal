package com.t9hacks.check_in;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Michael V. Swisher on 2/20/2016.
 */
public class DbOps extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dbReader.db";

    public DbOps(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Database operations", "Database created");
    }
    @Override
    public void onCreate(SQLiteDatabase myDb){
        myDb.execSQL(DbData.dbEntry.SQL_CREATE_ENTRIES);
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbData.dbEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldversion, int newVersion) {
        onUpgrade(db, oldversion, newVersion);
    }

    public void insertData(DbOps dop, JSONObject person) throws JSONException {
        SQLiteDatabase db = dop.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbData.dbEntry.COLUMN_NAME_TYPE,person.getString("type"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_ENTRY_ID,person.getString("id"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_KEY,person.getString("key"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_NAME,person.getString("name"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_EMAIL,person.getString("email"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_PHONE,person.getString("phone"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_SHIRT,person.getString("shirt"));
        contentValues.put(DbData.dbEntry.COLUMN_NAME_CHECKEDIN, person.getString("checked_in"));
        long success = db.insert(DbData.dbEntry.TABLE_NAME, null, contentValues);
        Log.d("Database operations", "One row inserted: " + person.getString("name"));
    }

    String[] columns = {
            DbData.dbEntry.COLUMN_NAME_TYPE,
            DbData.dbEntry.COLUMN_NAME_ENTRY_ID,
            DbData.dbEntry.COLUMN_NAME_KEY,
            DbData.dbEntry.COLUMN_NAME_NAME,
            DbData.dbEntry.COLUMN_NAME_EMAIL,
            DbData.dbEntry.COLUMN_NAME_PHONE,
            DbData.dbEntry.COLUMN_NAME_SHIRT,
            DbData.dbEntry.COLUMN_NAME_CHECKEDIN
    };

    public Cursor getData(DbOps dop){
        SQLiteDatabase db = dop.getReadableDatabase();


        Cursor cursor = db.query(DbData.dbEntry.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getRowWithName(DbOps dop, String someName){
        SQLiteDatabase db = dop.getReadableDatabase();
        Cursor cursor = db.query(DbData.dbEntry.TABLE_NAME, columns, "name='"+someName+"'", null, null, null, null);
        Log.d("Searching for", someName);
        //Log.d("Printing table", db.execSQL("select * From registeredPeople;"));
        return cursor;
    }
}
