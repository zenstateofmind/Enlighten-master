package com.example.nikhiljoshi.enlighten.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.nikhiljoshi.enlighten.data.Contract.BookContract;

/**
 * Ensure that the URI are being built the way we want them to be.
 * Also, try and get data out of the URI... such as the author name and the
 * book name from their respective URIs
 */
public class TestBookContract extends AndroidTestCase {

    public void testBuildAndRetrieveAuthorNameFromUri() {
        String authorName = "herge";
        final Uri uri = BookContract.BookEntry.buildBookWithAuthorName(authorName);
        assertTrue("Error: The URI path should contain the author path name ", uri.getPathSegments().contains(BookContract.PATH_AUTHOR));
        assertTrue("Error: Did not retrieve the author name from the Uri",
                authorName.equals(BookContract.BookEntry.getAuthorNameFromBookUri(uri)));
    }

    public void testBuildAndRetrieveBookNameFromUri() {
        String bookName = "tinTin";
        final Uri uri = BookContract.BookEntry.buidBookWithBookName(bookName);
        assertTrue("Error: The URI path should contain the book name path name ", uri.getPathSegments().contains(BookContract.PATH_NAME));
        assertTrue("Error: Did not retrieve the book name from the Uri",
                bookName.equals(BookContract.BookEntry.getBookNameFromBookUri(uri)));
    }


}
