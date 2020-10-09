package com.mb.memeboss.ui.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/*
        Created by  github.com/nayanraj210401
        This is the DatabaseManagerClass which is used in NotificationFrament to interact with the database.
 */


public class DBManager {
    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }
    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        dbHelper.close();
    }
    public void insert(String jsonObject) {
        ContentValues contentValue = new ContentValues();
//        Log.d("DB HELPER"," Data Added "+jsonObject);
        contentValue.put(DatabaseHelper.MEME_OBJ, jsonObject);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }
    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.MEME_OBJ};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Log.d("DB HELPER"," DataStatus: "+cursor);
        return cursor;
    }

    public boolean isOpen(){
        return database.isOpen();
    }

    public int update(long _id, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.MEME_OBJ, name);
//        contentValues.put(DatabaseHelper.DESC, desc);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }
    public void delete(long _id) {
        Log.d("DELETE","VALUE"+_id);
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}
