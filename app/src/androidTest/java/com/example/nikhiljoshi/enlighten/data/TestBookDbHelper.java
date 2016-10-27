package com.example.nikhiljoshi.enlighten.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.nikhiljoshi.enlighten.TestDataOperator;
import com.example.nikhiljoshi.enlighten.data.Contract.BookContract;

import java.util.HashSet;

import static com.example.nikhiljoshi.enlighten.data.Contract.BookContract.BookEntry.*;

/**
 * Created by nikhiljoshi on 4/27/16.
 */
public class TestBookDbHelper extends AndroidTestCase {

    public void setUp() {
        mContext.deleteDatabase(BookDbHelper.DATABASE_NAME);
    }

    /**
     * Test that the 'Book' table is created in 'book.db' database
     */
    public void testCreateBookTable() {
        SQLiteDatabase db = new BookDbHelper(mContext).getWritableDatabase();
        assertTrue(db.isOpen());

        final HashSet<String> tableNameSet = new HashSet<>();
        tableNameSet.add(BookContract.BookEntry.TABLE_NAME);

        final Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created", c.moveToFirst());

        do {
            tableNameSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: This means that the book table was somehow not created", tableNameSet.isEmpty());

        db.close();
    }

    /**
     * Test that all the columns in the book table are present
     */
    public void testAllColumnsPresent() {
        SQLiteDatabase db = new BookDbHelper(mContext).getReadableDatabase();
        assertTrue(db.isOpen());

        final Cursor c = db.rawQuery("PRAGMA table_info(" + BookContract.BookEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that the database has not been created", c.moveToFirst());

        final HashSet<String> bookColumnHashSet = new HashSet<>();
        bookColumnHashSet.add(COLUMN_AUTHOR);
        bookColumnHashSet.add(_ID);
        bookColumnHashSet.add(COLUMN_BOOK_DESCRIPTION);
        bookColumnHashSet.add(COLUMN_COVER_URL);
        bookColumnHashSet.add(COLUMN_NAME);
        bookColumnHashSet.add(COLUMN_PUBLISHED_DATE);
        bookColumnHashSet.add(COLUMN_WEEKS_ON_LIST);
        bookColumnHashSet.add(COLUMN_PUBLISHER);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            final String columnName = c.getString(columnNameIndex);
            bookColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("This means that all the intended columns arent present", bookColumnHashSet.isEmpty());
        db.close();
    }

    /**
     * Insert a piece of data and ensure that it can be retrieved
     */
    public void testInsertAndRetrieveBook() {

        mContext.deleteDatabase(BookDbHelper.DATABASE_NAME);

        // Get some sample book data
        ContentValues contentValues = TestDataOperator.getSampleBook();

        SQLiteDatabase db = new BookDbHelper(mContext).getWritableDatabase();

        // Insert the book data in the book table
        final long bookRowId = db.insert(TABLE_NAME,
                null,
                contentValues);

        // This ensures that the data has been successfully inserted
        assertTrue(bookRowId != -1);

        // Retrieve the information that was just inserted
        final Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_NAME + " = ? ",
                new String[]{TestDataOperator.BOOK_NAME},
                null,
                null,
                null);

        // Ensure that something gets returned?
        assertTrue("Error: This means that the inserted data wasn't successfully retrieved", cursor.moveToFirst());

        // Check the validity of the data
        TestDataOperator.validateCurrentRecord("The data doesnt match", cursor, contentValues);

    }


}
