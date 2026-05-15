package com.carpediem.util;

import com.carpediem.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility for calculating habit statistics.
 * Demonstrates: Streams, Lambda, Collections, Comparable/Comparator
 */
public class StatsUtil {

    /**
     * Returns set of dates (YYYY-MM-DD strings) where ALL tasks were completed.
     * Uses SQL to count per-day completions vs total task count.
     */
    public static Set<LocalDate> getFullDays(int userId, int groupId, Connection conn) throws SQLException {
        String sql = """
            SELECT dl.log_date, COUNT(*) as done_count,
                   (SELECT COUNT(*) FROM tasks WHERE group_id = ?) as total_tasks
            FROM daily_logs dl
            WHERE dl.user_id = ? AND dl.is_done = TRUE
            GROUP BY dl.log_date
            HAVING done_count >= total_tasks
            """;
        Set<LocalDate> fullDays = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fullDays.add(rs.getDate("log_date").toLocalDate());
            }
        }
        return fullDays;
    }

    /**
     * Monthly completion % = full days / elapsed days * 100
     * Uses Java Streams to filter and count full days in the given month.
     */
    public static double calcMonthCompletion(Set<LocalDate> allFullDays, int year, int month) {
        LocalDate now = LocalDate.now();
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last  = first.withDayOfMonth(first.lengthOfMonth());
        LocalDate upTo  = (now.getYear() == year && now.getMonthValue() == month) ? now : last;

        long elapsed = ChronoUnit.DAYS.between(first, upTo) + 1;
        if (elapsed <= 0) return 0;

        // Stream + Lambda: filter full days in this month
        long fullCount = allFullDays.stream()
            .filter(d -> d.getYear() == year && d.getMonthValue() == month)
            .count();

        return Math.round((fullCount * 100.0 / elapsed) * 10) / 10.0;
    }

    /**
     * Streak = consecutive full days going backward from most recent full day.
     * If today is a full day, streak includes today.
     * If today is not full, streak starts from yesterday.
     */
    public static int calcStreak(Set<LocalDate> allFullDays) {
        if (allFullDays.isEmpty()) return 0;

        LocalDate now = LocalDate.now();
        // Start from today if full, else from yesterday
        LocalDate check = allFullDays.contains(now) ? now : now.minusDays(1);

        int streak = 0;
        while (allFullDays.contains(check)) {
            streak++;
            check = check.minusDays(1);
            if (check.isBefore(LocalDate.of(2024, 1, 1))) break; // safety
        }
        return streak;
    }

    /**
     * Month-scoped streak: consecutive full days within the given month only.
     */
    public static int calcMonthStreak(Set<LocalDate> allFullDays, int year, int month) {
        LocalDate now = LocalDate.now();
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last  = first.withDayOfMonth(first.lengthOfMonth());
        LocalDate upTo  = (now.getYear() == year && now.getMonthValue() == month) ? now : last;

        LocalDate check = allFullDays.contains(upTo) ? upTo : upTo.minusDays(1);
        int streak = 0;
        while (!check.isBefore(first) && allFullDays.contains(check)) {
            streak++;
            check = check.minusDays(1);
        }
        return streak;
    }

    /**
     * Count full days in the last 7 days (including today).
     * Demonstrates Stream filter + count.
     */
    public static int calcWeekFullDays(Set<LocalDate> allFullDays) {
        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusDays(6);
        return (int) allFullDays.stream()
            .filter(d -> !d.isBefore(weekAgo) && !d.isAfter(now))
            .count();
    }

    /**
     * Per-task completion %: how many elapsed days was this task ticked.
     * Uses Streams to compute per task.
     */
    public static Map<Integer, Double> calcTaskPcts(int userId, int year, int month, Connection conn) throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last  = first.withDayOfMonth(first.lengthOfMonth());
        LocalDate upTo  = (now.getYear() == year && now.getMonthValue() == month) ? now : last;
        long elapsed = ChronoUnit.DAYS.between(first, upTo) + 1;

        String sql = """
            SELECT task_id, COUNT(*) as done_count
            FROM daily_logs
            WHERE user_id = ? AND is_done = TRUE
              AND YEAR(log_date) = ? AND MONTH(log_date) = ?
            GROUP BY task_id
            """;
        Map<Integer, Double> pcts = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                long done  = rs.getLong("done_count");
                pcts.put(taskId, Math.round((done * 100.0 / elapsed) * 10) / 10.0);
            }
        }
        return pcts;
    }

    /**
     * Sort users by monthPct descending using Comparator (lambda).
     * Demonstrates Comparator + Collections.sort.
     */
    public static List<User> sortByCompletion(List<User> users) {
        return users.stream()
            .sorted(Comparator.comparingDouble(User::getMonthPct).reversed())
            .collect(Collectors.toList());
    }
}
