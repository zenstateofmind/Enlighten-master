package com.example.nikhiljoshi.enlighten.pojo;

/**
 * Created by nikhiljoshi on 6/13/16.
 */
public class Pack {

    public final String name;
    public final String description;
    public final Long parentPackId;
    public final Long currentPackId;

    public Pack(String name, String description, Long parentPackId, Long currentPackId) {
        this.name = name;
        this.description = description;
        this.parentPackId = parentPackId;
        this.currentPackId = currentPackId;
    }
}
