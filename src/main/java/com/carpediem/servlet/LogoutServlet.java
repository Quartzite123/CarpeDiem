package com.carpediem.servlet;

import com.carpediem.util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        AuthUtil.clearSession(req);
        res.sendRedirect(req.getContextPath() + "/login");
    }
}
