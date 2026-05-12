package com.java.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiAIService implements AIService {
    private static String API_URL_TEMPLATE;
    private static String API_KEY;
    private static String MODEL_NAME;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    static {
        reloadConfig();
    }

    private static synchronized void reloadConfig() {
        Properties props = new Properties();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/";
        String key = "";
        String model = "gemini-2.5-flash";

        try (var input = GeminiAIService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                url = props.getProperty("ai.api.url", url);
                key = props.getProperty("ai.api.key", "");
                model = props.getProperty("ai.model", model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (key == null || key.isBlank() || key.contains("YOUR")) {
            loadConfigFromFileSystem(props);
            url = props.getProperty("ai.api.url", url);
            key = props.getProperty("ai.api.key", key);
            model = props.getProperty("ai.model", model);
        }

        API_URL_TEMPLATE = url;
        API_KEY = key;
        MODEL_NAME = model;

        System.out.println("Gemini config loaded: url=" + API_URL_TEMPLATE
                + ", model=" + MODEL_NAME
                + ", apiKey=" + ((API_KEY != null && !API_KEY.isBlank() && !API_KEY.contains("YOUR")) ? "SET" : "MISSING"));
    }

    private static void loadConfigFromFileSystem(Properties props) {
        String userDir = System.getProperty("user.dir", ".");
        String[] candidatePaths = new String[] {
                Paths.get(userDir, "src", "main", "resources", "config.properties").toString(),
                Paths.get(userDir, "config.properties").toString()
        };

        for (String candidate : candidatePaths) {
            if (!Files.exists(Paths.get(candidate))) {
                continue;
            }
            try (var input = Files.newInputStream(Paths.get(candidate))) {
                props.load(input);
                System.out.println("Loaded config.properties from file system: " + candidate);
                return;
            } catch (IOException e) {
                System.err.println("Loi doc config.properties tu file system (" + candidate + "): " + e.getMessage());
            }
        }
    }

    @Override
    public AIResponse analyzeProblem(Problem problem) {
        reloadConfig();
        AIResponse response = new AIResponse();
        if (API_KEY == null || API_KEY.isBlank() || API_KEY.contains("YOUR")) {
            response.setSuccess(false);
            response.setErrorMessage("Chưa cấu hình API Key trong config.properties (ai.api.key)");
            return response;
        }

        try {
            String prompt = buildAnalyzePrompt(problem);
            String jsonResponse = callGeminiAPI(prompt, problem.getImagePath(), buildAnalyzeSchema());
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
            reloadConfig();
            String prompt = "Hãy viết code " + language + " giải bài toán sau. Chỉ trả về code, không giải thích.\n\n" + problemDescription;
            String jsonResponse = callGeminiAPI(prompt, null, null);
            return extractTextFromResponse(jsonResponse);
        } catch (Exception e) {
            return "// Lỗi sinh code: " + e.getMessage();
        }
    }

    @Override
    public String generateChecker(String problemDescription) {
        try {
            reloadConfig();
            String prompt = "Hãy viết một script Python checker cho bài toán sau. Script nhận 2 file path: input_file và output_file. Trả về 'AC' nếu đúng, 'WA' nếu sai. Chỉ trả về code Python.\n\n" + problemDescription;
            String jsonResponse = callGeminiAPI(prompt, null, null);
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
        sb.append("  \"explanation\": \"Mô tả thật ngắn gọn bài toán\",\n");
        sb.append("  \"testcases\": [\n");
        sb.append("    {\"type\": \"small|large|edge|normal\", \"input\": \"...\", \"output\": \"...\"}\n");
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
        sb.append("\nYêu cầu: Chỉ sinh TỐI ĐA 5 testcase, ưu tiên ngắn gọn nhưng đa dạng.");
        sb.append(" Mỗi testcase chỉ chứa input/output cần thiết, không giải thích thêm.");
        sb.append(" Nếu đề quá đơn giản, vẫn chỉ cần 3-5 testcase đại diện.");
        sb.append("\nQUAN TRỌNG: Chỉ trả về JSON thuần túy, KHÔNG bọc trong markdown code block, KHÔNG thêm chữ nào bên ngoài JSON.");
        return sb.toString();
    }

    private AIResponse recoverPartialResponse(String rawText) {
        AIResponse result = new AIResponse();
        if (rawText == null || rawText.isBlank()) return null;

        Pattern explPattern = Pattern.compile("\"explanation\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher explMatcher = explPattern.matcher(rawText);
        if (explMatcher.find()) {
            result.setExplanation(explMatcher.group(1));
        }

        List<Testcase> testcases = new ArrayList<>();
        Pattern tcPattern = Pattern.compile(
                "\\{\\s*\"type\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*"
                + "\"input\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"\\s*,\\s*"
                + "\"output\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"\\s*\\}");
        Matcher tcMatcher = tcPattern.matcher(rawText);
        while (tcMatcher.find()) {
            Testcase tc = new Testcase();
            tc.setTestcaseType(tcMatcher.group(1));
            tc.setInputData(tcMatcher.group(2).replace("\\n", "\n").replace("\\t", "\t"));
            tc.setExpectedOutput(tcMatcher.group(3).replace("\\n", "\n").replace("\\t", "\t"));
            tc.setAiGenerated(true);
            testcases.add(tc);
        }

        if (testcases.isEmpty()) return null;
        result.setTestcases(testcases);
        result.setSuccess(true);
        System.out.println("[GeminiAIService] Recovered " + testcases.size() + " testcases từ JSON bị cắt ngang.");
        return result;
    }

    private JsonObject buildAnalyzeSchema() {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "OBJECT");

        JsonObject properties = new JsonObject();

        JsonObject explanation = new JsonObject();
        explanation.addProperty("type", "STRING");
        properties.add("explanation", explanation);

        JsonObject testcases = new JsonObject();
        testcases.addProperty("type", "ARRAY");
        JsonObject items = new JsonObject();
        items.addProperty("type", "OBJECT");
        JsonObject itemProps = new JsonObject();
        JsonObject typeProp = new JsonObject();
        typeProp.addProperty("type", "STRING");
        itemProps.add("type", typeProp);
        JsonObject inputProp = new JsonObject();
        inputProp.addProperty("type", "STRING");
        itemProps.add("input", inputProp);
        JsonObject outputProp = new JsonObject();
        outputProp.addProperty("type", "STRING");
        itemProps.add("output", outputProp);
        items.add("properties", itemProps);
        JsonArray itemRequired = new JsonArray();
        itemRequired.add("type");
        itemRequired.add("input");
        itemRequired.add("output");
        items.add("required", itemRequired);
        testcases.add("items", items);
        properties.add("testcases", testcases);

        JsonObject checkerNeeded = new JsonObject();
        checkerNeeded.addProperty("type", "BOOLEAN");
        properties.add("checker_needed", checkerNeeded);

        JsonObject checkerScript = new JsonObject();
        checkerScript.addProperty("type", "STRING");
        properties.add("checker_script", checkerScript);

        schema.add("properties", properties);

        JsonArray required = new JsonArray();
        required.add("explanation");
        required.add("testcases");
        schema.add("required", required);

        return schema;
    }

    private String callGeminiAPI(String textPrompt, String imagePath, JsonObject responseSchema) throws IOException {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();

        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", textPrompt);
        parts.add(textPart);

        boolean hasImage = imagePath != null && !imagePath.isBlank() && Files.exists(Paths.get(imagePath));
        if (hasImage) {
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
        generationConfig.addProperty("maxOutputTokens", 8192);
        if (responseSchema != null) {
            generationConfig.addProperty("responseMimeType", "application/json");
            generationConfig.add("responseSchema", responseSchema);
        }
        requestBody.add("generationConfig", generationConfig);

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL_TEMPLATE + MODEL_NAME + ":generateContent?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response.code() + ": " + response.body().string());
            }
            return response.body().string();
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) return "";
        JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
        if (root == null) return "";

        if (root.has("error") && root.get("error").isJsonObject()) {
            JsonObject errorObj = root.getAsJsonObject("error");
            String message = errorObj.has("message") ? errorObj.get("message").getAsString() : "Unknown Gemini error";
            throw new IllegalStateException(message);
        }

        JsonArray candidates = root.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) return "";
        JsonObject candidate = candidates.get(0).getAsJsonObject();
        JsonObject content = candidate.getAsJsonObject("content");
        if (content == null) return "";

        JsonArray parts = content.getAsJsonArray("parts");
        if (parts == null) return "";

        StringBuilder sb = new StringBuilder();
        for (JsonElement part : parts) {
            if (!part.isJsonObject()) continue;
            JsonObject p = part.getAsJsonObject();
            if (p.has("text") && !p.get("text").isJsonNull()) {
                sb.append(p.get("text").getAsString());
            }
        }
        return sb.toString();
    }

    private String extractJsonFromText(String text) {
        if (text == null) return "";

        String cleaned = text.trim();

        cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
        cleaned = cleaned.replaceFirst("\\s*```\\s*$", "");

        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            try {
                String unquoted = gson.fromJson(cleaned, String.class);
                if (unquoted != null && !unquoted.isBlank()) {
                    cleaned = unquoted.trim();
                }
            } catch (Exception ignored) {
                // Keep the original cleaned text.
            }
        }

        // Priority 1: extract from ```json ... ``` markdown block
        Pattern jsonCodeBlockPattern = Pattern.compile("```json\\s*([\\s\\S]*?)```", Pattern.DOTALL);
        Matcher matcher = jsonCodeBlockPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // Priority 2: extract from ``` ... ``` markdown block if it starts with {
        Pattern codeBlockPattern = Pattern.compile("```\\s*([\\s\\S]*?)```", Pattern.DOTALL);
        matcher = codeBlockPattern.matcher(text);
        if (matcher.find()) {
            String block = matcher.group(1).trim();
            if (block.startsWith("{")) {
                return block;
            }
        }

        // Priority 3: fallback to first { ... last }
        int start = cleaned.indexOf("{");
        int end = cleaned.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return cleaned.substring(start, end + 1);
        }

        return cleaned;
    }

    private AIResponse parseAnalyzeResponse(String jsonResponse) {
        AIResponse result = new AIResponse();
        if (jsonResponse == null || jsonResponse.isBlank()) {
            result.setSuccess(false);
            result.setErrorMessage("Gemini trả về phản hồi rỗng. Hãy kiểm tra API key, model, quota hoặc kết nối mạng.");
            return result;
        }

        JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Gemini trả về dữ liệu không hợp lệ hoặc rỗng.");
            return result;
        }

        if (root.has("error") && root.get("error").isJsonObject()) {
            JsonObject errorObj = root.getAsJsonObject("error");
            String message = errorObj.has("message") ? errorObj.get("message").getAsString() : "Unknown Gemini error";
            result.setSuccess(false);
            result.setErrorMessage("Gemini error: " + message);
            return result;
        }

        String text = extractTextFromResponse(jsonResponse);
        if (text == null || text.isBlank()) {
            result.setSuccess(false);
            result.setErrorMessage("Gemini không trả về nội dung AI hợp lệ.");
            return result;
        }

        String jsonBlock = extractJsonFromText(text);
        if (jsonBlock == null || jsonBlock.isBlank()) {
            result.setSuccess(false);
            result.setErrorMessage("AI trả về nội dung không có JSON để parse. Raw text: " + text);
            return result;
        }

        try {
            JsonElement parsed = JsonParser.parseString(jsonBlock);
            if (parsed != null && parsed.isJsonPrimitive() && parsed.getAsJsonPrimitive().isString()) {
                String nested = parsed.getAsString();
                String nestedBlock = extractJsonFromText(nested);
                if (nestedBlock != null && !nestedBlock.isBlank() && !nestedBlock.equals(jsonBlock)) {
                    parsed = JsonParser.parseString(nestedBlock);
                }
            }

            if (parsed == null || !parsed.isJsonObject()) {
                AIResponse recovered = recoverPartialResponse(text);
                if (recovered != null && recovered.isSuccess()) {
                    return recovered;
                }
                result.setSuccess(false);
                result.setErrorMessage("JSON parse thất bại: AI không trả về một JSON object hợp lệ.");
                return result;
            }

            JsonObject obj = parsed.getAsJsonObject();
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
            System.err.println("[GeminiAIService] JSON parse lỗi, thử recover từng testcase...");
            AIResponse recovered = recoverPartialResponse(text);
            if (recovered != null && recovered.isSuccess()) {
                return recovered;
            }
            result.setSuccess(false);
            result.setErrorMessage("Lỗi parse AI response: " + e.getMessage());
        }
        return result;
    }
}
