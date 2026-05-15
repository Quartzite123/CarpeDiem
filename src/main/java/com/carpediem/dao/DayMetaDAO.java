package com.carpediem.dao;

import com.carpediem.model.DayMeta;
import com.carpediem.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class DayMetaDAO {

    public DayMeta getByUserAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM day_meta WHERE user_id=? AND meta_date=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DayMeta m = new DayMeta();
                m.setId(rs.getInt("id"));
                m.setUserId(userId);
                m.setMetaDate(date);
                m.setMood(rs.getString("mood"));
                m.setStudyHours(rs.getDouble("study_hours"));
                m.setNotes(rs.getString("notes"));
                return m;
            }
        }
        return null;
    }

    public void upsert(int userId, LocalDate date, String mood, double hours, String notes) throws SQLException {
        String sql = """
            INSERT INTO day_meta (user_id, meta_date, mood, study_hours, notes)
            VALUES (?,?,?,?,?)
            ON DUPLICATE KEY UPDATE mood=?, study_hours=?, notes=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, mood);
            ps.setDouble(4, hours);
            ps.setString(5, notes);
            ps.setString(6, mood);
            ps.setDouble(7, hours);
            ps.setString(8, notes);
            ps.executeUpdate();
        }
    }
}
