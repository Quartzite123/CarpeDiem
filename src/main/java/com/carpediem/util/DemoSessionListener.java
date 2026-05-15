package com.carpediem.util;

import com.carpediem.servlet.DemoServlet;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Change 6d: Decrements the active demo session counter when a demo session expires/is destroyed.
 */
@WebListener
public class DemoSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // No action needed on creation — DemoServlet.activeDemoSessions.incrementAndGet() is called there
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object isDemo = se.getSession().getAttribute("isDemo");
        if (Boolean.TRUE.equals(isDemo)) {
            int current = DemoServlet.activeDemoSessions.decrementAndGet();
            if (current < 0) DemoServlet.activeDemoSessions.set(0); // safety floor
        }
    }
}
