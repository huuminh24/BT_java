package com.java.service;

import com.java.model.AIResponse;
import com.java.model.Problem;

public interface AIService {

    /**
     * Phân tích đề thi (text hoặc ảnh) và sinh testcase + checker script.
     * @param problem đề thi đã nhập (có title, description, imagePath)
     * @return AIResponse chứa testcases, checker, explanation
     */
    AIResponse analyzeProblem(Problem problem);

    /**
     * Tự động sinh code mẫu AC cho đề thi.
     * @param problemDescription mô tả đề thi
     * @param language ngôn ngữ: "java", "cpp"
     * @return source code mẫu
     */
    String generateSolution(String problemDescription, String language);

    /**
     * Sinh checker script tùy chỉnh nếu đề yêu cầu chấm linh hoạt.
     * @param problemDescription mô tả đề thi
     * @return script checker (Python/Java tùy prompt)
     */
    String generateChecker(String problemDescription);
}
