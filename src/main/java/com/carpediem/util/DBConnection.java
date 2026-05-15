package com.carpediem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility.
 * Reads credentials from environment variables at runtime,
 * with local fallback values for development.
 */
public class DBConnection {

    // Demo user constant — used by DemoServlet and DemoResetService
    public static final String DEMO_EMAIL = "demo@carpediem.app";

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    public  static final String UPLOAD_BASE;

    static {
        // Read from Railway env vars, fall back to local dev defaults
        String envUrl = System.getenv("DB_URL");
        URL = (envUrl != null && !envUrl.isEmpty())
            ? envUrl
            : "jdbc:mysql://localhost:3306/carpediem" +
              "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" +
              "&useUnicode=true&characterEncoding=UTF-8";

        String envUser = System.getenv("DB_USER");
        USER = (envUser != null && !envUser.isEmpty()) ? envUser : "root";

        String envPass = System.getenv("DB_PASSWORD");
        PASSWORD = (envPass != null && !envPass.isEmpty()) ? envPass : "carpediem123";

        String envUpload = System.getenv("UPLOAD_PATH");
        UPLOAD_BASE = (envUpload != null && !envUpload.isEmpty())
            ? envUpload
            : System.getProperty("user.home") + java.io.File.separator + "carpediem-uploads";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }

        // Create upload dirs on startup
        new java.io.File(UPLOAD_BASE + "/pdfs").mkdirs();
        new java.io.File(UPLOAD_BASE + "/photos").mkdirs();
        new java.io.File(UPLOAD_BASE + "/timetables").mkdirs();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
