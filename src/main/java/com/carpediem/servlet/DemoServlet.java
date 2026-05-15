package com.carpediem.servlet;

import com.carpediem.dao.UserDAO;
import com.carpediem.model.User;
import com.carpediem.util.AuthUtil;
import com.carpediem.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Change 5: Demo mode servlet — auto-logs in as demo@carpediem.app.
 * Change 6d: Caps simultaneous demo sessions at 10.
 * Change 6e: Rate-limits demo login per IP (30s cooldown).
 */
@WebServlet("/demo")
public class DemoServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    // Change 6d: Active demo session counter (decremented by DemoSessionListener)
    public static final AtomicInteger activeDemoSessions = new AtomicInteger(0);
    private static final int MAX_DEMO_SESSIONS = 10;

    // Change 6e: IP-based rate limiter
    private static final ConcurrentHashMap<String, Long> lastDemoAccess = new ConcurrentHashMap<>();
    private static final long DEMO_COOLDOWN_MS = 30_000; // 30 seconds

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/demo.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Change 6e: IP rate limiting
        String ip = req.getRemoteAddr();
        long now  = System.currentTimeMillis();
        Long last = lastDemoAccess.get(ip);
        if (last != null && (now - last) < DEMO_COOLDOWN_MS) {
            req.setAttribute("error", "Please wait 30 seconds before trying demo again.");
            req.getRequestDispatcher("/WEB-INF/views/demo.jsp").forward(req, res);
            return;
        }

        // Periodic cleanup of old IP entries to prevent memory growth
        if (lastDemoAccess.size() > 1000) {
            long cutoff = System.currentTimeMillis() - 3_600_000;
            lastDemoAccess.entrySet().removeIf(e -> e.getValue() < cutoff);
        }
        lastDemoAccess.put(ip, now);

        // Change 6d: Session cap
        if (activeDemoSessions.get() >= MAX_DEMO_SESSIONS) {
            req.setAttribute("error", "Demo is busy right now. Please try again in a few minutes.");
            req.getRequestDispatcher("/WEB-INF/views/demo.jsp").forward(req, res);
            return;
        }

        try {
            User demo = userDAO.findByEmail(DBConnection.DEMO_EMAIL);
            if (demo == null) {
                req.setAttribute("error", "Demo account not set up yet. Please try again later.");
                req.getRequestDispatcher("/WEB-INF/views/demo.jsp").forward(req, res);
                return;
            }

            // Invalidate any old session first, then create fresh
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            // Use AuthUtil.setSession with correct signature (req, userId, groupId, userName)
            AuthUtil.setSession(req, demo.getId(), demo.getGroupId(), demo.getName());
            req.getSession(false).setAttribute("isDemo", true);

            activeDemoSessions.incrementAndGet();
            res.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
