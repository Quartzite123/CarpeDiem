package com.carpediem.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Change 7 — DemoResetListener
 * On startup: seeds demo data after 3s delay.
 * Schedules midnight reset every day via ScheduledExecutorService.
 */
@WebListener
public class DemoResetListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "demo-reset-scheduler");
            t.setDaemon(true);
            return t;
        });

        // Seed on startup with 3s delay (DB may not be ready instantly)
        scheduler.schedule(() -> {
            System.out.println("[DemoReset] Running startup seed...");
            DemoResetService.reset();
        }, 3, TimeUnit.SECONDS);

        // Schedule daily midnight reset (Asia/Kolkata)
        long initialDelay = secondsUntilMidnight();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[DemoReset] Running midnight reset...");
            DemoResetService.reset();
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        System.out.println("[DemoReset] Scheduler started. Next midnight reset in "
                           + initialDelay + "s.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.shutdownNow();
    }

    /** Seconds from now until next midnight (Asia/Kolkata). */
    private long secondsUntilMidnight() {
        ZoneId zone = ZoneId.of("Asia/Kolkata");
        ZonedDateTime now      = ZonedDateTime.now(zone);
        ZonedDateTime midnight = ZonedDateTime.of(
            LocalDate.now(zone).plusDays(1),
            LocalTime.MIDNIGHT,
            zone);
        return Math.max(1, midnight.toEpochSecond() - now.toEpochSecond());
    }
}
