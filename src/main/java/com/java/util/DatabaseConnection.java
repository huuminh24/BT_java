package com.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean initialized = false;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Khong tim thay file config.properties, su dung gia tri mac dinh");
                url = "jdbc:mysql://localhost:3306/JudgeSystem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
                username = "root";
                password = "123";
                driver = "com.mysql.cj.jdbc.Driver";
            } else {
                props.load(input);
                url = ensureUtf8Params(props.getProperty("db.url", "jdbc:mysql://localhost:3306/JudgeSystem"));
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "123");
                driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            }
            Class.forName(driver);
            initialized = true;
            System.out.println("Ket noi CSDL da cau hinh: " + url);
        } catch (IOException e) {
            System.err.println("Loi doc config.properties: " + e.getMessage());
            url = "jdbc:mysql://localhost:3306/JudgeSystem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
            username = "root";
            password = "123";
            driver = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(driver);
                initialized = true;
            } catch (ClassNotFoundException ex) {
                System.err.println("Khong tim thay driver: " + ex.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Khong tim thay driver MySQL: " + e.getMessage());
        }
    }

    private static String ensureUtf8Params(String url) {
        if (url == null || url.isBlank()) {
            return "jdbc:mysql://localhost:3306/JudgeSystem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        }
        if (url.contains("characterEncoding")) {
            return url;
        }
        String sep = url.contains("?") ? "&" : "?";
        return url + sep + "useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            loadConfig();
        }
        return DriverManager.getConnection(url, username, password);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Ket noi CSDL thanh cong!");
            }
        } catch (SQLException e) {
            System.err.println("Ket noi CSDL that bai: " + e.getMessage());
        }
    }
}
