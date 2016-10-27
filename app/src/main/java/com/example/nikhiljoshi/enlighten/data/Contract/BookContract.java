package com.example.nikhiljoshi.enlighten.data.Contract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.BitSet;

/**
 * Contract that contains information of the columns that belong to
 * the book table
 *
 * @author Nikhil Joshi
 */
public class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.nikhiljoshi.enlighten";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOK = "book";

    public static final String PATH_AUTHOR = "author";

    public static final String PATH_NAME = "name";

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOK).build();

        public static final Uri CONTENT_URI_WITH_AUTHOR =
                CONTENT_URI.buildUpon().appendPath(PATH_AUTHOR).build();

        public static final Uri CONTENT_URI_WITH_NAME =
                CONTENT_URI.buildUpon().appendPath(PATH_NAME).build();

        public static final String CONTENT_TYPE  =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        //Name of the table that contains the books related information
        public static final String TABLE_NAME = "book";

        // Name of the book
        public static final String COLUMN_NAME = "name";

        // url for the cover image of the book
        public static final String COLUMN_COVER_URL = "cover_url";

        // Brief description of whats present in the book
        public static final String COLUMN_BOOK_DESCRIPTION = "description";

        // The publisher of this book
        public static final String COLUMN_PUBLISHER = "publisher";

        // The date when this book was published
        public static final String COLUMN_PUBLISHED_DATE = "published_date";

        // The author of the book
        public static final String COLUMN_AUTHOR = "author";

        // The number of weeks the
        public static final String COLUMN_WEEKS_ON_LIST = "weeks_on_list";

        public static Uri buildBookWithAuthorName(String authorName) {
            return CONTENT_URI_WITH_AUTHOR.buildUpon().appendPath(authorName).build();
        }

        public static String getAuthorNameFromBookUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getBookNameFromBookUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buidBookWithBookName(String bookName) {
            return CONTENT_URI_WITH_NAME.buildUpon().appendPath(bookName).build();
        }

        public static Uri buildBookUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }
}
