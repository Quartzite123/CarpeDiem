package com.carpediem.model;

public class Group {
    private int id;
    private String name;
    private String inviteCode;

    public Group() {}
    public Group(int id, String name, String inviteCode) {
        this.id = id; this.name = name; this.inviteCode = inviteCode;
    }

    public int    getId()               { return id; }
    public void   setId(int id)         { this.id = id; }
    public String getName()             { return name; }
    public void   setName(String n)     { this.name = n; }
    public String getInviteCode()       { return inviteCode; }
    public void   setInviteCode(String c){ this.inviteCode = c; }
}
