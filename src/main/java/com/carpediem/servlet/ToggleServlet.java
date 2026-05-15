package com.carpediem.servlet;

import com.carpediem.dao.LogDAO;
import com.carpediem.util.AuthUtil;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/api/toggle")
public class ToggleServlet extends HttpServlet {

    private final LogDAO logDAO = new LogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        int userId = AuthUtil.getUserId(req);
        if (userId == -1) { res.setStatus(401); return; }

        try {
            int taskId = Integer.parseInt(req.getParameter("taskId"));
            LocalDate date = LocalDate.parse(req.getParameter("date")); // YYYY-MM-DD

            // Only allow toggling today
            if (!date.equals(LocalDate.now())) {
                res.setStatus(403);
                res.getWriter().write(new JSONObject().put("error", "Can only edit today").toString());
                return;
            }

            boolean newState = logDAO.toggle(userId, taskId, date);
            JSONObject json = new JSONObject();
            json.put("done", newState);
            json.put("taskId", taskId);
            json.put("date", date.toString());
            res.getWriter().write(json.toString());

        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
