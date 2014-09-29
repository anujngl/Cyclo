package com.mabook.android.cyclo.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sng2c on 2014. 9. 25..
 */

public class CycloDatabase {
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "Cyclo";

    public static String TABLE_CONTROL_LOG = "CONTROL_LOG";
    public static String TABLE_SESSION = "SESSION";
    public static String TABLE_TRACK = "TRACK";

    private final Context mContext;
    private final SQLiteDatabase mDatabase;

    public CycloDatabase(Context context) {
        mContext = context.getApplicationContext();
        DBHelper opener = new DBHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        mDatabase = opener.getWritableDatabase();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long insertControlLog(String packageName, String appName, String message, String result) {
        ContentValues values = new ContentValues();
        values.put("package_name", packageName);
        values.put("app_name", appName);
        values.put("message", message);
        values.put("result", result);
        values.put("regtime", getDateTime());
        return mDatabase.insert(TABLE_CONTROL_LOG, null, values);
    }

    public long insertSession(String packageName, String appName, String sessionName) {
        ContentValues values = new ContentValues();
        values.put("package_name", packageName);
        values.put("app_name", appName);
        values.put("session_name", sessionName);
        values.put("start_time", getDateTime());
        return mDatabase.insert(TABLE_SESSION, null, values);
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public int updateSessionStop(long sessionId) {
        ContentValues values = new ContentValues();
        values.put("end_time", getDateTime());
        return mDatabase.update(TABLE_SESSION, values, "session_id = ?", new String[]{String.valueOf(sessionId)});
    }

    public int updateSessionName(int sessionId, String sessionName) {
        ContentValues values = new ContentValues();
        values.put("session_name", sessionName);
        return mDatabase.update(TABLE_SESSION, values, "session_id = ?", new String[]{String.valueOf(sessionId)});
    }

    public long insertTrack(long sessionId, double acc, double lat, double lng, double alt, double speed) {
        ContentValues values = new ContentValues();
        values.put("session_id", sessionId);
        values.put("regtime", getDateTime());
        values.put("acc", acc);
        values.put("lat", lat);
        values.put("lng", lng);
        values.put("alt", alt);
        values.put("speed", speed);
        return mDatabase.insert(TABLE_TRACK, null, values);
    }

    public void close() {
        mDatabase.close();
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE \"CONTROL_LOG\" (\"log_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"package_name\" VARCHAR, \"app_name\" VARCHAR, \"message\" VARCHAR, \"result\" VARCHAR, \"regtime\" DATETIME)");
            sqLiteDatabase.execSQL("CREATE TABLE \"SESSION\" (\"session_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"package_name\" VARCHAR, \"app_name\" VARCHAR, \"session_name\" VARCHAR, \"start_time\" DATETIME, \"end_time\" DATETIME)");
            sqLiteDatabase.execSQL("CREATE TABLE \"TRACK\" (\"track_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"session_id\" INTEGER, \"regtime\" DATETIME, \"acc\" DOUBLE, \"lat\" DOUBLE, \"lng\" DOUBLE, \"alt\" DOUBLE, \"speed\" DOUBLE)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // nothing yet
        }
    }

}
