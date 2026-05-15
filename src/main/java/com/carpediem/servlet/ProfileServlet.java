package com.carpediem.servlet;

import com.carpediem.dao.*;
import com.carpediem.model.*;
import com.carpediem.util.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;
        int userId = AuthUtil.getUserId(req);
        try {
            User me = userDAO.findById(userId);
            req.setAttribute("me", me);
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;

        // Change 6b: Block profile edits/uploads in demo mode
        boolean isDemo = Boolean.TRUE.equals(req.getSession().getAttribute("isDemo"));
        if (isDemo) {
            res.sendRedirect(req.getContextPath() + "/profile?error=demo");
            return;
        }

        int userId = AuthUtil.getUserId(req);

        try {
            if (ServletFileUpload.isMultipartContent(req)) {
                // Handle photo upload with size limit (Change 6a)
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setFileSizeMax(2 * 1024 * 1024);  // 2MB for photos

                List<FileItem> items;
                try {
                    items = upload.parseRequest(req);
                } catch (FileUploadBase.FileSizeLimitExceededException |
                         FileUploadBase.SizeLimitExceededException e) {
                    res.sendRedirect(req.getContextPath() + "/profile?error=size");
                    return;
                }

                String newName = null;
                for (FileItem item : items) {
                    if (item.isFormField() && "name".equals(item.getFieldName())) {
                        newName = item.getString("UTF-8").trim();
                    } else if (!item.isFormField() && item.getSize() > 0) {
                        String ext = item.getName().substring(item.getName().lastIndexOf('.'));
                        String fileName = "photo_" + userId + ext;
                        File dest = new File(DBConnection.UPLOAD_BASE + "/photos/" + fileName);
                        item.write(dest);
                        userDAO.updatePhoto(userId, fileName);
                    }
                }
                if (newName != null && !newName.isEmpty()) {
                    userDAO.updateName(userId, newName);
                    req.getSession().setAttribute("userName", newName);
                }
            } else {
                String name = req.getParameter("name");
                if (name != null && !name.trim().isEmpty()) {
                    userDAO.updateName(userId, name.trim());
                    req.getSession().setAttribute("userName", name.trim());
                }
            }
            res.sendRedirect(req.getContextPath() + "/profile?updated=1");
        } catch (Exception e) { throw new ServletException(e); }
    }
}
