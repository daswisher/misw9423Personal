package com.t9hacks.check_in;

import android.provider.BaseColumns;

/**
 * Created by Michael V. Swisher on 2/20/2016.
 */
public class DbData {

    public DbData(){}

    public static abstract class dbEntry implements BaseColumns {
        public static final String TABLE_NAME = "registeredPeople";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_SHIRT = "shirt";
        public static final String COLUMN_NAME_CHECKEDIN = "checkedin";
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP =",";
        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+
                dbEntry.TABLE_NAME + //" (" + dbEntry._ID + " INTEGER PRIMARY KEY," +
                dbEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_KEY + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_SHIRT + TEXT_TYPE + COMMA_SEP +
                dbEntry.COLUMN_NAME_CHECKEDIN + TEXT_TYPE +
                " )";
        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + dbEntry.TABLE_NAME;
    }
}
