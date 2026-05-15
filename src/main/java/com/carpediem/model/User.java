package com.carpediem.model;

/**
 * User model. Implements Comparable for leaderboard sorting.
 * Transient stats fields are populated at query time, not stored in DB.
 */
public class User implements Comparable<User> {

    // DB fields
    private int    id;
    private String name;
    private String email;
    private String passwordHash;
    private String photoPath;
    private int    groupId;
    private String createdAt;

    // Computed stats (not in DB)
    private double monthPct;
    private int    monthStreak;
    private int    overallStreak;
    private int    weekFullDays;

    public User() {}

    public User(int id, String name, String email, String passwordHash,
                String photoPath, int groupId) {
        this.id = id; this.name = name; this.email = email;
        this.passwordHash = passwordHash; this.photoPath = photoPath;
        this.groupId = groupId;
    }

    // Comparable: sort descending by monthPct
    @Override
    public int compareTo(User other) {
        return Double.compare(other.monthPct, this.monthPct);
    }

    // ── Getters & Setters ──────────────────────────────────────────────────
    public int    getId()           { return id; }
    public void   setId(int id)     { this.id = id; }

    public String getName()             { return name; }
    public void   setName(String n)     { this.name = n; }

    public String getEmail()            { return email; }
    public void   setEmail(String e)    { this.email = e; }

    public String getPasswordHash()         { return passwordHash; }
    public void   setPasswordHash(String p) { this.passwordHash = p; }

    public String getPhotoPath()            { return photoPath; }
    public void   setPhotoPath(String p)    { this.photoPath = p; }

    public int  getGroupId()          { return groupId; }
    public void setGroupId(int g)     { this.groupId = g; }

    public String getCreatedAt()          { return createdAt; }
    public void   setCreatedAt(String c)  { this.createdAt = c; }

    public double getMonthPct()           { return monthPct; }
    public void   setMonthPct(double p)   { this.monthPct = p; }

    public int  getMonthStreak()          { return monthStreak; }
    public void setMonthStreak(int s)     { this.monthStreak = s; }

    public int  getOverallStreak()        { return overallStreak; }
    public void setOverallStreak(int s)   { this.overallStreak = s; }

    public int  getWeekFullDays()         { return weekFullDays; }
    public void setWeekFullDays(int w)    { this.weekFullDays = w; }

    /** Returns a safe photo URL for use in JSP. */
    public String getPhotoUrl() {
        return (photoPath != null && !photoPath.isEmpty())
            ? "/carpediem/api/photo?id=" + id
            : null;
    }

    /** Initials fallback when no photo. */
    public String getInitials() {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        return parts.length >= 2
            ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
            : name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}
