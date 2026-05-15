package com.carpediem.dao;

import com.carpediem.model.Group;
import com.carpediem.util.DBConnection;

import java.sql.*;
import java.util.UUID;

public class GroupDAO {

    public Group findById(int id) throws SQLException {
        String sql = "SELECT * FROM squad_groups WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public Group findByInviteCode(String code) throws SQLException {
        String sql = "SELECT * FROM squad_groups WHERE invite_code = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code.trim().toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public int insert(String name) throws SQLException {
        String code = generateCode();
        String sql  = "INSERT INTO squad_groups (name, invite_code) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, code);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    /** Generate a random 6-char uppercase invite code */
    public String generateCode() {
        return UUID.randomUUID().toString()
            .replace("-", "").substring(0, 6).toUpperCase();
    }

    /** Get the invite code for a group */
    public String getInviteCode(int groupId) throws SQLException {
        Group g = findById(groupId);
        return g != null ? g.getInviteCode() : "";
    }

    private Group map(ResultSet rs) throws SQLException {
        return new Group(rs.getInt("id"), rs.getString("name"), rs.getString("invite_code"));
    }
}
