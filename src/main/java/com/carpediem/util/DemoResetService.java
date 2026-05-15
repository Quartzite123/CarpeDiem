package com.carpediem.util;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Change 7 — DemoResetService
 * Wipes and re-seeds 60 days of realistic daily_logs, day_meta, and todos
 * for the demo group (invite code DEMO00).
 *
 * Completion rates per user (in insertion order from demo-seed.sql):
 *   Kavya Nair   ~90%
 *   Arjun Sharma ~75%
 *   Priya Patel  ~60%
 *   Rohan Mehta  ~45%
 *   Demo         ~30%
 */
public class DemoResetService {

    private static final String INVITE_CODE = "DEMO00";

    // completion rate order matches user insertion order in demo-seed.sql
    private static final double[] COMPLETION_RATES = { 0.90, 0.75, 0.60, 0.45, 0.30 };

    public static void reset() {
        try (Connection c = DBConnection.getConnection()) {
            int groupId = getGroupId(c);
            if (groupId < 0) {
                System.out.println("[DemoReset] Demo group not found — skipping reset.");
                return;
            }

            List<Integer> userIds  = getUserIds(c, groupId);
            List<Integer> taskIds  = getTaskIds(c, groupId);

            if (userIds.isEmpty() || taskIds.isEmpty()) {
                System.out.println("[DemoReset] No users or tasks found — skipping.");
                return;
            }

            // Wipe existing demo data
            String inUsers = buildIn(userIds);
            try (Statement st = c.createStatement()) {
                st.executeUpdate("DELETE FROM daily_logs WHERE user_id IN (" + inUsers + ")");
                st.executeUpdate("DELETE FROM day_meta   WHERE user_id IN (" + inUsers + ")");
                st.executeUpdate("DELETE FROM todos       WHERE user_id IN (" + inUsers + ")");
            }

            // Re-seed 60 days of logs up to yesterday
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate start     = yesterday.minusDays(59);

            Random rng = new Random(42);

            String logSql = "INSERT INTO daily_logs (user_id, task_id, log_date, is_done) VALUES (?,?,?,1)";
            try (PreparedStatement logPs = c.prepareStatement(logSql)) {
                for (int ui = 0; ui < userIds.size(); ui++) {
                    int    uid  = userIds.get(ui);
                    double rate = COMPLETION_RATES[Math.min(ui, COMPLETION_RATES.length - 1)];

                    for (LocalDate day = start; !day.isAfter(yesterday); day = day.plusDays(1)) {
                        for (int taskId : taskIds) {
                            if (rng.nextDouble() < rate) {
                                logPs.setInt(1, uid);
                                logPs.setInt(2, taskId);
                                logPs.setDate(3, Date.valueOf(day));
                                logPs.addBatch();
                            }
                        }
                    }
                }
                logPs.executeBatch();
            }

            // Seed 8 realistic todos for the Demo user
            int demoUid = getDemoUserId(c);
            if (demoUid > 0) {
                String todoSql = "INSERT INTO todos (user_id, title, subject_tag, due_date, priority) VALUES (?,?,?,?,?)";
                try (PreparedStatement todoPs = c.prepareStatement(todoSql)) {
                    Object[][] todos = {
                        { "Solve 5 LeetCode mediums",      "DSA",      yesterday.plusDays(2),  "HIGH"   },
                        { "Complete Arrays module",         "DSA",      yesterday.plusDays(5),  "HIGH"   },
                        { "Read Clean Code Ch 4-5",         "Reading",  yesterday.plusDays(3),  "MEDIUM" },
                        { "Finish ML Coursera Week 3",      "AI Study", yesterday.plusDays(7),  "MEDIUM" },
                        { "Push portfolio project",         "Coding",   yesterday.plusDays(4),  "HIGH"   },
                        { "Gym — leg day",                  "Gym",      yesterday.plusDays(1),  "MEDIUM" },
                        { "Review OS notes before exam",    "DSA",      yesterday.minusDays(1), "HIGH"   }, // overdue
                        { "Write weekly reflection",        "Reading",  yesterday.plusDays(6),  "LOW"    },
                    };

                    for (Object[] todo : todos) {
                        todoPs.setInt(1, demoUid);
                        todoPs.setString(2, (String) todo[0]);
                        todoPs.setString(3, (String) todo[1]);
                        todoPs.setDate(4, Date.valueOf((LocalDate) todo[2]));
                        todoPs.setString(5, (String) todo[3]);
                        todoPs.addBatch();
                    }
                    todoPs.executeBatch();
                }
            }

            System.out.println("[DemoReset] Demo data re-seeded successfully.");

        } catch (Exception e) {
            System.err.println("[DemoReset] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── helpers ────────────────────────────────────────────

    private static int getGroupId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM squad_groups WHERE invite_code=? LIMIT 1")) {
            ps.setString(1, INVITE_CODE);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private static List<Integer> getUserIds(Connection c, int groupId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM users WHERE group_id=? ORDER BY id")) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getInt(1));
        }
        return ids;
    }

    private static List<Integer> getTaskIds(Connection c, int groupId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM tasks WHERE group_id=? ORDER BY id")) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getInt(1));
        }
        return ids;
    }

    private static int getDemoUserId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM users WHERE email=? LIMIT 1")) {
            ps.setString(1, DBConnection.DEMO_EMAIL);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private static String buildIn(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(ids.get(i));
        }
        return sb.toString();
    }
}
