package com.java.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.java.model.AIResponse;
import com.java.model.Problem;
import com.java.model.Testcase;
import okhttp3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class GeminiAIService implements AIService {
    private static final String API_URL_TEMPLATE;
    private static final String API_KEY;
    private static final String API_FORMAT; // "gemini" or "openai"
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    private static final String API_MODEL;

    static {
        Properties props = new Properties();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
        String key = "";
        String format = "gemini";
        String model = "gemini-2.5-flash";
        try (var input = GeminiAIService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                url = props.getProperty("ai.api.url", url);
                key = props.getProperty("ai.api.key", "");
                format = props.getProperty("ai.format", "gemini");
                model = props.getProperty("ai.model", model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        API_URL_TEMPLATE = url;
        API_KEY = key;
        API_FORMAT = format;
        API_MODEL = model;
    }

    @Override
    public AIResponse analyzeProblem(Problem problem) {
        AIResponse response = new AIResponse();
        if (API_KEY == null || API_KEY.isBlank() || API_KEY.contains("YOUR")) {
            response.setSuccess(false);
            response.setErrorMessage("Chưa cấu hình API Key trong config.properties (ai.api.key)");
            return response;
        }

        try {
            String prompt = buildAnalyzePrompt(problem);
            String jsonResponse = callGeminiAPI(prompt, problem.getImagePath());
            response = parseAnalyzeResponse(jsonResponse);
        } catch (Exception e) {
            // Retry once with truncated continuation prompt
            try {
                String retryPrompt = "Bạn vừa bị cắt JSON giữa chừng. Hãy hoàn thành JSON bị cắt với cùng cấu trúc. "
                    + "Chỉ trả về phần còn thiếu của JSON (testcases, checker_needed, checker_script). "
                    + "Không giải thích, chỉ trả về JSON.";
                String jsonResponse = callGeminiAPI(retryPrompt, null);
                AIResponse retryResponse = parseAnalyzeResponse(jsonResponse);
                if (retryResponse.isSuccess()) {
                    return retryResponse;
                }
            } catch (Exception retryEx) {
                // ignore retry error, use original error
            }
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public String generateSolution(String problemDescription, String language) {
        try {
            String prompt = "Hãy viết code " + language + " giải bài toán sau. Chỉ trả về code, không giải thích.\n\n" + problemDescription;
            String jsonResponse = callGeminiAPI(prompt, null);
            return extractTextFromResponse(jsonResponse);
        } catch (Exception e) {
            return "// Lỗi sinh code: " + e.getMessage();
        }
    }

    @Override
    public String generateChecker(String problemDescription) {
        try {
            String prompt = "Hãy viết một script Python checker cho bài toán sau. Script nhận 2 file path: input_file và output_file. Trả về 'AC' nếu đúng, 'WA' nếu sai. Chỉ trả về code Python.\n\n" + problemDescription;
            String jsonResponse = callGeminiAPI(prompt, null);
            return extractTextFromResponse(jsonResponse);
        } catch (Exception e) {
            return "# Lỗi sinh checker: " + e.getMessage();
        }
    }

    private String buildAnalyzePrompt(Problem problem) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bạn là chuyên gia lập trình thi đấu (competitive programming). Nhiệm vụ của bạn là:\n");
        sb.append("1. Phân tích đề bài kỹ lưỡng, xác định CHÍNH XÁC thuật toán và công thức.\n");
        sb.append("2. Sinh 5 testcase đa dạng VÀ TỰ KIỂM TRA output bằng cách chạy thuật toán tay từng bước.\n");
        sb.append("3. Chỉ đưa ra output sau khi đã xác nhận kết quả là đúng.\n\n");
        sb.append("QUY TẮC BẮT BUỘC:\n");
        sb.append("- Output phải là KẾT QUẢ CHÍNH XÁC của bài toán, không phải ước lượng.\n");
        sb.append("- Với mỗi testcase, hãy trace thuật toán từng bước để xác nhận output.\n");
        sb.append("- Edge cases: n=1, tất cả âm, tất cả bằng nhau, số rất lớn/rất nhỏ.\n");
        sb.append("- Input và output phải đúng format: KHÔNG có khoảng trắng thừa cuối dòng.\n\n");
        sb.append("Trả về JSON theo đúng cấu trúc sau (KHÔNG thêm text ngoài JSON):\n");
        sb.append("{\n");
        sb.append("  \"explanation\": \"Mô tả thuật toán và công thức giải\",\n");
        sb.append("  \"testcases\": [\n");
        sb.append("    {\"type\": \"small|large|edge|normal\", \"input\": \"...\", \"output\": \"...\", \"trace\": \"giải thích tại sao output này đúng\"}\n");
        sb.append("  ],\n");
        sb.append("  \"checker_needed\": false,\n");
        sb.append("  \"checker_script\": null\n");
        sb.append("}\n\n");
        sb.append("ĐỀ THI:\n");
        sb.append("Tiêu đề: ").append(problem.getTitle()).append("\n");
        sb.append("Nội dung:\n").append(problem.getDescription()).append("\n");
        if (problem.getContestType() != null && !problem.getContestType().isBlank()) {
            sb.append("Loại kỳ thi: ").append(problem.getContestType()).append("\n");
        }
        sb.append("Giới hạn thời gian: ").append(problem.getTimeLimit()).append("ms\n");
        sb.append("Giới hạn bộ nhớ: ").append(problem.getMemoryLimit()).append("MB\n");
        return sb.toString();
    }

    private String callGeminiAPI(String textPrompt, String imagePath) throws IOException {
        boolean isOpenAI = "openai".equalsIgnoreCase(API_FORMAT);
        String apiUrl = isOpenAI ? API_URL_TEMPLATE : API_URL_TEMPLATE + API_KEY;

        JsonObject requestBody = new JsonObject();

        if (isOpenAI) {
            // OpenAI-compatible format (NVIDIA NIM, MiniMax, OpenAI, etc.)
            JsonArray messages = new JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", textPrompt);
            messages.add(message);
            requestBody.add("messages", messages);
            requestBody.addProperty("model", API_MODEL);
            requestBody.addProperty("temperature", 0.2);
            requestBody.addProperty("max_tokens", 32768);
        } else {
            // Google Gemini format
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", textPrompt);
            parts.add(textPart);

            if (imagePath != null && !imagePath.isBlank() && Files.exists(Paths.get(imagePath))) {
                byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                JsonObject inlineData = new JsonObject();
                inlineData.addProperty("mimeType", "image/png");
                inlineData.addProperty("data", base64Image);
                JsonObject imagePart = new JsonObject();
                imagePart.add("inlineData", inlineData);
                parts.add(imagePart);
            }

            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);

            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.2);
            generationConfig.addProperty("maxOutputTokens", 32768);
            requestBody.add("generationConfig", generationConfig);
        }

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request.Builder reqBuilder = new Request.Builder()
                .url(apiUrl)
                .post(body);
        if (isOpenAI) {
            reqBuilder.header("Authorization", "Bearer " + API_KEY);
        }

        try (Response response = client.newCall(reqBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response.code() + ": " + response.body().string());
            }
            return response.body().string();
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
        boolean isOpenAI = "openai".equalsIgnoreCase(API_FORMAT);

        if (isOpenAI) {
            // OpenAI format: choices[0].message.content
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) return "";
            JsonObject choice = choices.get(0).getAsJsonObject();
            JsonObject message = choice.getAsJsonObject("message");
            if (message == null || !message.has("content")) return "";
            return message.get("content").getAsString();
        } else {
            // Gemini format: candidates[0].content.parts[0].text
            JsonArray candidates = root.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) return "";
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            JsonObject content = candidate.getAsJsonObject("content");
            if (content == null) return "";
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null) return "";
            StringBuilder sb = new StringBuilder();
            for (JsonElement part : parts) {
                JsonObject p = part.getAsJsonObject();
                if (p.has("text")) {
                    sb.append(p.get("text").getAsString());
                }
            }
            return sb.toString();
        }
    }

    private AIResponse parseAnalyzeResponse(String jsonResponse) {
        AIResponse result = new AIResponse();
        String text = extractTextFromResponse(jsonResponse);

        // Try to extract JSON block from markdown
        String jsonBlock = text;
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            jsonBlock = text.substring(start, end + 1);
        }

        try {
            JsonObject obj = gson.fromJson(jsonBlock, JsonObject.class);
            JsonElement expEl = obj.get("explanation");
            result.setExplanation(expEl != null && !expEl.isJsonNull() ? expEl.getAsString() : "");
            JsonElement chkEl = obj.get("checker_script");
            result.setGeneratedChecker(chkEl != null && !chkEl.isJsonNull() ? chkEl.getAsString() : "");

            List<Testcase> testcases = new ArrayList<>();
            if (obj.has("testcases") && obj.get("testcases").isJsonArray()) {
                JsonArray tcArray = obj.getAsJsonArray("testcases");
                for (JsonElement e : tcArray) {
                    JsonObject tcObj = e.getAsJsonObject();
                    Testcase tc = new Testcase();
                    tc.setInputData(tcObj.has("input") ? tcObj.get("input").getAsString() : "");
                    tc.setExpectedOutput(tcObj.has("output") ? tcObj.get("output").getAsString() : "");
                    tc.setTestcaseType(tcObj.has("type") ? tcObj.get("type").getAsString() : "normal");
                    tc.setAiGenerated(true);
                    testcases.add(tc);
                }
            }
            result.setTestcases(testcases);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Lỗi parse AI response: " + e.getMessage() + "\nRaw: " + text);
        }
        return result;
    }
}
