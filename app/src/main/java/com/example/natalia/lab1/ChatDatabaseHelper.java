package com.example.natalia.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Natalia on 2016-10-18.
 */
public class ChatDatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "MyDatabase" ;
    public static int VERSION_NUM = 1;
    public static final String TABLE_NAME = "Chats";
    public final static String KEY_ID = "ID";
    public final static String KEY_MESSAGE = "Message";
    Context context;
    public ChatDatabaseHelper(Context ctx ) {

        super(ctx, DATABASE_NAME, null, VERSION_NUM);
        context = ctx;
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i("ChatDatabaseHelper", "Calling onCreate");
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MESSAGE + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
        Log.i("ChatDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion +  "newVersion=" + newVersion);
    }

    public boolean insertData (String message, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        db.beginTransaction();
        contentValues.put(KEY_MESSAGE, message);
        long result = db.insert(TABLE_NAME, null ,contentValues);
        Log.i("ChatDatabaseHelper", "DB Insert Result" + result);
        db.setTransactionSuccessful();
        db.endTransaction();
        if(result == -1)
            return false;
        else
            return true;

    }
    
}

