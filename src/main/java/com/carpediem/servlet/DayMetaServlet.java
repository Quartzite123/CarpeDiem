package com.carpediem.servlet;

import com.carpediem.dao.DayMetaDAO;
import com.carpediem.util.AuthUtil;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/api/meta")
public class DayMetaServlet extends HttpServlet {

    private final DayMetaDAO metaDAO = new DayMetaDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        int userId = AuthUtil.getUserId(req);
        if (userId == -1) { res.setStatus(401); return; }

        try {
            String mood  = req.getParameter("mood");
            String hours = req.getParameter("hours");
            String notes = req.getParameter("notes");
            double h = (hours != null && !hours.isEmpty()) ? Double.parseDouble(hours) : 0;
            metaDAO.upsert(userId, LocalDate.now(), mood, h, notes);
            res.getWriter().write(new JSONObject().put("ok", true).toString());
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
