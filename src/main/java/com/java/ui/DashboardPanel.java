package com.java.ui;

import com.java.model.Problem;
import com.java.model.Submission;
import com.java.service.ProblemService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private ProblemService problemService;
    private JLabel statsLabel;
    private SwingWorker<Void, Void> statsWorker;

    public DashboardPanel(MainFrame mainFrame, ProblemService problemService) {
        this.mainFrame = mainFrame;
        this.problemService = problemService;
        setLayout(new BorderLayout(0, 24));
        setBackground(AppTheme.BG_DARK);
        setBorder(AppTheme.BORDER_EMPTY_LG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_DARK);
        header.setOpaque(false);
        JLabel welcome = AppTheme.createHeadingLabel("Dashboard");
        welcome.setFont(AppTheme.FONT_TITLE);
        statsLabel = AppTheme.createBodyLabel("Đang tải thống kê...");
        header.add(welcome, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setBackground(AppTheme.BG_DARK);
        statsRow.setOpaque(false);
        statsRow.add(createStatCard("Đề thi", "0", AppTheme.ACCENT_CYAN));
        statsRow.add(createStatCard("Testcases", "0", AppTheme.ACCENT_PURPLE));
        statsRow.add(createStatCard("Code mẫu", "0", AppTheme.ACCENT_GREEN));
        statsRow.add(createStatCard("Submissions", "0", AppTheme.ACCENT_YELLOW));
        add(statsRow, BorderLayout.CENTER);

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(AppTheme.BG_DARK);
        grid.setOpaque(false);

        grid.add(createCard("📝 Nhập đề thi", "Tạo bài toán mới với text hoặc ảnh", AppTheme.ACCENT_CYAN, "PROBLEM_ENTRY"));
        grid.add(createCard("🤖 AI Phân tích", "Sinh testcase & code tự động bằng Gemini", AppTheme.ACCENT_PURPLE, "AI_PANEL"));
        grid.add(createCard("💻 Nộp code mẫu", "Nhập AC/WA/TLE và chấm thử", AppTheme.ACCENT_GREEN, "CODE_SUBMIT"));
        grid.add(createCard("📊 Kết quả chấm", "Xem submissions với màu AC/WA/TLE", AppTheme.ACCENT_YELLOW, "RESULT"));
        grid.add(createCard("📖 Hướng dẫn", "Xem tài liệu sử dụng chi tiết", AppTheme.TEXT_SECONDARY, "DOCS"));
        grid.add(createCard("🚪 Thoát", "Đóng ứng dụng", AppTheme.ACCENT_RED, "EXIT"));

        add(grid, BorderLayout.SOUTH);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refreshStats();
    }

    public void refreshStats() {
        if (statsWorker != null && !statsWorker.isDone()) {
            statsWorker.cancel(true);
        }
        statsWorker = new SwingWorker<>() {
            private int problemCount = 0;
            private int tcCount = 0;
            private int codeCount = 0;
            private int subCount = 0;
            private int acCount = 0;
            private int waCount = 0;

            @Override
            protected Void doInBackground() {
                try {
                    List<Problem> problems = problemService.getAllProblems();
                    problemCount = problems.size();
                    for (Problem p : problems) {
                        tcCount += problemService.getTestcasesByProblem(p.getId()).size();
                        codeCount += problemService.getSampleCodesByProblem(p.getId()).size();
                        List<Submission> subs = problemService.getSubmissionsByProblem(p.getId());
                        subCount += subs.size();
                        for (Submission s : subs) {
                            if ("AC".equals(s.getStatus())) acCount++;
                            if ("WA".equals(s.getStatus())) waCount++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                statsLabel.setText(String.format("AC: %d | WA: %d | Tổng: %d submissions", acCount, waCount, subCount));
                updateStatCardValue(0, String.valueOf(problemCount));
                updateStatCardValue(1, String.valueOf(tcCount));
                updateStatCardValue(2, String.valueOf(codeCount));
                updateStatCardValue(3, String.valueOf(subCount));
            }
        };
        statsWorker.execute();
    }

    private void updateStatCardValue(int index, String value) {
        Component centerPanel = getComponent(1);
        if (centerPanel instanceof JPanel) {
            Component card = ((JPanel) centerPanel).getComponent(index);
            if (card instanceof JPanel) {
                for (Component c : ((JPanel) card).getComponents()) {
                    if (c instanceof JLabel && ((JLabel) c).getFont().equals(AppTheme.FONT_TITLE)) {
                        ((JLabel) c).setText(value);
                        break;
                    }
                }
            }
        }
    }

    private JPanel createStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                AppTheme.BORDER_EMPTY_MD
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(AppTheme.FONT_TITLE);
        valueLabel.setForeground(accent);

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_SECONDARY);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createCard(String title, String desc, Color accent, String action) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                AppTheme.BORDER_EMPTY_MD
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_SUBHEAD);
        lblTitle.setForeground(accent);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(AppTheme.FONT_SMALL);
        lblDesc.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        textPanel.setBackground(AppTheme.BG_CARD);
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblDesc);

        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(AppTheme.BG_CARD.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(AppTheme.BG_CARD);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if ("EXIT".equals(action)) System.exit(0);
                else if ("DOCS".equals(action)) {
                    JOptionPane.showMessageDialog(card,
                        "HƯỚNG DẪN SỬ DỤNG\n\n" +
                        "1. Nhập đề thi: Vào 'Nhập đề thi' → Điền tiêu đề, nội dung → Chọn ảnh (nếu có) → Lưu\n" +
                        "2. AI Phân tích: Vào 'AI Phân tích' → Chọn đề → Nhấn 'Phân tích' → AI sẽ sinh testcase & code\n" +
                        "3. Nộp code: Vào 'Nộp code mẫu' → Chọn đề → Dán code → Chấm thử\n" +
                        "4. Xem kết quả: Vào 'Kết quả chấm' → Tải kết quả\n\n" +
                        "Lưu ý: Cần cấu hình API Key Gemini trong config.properties để dùng AI",
                        "Hướng dẫn sử dụng", JOptionPane.INFORMATION_MESSAGE);
                }
                else mainFrame.showPanel(action);
            }
        });
        return card;
    }
}
