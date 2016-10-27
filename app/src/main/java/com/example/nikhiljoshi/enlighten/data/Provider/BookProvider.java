package com.example.nikhiljoshi.enlighten.data.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.nikhiljoshi.enlighten.data.BookDbHelper;
import com.example.nikhiljoshi.enlighten.data.Contract.BookContract;

/**
 * Content provider for the Book table
 */
public class BookProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int BOOK = 100;
    static final int BOOK_WITH_NAME = 101;
    static final int BOOK_WITH_AUTHOR = 102;

    private BookDbHelper mBookDbHelper;

    @Override
    public boolean onCreate() {
        mBookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOK:
                return BookContract.BookEntry.CONTENT_TYPE;
            /**
             * TODO: Try and find out what happens when a specific URI can either return and mime type. How do you represent it?
             * In this case, book with author can be either of item type or dir type. How exactly
             * are you supposed to represent this guy?
             */
            case BOOK_WITH_AUTHOR:
                return BookContract.BookEntry.CONTENT_TYPE;
            case BOOK_WITH_NAME:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unkown Uri: " + uri);
        }
    }

    /**
     * Given a specific values for a book and URI, we insert the information
     * into the db
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mBookDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch(match) {
            case BOOK: {
                final long _id = db.insert(BookContract.BookEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    returnUri =  BookContract.BookEntry.buildBookUriWithId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert data into: " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Failed to insert row data: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * Delete data in the book table
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mBookDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsDeleted = 0;

        switch (match) {
            case BOOK: {
                numRowsDeleted = db.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            } default:
                throw new UnsupportedOperationException("Failed to delete rows in the database for the following uri: " + uri);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mBookDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsUpdated = 0;

        switch (match) {
            case BOOK: {
                numRowsUpdated = db.update(
                        BookContract.BookEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            } default:
                throw new UnsupportedOperationException("Failed to update the rows for the " +
                        "following Uri: " + uri);
        }

        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mBookDbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch(match) {
            case BOOK: {
                retCursor = db.query(
                        BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            } case BOOK_WITH_AUTHOR: {
                String author = BookContract.BookEntry.getAuthorNameFromBookUri(uri);
                retCursor = db.query(
                        BookContract.BookEntry.TABLE_NAME,
                        projection,
                        BookContract.BookEntry.COLUMN_AUTHOR + " = ? ",
                        new String[]{author},
                        null,
                        null,
                        sortOrder
                );
                break;
            } case BOOK_WITH_NAME: {
                String bookName = BookContract.BookEntry.getBookNameFromBookUri(uri);
                retCursor = db.query(
                        BookContract.BookEntry.TABLE_NAME,
                        projection,
                        BookContract.BookEntry.COLUMN_NAME + " = ? ",
                        new String[]{bookName},
                        null,
                        null,
                        sortOrder
                );
                break;
            } default:
                throw new UnsupportedOperationException("Error: Failed in querying with the " +
                        "uri: " + uri);
        }

        // If any changes are made to the data that we have gotten in this
        // cursor... content resolver does the magic job of letting us know!
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * This UriMatcher is essentially a key value pair that matches URI paths to a specific constant.
     * This UriMatcher gets used in the insert, delete and query methods.
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BookContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BookContract.PATH_BOOK, BOOK);
        matcher.addURI(authority, BookContract.PATH_BOOK + "/" + BookContract.PATH_AUTHOR + "/*", BOOK_WITH_AUTHOR);
        matcher.addURI(authority, BookContract.PATH_BOOK + "/" + BookContract.PATH_NAME + "/*", BOOK_WITH_NAME);

        return matcher;
    }
}
