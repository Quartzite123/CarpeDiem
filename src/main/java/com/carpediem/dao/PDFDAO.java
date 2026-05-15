package com.carpediem.dao;

import com.carpediem.model.PDF;
import com.carpediem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PDFDAO {

    public List<PDF> getByGroup(int groupId) throws SQLException {
        String sql = """
            SELECT p.*, u.name as uploader_name
            FROM pdfs p JOIN users u ON p.user_id = u.id
            WHERE p.group_id = ?
            ORDER BY p.uploaded_at DESC
            """;
        List<PDF> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public PDF getById(int id) throws SQLException {
        String sql = "SELECT p.*, u.name as uploader_name FROM pdfs p JOIN users u ON p.user_id=u.id WHERE p.id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public int insert(PDF pdf) throws SQLException {
        String sql = "INSERT INTO pdfs (user_id, group_id, title, subject_tag, file_path, original_name) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pdf.getUserId());
            ps.setInt(2, pdf.getGroupId());
            ps.setString(3, pdf.getTitle());
            ps.setString(4, pdf.getSubjectTag());
            ps.setString(5, pdf.getFilePath());
            ps.setString(6, pdf.getOriginalName());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public void delete(int id, int userId) throws SQLException {
        // Only uploader can delete
        String sql = "DELETE FROM pdfs WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private PDF map(ResultSet rs) throws SQLException {
        PDF p = new PDF();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setGroupId(rs.getInt("group_id"));
        p.setTitle(rs.getString("title"));
        p.setSubjectTag(rs.getString("subject_tag"));
        p.setFilePath(rs.getString("file_path"));
        p.setOriginalName(rs.getString("original_name"));
        p.setUploadedAt(rs.getString("uploaded_at"));
        p.setUploaderName(rs.getString("uploader_name"));
        return p;
    }
}
