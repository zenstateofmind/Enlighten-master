package com.example.nikhiljoshi.enlighten.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.nikhiljoshi.enlighten.UtilityForTest;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;

import java.util.HashSet;

import static com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract.*;

/**
 * Created by nikhiljoshi on 6/3/16.
 */
public class TestEnlightenDbHelper extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase(EnlightenDbHelper.DATABASE_NAME);
    }

    /**
     * Test to ensure that the 'Friend' table was created
     */
    public void testTableCreation(){

        HashSet<String> tableNames = new HashSet<>();
        tableNames.add(EnlightenContract.FriendEntry.TABLE_NAME);
        tableNames.add(PackEntry.TABLE_NAME);

        SQLiteDatabase db = new EnlightenDbHelper(mContext).getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Tables have not been created", cursor.moveToFirst());

        do {
            tableNames.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        assertTrue("Certain tables have not been created", tableNames.isEmpty());

        db.close();
    }

    /**
     * Test to ensure that all the columns in the friend database exist
     */
    public void testAllFriendColumnsPresent() {

        HashSet<String> friendColumns = new HashSet<>();
        friendColumns.add(FriendEntry._ID);
        friendColumns.add(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID);
        friendColumns.add(FriendEntry.COLUMN_USER_ID);
        friendColumns.add(FriendEntry.COLUMN_USER_NAME);
        friendColumns.add(FriendEntry.COLUMN_PROFILE_NAME);
        friendColumns.add(FriendEntry.COLUMN_PROFILE_PICTURE_URL);
        friendColumns.add(FriendEntry.COLUMN_PACK_KEY);

        SQLiteDatabase db = new EnlightenDbHelper(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + FriendEntry.TABLE_NAME + ")", null);

        assertTrue("Columns havent been created for " + FriendEntry.TABLE_NAME, cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            friendColumns.remove(cursor.getString(columnNameIndex));
        } while (cursor.moveToNext());

        assertTrue("All columns that we expected arent present", friendColumns.isEmpty());

        db.close();
    }

    public void testAllPackColumnsPresent() {

        HashSet<String> packColumns = new HashSet<>();
        packColumns.add(PackEntry._ID);
        packColumns.add(PackEntry.COLUMN_CURRENT_SESSION_USER_ID);
        packColumns.add(PackEntry.COLUMN_DESCRIPTION);
        packColumns.add(PackEntry.COLUMN_PACK_NAME);
        packColumns.add(PackEntry.COLUMN_PACK_PARENT_ID);

        SQLiteDatabase db = new EnlightenDbHelper(mContext).getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + PackEntry.TABLE_NAME + ")", null);

        assertTrue("Columns havent been created for " + PackEntry.TABLE_NAME, cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            packColumns.remove(cursor.getString(columnNameIndex));
        } while (cursor.moveToNext());

        assertTrue("All columns that we expected arent present", packColumns.isEmpty());

        db.close();

    }

    public void testDataInsertionInPackTable() {
        SQLiteDatabase db = new EnlightenDbHelper(mContext).getWritableDatabase();
        ContentValues packData = UtilityForTest.createSamplePackData();

        long rowId = db.insert(PackEntry.TABLE_NAME, null, packData);

        assertTrue("Data was inserted into the table", rowId != -1);

        Cursor cursor = db.query(PackEntry.TABLE_NAME,
                null, null, null, null, null, null);

        assertTrue("The inserted data cannot be queried!", cursor.moveToFirst());

        assertTrue("The inserted data has been garbled up for some reason",
                UtilityForTest.validateResults(cursor, packData));

    }


    /**
     * Test to ensure that data can be inserted into the friend table
     * and that the data has been inserted accurately
     */
    public void testDataInsertionInFriendTable() {

        SQLiteDatabase db = new EnlightenDbHelper(mContext).getWritableDatabase();

        ContentValues packData = UtilityForTest.createSamplePackData();

        long rowId = db.insert(PackEntry.TABLE_NAME, null, packData);

        ContentValues contentValues = UtilityForTest.createSampleFriendData(rowId);

        long _id = db.insert(FriendEntry.TABLE_NAME, null, contentValues);

        assertTrue("Data was not inserted into the table", _id != -1);

        //String table, String[] columns, String selection,
        //String[] selectionArgs, String groupBy, String having,
         //       String orderBy
        Cursor cursor = db.query(FriendEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Inserted data cannot be retrieved from the " + FriendEntry.TABLE_NAME + " table",
                cursor.moveToFirst());

        assertTrue("The inserted data doesnt match the one that was put into the table",
                UtilityForTest.validateResults(cursor, contentValues));

    }

    /**
     * 1) Create a db with v1
     * 2) Add data to friends table. Ensure that pack_id doesn't exist and pack table dont exist
     * 3) create a db with v2
     * 4) Ensure that pack_id column exists as well as pack table
     * 5) Inserted data should exist
     * 6) Insert data and that should take things in
     */
//    public void testUpgradeFromVersion1ToVersion2() {
//        SQLiteDatabase db = new EnlightenDbHelper(mContext, 1).getWritableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
//
//        HashSet<String> tableNames = new HashSet<>();
//        tableNames.add(EnlightenContract.FriendEntry.TABLE_NAME);
//        tableNames.add(PackEntry.TABLE_NAME);
//
//        assertTrue("Tables have not been created", cursor.moveToFirst());
//
//        do {
//            tableNames.remove(cursor.getString(0));
//        } while (cursor.moveToNext());
//
//        assertTrue("The database shouldn't contain Pack table", tableNames.contains(PackEntry.TABLE_NAME));
//        assertTrue("The database should contain friend table", !tableNames.contains(FriendEntry.TABLE_NAME));
//
//        /////// Friend table contains the columns we expect it to -- version 1 ////
//
//        HashSet<String> friendColumns = new HashSet<>();
//        friendColumns.add(FriendEntry._ID);
//        friendColumns.add(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID);
//        friendColumns.add(FriendEntry.COLUMN_USER_ID);
//        friendColumns.add(FriendEntry.COLUMN_USER_NAME);
//        friendColumns.add(FriendEntry.COLUMN_PROFILE_NAME);
//        friendColumns.add(FriendEntry.COLUMN_PROFILE_PICTURE_URL);
//
//        cursor = db.rawQuery("PRAGMA table_info(" + FriendEntry.TABLE_NAME + ")", null);
//
//        assertTrue("Columns havent been created for " + FriendEntry.TABLE_NAME, cursor.moveToFirst());
//
//        int columnNameIndex = cursor.getColumnIndex("name");
//        do {
//            friendColumns.remove(cursor.getString(columnNameIndex));
//        } while (cursor.moveToNext());
//
//        assertTrue("Mess up in the columns!", friendColumns.isEmpty());
//
//        ////// Insert content into the database at this point -- version 1 ///////////
//        ContentValues contentValues = UtilityForTest.createSampleFriendData(-1);
//        contentValues.remove(FriendEntry.COLUMN_PACK_KEY);
//
//        long row_id = db.insert(FriendEntry.TABLE_NAME, null, contentValues);
//
//        assertTrue("Can insert into v1 of the table", row_id != -1);
//
//        //////////// now upgrade the database -- version 2 ////////////////
//
//        db = new EnlightenDbHelper(mContext, 2).getWritableDatabase();
//
//        //////////////// ensure both the tables were created ///////////////
//
//        tableNames = new HashSet<>();
//        tableNames.add(EnlightenContract.FriendEntry.TABLE_NAME);
//        tableNames.add(PackEntry.TABLE_NAME);
//
//        cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
//
//        assertTrue("Tables have not been created", cursor.moveToFirst());
//
//        do {
//            tableNames.remove(cursor.getString(0));
//        } while (cursor.moveToNext());
//
//        assertTrue("Not all tables have been created", tableNames.isEmpty());
//
//        /////////////// Ensure that all the columns in both the tables exist /////////////
//
//        //// friend table ////
//
//        friendColumns = new HashSet<>();
//        friendColumns.add(FriendEntry._ID);
//        friendColumns.add(FriendEntry.COLUMN_CURRENT_SESSION_USER_ID);
//        friendColumns.add(FriendEntry.COLUMN_USER_ID);
//        friendColumns.add(FriendEntry.COLUMN_USER_NAME);
//        friendColumns.add(FriendEntry.COLUMN_PROFILE_NAME);
//        friendColumns.add(FriendEntry.COLUMN_PROFILE_PICTURE_URL);
//        friendColumns.add(FriendEntry.COLUMN_PACK_KEY);
//
//        cursor = db.rawQuery("PRAGMA table_info(" + FriendEntry.TABLE_NAME + ")", null);
//
//        assertTrue("Columns havent been created for " + FriendEntry.TABLE_NAME, cursor.moveToFirst());
//
//        columnNameIndex = cursor.getColumnIndex("name");
//        do {
//            friendColumns.remove(cursor.getString(columnNameIndex));
//        } while (cursor.moveToNext());
//
//        assertTrue("Mess up in the columns!", friendColumns.isEmpty());
//
//        ///// pack table //////
//
//        HashSet<String> packColumns = new HashSet<>();
//        packColumns.add(PackEntry._ID);
//        packColumns.add(PackEntry.COLUMN_DESCRIPTION);
//        packColumns.add(PackEntry.COLUMN_PACK_NAME);
//        packColumns.add(PackEntry.COLUMN_PACK_PARENT_ID);
//
//        cursor = db.rawQuery("PRAGMA table_info(" + PackEntry.TABLE_NAME + ")", null);
//
//        assertTrue("Columns havent been created for " + PackEntry.TABLE_NAME, cursor.moveToFirst());
//
//        columnNameIndex = cursor.getColumnIndex("name");
//        do {
//            packColumns.remove(cursor.getString(columnNameIndex));
//        } while (cursor.moveToNext());
//
//        assertTrue("All columns that we expected arent present", packColumns.isEmpty());
//
//        ////////////////////////// Ensure that the data inserted in v1 has not been deleted /////////////
//
//        cursor = db.query(FriendEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null);
//
//        assertTrue("Inserted data from v1 cannot be retrieved in v2 from the " + FriendEntry.TABLE_NAME + " table",
//                cursor.moveToFirst());
//
//        assertTrue("The inserted data doesnt match the one that was put into the table",
//                UtilityForTest.validateResults(cursor, contentValues));
//
//
//    }

}