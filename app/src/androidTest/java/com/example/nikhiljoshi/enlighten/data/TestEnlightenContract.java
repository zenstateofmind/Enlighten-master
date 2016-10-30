package com.example.nikhiljoshi.enlighten.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import static com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract.*;

/**
 * Created by nikhiljoshi on 6/5/16.
 */
public class TestEnlightenContract extends AndroidTestCase {

    private static final long CURRENT_USER_ID = 12345L;
    private static final String USER_NAME = "username";
    private static final long FRIEND_USER_ID = 1245L;
    private static final long PACK_ID = 12L;

    private static final String CURRENT_PACK_NAME = "India";
    private static final String PARENT_PACK_NAME = "VC";

    public void testBuildFriendUri() {

        Uri uriWithCurrentUserId = FriendEntry.buildFriendUriWithCurrentUserSessionId(CURRENT_USER_ID);
        assertNotNull("Error: URI shouldn't be null", uriWithCurrentUserId);

        assertEquals("The user session id has changed from the one inserted into the user id",
                CURRENT_USER_ID, FriendEntry.getCurrentUserIdFromFriendUri(uriWithCurrentUserId));


        Uri uriWithCurrentSessionIdAndUserName = FriendEntry.buildUriWithCurrentUserIdAndFriendUserId(CURRENT_USER_ID, FRIEND_USER_ID);
        assertNotNull("Error, URI shouldn't be null", uriWithCurrentSessionIdAndUserName);

        assertEquals("The user id of the session holder don't match", CURRENT_USER_ID,
                FriendEntry.getCurrentUserIdFromFriendUri(uriWithCurrentSessionIdAndUserName));

        assertEquals("The user name doesn't match", FRIEND_USER_ID,
                FriendEntry.getFriendUserIDFromFriendUri(uriWithCurrentSessionIdAndUserName));


        Uri uriWithPackId = FriendEntry.buildUriWithCurrentUserIdAndPackId(CURRENT_USER_ID, PACK_ID);
        assertNotNull("Error, URI shouldn't be null", uriWithPackId);
        assertEquals("The user session id is messed up for some reason",
                CURRENT_USER_ID, FriendEntry.getCurrentUserIdFromPackUri(uriWithPackId));
        assertEquals("The pack id is messed up for some reason",
                PACK_ID, FriendEntry.getPackIdFromPackUri(uriWithPackId));

    }

    public void testBuildPathUri() {
        Uri uriWithPackName = PackEntry.buildPackUriWithPackId(CURRENT_USER_ID, PACK_ID);
        assertNotNull("Error: URI shouldn't be null", uriWithPackName);

        long packId = PackEntry.getPackIdFromPackIdUri(uriWithPackName);
        assertEquals("Error: pack name has been garbled up", PACK_ID, packId);
        assertEquals("Error: user id has been garbled up", CURRENT_USER_ID,
                PackEntry.getCurrentUserIdFromPackIdUri(uriWithPackName));



        Uri uriWithParentPackName = PackEntry.buildPackUriWithParentPackName(CURRENT_USER_ID, PARENT_PACK_NAME);
        assertNotNull("Error: URI shouldn't be null", uriWithParentPackName);

        String parentPackName = PackEntry.getParentPackNameFromParentPackNameUri(uriWithParentPackName);
        assertEquals("Error: parent pack name has been garbled up", PARENT_PACK_NAME, parentPackName);
        assertEquals("Error: user id has been garbled up", CURRENT_USER_ID,
                PackEntry.getCurrentUserIdFromParentPackNameUri(uriWithParentPackName));
    }
}
