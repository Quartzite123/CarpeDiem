package com.carpediem.servlet;

import com.carpediem.dao.PDFDAO;
import com.carpediem.model.PDF;
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

@WebServlet("/notes")
public class PDFServlet extends HttpServlet {

    private final PDFDAO pdfDAO = new PDFDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;
        int groupId = AuthUtil.getGroupId(req);

        String action = req.getParameter("action");

        if ("download".equals(action)) {
            try {
                int pdfId = Integer.parseInt(req.getParameter("id"));
                PDF pdf = pdfDAO.getById(pdfId);
                if (pdf == null) { res.setStatus(404); return; }
                File f = new File(DBConnection.UPLOAD_BASE + "/pdfs/" + pdf.getFilePath());
                if (!f.exists()) { res.setStatus(404); return; }
                res.setContentType("application/pdf");
                res.setHeader("Content-Disposition", "inline; filename=\"" + pdf.getOriginalName() + "\"");
                try (InputStream in = new FileInputStream(f)) { in.transferTo(res.getOutputStream()); }
            } catch (Exception e) { throw new ServletException(e); }
            return;
        }

        // Default: show notes library
        try {
            List<PDF> pdfs = pdfDAO.getByGroup(groupId);
            req.setAttribute("pdfs",    pdfs);
            req.setAttribute("groupId", groupId);
            req.getRequestDispatcher("/WEB-INF/views/notes.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;

        // Change 6b: Block uploads in demo mode
        boolean isDemo = Boolean.TRUE.equals(req.getSession().getAttribute("isDemo"));
        if (isDemo) {
            res.sendRedirect(req.getContextPath() + "/notes?error=demo");
            return;
        }

        int userId  = AuthUtil.getUserId(req);
        int groupId = AuthUtil.getGroupId(req);

        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                PDF pdf = pdfDAO.getById(id);
                if (pdf != null && pdf.getUserId() == userId) {
                    new File(DBConnection.UPLOAD_BASE + "/pdfs/" + pdf.getFilePath()).delete();
                    pdfDAO.delete(id, userId);
                }
                res.sendRedirect(req.getContextPath() + "/notes");
            } catch (Exception e) { throw new ServletException(e); }
            return;
        }

        // Upload with size limits (Change 6a)
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
                res.sendRedirect(req.getContextPath() + "/notes?error=size");
                return;
            }

            String title = "", subject = "";
            FileItem fileItem = null;
            for (FileItem item : items) {
                if (item.isFormField()) {
                    if ("title".equals(item.getFieldName()))   title   = item.getString("UTF-8");
                    if ("subject".equals(item.getFieldName())) subject = item.getString("UTF-8");
                } else {
                    fileItem = item;
                }
            }

            if (fileItem != null && fileItem.getSize() > 0) {
                String fileName = UUID.randomUUID() + ".pdf";
                File dest = new File(DBConnection.UPLOAD_BASE + "/pdfs/" + fileName);
                fileItem.write(dest);

                PDF pdf = new PDF();
                pdf.setUserId(userId); pdf.setGroupId(groupId);
                pdf.setTitle(title.isEmpty() ? fileItem.getName() : title);
                pdf.setSubjectTag(subject);
                pdf.setFilePath(fileName);
                pdf.setOriginalName(fileItem.getName());
                pdfDAO.insert(pdf);
            }
            res.sendRedirect(req.getContextPath() + "/notes");
        } catch (Exception e) { throw new ServletException(e); }
    }
}
