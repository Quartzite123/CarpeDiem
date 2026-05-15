package com.carpediem.model;

public class PDF {
    private int id;
    private int userId;
    private int groupId;
    private String title;
    private String subjectTag;
    private String filePath;
    private String originalName;
    private String uploadedAt;
    private String uploaderName; // joined from users table

    public PDF() {}

    public int    getId()                  { return id; }
    public void   setId(int id)            { this.id = id; }
    public int    getUserId()              { return userId; }
    public void   setUserId(int u)         { this.userId = u; }
    public int    getGroupId()             { return groupId; }
    public void   setGroupId(int g)        { this.groupId = g; }
    public String getTitle()               { return title; }
    public void   setTitle(String t)       { this.title = t; }
    public String getSubjectTag()          { return subjectTag; }
    public void   setSubjectTag(String s)  { this.subjectTag = s; }
    public String getFilePath()            { return filePath; }
    public void   setFilePath(String f)    { this.filePath = f; }
    public String getOriginalName()        { return originalName; }
    public void   setOriginalName(String n){ this.originalName = n; }
    public String getUploadedAt()          { return uploadedAt; }
    public void   setUploadedAt(String u)  { this.uploadedAt = u; }
    public String getUploaderName()        { return uploaderName; }
    public void   setUploaderName(String n){ this.uploaderName = n; }
}
