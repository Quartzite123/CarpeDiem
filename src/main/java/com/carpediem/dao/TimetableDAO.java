package com.carpediem.dao;

import com.carpediem.model.Timetable;
import com.carpediem.util.DBConnection;

import java.sql.*;

public class TimetableDAO {

    public Timetable getByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM timetable WHERE user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timetable t = new Timetable();
                t.setId(rs.getInt("id"));
                t.setUserId(userId);
                t.setBaseFilePath(rs.getString("base_file_path"));
                t.setBaseFileType(rs.getString("base_file_type"));
                t.setPersonalEdits(rs.getString("personal_edits"));
                t.setUpdatedAt(rs.getString("updated_at"));
                return t;
            }
        }
        return null;
    }

    public void upsertBase(int userId, String filePath, String fileType) throws SQLException {
        String sql = """
            INSERT INTO timetable (user_id, base_file_path, base_file_type)
            VALUES (?,?,?)
            ON DUPLICATE KEY UPDATE base_file_path=?, base_file_type=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, filePath); ps.setString(3, fileType);
            ps.setString(4, filePath); ps.setString(5, fileType);
            ps.executeUpdate();
        }
    }

    public void saveEdits(int userId, String editsJson) throws SQLException {
        String sql = """
            INSERT INTO timetable (user_id, personal_edits)
            VALUES (?,?)
            ON DUPLICATE KEY UPDATE personal_edits=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, editsJson);
            ps.setString(3, editsJson);
            ps.executeUpdate();
        }
    }
}
