package com.scholarscraper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * // -------------------------------------------------------------------------
 * /** Handle reading, adding, and deleting items to the database
 * 
 * @author Alex Lamar, Paul Yea, Brianna Beitzel
 * @version May 5, 2013
 */
public class DbHelper
    extends SQLiteOpenHelper
{
    private static final int    DATABASE_VERSION = 1;
    private static final String DATABASE_NAME    = "AlarmIdManager";
    private static final String TABLE_CONTACTS   = "alarmIds";
    private static final String KEY_ID           = "id";


    // ----------------------------------------------------------
    /**
     * Create a new DbHelper object.
     * 
     * @param context
     */
    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID
                + " INTEGER PRIMARY KEY" + ")";
        database.execSQL(CREATE_CONTACTS_TABLE);

    }


    @Override
    public void onUpgrade(
        SQLiteDatabase database,
        int oldVersion,
        int newVersion)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(database);

    }


    // ----------------------------------------------------------
    /**
     * Add an alarm to the database
     * 
     * @param id
     *            AlarmId that is being added to the databse
     */
    public void addAlarmId(AlarmId id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id.getAlarmId());
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    // ----------------------------------------------------------
    /**
     * return arrayList that contains all AlarmId in the database
     * 
     * @return arrayList that contains all AlarmId in the database
     */
    public List<AlarmId> getAllIds()
    {
        List<AlarmId> contactList = new ArrayList<AlarmId>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do
            {
                AlarmId contact = new AlarmId();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                // Adding contact to list
                contactList.add(contact);
            }
            while (cursor.moveToNext());
        }
        return contactList;
    }


    // ----------------------------------------------------------
    /**
     * Delete an alarm from the database
     * 
     * @param id
     *            AlarmId that is being removed from the database
     */
    public void deleteAlarmId(AlarmId id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
            TABLE_CONTACTS,
            KEY_ID + " = ?",
            new String[] { String.valueOf(id.getAlarmId()) });
        db.close();
    }

}
