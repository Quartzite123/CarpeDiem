package com.carpediem.servlet;

import com.carpediem.dao.*;
import com.carpediem.model.*;
import com.carpediem.util.AuthUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private final UserDAO  userDAO  = new UserDAO();
    private final GroupDAO groupDAO = new GroupDAO();
    private final TaskDAO  taskDAO  = new TaskDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String name       = req.getParameter("name");
        String email      = req.getParameter("email");
        String password   = req.getParameter("password");
        String action     = req.getParameter("action");   // "create" or "join"
        String inviteCode = req.getParameter("inviteCode");
        String groupName  = req.getParameter("groupName");

        try {
            // Check email not taken
            if (userDAO.findByEmail(email) != null) {
                req.setAttribute("error", "Email already registered");
                req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, res);
                return;
            }

            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(hash);

            int groupId;
            if ("create".equals(action)) {
                // Create new group, seed default tasks
                groupId = groupDAO.insert(groupName);
                taskDAO.seedDefaults(groupId);
            } else {
                // Join existing group via invite code
                Group group = groupDAO.findByInviteCode(inviteCode);
                if (group == null) {
                    req.setAttribute("error", "Invalid invite code");
                    req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, res);
                    return;
                }
                groupId = group.getId();
            }

            user.setGroupId(groupId);
            int userId = userDAO.insert(user);
            AuthUtil.setSession(req, userId, groupId, name);
            res.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (Exception e) {
            req.setAttribute("error", "Server error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, res);
        }
    }
}
