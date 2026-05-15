package com.carpediem.model;

import java.time.LocalDate;

public class DailyLog {
    private int id;
    private int userId;
    private int taskId;
    private LocalDate logDate;
    private boolean isDone;

    public DailyLog() {}

    public int       getId()             { return id; }
    public void      setId(int id)       { this.id = id; }
    public int       getUserId()         { return userId; }
    public void      setUserId(int u)    { this.userId = u; }
    public int       getTaskId()         { return taskId; }
    public void      setTaskId(int t)    { this.taskId = t; }
    public LocalDate getLogDate()        { return logDate; }
    public void      setLogDate(LocalDate d){ this.logDate = d; }
    public boolean   isDone()            { return isDone; }
    public void      setDone(boolean d)  { this.isDone = d; }
}
