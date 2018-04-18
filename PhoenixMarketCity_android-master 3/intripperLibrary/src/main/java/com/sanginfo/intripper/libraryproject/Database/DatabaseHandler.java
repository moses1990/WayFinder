package com.sanginfo.intripper.libraryproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mosesafonso on 21/02/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WayFinderDatabase";

    // Cars table name
    private static final String TABLE_CUSTOMERSUPPORT = "CustomerSupport";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_PHONE = "Phone";
    private static final String KEY_QWERY = "Qwery";
    private static final String KEY_DATE_TIME = "DateTime";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CUSTOMERSUPPORT_TABLE = "CREATE TABLE " + TABLE_CUSTOMERSUPPORT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_QWERY + " TEXT,"
                + KEY_DATE_TIME + " TEXT" +")";
        db.execSQL(CREATE_CUSTOMERSUPPORT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERSUPPORT);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public  void addDetails(String name,String phone,String qwery) {

        Date currentTime = Calendar.getInstance().getTime();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_QWERY, qwery);;
        values.put(KEY_DATE_TIME, currentTime.toString());

        // Inserting Row
        db.insert(TABLE_CUSTOMERSUPPORT, null, values);
        db.close(); // Closing database connection
    }

    public int getCarsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CUSTOMERSUPPORT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public  void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_CUSTOMERSUPPORT);
        db.close(); // Closing database connection
    }

}