package com.carpediem.servlet;

import com.carpediem.dao.UserDAO;
import com.carpediem.model.User;
import com.carpediem.util.*;
import org.json.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/api/leaderboard")
public class LeaderboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        int userId  = AuthUtil.getUserId(req);
        int groupId = AuthUtil.getGroupId(req);
        if (userId == -1) { res.setStatus(401); return; }

        try (Connection conn = DBConnection.getConnection()) {
            LocalDate now = LocalDate.now();
            List<User> members = userDAO.getGroupMembers(groupId);
            for (User u : members) {
                Set<LocalDate> fd = StatsUtil.getFullDays(u.getId(), groupId, conn);
                u.setMonthPct(StatsUtil.calcMonthCompletion(fd, now.getYear(), now.getMonthValue()));
                u.setMonthStreak(StatsUtil.calcMonthStreak(fd, now.getYear(), now.getMonthValue()));
                u.setOverallStreak(StatsUtil.calcStreak(fd));
                u.setWeekFullDays(StatsUtil.calcWeekFullDays(fd));
            }
            List<User> sorted = StatsUtil.sortByCompletion(members);

            JSONArray arr = new JSONArray();
            for (int i = 0; i < sorted.size(); i++) {
                User u = sorted.get(i);
                JSONObject obj = new JSONObject();
                obj.put("rank",          i + 1);
                obj.put("id",            u.getId());
                obj.put("name",          u.getName());
                obj.put("initials",      u.getInitials());
                obj.put("hasPhoto",      u.getPhotoPath() != null && !u.getPhotoPath().isEmpty());
                obj.put("monthPct",      u.getMonthPct());
                obj.put("monthStreak",   u.getMonthStreak());
                obj.put("overallStreak", u.getOverallStreak());
                obj.put("weekFullDays",  u.getWeekFullDays());
                arr.put(obj);
            }
            res.getWriter().write(arr.toString());

        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
