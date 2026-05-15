package com.carpediem.servlet;

import com.carpediem.dao.TimetableDAO;
import com.carpediem.model.Timetable;
import com.carpediem.util.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;
import java.util.UUID;

@WebServlet("/timetable")
public class TimetableServlet extends HttpServlet {

    private final TimetableDAO ttDAO = new TimetableDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;
        int userId = AuthUtil.getUserId(req);

        String action = req.getParameter("action");
        if ("serve".equals(action)) {
            try {
                Timetable tt = ttDAO.getByUser(userId);
                if (tt == null || !tt.hasBase()) { res.setStatus(404); return; }
                File f = new File(DBConnection.UPLOAD_BASE + "/timetables/" + tt.getBaseFilePath());
                if (!f.exists()) { res.setStatus(404); return; }
                String mime = "pdf".equals(tt.getBaseFileType()) ? "application/pdf" : "image/jpeg";
                res.setContentType(mime);
                try (InputStream in = new FileInputStream(f)) { in.transferTo(res.getOutputStream()); }
            } catch (Exception e) { throw new ServletException(e); }
            return;
        }

        try {
            Timetable tt = ttDAO.getByUser(userId);
            req.setAttribute("timetable", tt);
            req.getRequestDispatcher("/WEB-INF/views/timetable.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;

        // Change 6b: Block uploads in demo mode
        boolean isDemo = Boolean.TRUE.equals(req.getSession().getAttribute("isDemo"));
        if (isDemo) {
            res.sendRedirect(req.getContextPath() + "/timetable?error=demo");
            return;
        }

        int userId = AuthUtil.getUserId(req);
        String action = req.getParameter("action");

        // Save personal edits (JSON from JS)
        if ("saveEdits".equals(action)) {
            try {
                String edits = req.getParameter("edits");
                ttDAO.saveEdits(userId, edits);
                res.setContentType("application/json");
                res.getWriter().write("{\"ok\":true}");
            } catch (Exception e) { throw new ServletException(e); }
            return;
        }

        // Upload base timetable with size limits (Change 6a)
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(5 * 1024 * 1024);  // 5MB per file
            upload.setSizeMax(6 * 1024 * 1024);       // 6MB total request

            List<FileItem> items;
            try {
                items = upload.parseRequest(req);
            } catch (FileUploadBase.FileSizeLimitExceededException |
                     FileUploadBase.SizeLimitExceededException e) {
                res.sendRedirect(req.getContextPath() + "/timetable?error=size");
                return;
            }

            for (FileItem item : items) {
                if (!item.isFormField() && item.getSize() > 0) {
                    String orig = item.getName().toLowerCase();
                    String type = orig.endsWith(".pdf") ? "pdf" : "image";
                    String ext  = orig.endsWith(".pdf") ? ".pdf" : ".jpg";
                    String fileName = UUID.randomUUID() + ext;
                    File dest = new File(DBConnection.UPLOAD_BASE + "/timetables/" + fileName);
                    item.write(dest);
                    ttDAO.upsertBase(userId, fileName, type);
                }
            }
            res.sendRedirect(req.getContextPath() + "/timetable");
        } catch (Exception e) { throw new ServletException(e); }
    }
}
