# AI-Powered Competitive Programming Judge System

Hệ thống chấm bài lập trình thi đấu tích hợp AI, cho phép nhập đề thi (text/ảnh), tự động phân tích đề bằng Google Gemini API, sinh testcase + checker script + code mẫu, và chấm thử đa ngôn ngữ (Java/C++/Python) với giao diện Cyberpunk Dark.

---

## Giới thiệu dự án

Hệ thống **AI-Powered CP Judge** là một ứng dụng desktop được xây dựng nhằm hỗ trợ giảng viên và người dùng trong việc:

- **Nhập đề thi** lập trình thi đấu (IOI, ICPC, Codeforces, AtCoder...) dưới dạng text hoặc ảnh chụp đề bài
- **Phân tích đề bằng AI** — Sử dụng Google Gemini 2.0 Flash để đọc hiểu đề bài, sinh tự động các testcase đa dạng (small, large, edge, normal), checker script (Python), và code giải mẫu
- **Chấm bài tự động** — Compile và thực thi code Java/C++/Python, so sánh output, phát hiện TLE/WA/RE/CE
- **Xem kết quả trực quan** — Bảng kết quả với mã màu: AC (xanh), WA (đỏ), TLE (vàng), RE/CE (xám)

Hệ thống phù hợp cho việc chuẩn bị đề thi, kiểm tra chất lượng testcase, và đánh giá lời giải trước khi đưa lên hệ thống chấm chính thức.

---

## Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Ngôn ngữ | Java | 21 (LTS) |
| Build Tool | Apache Maven | 3.9+ |
| Cơ sở dữ liệu | MySQL | 8.0 (Docker) |
| AI API | Google Gemini | 2.0 Flash |
| HTTP Client | OkHttp | 4.12.0 |
| JSON Parser | Gson | 2.10.1 |
| UI Framework | Java Swing + FlatLaf | 3.4.1 |
| JDBC | MySQL Connector/J | 8.3.0 |
| Container | Docker + Docker Compose | — |
| C++ Compiler | g++ | C++17 (`-O2 -std=c++17`) |
| Python Runtime | Python 3 | 3.8+ (cho checker script) |

### Dependencies (pom.xml)

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
    <version>3.4.1</version>
</dependency>
```

---

## Cài đặt

### 1. Yêu cầu hệ thống (Prerequisites)

| Yêu cầu | Mục đích | Kiểm tra |
|---|---|---|
| **JDK 21+** | Chạy ứng dụng Java | `java -version` |
| **Apache Maven 3.9+** | Build project | `mvn -version` |
| **Docker + Docker Compose** | Chạy MySQL 8.0 | `docker --version` |
| **g++ (MinGW trên Windows)** | Biên dịch code C++ nộp lên | `g++ --version` |
| **Python 3.8+** | Chạy checker script | `python --version` |
| **Google Gemini API Key** | Gọi AI phân tích đề | Xem mục [Cấu hình AI API](#cấu-hình-ai-api) |

### 2. Clone repository

```bash
git clone <repository-url>
cd BT_Java-master
```

### 3. Khởi động MySQL bằng Docker

```bash
docker-compose up -d
```

Container sẽ khởi động MySQL 8.0 trên port `3306` với:
- Database: `JudgeSystem`
- Root password: `123`
- Tự động chạy `init.sql` để tạo bảng

Kiểm tra container đang chạy:

```bash
docker ps
```

Dừng container khi không sử dụng:

```bash
docker-compose down
```

> **Lưu ý:** Nếu port 3306 đã được sử dụng, sửa port trong `docker-compose.yml` và cập nhật `db.url` trong `config.properties`.

### 4. Cấu hình API Key

Mở file `src/main/resources/config.properties` và thay thế `YOUR_GEMINI_API_KEY_HERE` bằng API key thực tế:

```properties
# AI API Configuration (Google Gemini)
ai.api.key=AIzaSy...your-api-key-here
```

### 5. Build project

```bash
mvn clean package
```

Maven sẽ:
1. Compile source code
2. Chạy maven-shade-plugin để tạo fat JAR chứa tất cả dependencies
3. Output: `target/JudgeSystem-1.0-SNAPSHOT.jar`

### 6. Chạy ứng dụng

```bash
java -jar target/JudgeSystem-1.0-SNAPSHOT.jar
```

Hoặc chạy trực tiếp từ Maven:

```bash
mvn exec:java -Dexec.mainClass="com.java.Main"
```

---

## Cấu trúc dự án

```
BT_Java-master/
├── docker-compose.yml              # Docker Compose cho MySQL 8.0
├── init.sql                        # SQL script tạo bảng tự động
├── pom.xml                         # Maven config (dependencies, plugins)
├── JudgeSystemData/                # Thư mục dữ liệu runtime (tự tạo)
│   ├── Problems/                   # Ảnh đề bài + checker script
│   ├── Testcases/                  # File input/output testcase
│   ├── Samples/                    # File code mẫu đã lưu
│   └── Submissions/                # File output submission
├── src/main/
│   ├── resources/
│   │   └── config.properties       # Cấu hình DB, API key, đường dẫn file
│   └── java/com/java/
│       ├── Main.java               # Entry point - khởi tạo folder, test DB, launch UI
│       ├── dao/
│       │   ├── ProblemDAO.java     # CRUD bảng Problems (add, getById, getAll, update, updateChecker, delete, count)
│       │   ├── TestcaseDAO.java    # CRUD bảng Testcases (add, getByProblemId, update, delete)
│       │   ├── SampleCodeDAO.java  # CRUD bảng SampleCodes (add, getByProblemId, getById)
│       │   └── SubmissionDAO.java  # CRUD bảng Submissions (add, getByProblemId)
│       ├── model/
│       │   ├── Problem.java        # id, title, description, imagePath, timeLimit, memoryLimit, contestType, checkerScript, createdAt
│       │   ├── Testcase.java       # id, problemId, inputData, expectedOutput, testcaseType, isAiGenerated
│       │   ├── SampleCode.java    # id, problemId, codeContent, language, expectedType, isAiGenerated, createdAt
│       │   ├── Submission.java     # id, problemId, sampleCodeId, testcaseId, actualOutput, executionTime, memoryUsed, status, errorMessage, submittedAt
│       │   ├── JudgeResult.java    # status, actualOutput, executionTime, memoryUsed, errorMessage + factory methods (AC/WA/TLE/MLE/RE/CE)
│       │   └── AIResponse.java     # explanation, testcases, generatedSolution, generatedChecker, success, errorMessage
│       ├── service/
│       │   ├── AIService.java      # Interface: analyzeProblem, generateSolution, generateChecker
│       │   ├── GeminiAIService.java   # Implement: gọi Gemini API, parse JSON response, hỗ trợ ảnh
│       │   ├── JudgeEngine.java    # Core: compile Java/C++/Python, run với timeout, so sánh output (trailing whitespace tolerant)
│       │   ├── JudgeService.java   # Interface: judge, judgeWithChecker
│       │   ├── DefaultJudgeService.java  # Implement: bridge JudgeEngine + FileManager + checker script
│       │   └── ProblemService.java # Orchestrator: điều phối DAOs, FileManager, JudgeService
│       ├── ui/
│       │   ├── AppTheme.java       # Cyberpunk dark theme: colors, fonts, borders, UI factories, status colors
│       │   ├── MainFrame.java      # JFrame chính với CardLayout (5 panels)
│       │   ├── DashboardPanel.java # Trang chủ: thống kê + navigation cards
│       │   ├── ProblemEntryPanel.java  # Nhập đề: form + danh sách đề + upload ảnh
│       │   ├── AIPanel.java        # AI: chọn đề → phân tích → sinh testcase & code
│       │   ├── CodeSubmitPanel.java # Nộp code: chọn đề + ngôn ngữ + chấm thử
│       │   └── ResultPanel.java    # Kết quả: bảng submissions + filter + màu status
│       └── util/
│           ├── DatabaseConnection.java  # Singleton connection từ config.properties
│           └── FileManager.java    # File I/O: ảnh, testcase, code mẫu, checker, submission
└── target/
    └── JudgeSystem-1.0-SNAPSHOT.jar  # Fat JAR output
```

---

## Cơ sở dữ liệu

### ERD (Entity Relationship Diagram)

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   Problems   │──1:N──│  Testcases   │       │ SampleCodes  │
│              │       │              │       │              │
│ id (PK)      │       │ id (PK)      │       │ id (PK)      │
│ title        │       │ problem_id(FK)│      │ problem_id(FK)│
│ description  │       │ input_data   │       │ code_content │
│ image_path   │       │ expected_out │       │ language     │
│ time_limit   │       │ testcase_type│       │ expected_type│
│ memory_limit │       │ is_ai_gen    │       │ is_ai_gen    │
│ contest_type │       └──────────────┘       └──────────────┘
│ checker_scr  │               │
│ created_at   │               │
└──────┬───────┘               │
       │                       │
       └──────────1:N──────────┘
                │
        ┌──────────────┐
        │ Submissions  │
        │              │
        │ id (PK)      │
        │ problem_id(FK)│
        │ sample_code_id(FK)│
        │ testcase_id(FK)│
        │ actual_output│
        │ execution_time│
        │ memory_used  │
        │ status       │
        │ error_message│
        │ submitted_at │
        └──────────────┘
```

### Chi tiết bảng

#### Problems

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INT | PK, AUTO_INCREMENT | Mã đề |
| `title` | VARCHAR(255) | NOT NULL | Tiêu đề bài toán |
| `description` | TEXT | — | Nội dung đề bài |
| `image_path` | VARCHAR(500) | — | Đường dẫn ảnh đề bài |
| `time_limit` | INT | DEFAULT 2000 | Giới hạn thời gian (ms) |
| `memory_limit` | INT | DEFAULT 256 | Giới hạn bộ nhớ (MB) |
| `contest_type` | VARCHAR(50) | DEFAULT 'ICPC' | Loại kỳ thi (ICPC/IOI/Codeforces/AtCoder/Other) |
| `checker_script` | TEXT | — | Checker script Python (nếu có) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Thời gian tạo |

#### Testcases

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INT | PK, AUTO_INCREMENT | Mã testcase |
| `problem_id` | INT | FK → Problems(id) ON DELETE CASCADE | Đề thi liên quan |
| `input_data` | TEXT | — | Dữ liệu đầu vào |
| `expected_output` | TEXT | — | Kết quả mong đợi |
| `testcase_type` | VARCHAR(50) | DEFAULT 'normal' | Loại: small/large/edge/normal |
| `is_ai_generated` | BOOLEAN | DEFAULT FALSE | Sinh bởi AI? |

#### SampleCodes

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INT | PK, AUTO_INCREMENT | Mã code mẫu |
| `problem_id` | INT | FK → Problems(id) ON DELETE CASCADE | Đề thi liên quan |
| `code_content` | TEXT | NOT NULL | Nội dung code |
| `language` | VARCHAR(20) | NOT NULL DEFAULT 'java' | Ngôn ngữ: java/cpp/python |
| `expected_type` | VARCHAR(10) | NOT NULL DEFAULT 'AC' | Kết quả mong đợi: AC/WA/TLE/RE |
| `is_ai_generated` | BOOLEAN | DEFAULT FALSE | Sinh bởi AI? |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Thời gian tạo |

#### Submissions

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INT | PK, AUTO_INCREMENT | Mã submission |
| `problem_id` | INT | FK → Problems(id) ON DELETE CASCADE | Đề thi |
| `sample_code_id` | INT | FK → SampleCodes(id) ON DELETE CASCADE | Code mẫu đã nộp |
| `testcase_id` | INT | FK → Testcases(id) ON DELETE CASCADE | Testcase đã chạy |
| `actual_output` | TEXT | — | Kết quả thực tế |
| `execution_time` | INT | — | Thời gian thực thi (ms) |
| `memory_used` | INT | — | Bộ nhớ sử dụng (KB) |
| `status` | VARCHAR(10) | NOT NULL DEFAULT 'PENDING' | Kết quả: AC/WA/TLE/RE/CE/MLE |
| `error_message` | TEXT | — | Thông báo lỗi (nếu có) |
| `submitted_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Thời gian nộp |

---

## Hướng dẫn sử dụng

### Màn hình Dashboard

Màn hình mặc định khi khởi động ứng dụng, hiển thị:

1. **4 thẻ thống kê** — Số lượng: Đề thi, Testcases, Code mẫu, Submissions
2. **Thanh thống kê** — Số lượng AC/WA và tổng submissions
3. **6 card điều hướng** — Click để chuyển màn hình:
   - **Nhập đề thi** → Chuyển sang màn hình tạo đề
   - **AI Phân tích** → Chuyển sang màn hình sinh testcase bằng Gemini
   - **Nộp code mẫu** → Chuyển sang màn hình nộp code & chấm thử
   - **Kết quả chấm** → Chuyển sang bảng kết quả submissions
   - **Hướng dẫn** → Hiện popup hướng dẫn nhanh
   - **Thoát** → Đóng ứng dụng

> Các thẻ thống kê tự động cập nhật khi quay lại Dashboard.

### Màn hình Nhập đề thi

Tạo và quản lý đề bài lập trình:

1. **Danh sách đề đã có** (bên trái) — Click để xem chi tiết, nút **Xóa đề** để xóa (kèm cascade tất cả testcase, code, submissions)
2. **Form nhập đề** (bên phải):
   - **Tiêu đề** — Tên bài toán (bắt buộc)
   - **Nội dung đề** — Mô tả chi tiết bài toán (bắt buộc)
   - **Ảnh đề bài** — Nhấn "Chọn ảnh..." để upload file JPG/PNG/GIF. Ảnh sẽ được gửi kèm cho Gemini API để phân tích
   - **Loại kỳ thi** — Chọn: ICPC, IOI, Codeforces, AtCoder, Other
   - **Time limit** — Giới hạn thời gian (ms), mặc định 2000
   - **Memory limit** — Giới hạn bộ nhớ (MB), mặc định 256
3. **Nút chức năng**:
   - **Lưu đề thi** — Lưu vào CSDL, ảnh được copy vào `JudgeSystemData/Problems/`
   - **Mới** — Xóa trắng form
   - **Quay lại** — Về Dashboard

### Màn hình AI Phân tích

Sử dụng Google Gemini để phân tích đề và sinh testcase tự động:

1. **Chọn đề thi** — Dropdown hiển thị danh sách đề đã tạo. Nhấn 🔄 để tải lại
2. **Tuỳ chọn**:
   - ☑ **Tự động sinh code AC** — AI sẽ tạo code giải đúng bằng Java
   - ☑ **Sinh checker script** — AI sẽ tạo script Python kiểm tra output (dành cho bài có nhiều đáp án)
3. **Nút "Phân tích đề & Sinh testcase"** — Gọi Gemini API:
   - AI đọc hiểu nội dung đề (và ảnh nếu có)
   - Sinh ít nhất 5 testcase đa dạng: `small`, `large`, `edge`, `normal`
   - Nếu tick "Sinh checker script" → lưu checker vào DB
   - Nếu tick "Tự động sinh code AC" → AI tạo code Java AC và lưu vào SampleCodes
4. **Bảng testcase** — Hiển thị các testcase đã sinh: STT, Loại, Input, Expected Output, AI?
5. **Nút "Xem testcase hiện có"** — Hiển thị testcase đã lưu cho đề đang chọn
6. **Log** — Hiển thị quá trình gọi API, kết quả parse, lỗi (nếu có)

> Quá trình gọi API chạy trên background thread (SwingWorker), UI không bị đóng băng.

### Màn hình Nộp code mẫu

Nhập code giải và chạy thử trên các testcase:

1. **Chọn đề thi** — Dropdown danh sách đề
2. **Chọn ngôn ngữ** — `java`, `cpp`, `python`
3. **Kết quả mong đợi** — `AC`, `WA`, `TLE`, `RE` (để ghi nhận mục đích code)
4. **Nhập code**:
   - Gõ trực tiếp vào text area
   - Hoặc nhấn **Chọn file** để upload file .java/.cpp/.py
5. **Nút chức năng**:
   - **Lưu code mẫu** — Lưu vào DB, file lưu tại `JudgeSystemData/Samples/`
   - **Chấm thử** — Chạy code trên tất cả testcase của đề:
     - Compile code (Java → `javac`, C++ → `g++ -O2 -std=c++17`, Python → không cần compile)
     - Thực thi từng testcase với time limit của đề
     - So sánh output (bỏ qua trailing whitespace)
     - Nếu đề có checker script → dùng checker thay vì so sánh trực tiếp
     - Hiển thị kết quả trong bảng: Testcase, Status (màu), Time, Error
   - **Xem code mẫu đã lưu** — Hiện danh sách code mẫu cho đề đang chọn
6. **Kết quả chấm** — Bảng hiển thị mỗi testcase một hàng với Status được tô màu:
   - 🟢 **AC** — Accepted (xanh)
   - 🔴 **WA** — Wrong Answer (đỏ)
   - 🟡 **TLE** — Time Limit Exceeded (vàng)
   - ⚪ **RE/CE** — Runtime/Compile Error (xám)

### Màn hình Kết quả chấm

Xem tất cả submissions đã chấm:

1. **Lọc theo đề** — Dropdown chọn đề hoặc "Tất cả đề thi"
2. **Bảng kết quả** — Các cột: ID, Đề thi, Code mẫu, Testcase, Status (màu pill), Time (ms), Memory (KB), Error
3. **Thanh thống kê** — Tổng + số AC/WA/TLE/Other
4. **Nút**:
   - **Tải kết quả** — Refresh bảng
   - **Xóa kết quả** — Xóa dữ liệu
   - **Quay lại Dashboard**

---

## Cấu hình AI API

### Lấy Google Gemini API Key

1. Truy cập [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Đăng nhập bằng tài khoản Google
3. Nhấn **Create API Key**
4. Chọn project Google Cloud (hoặc tạo mới)
5. Copy API key tạo được

### Cấu hình vào ứng dụng

Mở file `src/main/resources/config.properties`:

```properties
# AI API Configuration (Google Gemini)
ai.api.key=AIzaSy...your-actual-api-key-here
ai.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=
ai.model=gemini-2.0-flash
```

> **Lưu ý:** Phải build lại project (`mvn clean package`) sau khi thay đổi `config.properties` vì file này được đóng gói vào JAR.

### Cách hoạt động của AI

1. **Phân tích đề** (`GeminiAIService.analyzeProblem`):
   - Xây dựng prompt yêu cầu AI trả về JSON gồm: `explanation`, `testcases[]` (với type/input/output), `checker_needed`, `checker_script`
   - Nếu đề có ảnh → encode Base64 và gửi kèm dưới dạng `inlineData`
   - Parse JSON response, lưu testcase vào DB

2. **Sinh code giải** (`GeminiAIService.generateSolution`):
   - Prompt: "Hãy viết code {language} giải bài toán sau. Chỉ trả về code, không giải thích."
   - Lưu kết quả vào SampleCodes

3. **Sinh checker** (`GeminiAIService.generateChecker`):
   - Prompt: "Hãy viết một script Python checker cho bài toán sau. Script nhận 2 file path: input_file và output_file. Trả về 'AC' nếu đúng, 'WA' nếu sai."
   - Lưu vào `checker_script` của Problems

### Thông số API

| Tham số | Giá trị |
|---|---|
| Model | `gemini-2.0-flash` |
| Temperature | 0.2 |
| Max output tokens | 8192 |
| Timeout | Mặc định OkHttp |

---

## Xử lý sự cố

### Lỗi kết nối cơ sở dữ liệu

**Triệu chứng:** Hiện "Ket noi CSDL that bai" ở console

**Nguyên nhân & Khắc phục:**
1. Chưa khởi động Docker container:
   ```bash
   docker-compose up -d
   ```
2. MySQL chưa sẵn sàng — Đợi 10-30 giây sau khi container start
3. Port 3306 bị chiếm — Thay port trong `docker-compose.yml` và cập nhật `db.url`
4. Sai password — Kiểm tra `db.password` trong `config.properties` khớp với `MYSQL_ROOT_PASSWORD` trong `docker-compose.yml`

### Lỗi "Chưa cấu hình API Key Gemini"

**Nguyên nhân:** `ai.api.key` trong `config.properties` vẫn là `YOUR_GEMINI_API_KEY_HERE`

**Khắc phục:**
1. Lấy API key từ [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Cập nhật vào `config.properties`
3. Build lại: `mvn clean package`

### Lỗi compile C++ ("g++ not found")

**Nguyên nhân:** Chưa cài g++ hoặc không có trong PATH

**Khắc phục:**
- **Windows:** Cài [MinGW-w64](https://www.mingw-w64.org/) hoặc MSYS2, thêm `bin` vào PATH
- **Linux:** `sudo apt install g++`
- **macOS:** `xcode-select --install`

### Lỗi chạy Python checker

**Nguyên nhân:** Chưa cài Python hoặc lệnh `python` không khả dụng

**Khắc phục:**
- Đảm bảo `python` (hoặc `python3`) có trong PATH
- Trên một số hệ thống, cần đổi `python` → `python3` trong `DefaultJudgeService.java` dòng 45

### Lỗi Java compile ("javac not found")

**Nguyên nhân:** JRE được cài thay vì JDK

**Khắc phục:** Cài JDK 17+ (không phải chỉ JRE), đảm bảo `javac` có trong PATH

### AI trả về lỗi parse JSON

**Triệu chứng:** "Lỗi parse AI response" trong log

**Nguyên nhân:** Gemini có thể trả về JSON không hợp lệ hoặc bọc trong markdown code block

**Khắc phục:** Hệ thống đã có cơ chế extract JSON từ markdown (tìm `{` đầu tiên đến `}` cuối cùng). Nếu vẫn lỗi, thử:
1. Chạy lại phân tích (AI có thể trả kết quả khác)
2. Kiểm tra nội dung đề bài rõ ràng hơn
3. Giảm độ phức tạp của đề

### Ứng dụng không khởi động

**Kiểm tra:**
1. Java version: `java -version` → phải là 21+
2. JAR file tồn tại: `ls target/JudgeSystem-1.0-SNAPSHOT.jar`
3. Chạy với log chi tiết: `java -jar target/JudgeSystem-1.0-SNAPSHOT.jar` và xem console output

### Port 3306 đã được sử dụng

**Khắc phục:**
1. Dừng MySQL local nếu đang chạy: `net stop mysql` (Windows) hoặc `sudo systemctl stop mysql` (Linux)
2. Hoặc đổi port trong `docker-compose.yml`:
   ```yaml
   ports:
     - "3307:3306"
   ```
   Và cập nhật `config.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3307/JudgeSystem
   ```

---

## Team & Phân công

| Thành viên | Vai trò | Module chính |
|---|---|---|
| Vũ | Core Backend & Database | ERD, Schema, DAO, FileManager, Main entry |
| Hoàng | AI Integration | Gemini API, Prompt Engineering, JSON Parser |
| Mạnh | Judge Engine & QA | Multi-language Runner, Timeout, Comparator, Docs |
| Minh | UI/UX | Java Swing Dashboard, Problem Entry, AI Panel, Result Viewer |

---

## Tài liệu tham khảo

- [INSTALL.md](INSTALL.md) — Hướng dẫn cài đặt chi tiết
- [USER_GUIDE.md](USER_GUIDE.md) — Hướng dẫn sử dụng từng chức năng
- [EVALUATION_REPORT.md](EVALUATION_REPORT.md) — Báo cáo đánh giá kết quả kiểm thử
