package com.java;

import com.java.ui.AppTheme;
import com.java.ui.MainFrame;
import com.java.util.DatabaseConnection;
import com.java.util.FileManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FileManager.initFolders();
        DatabaseConnection.testConnection();

        SwingUtilities.invokeLater(() -> {
            AppTheme.applyGlobalTheme();
            new MainFrame().setVisible(true);
        });
    }
}
