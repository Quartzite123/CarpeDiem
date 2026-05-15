package com.carpediem.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthUtil {

    /** Returns userId from session, or -1 if not logged in. */
    public static int getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return -1;
        Object id = session.getAttribute("userId");
        return (id instanceof Integer) ? (Integer) id : -1;
    }

    /** Returns groupId from session, or -1 if not set. */
    public static int getGroupId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return -1;
        Object id = session.getAttribute("groupId");
        return (id instanceof Integer) ? (Integer) id : -1;
    }

    /** Returns user's display name from session. */
    public static String getUserName(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return "";
        Object name = session.getAttribute("userName");
        return name != null ? name.toString() : "";
    }

    /**
     * Checks login. If not logged in, redirects to /login and returns false.
     * Usage: if (!AuthUtil.requireLogin(req, res)) return;
     */
    public static boolean requireLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (getUserId(req) == -1) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    /** Sets session attributes after successful login. */
    public static void setSession(HttpServletRequest req, int userId, int groupId, String userName) {
        HttpSession session = req.getSession(true);
        session.setAttribute("userId",  userId);
        session.setAttribute("groupId", groupId);
        session.setAttribute("userName", userName);
    }

    /** Invalidates the session (logout). */
    public static void clearSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
    }
}
