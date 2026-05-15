package com.carpediem.servlet;

import com.carpediem.dao.*;
import com.carpediem.model.*;
import com.carpediem.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final UserDAO    userDAO    = new UserDAO();
    private final TaskDAO    taskDAO    = new TaskDAO();
    private final LogDAO     logDAO     = new LogDAO();
    private final DayMetaDAO metaDAO    = new DayMetaDAO();
    private final GroupDAO   groupDAO   = new GroupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;

        int userId  = AuthUtil.getUserId(req);
        int groupId = AuthUtil.getGroupId(req);

        try (Connection conn = DBConnection.getConnection()) {
            LocalDate now = LocalDate.now();
            int year  = now.getYear();
            int month = now.getMonthValue();

            // --- Current user data ---
            User me = userDAO.findById(userId);
            List<Task> tasks = taskDAO.getByGroupId(groupId);

            // Grid for current month
            Map<Integer, Map<Integer, Boolean>> grid = logDAO.getMonthGrid(userId, year, month);

            // Day meta for today
            DayMeta todayMeta = metaDAO.getByUserAndDate(userId, now);

            // Stats for current user
            Set<LocalDate> fullDays = StatsUtil.getFullDays(userId, groupId, conn);
            me.setMonthPct(StatsUtil.calcMonthCompletion(fullDays, year, month));
            me.setMonthStreak(StatsUtil.calcMonthStreak(fullDays, year, month));
            me.setOverallStreak(StatsUtil.calcStreak(fullDays));
            me.setWeekFullDays(StatsUtil.calcWeekFullDays(fullDays));

            Map<Integer, Double> taskPcts = StatsUtil.calcTaskPcts(userId, year, month, conn);

            // --- Leaderboard ---
            List<User> members = userDAO.getGroupMembers(groupId);
            for (User u : members) {
                Set<LocalDate> fd = StatsUtil.getFullDays(u.getId(), groupId, conn);
                u.setMonthPct(StatsUtil.calcMonthCompletion(fd, year, month));
                u.setMonthStreak(StatsUtil.calcMonthStreak(fd, year, month));
                u.setOverallStreak(StatsUtil.calcStreak(fd));
                u.setWeekFullDays(StatsUtil.calcWeekFullDays(fd));
            }
            // Sort using Comparator lambda (Streams)
            List<User> leaderboard = StatsUtil.sortByCompletion(members);

            // Group invite code
            String inviteCode = groupDAO.getInviteCode(groupId);

            // --- Set request attributes for JSP ---
            req.setAttribute("me",          me);
            req.setAttribute("tasks",       tasks);
            req.setAttribute("grid",        grid);
            req.setAttribute("todayMeta",   todayMeta);
            req.setAttribute("taskPcts",    taskPcts);
            req.setAttribute("leaderboard", leaderboard);
            req.setAttribute("inviteCode",  inviteCode);
            req.setAttribute("year",        year);
            req.setAttribute("month",       month);
            req.setAttribute("today",       now.getDayOfMonth());
            req.setAttribute("daysInMonth", now.lengthOfMonth());

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
