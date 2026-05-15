package com.carpediem.model;

public class Task {
    private int id;
    private String name;
    private int groupId;
    private int position;

    public Task() {}
    public Task(int id, String name, int groupId, int position) {
        this.id = id; this.name = name; this.groupId = groupId; this.position = position;
    }

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }
    public String getName()            { return name; }
    public void   setName(String n)    { this.name = n; }
    public int    getGroupId()         { return groupId; }
    public void   setGroupId(int g)    { this.groupId = g; }
    public int    getPosition()        { return position; }
    public void   setPosition(int p)   { this.position = p; }
}
