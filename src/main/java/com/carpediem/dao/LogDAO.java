package com.carpediem.dao;

import com.carpediem.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LogDAO {

    /**
     * Toggle a task for a given date.
     * Uses INSERT ... ON DUPLICATE KEY UPDATE for atomic upsert.
     * Returns the new isDone value.
     */
    public boolean toggle(int userId, int taskId, LocalDate date) throws SQLException {
        // First check current state
        String check = "SELECT is_done FROM daily_logs WHERE user_id=? AND task_id=? AND log_date=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(check)) {
            ps.setInt(1, userId);
            ps.setInt(2, taskId);
            ps.setDate(3, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            boolean current = rs.next() && rs.getBoolean("is_done");
            boolean newState = !current;

            String upsert = """
                INSERT INTO daily_logs (user_id, task_id, log_date, is_done)
                VALUES (?,?,?,?)
                ON DUPLICATE KEY UPDATE is_done = ?
                """;
            try (PreparedStatement ups = c.prepareStatement(upsert)) {
                ups.setInt(1, userId);
                ups.setInt(2, taskId);
                ups.setDate(3, Date.valueOf(date));
                ups.setBoolean(4, newState);
                ups.setBoolean(5, newState);
                ups.executeUpdate();
            }
            return newState;
        }
    }

    /**
     * Returns month grid: taskId -> (dayOfMonth -> isDone)
     * Used to build the habit grid in the dashboard.
     */
    public Map<Integer, Map<Integer, Boolean>> getMonthGrid(int userId, int year, int month) throws SQLException {
        String sql = """
            SELECT task_id, DAY(log_date) as day, is_done
            FROM daily_logs
            WHERE user_id = ? AND YEAR(log_date) = ? AND MONTH(log_date) = ?
            """;
        Map<Integer, Map<Integer, Boolean>> grid = new HashMap<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                int day    = rs.getInt("day");
                boolean done = rs.getBoolean("is_done");
                grid.computeIfAbsent(taskId, k -> new HashMap<>()).put(day, done);
            }
        }
        return grid;
    }

    /** Get isDone for a specific cell — used after toggle to return state */
    public boolean getState(int userId, int taskId, LocalDate date) throws SQLException {
        String sql = "SELECT is_done FROM daily_logs WHERE user_id=? AND task_id=? AND log_date=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, taskId);
            ps.setDate(3, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("is_done");
        }
    }
}
