package com.carpediem.servlet;

import com.carpediem.dao.TodoDAO;
import com.carpediem.model.Todo;
import com.carpediem.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/todo")
public class TodoServlet extends HttpServlet {

    private final TodoDAO todoDAO = new TodoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;
        int userId = AuthUtil.getUserId(req);
        try {
            List<Todo> todos = todoDAO.getByUser(userId);
            req.setAttribute("todos", todos);
            req.getRequestDispatcher("/WEB-INF/views/todo.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!AuthUtil.requireLogin(req, res)) return;
        int userId = AuthUtil.getUserId(req);
        String action = req.getParameter("action");

        try {
            switch (action == null ? "" : action) {
                case "add" -> {
                    // Change 6c: Demo todo spam protection — max 20 todos
                    boolean isDemo = Boolean.TRUE.equals(req.getSession().getAttribute("isDemo"));
                    if (isDemo) {
                        int count = todoDAO.countByUser(userId);
                        if (count >= 20) {
                            res.sendRedirect(req.getContextPath() + "/todo?error=limit");
                            return;
                        }
                    }
                    Todo t = new Todo();
                    t.setUserId(userId);
                    t.setTitle(req.getParameter("title"));
                    t.setSubjectTag(req.getParameter("subject"));
                    String due = req.getParameter("dueDate");
                    if (due != null && !due.isEmpty()) t.setDueDate(LocalDate.parse(due));
                    t.setPriority(req.getParameter("priority") != null ? req.getParameter("priority") : "MEDIUM");
                    todoDAO.insert(t);
                }
                case "toggle" -> todoDAO.toggleDone(Integer.parseInt(req.getParameter("id")), userId);
                case "delete" -> todoDAO.delete(Integer.parseInt(req.getParameter("id")), userId);
            }
            res.sendRedirect(req.getContextPath() + "/todo");
        } catch (Exception e) { throw new ServletException(e); }
    }
}
