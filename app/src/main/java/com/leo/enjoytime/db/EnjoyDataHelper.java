package com.leo.enjoytime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leo on 16/3/
 */
public class EnjoyDataHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "enjoy_time";
    private static final int DB_VERSION = 2;

    public static final String TABLE_NAME_ITEM = "ITEM";
    public static final String ITEM_COLUMN_ID = "_ID";
    public static final String ITEM_COLUMN_TYPE = "TYPE";
    public static final String ITEM_COLUMN_URL = "URL";
    public static final String ITEM_COLUMN_CREATE_AT = "CREATE_AT";
    public static final String ITEM_COLUMN_DESC = "DESC";
    public static final String ITEM_COLUMN_FAVOR = "FAVOR_FLAG";
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME_ITEM +" (" +
            ITEM_COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
            ITEM_COLUMN_TYPE +" TEXT," +
            ITEM_COLUMN_URL +" TEXT," +
            ITEM_COLUMN_CREATE_AT + " TEXT," +
            ITEM_COLUMN_DESC +" TEXT," +
            ITEM_COLUMN_FAVOR +" INTEGER)";

    public static final String TABLE_NAME_DIGEST = "DIGEST";
    public static final String ITEM_COLUMN_DIGEST_ID = "_ID";
    public static final String ITEM_COLUMN_DIGEST_TYPE = "TYPE";
    public static final String ITEM_COLUMN_DIGEST_SOURCE_URL = "URL";
    public static final String ITEM_COLUMN_DIGEST_THUMBNAIL = "THUNB_NAIL";
    public static final String ITEM_COLUMN_DIGEST_TITLE = "TITLE";
    public static final String ITEM_COLUMN_DIGEST_SUMMARY = "SUMMARY";
    private static final String CREATE_TABLE_DIGEST_ITEM = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME_DIGEST +" (" +
            ITEM_COLUMN_DIGEST_ID +" INTEGER PRIMARY KEY," +
            ITEM_COLUMN_DIGEST_TYPE +" TEXT," +
            ITEM_COLUMN_DIGEST_SOURCE_URL +" TEXT," +
            ITEM_COLUMN_DIGEST_THUMBNAIL + " TEXT," +
            ITEM_COLUMN_DIGEST_TITLE +" TEXT," +
            ITEM_COLUMN_DIGEST_SUMMARY +" TEXT,"+
            ITEM_COLUMN_FAVOR +" INTEGER)";

    private static EnjoyDataHelper instance;

    public static EnjoyDataHelper getInstance(Context context){
        if (instance == null)
          synchronized (EnjoyDataHelper.class) {
            if (instance == null)
                return new EnjoyDataHelper(context);
          }
        return instance;
    }

    public EnjoyDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_DIGEST_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<newVersion){
            if(newVersion == DB_VERSION){
                upgrade1To2(db);
            }
        }

    }

    private void upgrade1To2(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DIGEST_ITEM);
    }
}
