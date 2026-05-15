package com.carpediem.servlet;

import com.carpediem.dao.TaskDAO;
import com.carpediem.util.AuthUtil;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/task/rename")
public class TaskServlet extends HttpServlet {

    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        int userId  = AuthUtil.getUserId(req);
        int groupId = AuthUtil.getGroupId(req);
        if (userId == -1) { res.setStatus(401); return; }

        try {
            int    taskId  = Integer.parseInt(req.getParameter("taskId"));
            String newName = req.getParameter("name").trim();
            if (newName.isEmpty()) {
                res.getWriter().write(new JSONObject().put("error", "Name cannot be empty").toString());
                return;
            }
            taskDAO.rename(taskId, groupId, newName);
            res.getWriter().write(new JSONObject().put("ok", true).put("name", newName).toString());
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
