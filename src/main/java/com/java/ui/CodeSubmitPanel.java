package com.java.ui;

import com.java.model.Problem;
import com.java.model.SampleCode;
import com.java.model.Submission;
import com.java.service.DefaultJudgeService;
import com.java.service.ProblemService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Files;
import java.util.List;

public class CodeSubmitPanel extends JPanel implements Refreshable {
    private ProblemService problemService;
    private JComboBox<ProblemComboItem> problemCombo;
    private JComboBox<String> languageCombo;
    private JComboBox<String> expectedTypeCombo;
    private JTextArea codeArea;
    private JTable resultTable;
    private DefaultTableModel resultTableModel;
    private JLabel statusLabel;
    private SwingWorker<List<Submission>, Void> judgeWorker;
    private int currentTrialSampleCodeId = -1;
    private int currentTrialProblemId = -1;

    public CodeSubmitPanel(ProblemService problemService) {
        this.problemService = problemService;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG_DARK);
        setBorder(AppTheme.BORDER_EMPTY_LG);

        JLabel lblTitle = AppTheme.createHeadingLabel("💻 Nộp code mẫu & Chấm thử");

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(AppTheme.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Đề thi:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        problemCombo = new JComboBox<>();
        problemCombo.setPreferredSize(new Dimension(300, 28));
        topPanel.add(problemCombo, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton btnRefresh = new JButton("🔄");
        btnRefresh.addActionListener(e -> refreshProblems());
        topPanel.add(btnRefresh, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Ngôn ngữ:"), gbc);
        gbc.gridx = 1;
        languageCombo = new JComboBox<>(new String[]{"java", "cpp", "python"});
        topPanel.add(languageCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Kết quả mong đợi:"), gbc);
        gbc.gridx = 1;
        expectedTypeCombo = new JComboBox<>(new String[]{"AC", "WA", "TLE", "RE"});
        topPanel.add(expectedTypeCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        JButton btnUploadFile = new JButton("📁 Chọn file");
        btnUploadFile.addActionListener(e -> uploadCodeFile());
        topPanel.add(btnUploadFile, gbc);

        JPanel northPanel = new JPanel(new BorderLayout(0, 8));
        northPanel.setBackground(AppTheme.BG_DARK);
        northPanel.add(lblTitle, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        codeArea = AppTheme.createStyledTextArea(15, 50);
        add(new JScrollPane(codeArea), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBackground(AppTheme.BG_DARK);

        statusLabel = new JLabel("Sẵn sàng chấm");
        statusLabel.setForeground(AppTheme.TEXT_SECONDARY);
        rightPanel.add(statusLabel, BorderLayout.NORTH);

        String[] columns = {"Testcase", "Status", "Time (ms)", "Error"};
        resultTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        resultTable = new JTable(resultTableModel);
        resultTable.setFont(AppTheme.FONT_SMALL);
        resultTable.setRowHeight(32);
        resultTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        resultTable.getColumnModel().getColumn(1).setCellRenderer(new StatusRenderer());
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel codeListPanel = new JPanel(new BorderLayout(4, 4));
        codeListPanel.setBackground(AppTheme.BG_DARK);
        JButton btnViewCodes = new JButton("Xem code mẫu đã lưu");
        btnViewCodes.addActionListener(e -> viewExistingCodes());
        codeListPanel.add(btnViewCodes, BorderLayout.NORTH);
        rightPanel.add(codeListPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = AppTheme.createAccentButton("💾 Lưu code mẫu", AppTheme.ACCENT_GREEN);
        JButton btnJudge = AppTheme.createAccentButton("⚖️ Chấm thử", AppTheme.ACCENT_CYAN);
        btnSave.addActionListener(e -> saveCode());
        btnJudge.addActionListener(e -> runJudge());
        btnPanel.add(btnSave);
        btnPanel.add(btnJudge);
        add(btnPanel, BorderLayout.SOUTH);

        refreshProblems();
    }

    @Override
    public void refresh() {
        refreshProblems();
    }

    private void refreshProblems() {
        problemCombo.removeAllItems();
        List<Problem> list = problemService.getAllProblems();
        for (Problem p : list) {
            problemCombo.addItem(new ProblemComboItem(p.getId(), p.getTitle()));
        }
    }

    private void uploadCodeFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Code files", "java", "cpp", "py", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = Files.readString(chooser.getSelectedFile().toPath());
                codeArea.setText(content);
                String name = chooser.getSelectedFile().getName().toLowerCase();
                if (name.endsWith(".java")) languageCombo.setSelectedItem("java");
                else if (name.endsWith(".cpp")) languageCombo.setSelectedItem("cpp");
                else if (name.endsWith(".py")) languageCombo.setSelectedItem("python");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + e.getMessage());
            }
        }
    }

    private void viewExistingCodes() {
        ProblemComboItem selected = (ProblemComboItem) problemCombo.getSelectedItem();
        if (selected == null) return;

        List<SampleCode> codes = problemService.getSampleCodesByProblem(selected.id);
        if (codes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có code mẫu nào cho đề này.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (SampleCode sc : codes) {
            sb.append(String.format("[ID=%d] %s | %s | AI=%s\n", sc.getId(), sc.getLanguage(), sc.getExpectedType(), sc.isAiGenerated() ? "✓" : "✗"));
            sb.append("---\n");
        }
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Code mẫu đã lưu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveCode() {
        ProblemComboItem selected = (ProblemComboItem) problemCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Chọn đề thi!");
            return;
        }
        String code = codeArea.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Code trống!");
            return;
        }
        int id = problemService.addSampleCode(selected.id, code, (String) languageCombo.getSelectedItem(), (String) expectedTypeCombo.getSelectedItem(), false);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "✅ Lưu code thành công! ID = " + id);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Lưu code thất bại!");
        }
    }

    private void runJudge() {
        ProblemComboItem selected = (ProblemComboItem) problemCombo.getSelectedItem();
        if (selected == null) return;
        String code = codeArea.getText().trim();
        if (code.isEmpty()) return;

        if (judgeWorker != null && !judgeWorker.isDone()) {
            judgeWorker.cancel(true);
        }

        resultTableModel.setRowCount(0);
        statusLabel.setText("⏳ Đang chấm bài...");
        statusLabel.setForeground(AppTheme.ACCENT_YELLOW);

        // Reset trial ID if problem changed
        if (selected.id != currentTrialProblemId) {
            currentTrialProblemId = selected.id;
            currentTrialSampleCodeId = -1;
        }

        String language = (String) languageCombo.getSelectedItem();
        String expectedType = (String) expectedTypeCombo.getSelectedItem();

        if (currentTrialSampleCodeId <= 0) {
            currentTrialSampleCodeId = problemService.addSampleCode(selected.id, code, language, expectedType, false);
        } else {
            boolean ok = problemService.updateSampleCode(currentTrialSampleCodeId, code, language, expectedType);
            if (!ok) {
                statusLabel.setText("❌ Lỗi cập nhật code tạm.");
                return;
            }
        }

        if (currentTrialSampleCodeId <= 0) {
            statusLabel.setText("❌ Lỗi lưu code tạm.");
            return;
        }

        judgeWorker = new SwingWorker<>() {
            @Override
            protected List<Submission> doInBackground() {
                return problemService.runJudging(selected.id, currentTrialSampleCodeId, new DefaultJudgeService());
            }

            @Override
            protected void done() {
                try {
                    List<Submission> results = get();
                    int ac = 0, wa = 0, tle = 0, other = 0;
                    for (Submission sub : results) {
                        String errorPreview = sub.getErrorMessage() != null ?
                            (sub.getErrorMessage().length() > 50 ? sub.getErrorMessage().substring(0, 50) + "..." : sub.getErrorMessage()) : "";
                        resultTableModel.addRow(new Object[]{
                            "TC#" + sub.getTestcaseId(),
                            sub.getStatus(),
                            sub.getExecutionTime() + "ms",
                            errorPreview
                        });
                        switch (sub.getStatus()) {
                            case "AC" -> ac++;
                            case "WA" -> wa++;
                            case "TLE" -> tle++;
                            default -> other++;
                        }
                    }
                    String verdict;
                    if (wa == 0 && tle == 0 && other == 0 && ac > 0) verdict = "✅ All AC!";
                    else verdict = String.format("AC: %d | WA: %d | TLE: %d | Other: %d", ac, wa, tle, other);
                    statusLabel.setText(verdict);
                    statusLabel.setForeground(wa > 0 || tle > 0 ? AppTheme.ACCENT_RED : AppTheme.ACCENT_GREEN);
                } catch (Exception ex) {
                    if (!isCancelled()) {
                        statusLabel.setText("❌ Lỗi: " + ex.getMessage());
                        statusLabel.setForeground(AppTheme.ACCENT_RED);
                    }
                } finally {
                    judgeWorker = null;
                }
            }
        };
        judgeWorker.execute();
    }

    private static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? AppTheme.BG_DARK : new Color(0x19, 0x24, 0x34));
                c.setForeground(AppTheme.TEXT_PRIMARY);
            }
            return c;
        }
    }

    private static class ProblemComboItem {
        int id;
        String title;
        ProblemComboItem(int id, String title) { this.id = id; this.title = title; }
        @Override public String toString() { return "[" + id + "] " + title; }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));

            String status = value != null ? value.toString() : "";
            Color bg = AppTheme.statusColor(status);
            label.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 50));
            label.setForeground(bg.brighter());
            label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            return label;
        }
    }
}
