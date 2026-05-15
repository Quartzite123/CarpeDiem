package com.carpediem.servlet;

import com.carpediem.dao.GroupDAO;
import com.carpediem.dao.UserDAO;
import com.carpediem.model.User;
import com.carpediem.util.AuthUtil;
import com.carpediem.util.DBConnection;
import com.carpediem.util.StatsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Change 8 — Squad Members Page
 * GET /group — shows all squad members with stats, sorted by monthly %.
 */
@WebServlet("/group")
public class GroupServlet extends HttpServlet {

    private final UserDAO  userDAO  = new UserDAO();
    private final GroupDAO groupDAO = new GroupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;

        int groupId = AuthUtil.getGroupId(req);
        int myId    = AuthUtil.getUserId(req);

        try (Connection conn = DBConnection.getConnection()) {
            List<User> members = userDAO.getGroupMembers(groupId);
            int year  = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();

            // Enrich each member with stats
            for (User u : members) {
                Set<LocalDate> fd = StatsUtil.getFullDays(u.getId(), groupId, conn);
                u.setMonthPct((int) StatsUtil.calcMonthCompletion(fd, year, month));
                u.setMonthStreak(StatsUtil.calcMonthStreak(fd, year, month));
                u.setOverallStreak(StatsUtil.calcStreak(fd));
                u.setWeekFullDays(StatsUtil.calcWeekFullDays(fd));
            }

            // Sort by monthly % descending (Stream + Comparator lambda)
            List<User> sorted = StatsUtil.sortByCompletion(members);

            // Invite code via DAO
            String inviteCode = groupDAO.getInviteCode(groupId);

            req.setAttribute("members",    sorted);
            req.setAttribute("myId",       myId);
            req.setAttribute("inviteCode", inviteCode);
            req.getRequestDispatcher("/WEB-INF/views/group.jsp").forward(req, res);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
