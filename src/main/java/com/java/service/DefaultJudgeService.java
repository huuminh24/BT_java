package com.java.service;

import com.java.model.JudgeResult;
import com.java.util.FileManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultJudgeService implements JudgeService {
    private JudgeEngine engine = new JudgeEngine();

    @Override
    public JudgeResult judge(String sourceCodePath, String language, String inputPath, String expectedOutputPath, int timeLimitMs) {
        try {
            String code = FileManager.readFile(sourceCodePath);
            String input = FileManager.readFile(inputPath);
            String expected = FileManager.readFile(expectedOutputPath);
            return engine.judge(code, language, input, expected, timeLimitMs, 256);
        } catch (IOException e) {
            return JudgeResult.runtimeError("IO Error: " + e.getMessage());
        }
    }

    @Override
    public JudgeResult judgeWithChecker(String sourceCodePath, String language, String inputPath, String checkerPath, int timeLimitMs) {
        try {
            String code = FileManager.readFile(sourceCodePath);
            String input = FileManager.readFile(inputPath);
            String checker = FileManager.readFile(checkerPath);

            java.io.File checkerFile = java.io.File.createTempFile("checker_", ".py");
            checkerFile.deleteOnExit();
            java.nio.file.Files.writeString(checkerFile.toPath(), checker, StandardCharsets.UTF_8);

            java.io.File inputFile = java.io.File.createTempFile("input_", ".txt");
            inputFile.deleteOnExit();
            java.nio.file.Files.writeString(inputFile.toPath(), input, StandardCharsets.UTF_8);

            JudgeResult runResult = engine.judge(code, language, input, "", timeLimitMs, 256);

            if (!"RE".equals(runResult.getStatus()) && !"CE".equals(runResult.getStatus()) && !"TLE".equals(runResult.getStatus())) {
                java.io.File actualFile = java.io.File.createTempFile("actual_", ".txt");
                actualFile.deleteOnExit();
                java.nio.file.Files.writeString(actualFile.toPath(), runResult.getActualOutput() != null ? runResult.getActualOutput() : "", StandardCharsets.UTF_8);

                ProcessBuilder pb = new ProcessBuilder("python", checkerFile.getAbsolutePath(), inputFile.getAbsolutePath(), actualFile.getAbsolutePath());
                pb.redirectErrorStream(true);
                Process proc = pb.start();
                String checkerOutput = new String(proc.getInputStream().readAllBytes());
                proc.waitFor();

                String trimmed = checkerOutput.trim();
                if (trimmed.startsWith("AC")) {
                    runResult.setStatus("AC");
                } else if (trimmed.startsWith("WA")) {
                    runResult.setStatus("WA");
                } else if (trimmed.startsWith("PARTIAL")) {
                    runResult.setStatus("WA");
                } else {
                    runResult.setStatus("WA");
                    runResult.setErrorMessage("Checker output unrecognized: " + trimmed);
                }
            }

            return runResult;
        } catch (IOException e) {
            return JudgeResult.runtimeError("IO Error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return JudgeResult.runtimeError("Checker interrupted: " + e.getMessage());
        }
    }
}
