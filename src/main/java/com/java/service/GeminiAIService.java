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
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    static {
        Properties props = new Properties();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
        String key = "";
        try (var input = GeminiAIService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                url = props.getProperty("ai.api.url", url);
                key = props.getProperty("ai.api.key", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        API_URL_TEMPLATE = url;
        API_KEY = key;
    }

    @Override
    public AIResponse analyzeProblem(Problem problem) {
        AIResponse response = new AIResponse();
        if (API_KEY == null || API_KEY.isBlank() || API_KEY.contains("YOUR")) {
            response.setSuccess(false);
            response.setErrorMessage("Chưa cấu hình API Key Gemini trong config.properties");
            return response;
        }

        try {
            String prompt = buildAnalyzePrompt(problem);
            String jsonResponse = callGeminiAPI(prompt, problem.getImagePath());
            response = parseAnalyzeResponse(jsonResponse);
        } catch (Exception e) {
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
        sb.append("Bạn là trợ lý AI cho hệ thống chấm bài lập trình.\n");
        sb.append("Hãy phân tích đề thi sau và trả về KẾT QUẢ DƯỚI DẠNG JSON với cấu trúc:\n");
        sb.append("{\n");
        sb.append("  \"explanation\": \"Mô tả ngắn gọn bài toán\",\n");
        sb.append("  \"testcases\": [\n");
        sb.append("    {\"type\": \"small|large|edge|normal\", \"input\": \"...\", \"output\": \"...\"},\n");
        sb.append("  ],\n");
        sb.append("  \"checker_needed\": true/false,\n");
        sb.append("  \"checker_script\": \"nếu checker_needed=true thì viết script python checker\"\n");
        sb.append("}\n\n");
        sb.append("Đề thi:\n");
        sb.append("Tiêu đề: ").append(problem.getTitle()).append("\n");
        sb.append("Nội dung: ").append(problem.getDescription()).append("\n");
        sb.append("Loại kỳ thi: ").append(problem.getContestType()).append("\n");
        sb.append("Giới hạn thời gian: ").append(problem.getTimeLimit()).append("ms\n");
        sb.append("Giới hạn bộ nhớ: ").append(problem.getMemoryLimit()).append("MB\n");
        sb.append("\nYêu cầu: Sinh ít nhất 5 testcase đa dạng (nhỏ, lớn, edge cases).");
        return sb.toString();
    }

    private String callGeminiAPI(String textPrompt, String imagePath) throws IOException {
        String apiUrl = API_URL_TEMPLATE + API_KEY;

        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();

        // Text part
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", textPrompt);
        parts.add(textPart);

        // Image part (if available)
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

        // Request JSON config
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.2);
        generationConfig.addProperty("maxOutputTokens", 8192);
        requestBody.add("generationConfig", generationConfig);

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response.code() + ": " + response.body().string());
            }
            return response.body().string();
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
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
            result.setExplanation(obj.has("explanation") ? obj.get("explanation").getAsString() : "");
            result.setGeneratedChecker(obj.has("checker_script") ? obj.get("checker_script").getAsString() : "");

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
