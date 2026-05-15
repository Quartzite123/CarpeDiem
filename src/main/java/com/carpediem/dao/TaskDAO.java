package com.carpediem.dao;

import com.carpediem.model.Task;
import com.carpediem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Task> getByGroupId(int groupId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE group_id = ? ORDER BY position, id";
        List<Task> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int insert(String name, int groupId, int position) throws SQLException {
        String sql = "INSERT INTO tasks (name, group_id, position) VALUES (?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, groupId);
            ps.setInt(3, position);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public void rename(int taskId, int groupId, String newName) throws SQLException {
        String sql = "UPDATE tasks SET name = ? WHERE id = ? AND group_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, taskId);
            ps.setInt(3, groupId);
            ps.executeUpdate();
        }
    }

    /** Seed default tasks for a new group */
    public void seedDefaults(int groupId) throws SQLException {
        String[] defaults = {"DSA", "Gym", "AI Study", "Reading", "Coding"};
        for (int i = 0; i < defaults.length; i++) {
            insert(defaults[i], groupId, i);
        }
    }

    private Task map(ResultSet rs) throws SQLException {
        return new Task(rs.getInt("id"), rs.getString("name"),
                        rs.getInt("group_id"), rs.getInt("position"));
    }
}
