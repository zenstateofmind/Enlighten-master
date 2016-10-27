package com.example.nikhiljoshi.enlighten.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.nikhiljoshi.enlighten.data.Contract.BookContract.BookEntry.*;

/**
 * This class contains the sql statements that create the 'book' table in
 * 'book.db' database
 */
public class BookDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "book.db";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_BOOK_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_COVER_URL + " TEXT NOT NULL, " +
                COLUMN_PUBLISHED_DATE + " TEXT NOT NULL, " +
                COLUMN_PUBLISHER + " TEXT NOT NULL, " +
                COLUMN_WEEKS_ON_LIST + " TEXT NOT NULL " +
                ");";

        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No changes need to be made at this point in time
        // When you upgrade
    }
}
