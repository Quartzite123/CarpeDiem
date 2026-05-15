package com.carpediem.dao;

import com.carpediem.model.Todo;
import com.carpediem.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {

    public List<Todo> getByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM todos WHERE user_id=? ORDER BY is_done, due_date, priority";
        List<Todo> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Change 6c: Count todos for a user (used for demo spam protection) */
    public int countByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM todos WHERE user_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int insert(Todo t) throws SQLException {
        String sql = "INSERT INTO todos (user_id,title,subject_tag,due_date,priority) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getSubjectTag());
            ps.setDate(4, t.getDueDate() != null ? Date.valueOf(t.getDueDate()) : null);
            ps.setString(5, t.getPriority());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public void toggleDone(int id, int userId) throws SQLException {
        String sql = "UPDATE todos SET is_done = NOT is_done WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM todos WHERE id=? AND user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private Todo map(ResultSet rs) throws SQLException {
        Todo t = new Todo();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setTitle(rs.getString("title"));
        t.setSubjectTag(rs.getString("subject_tag"));
        Date d = rs.getDate("due_date");
        if (d != null) t.setDueDate(d.toLocalDate());
        t.setPriority(rs.getString("priority"));
        t.setDone(rs.getBoolean("is_done"));
        t.setCreatedAt(rs.getString("created_at"));
        return t;
    }
}
