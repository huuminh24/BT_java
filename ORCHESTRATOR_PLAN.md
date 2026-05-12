# ORCHESTRATOR PLAN - AI-Powered CP Judge System

## 1. HIỆN TRẠNG GAP ANALYSIS

| Module | Status | Critical Issues |
|--------|--------|-----------------|
| CSDL Schema | 60% | `ProblemDAO` dùng `content_text` vs `init.sql` dùng `description`. `TestcaseDAO` dùng `input_path/output_path` vs schema `input_data/expected_output`. Model `Testcase` thiếu `getInputText()`. |
| DAO Layer | 50% | Thiếu `SampleCodeDAO`, `SubmissionDAO`. `ProblemDAO`, `TestcaseDAO` bị mismatch column names. |
| File Manager | 70% | Ổn nhưng `ProblemService` gọi `FileManager.saveToFile` với relative path sai cách. |
| Judge Engine | 40% | Chỉ Java, chưa timeout/TLE, chưa C++, chưa ghi DB, chưa RE/MLE/CE. |
| AI Service | 0% | Hoàn toàn thiếu. Cần Gemini API integration + prompt engineering. |
| UI (Swing) | 0% | Chỉ có console `App.java`. Cần full Swing app với CardLayout. |
| Entry Point | Broken | `pom.xml` -> `com.java.Main` không tồn tại. |
| Docs | 0% | Thiếu README, install guide, user guide, báo cáo thử nghiệm. |

## 2. KIẾN TRÚC ĐỀ XUẤT

```
┌─────────────────────────────────────────────┐
│  UI Layer (Java Swing - CardLayout)         │
│  ├── DashboardFrame (Main entry)            │
│  ├── ProblemPanel (nhập đề + upload ảnh)    │
│  ├── AIPanel (prompt AI, sinh testcase)    │
│  ├── CodePanel (nhập code AC/WA/TLE)       │
│  └── ResultPanel (bôi màu AC/WA/TLE/RE/CE)  │
├─────────────────────────────────────────────┤
│  Service Layer                              │
│  ├── ProblemService (CRUD + File sync)      │
│  ├── AIService (Gemini API, OCR, JSON parse)│
│  ├── JudgeService (compile/run/compare)     │
│  └── SubmissionService (lưu kết quả chấm)   │
├─────────────────────────────────────────────┤
│  DAO Layer                                  │
│  ├── ProblemDAO, TestcaseDAO                │
│  ├── SampleCodeDAO, SubmissionDAO           │
│  └── BaseDAO (helper transaction)           │
├─────────────────────────────────────────────┤
│  Model Layer                                │
│  ├── Problem, Testcase, SampleCode          │
│  ├── Submission, JudgeResult                │
│  └── AIResponse (wrapper cho JSON AI)       │
├─────────────────────────────────────────────┤
│  Util Layer                                 │
│  ├── DatabaseConnection                     │
│  ├── FileManager                            │
│  └── ConfigLoader                           │
└─────────────────────────────────────────────┘
```

## 3. PHÂN CÔNG AGENT & THỨ TỰ THỰC HIỆN

### AGENT VŨ: Core Backend & Cấu trúc Dữ liệu
**Scope:** Fix schema mismatch, hoàn thiện DAO, FileManager sync, entry point.
**Files:**
- `init.sql` — sửa schema: thêm `content_text` hoặc đổi DAO về `description`. Quyết định: đổi schema về `description` cho đúng tiếng Anh, thêm `checker_script TEXT` vào `Problems`.
- `ProblemDAO.java` — fix column name `description`, thêm `getById`, `getByContestType`.
- `TestcaseDAO.java` — fix column names `input_data`/`expected_output`, thêm `getByProblemId`, `getByType`.
- `SampleCodeDAO.java` — tạo mới (CRUD đầy đủ).
- `SubmissionDAO.java` — tạo mới (CRUD + query by problem/samplecode).
- `config.properties` — tạo trong `resources/`.
- `Main.java` — tạo entry point `com.java.Main` khởi tạo DB + FileManager.

### AGENT HOÀNG: Tích hợp AI (API & Prompt Engineering)
**Scope:** Gemini API, prompt design, JSON parsing, OCR text extraction.
**Files:**
- `AIService.java` — service gọi Gemini API qua OkHttp.
- `PromptTemplates.java` — constants cho 3 loại prompt: (a) Phân tích đề + sinh testcase, (b) Sinh solution code, (c) Sinh checker script.
- `AIResponseParser.java` — parse JSON response từ Gemini, trích xuất testcases, code, explanation.
- `OCRService.java` — nếu đầu vào là ảnh, gọi Gemini vision để OCR rồi phân tích.

### AGENT MẠNH: Hệ thống chấm (Code Runner) & QA
**Scope:** Multi-language runner, timeout, sandbox cơ bản, comparator, submission recording.
**Files:**
- `JudgeService.java` — refactor từ `JudgeEngine`, hỗ trợ Java + C++, timeout, memory check cơ bản.
- `Compiler.java` — compile Java (javac) và C++ (g++).
- `SandboxRunner.java` — ProcessBuilder với timeout (FutureTask + ExecutorService).
- `Comparator.java` — so sánh output (trim + normalize line endings), hỗ trợ custom checker.
- `CheckerScriptRunner.java` — chạy script checker nếu đề yêu cầu.

### AGENT MINH: Giao diện (Java Swing UI/UX)
**Scope:** Full Swing app với CardLayout, các form nhập liệu, hiển thị kết quả màu sắc.
**Files:**
- `MainFrame.java` — JFrame chính với CardLayout, menu navigation.
- `DashboardPanel.java` — danh sách đề thi, tìm kiếm, filter.
- `ProblemEntryPanel.java` — form nhập title, description (textarea), upload ảnh (JFileChooser), chọn contest type.
- `AIPanel.java` — bảng điều khiển AI: chọn problem -> bấm "Phân tích đề" -> "Sinh testcase" -> "Sinh solution". Hiển thị log AI.
- `CodeSubmitPanel.java` — form nhập/nộp code mẫu (AC/WA/TLE), chọn ngôn ngữ (Java/C++), bấm chấm.
- `ResultPanel.java` — JTable hiển thị submissions, renderer bôi màu: xanh lá (AC), đỏ (WA), vàng (TLE), xám (RE/CE).

### AGENT DOCS: Tài liệu & Báo cáo
**Scope:** README, Install Guide, User Guide, Báo cáo thử nghiệm.
**Files:**
- `README.md` — tổng quan dự án, stack, team.
- `INSTALL_GUIDE.md` — Docker, Maven, JDK 17, API Key setup.
- `USER_GUIDE.md` — từng bước sử dụng phần mềm với screenshot mô tả.
- `TEST_REPORT.md` — đánh giá testcase AI trên 3-5 đề ICPC/IOI thật, so sánh độ mạnh testcase.

## 4. THỨ TỰ ORCHESTRATOR & DEPENDENCIES

```
Wave 1 (Song song — Foundation):
  ├── Agent Vũ: Fix Schema + DAO + Config + Main entry
  └── Agent Hoàng: AI Service skeleton (interface + mock) để Agent Minh có interface để gọi

Wave 2 (Song song — Core Logic):
  ├── Agent Vũ: FileManager sync + ProblemService hoàn chỉnh
  ├── Agent Hoàng: AI Prompt + API integration + JSON Parser
  └── Agent Mạnh: JudgeService + Compiler + SandboxRunner + Comparator

Wave 3 (Song song — Integration):
  ├── Agent Minh: Swing UI (tất cả panels)
  └── Agent Mạnh: Submission recording + QA test cases

Wave 4 (Tuần tự — Polish & Docs):
  ├── Integration test end-to-end
  ├── Bug fix cross-module
  └── Agent Docs: Viết tài liệu + Báo cáo thử nghiệm
```

## 5. QUY TẮC INTERFACE GIỮA CÁC AGENT

**AI Service Interface:**
```java
public interface AIService {
    AIAnalysis analyzeProblem(String textOrImagePath); // trả về testcases + explanation
    String generateSolution(String problemDescription, String language); // code mẫu AC
    String generateChecker(String problemDescription); // script checker
}
```

**Judge Service Interface:**
```java
public interface JudgeService {
    JudgeResult judge(String sourceCodePath, String language, String inputPath, String expectedOutputPath, int timeLimitMs);
    JudgeResult judgeWithChecker(String sourceCodePath, String language, String inputPath, String checkerPath, int timeLimitMs);
}
```

**Problem Service Interface:**
```java
public interface ProblemService {
    int createProblem(Problem p, String imagePath); // trả về problemId
    boolean addTestcase(int problemId, String input, String output, String type, boolean aiGenerated);
    int addSampleCode(int problemId, String code, String lang, String expectedType, boolean aiGenerated);
    List<Submission> runJudging(int problemId, int sampleCodeId); // chạy tất cả testcase
}
```

## 6. CƠ CHẾ CHẠY SONG SONG (Mô phỏng Orchestrator)

Vì là single-agent, "song song" = tạo các file/module độc lập cùng lúc khi interface đã chốt:
- Trong Wave 1: Vũ viết DAO files, Hoàng viết AIService interface + mock — không phụ thuộc.
- Trong Wave 2: Vũ (FileManager), Hoàng (AI integration), Mạnh (Judge) — 3 agent code 3 module riêng biệt, chỉ giao tiếp qua interface đã định nghĩa.
- Trong Wave 3: Minh code UI gọi các service đã có.

## 7. VERIFY END-TO-END

1. `docker-compose up -d` → MySQL chạy, schema auto-init.
2. `mvn clean package` → build fat JAR thành công.
3. `java -jar target/JudgeSystem-1.0-SNAPSHOT.jar` → Swing UI mở.
4. Nhập đề thi (text + ảnh) → lưu DB + file ảnh.
5. Bấm "AI Phân tích" → Gemini trả về testcase + solution.
6. Nhập code WA → chấm → kết quả WA hiển thị đỏ.
7. Nhập code AC → chấm → kết quả AC hiển thị xanh.
8. Xuất báo cáo từ menu.
