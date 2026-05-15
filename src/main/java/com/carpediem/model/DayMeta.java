package com.carpediem.model;

import java.time.LocalDate;

public class DayMeta {
    private int id;
    private int userId;
    private LocalDate metaDate;
    private String mood;
    private double studyHours;
    private String notes;

    public DayMeta() {}

    public int       getId()               { return id; }
    public void      setId(int id)         { this.id = id; }
    public int       getUserId()           { return userId; }
    public void      setUserId(int u)      { this.userId = u; }
    public LocalDate getMetaDate()         { return metaDate; }
    public void      setMetaDate(LocalDate d){ this.metaDate = d; }
    public String    getMood()             { return mood; }
    public void      setMood(String m)     { this.mood = m; }
    public double    getStudyHours()       { return studyHours; }
    public void      setStudyHours(double h){ this.studyHours = h; }
    public String    getNotes()            { return notes; }
    public void      setNotes(String n)    { this.notes = n; }
}
