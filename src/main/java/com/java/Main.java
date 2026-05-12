package com.java;

import com.java.ui.AppTheme;
import com.java.ui.MainFrame;
import com.java.util.DatabaseConnection;
import com.java.util.FileManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== APP STARTING ===");
        try {
            FileManager.initFolders();
            System.out.println("Folders OK");
            DatabaseConnection.testConnection();
            System.out.println("DB OK");

            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("Applying theme...");
                    AppTheme.applyGlobalTheme();
                    System.out.println("Theme OK");
                    System.out.println("Creating MainFrame...");
                    MainFrame frame = new MainFrame();
                    System.out.println("MainFrame OK, setting visible...");
                    frame.setVisible(true);
                    System.out.println("=== WINDOW SHOULD BE VISIBLE NOW ===");
                } catch (Exception e) {
                    System.err.println("GUI ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("STARTUP ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
