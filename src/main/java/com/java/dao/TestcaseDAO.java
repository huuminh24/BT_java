package com.java.dao;

import com.java.model.Testcase;
import com.java.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestcaseDAO {

    // 1. Hàm Thêm Testcase mới
    public boolean addTestcase(Testcase t) {
        String sql = "INSERT INTO Testcases (problem_id, input_data, expected_output, testcase_type, is_ai_generated) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, t.getProblemId());
            ps.setString(2, t.getInputData());
            ps.setString(3, t.getExpectedOutput());
            ps.setString(4, t.getTestcaseType());
            ps.setBoolean(5, t.isAiGenerated());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        t.setId(rs.getInt(1));
                    }
                }
            }
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Hàm Lấy danh sách Testcase
    public List<Testcase> getTestcasesByProblemId(int problemId) {
        List<Testcase> list = new ArrayList<>();
        String sql = "SELECT * FROM Testcases WHERE problem_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, problemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String input = rs.getString("input_data");
                    String output = rs.getString("expected_output");
                    String type = rs.getString("testcase_type");
                    boolean aiGen = rs.getBoolean("is_ai_generated");

                    list.add(new Testcase(id, problemId, input, output, type, aiGen));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // 3. Hàm CẬP NHẬT (Sửa) Testcase
    public boolean updateTestcase(Testcase t) {
        String sql = "UPDATE Testcases SET input_data = ?, expected_output = ?, testcase_type = ?, is_ai_generated = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getInputData());
            ps.setString(2, t.getExpectedOutput());
            ps.setString(3, t.getTestcaseType());
            ps.setBoolean(4, t.isAiGenerated());
            ps.setInt(5, t.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Hàm XÓA Testcase
    public boolean deleteTestcase(int id) {
        String sql = "DELETE FROM Testcases WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}