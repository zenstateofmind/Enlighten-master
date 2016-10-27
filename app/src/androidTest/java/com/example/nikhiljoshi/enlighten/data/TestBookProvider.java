package com.example.nikhiljoshi.enlighten.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.nikhiljoshi.enlighten.TestDataOperator;
import com.example.nikhiljoshi.enlighten.data.Contract.BookContract;
import com.example.nikhiljoshi.enlighten.data.Provider.BookProvider;

/**
 * Test Book Provider
 * @author Nikhil Joshi
 */
public class TestBookProvider extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();

    }

    private void deleteAllRecords() {
        final SQLiteDatabase db = new BookDbHelper(mContext).getWritableDatabase();
        final int numRowsDeleted = db.delete(BookContract.BookEntry.TABLE_NAME,
                null,
                null);
        db.close();
    }

    /*
    Test to ensure that the content provider has been propoerly registered
     */
    public void testProviderRegistry() {
        final PackageManager packageManager = mContext.getPackageManager();

        // This is the component name for BookProvider class
        final ComponentName componentName = new ComponentName(mContext.getPackageName(),
                BookProvider.class.getName());

        try {
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);
            assertEquals("Error: BookProvider was registered with authority: " + providerInfo.authority +
            " instead of authority: " + BookContract.CONTENT_AUTHORITY,
                    BookContract.CONTENT_AUTHORITY, providerInfo.authority);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: BookProvider hasn't been registered properly", false);
        }

    }

    /**
     * Ensure that the MIME type for a given uri is what we intend it to be
     */
    public void testGetType() {
        String type = mContext.getContentResolver().getType(BookContract.BookEntry.CONTENT_URI);
        assertEquals("Error: Basic content_uri should be of dir type: ",
                type, BookContract.BookEntry.CONTENT_TYPE);

        // Create a sample URI with an authors name. This
        String authorName = "herge";
        type = mContext.getContentResolver().getType(BookContract.BookEntry.buildBookWithAuthorName(authorName));
        assertEquals("Error: Content Uri with the author name needs to be of item type",
                type, BookContract.BookEntry.CONTENT_TYPE);

        String bookName = "tintin";
        type = mContext.getContentResolver().getType(BookContract.BookEntry.buidBookWithBookName(bookName));
        assertEquals("Error: Content uri with book name needs to be of item type",
                type, BookContract.BookEntry.CONTENT_ITEM_TYPE);
    }

    /**
     * Steps: <br>
     * 1) Use {@link BookDbHelper} to get an instance of writable database <br>
     * 2) With the {@link android.database.sqlite.SQLiteDatabase} insert some dummy data into the Book table <br>
     * 3) Use {@link BookProvider} to query the Book table and ensure that we can get back the data
     * that was inserted through the traditional SQLiteDatabase methods
     */
    public void testBasicBookQuery() {
        final ContentValues sampleBook = TestDataOperator.getSampleBook();

        final SQLiteDatabase db = new BookDbHelper(mContext).getWritableDatabase();

        final long _id = db.insert(
                BookContract.BookEntry.TABLE_NAME,
                null,
                sampleBook
        );

        //Ensure that the data was accurately added into the database
        assertTrue("Error: data wasn't accurately added into the database, id: " + _id, _id > 0);

        final Cursor cursor = getContext().getContentResolver().query(
                BookContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: The cursor had to contain atleast the values that were inserted " +
                "into the book database", cursor.moveToFirst());

        TestDataOperator.validateCurrentRecord("Error: the data doesn't match up... either" +
                " there was some problems in inserting into the database or while querying " +
                "the database. Check where the issues are and fix it! ", cursor, sampleBook);

        db.close();
    }

    /**
     * This test ensures that book information can be updated <br>
     * Steps: <br>
     *     1) Insert into the database using {@link BookDbHelper} <br>
     *     2) Ensure that it has been inserted <br>
     *     3) Change the sample data and try to update the content using {@link BookProvider} <br>
     *     4) The content should be updated
     */
    public void testUpdateBooks() {
        final SQLiteDatabase db = new BookDbHelper(mContext).getWritableDatabase();
        final ContentValues sampleBook = TestDataOperator.getSampleBook();

        final long _id = db.insert(
                BookContract.BookEntry.TABLE_NAME,
                null,
                sampleBook
        );

        assertTrue("Error: The insert statement didn't go through properly: id " + _id,
                _id > 0);

        String updatedWeeksOnList = "103";
        sampleBook.put(BookContract.BookEntry.COLUMN_WEEKS_ON_LIST, updatedWeeksOnList);

        final int numRowsUpdated = getContext().getContentResolver().update(
                BookContract.BookEntry.CONTENT_URI,
                sampleBook,
                null,
                null
        );

        assertTrue("Error: One row should have been updated but the number of rows that " +
                " have been updated are: " + numRowsUpdated, numRowsUpdated == 1);

        final Cursor cursor = getContext().getContentResolver().query(
                BookContract.BookEntry.buidBookWithBookName(sampleBook.getAsString(BookContract.BookEntry.COLUMN_NAME)),
                null,
                null,
                null,
                null
        );

       assertTrue("Error: the updated data should have been erased. Cannot retrieve information" +
               " from the database: ", cursor.moveToFirst());

        final int weeksOnListColIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_WEEKS_ON_LIST);
        final String weeksOnList = cursor.getString(weeksOnListColIndex);

        assertEquals("Error: the weeks on list hasnt been updated", weeksOnList, updatedWeeksOnList);

        db.close();

    }


    /**
     * Test that the {@link BookProvider} can delete records from the book database <br>
     * Steps: <br>
     *     1) Insert information into the database using {@link BookDbHelper} and {@link SQLiteDatabase} <br>
     *     2) Query the database and ensure that the content exists <br>
     *     3) Delete the content in the database using the {@link BookProvider} <br>
     *     4) Query again and ensure that the data doesnt exist
     */
    public void testDeleteRecords() {
        final SQLiteDatabase db = new BookDbHelper(mContext).getWritableDatabase();
        final ContentValues sampleBook = TestDataOperator.getSampleBook();
        final long _id = db.insert(
                BookContract.BookEntry.TABLE_NAME,
                null,
                sampleBook
        );

        assertTrue("Error: The data hasnt been inserted properly... id: " + _id, _id > 0);

        final Cursor cursor = getContext().getContentResolver().query(
                BookContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: The database doesn't contain any information!", cursor.moveToFirst());

        final int numRowsDeleted = getContext().getContentResolver().delete(
                BookContract.BookEntry.CONTENT_URI,
                BookContract.BookEntry.COLUMN_NAME + " = ? ",
                new String[]{sampleBook.getAsString(BookContract.BookEntry.COLUMN_NAME)}
        );

        assertTrue("Error: One row in the database should have been deleted", numRowsDeleted == 1);

        db.close();
    }

    /**
     * Insert information into the Book table using {@link BookProvider} <br>
     * Steps: <br>
     *     1) Insert into the table using {@link BookProvider} <br>
     *     2) Query the table and ensure that the data is present <br>
     *     3) Go on an offsite to Miami !!!!
     */
    public void testInsertBook() {
        final ContentValues sampleBook = TestDataOperator.getSampleBook();
        final Uri insertedUri = getContext().getContentResolver().insert(
                BookContract.BookEntry.CONTENT_URI, sampleBook);

        final long _id = ContentUris.parseId(insertedUri);

        assertTrue("Error: Data hasn't been inserted using the content provider insert method: id: " + _id,
                _id > 0);

        final Cursor cursor = getContext().getContentResolver().query(
                BookContract.BookEntry.buidBookWithBookName(sampleBook.getAsString(BookContract.BookEntry.COLUMN_NAME)),
                null,
                null,
                null,
                null
        );

        assertTrue("Error: The inserted data cannot be somehow retrieved", cursor.moveToFirst());
    }

}
