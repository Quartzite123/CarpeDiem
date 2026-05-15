package com.carpediem.servlet;

import com.carpediem.dao.UserDAO;
import com.carpediem.model.User;
import com.carpediem.util.DBConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;

@WebServlet("/api/photo")
public class PhotoServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            User u = userDAO.findById(id);
            if (u == null || u.getPhotoPath() == null) { res.setStatus(404); return; }

            File f = new File(DBConnection.UPLOAD_BASE + "/photos/" + u.getPhotoPath());
            if (!f.exists()) { res.setStatus(404); return; }

            String mime = Files.probeContentType(f.toPath());
            res.setContentType(mime != null ? mime : "image/jpeg");
            try (InputStream in = new FileInputStream(f)) {
                in.transferTo(res.getOutputStream());
            }
        } catch (Exception e) { res.setStatus(500); }
    }
}
