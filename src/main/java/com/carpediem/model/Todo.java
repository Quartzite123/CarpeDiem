package com.carpediem.model;

import java.time.LocalDate;

public class Todo {
    private int id;
    private int userId;
    private String title;
    private String subjectTag;
    private LocalDate dueDate;
    private String priority; // HIGH, MEDIUM, LOW
    private boolean isDone;
    private String createdAt;

    public Todo() {}

    public int       getId()               { return id; }
    public void      setId(int id)         { this.id = id; }
    public int       getUserId()           { return userId; }
    public void      setUserId(int u)      { this.userId = u; }
    public String    getTitle()            { return title; }
    public void      setTitle(String t)    { this.title = t; }
    public String    getSubjectTag()       { return subjectTag; }
    public void      setSubjectTag(String s){ this.subjectTag = s; }
    public LocalDate getDueDate()          { return dueDate; }
    public void      setDueDate(LocalDate d){ this.dueDate = d; }
    public String    getPriority()         { return priority; }
    public void      setPriority(String p) { this.priority = p; }
    public boolean   isDone()              { return isDone; }
    public void      setDone(boolean d)    { this.isDone = d; }
    public String    getCreatedAt()        { return createdAt; }
    public void      setCreatedAt(String c){ this.createdAt = c; }

    public boolean isOverdue() {
        return dueDate != null && !isDone && dueDate.isBefore(LocalDate.now());
    }
}
