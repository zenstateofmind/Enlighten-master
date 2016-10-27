package com.example.nikhiljoshi.enlighten.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nikhiljoshi on 5/30/16.
 */
public class FriendIds {

    @SerializedName("ids")
    public final List<Long> ids;

    @SerializedName("previous_cursor")
    public final Long previous_cursor;

    @SerializedName("next_cursor")
    public final Long next_cursor;

    public FriendIds(List<Long> ids, Long previous_cursor, Long next_cursor) {
        this.ids = ids;
        this.previous_cursor = previous_cursor;
        this.next_cursor = next_cursor;
    }
}
