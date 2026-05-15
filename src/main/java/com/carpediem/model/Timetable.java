package com.carpediem.model;

public class Timetable {
    private int id;
    private int userId;
    private String baseFilePath;
    private String baseFileType; // "pdf" or "image"
    private String personalEdits; // JSON string
    private String updatedAt;

    public Timetable() {}

    public int    getId()                  { return id; }
    public void   setId(int id)            { this.id = id; }
    public int    getUserId()              { return userId; }
    public void   setUserId(int u)         { this.userId = u; }
    public String getBaseFilePath()        { return baseFilePath; }
    public void   setBaseFilePath(String p){ this.baseFilePath = p; }
    public String getBaseFileType()        { return baseFileType; }
    public void   setBaseFileType(String t){ this.baseFileType = t; }
    public String getPersonalEdits()       { return personalEdits; }
    public void   setPersonalEdits(String e){ this.personalEdits = e; }
    public String getUpdatedAt()           { return updatedAt; }
    public void   setUpdatedAt(String u)   { this.updatedAt = u; }

    public boolean hasBase() {
        return baseFilePath != null && !baseFilePath.isEmpty();
    }
}
