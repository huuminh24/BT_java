# Plan Tối ưu hóa toàn diện Java Judge System

## Phase 1: Critical Bugs (P0)

### 1.1 Fix C++ RE trên Windows
- **Root cause:** App đang chạy JAR cũ, hoặc `solution.exe` cần full path trong ProcessBuilder.
- **Fix:** Đổi `new ProcessBuilder("solution.exe")` thành `new ProcessBuilder(workDir.resolve("solution.exe").toString())`.
- **Files:** `JudgeEngine.java:171`

### 1.2 Memory Limit Enforcement (MLE)
- **Root cause:** `estimateMemoryUsage()` trả về 0, không giới hạn bộ nhớ process.
- **Fix:** Sử dụng `ProcessHandle` để đo RSS memory, destroy process nếu vượt limit.
- **Files:** `JudgeEngine.java:297-299`

### 1.3 Stdout/Stderr Size Limit
- **Root cause:** Code độc hại in ra GB dữ liệu sẽ OOM StringBuilder.
- **Fix:** Giới hạn stdout/stderr max 10MB, dừng đọc khi vượt ngưỡng.
- **Files:** `JudgeEngine.java:198-214`

## Phase 2: Performance (P1)

### 2.1 Dashboard N+1 Query Fix
- **Root cause:** `refreshStats()` loop qua từng problem → gọi 3 DAO methods mỗi problem.
- **Fix:** Thêm aggregate query trong SubmissionDAO/TestcaseDAO.
- **Files:** `DashboardPanel.java:76-89`, `SubmissionDAO.java`, `TestcaseDAO.java`

### 2.2 Batch Insert Submissions
- **Root cause:** Mỗi testcase chấm xong insert 1 submission → N queries.
- **Fix:** Dùng `PreparedStatement.addBatch()` + `executeBatch()`.
- **Files:** `ProblemService.java:126-161`, `SubmissionDAO.java`

### 2.3 Connection Pool (HikariCP)
- **Root cause:** Mỗi DAO mở connection mới, không reuse.
- **Fix:** Thêm HikariCP dependency, dùng pool 10 connections.
- **Files:** `pom.xml`, `DatabaseConnection.java`

## Phase 3: UI/UX (P2)

### 3.1 CodeSubmitPanel Layout
- **Issue:** WEST panel quá hẹp, result table không rõ ràng.
- **Fix:** Chuyển sang BorderLayout rõ ràng: top (config), center (code), east (results).
- **Files:** `CodeSubmitPanel.java:37-117`

### 3.2 ProblemEntryPanel: Thêm nút Update
- **Issue:** Chỉ có "Lưu đề mới", không sửa đề đã chọn.
- **Fix:** Thêm `btnUpdate`, kiểm tra nếu problem đã chọn thì update thay vì create.
- **Files:** `ProblemEntryPanel.java:139-146`, `ProblemService.java`

### 3.3 Progress Bar cho AI Analysis
- **Issue:** Không biết AI đang làm gì, có thể treo.
- **Fix:** Thêm `JProgressBar` indeterminate + status text.
- **Files:** `AIPanel.java:91-95`

### 3.4 Table Alternating Colors + Better Scroll
- **Issue:** Bảng khó đọc, không phân biệt hàng.
- **Fix:** `setDefaultRenderer` với alternating row màu.
- **Files:** `ResultPanel.java`, `CodeSubmitPanel.java`

### 3.5 Responsive Window + Icons
- **Issue:** Cửa sổ cố định 1400x900, không có app icon.
- **Fix:** `setMinimumSize()`, thêm window icon.
- **Files:** `MainFrame.java:13-17`

## Phase 4: Business Logic (P2)

### 4.1 Atomic Operations
- **Issue:** `addTestcaseFull` insert DB rồi save file → không rollback nếu file lỗi.
- **Fix:** Dùng transaction, xóa DB record nếu file save fail.
- **Files:** `ProblemService.java:61-79`

### 4.2 Input Validation nâng cao
- **Issue:** `saveProblem` không kiểm tra description length, image size.
- **Fix:** Giới hạn desc 65535 chars, ảnh max 5MB.
- **Files:** `ProblemEntryPanel.java:198-225`

### 4.3 Caching Problem List
- **Issue:** Mỗi panel refresh đều query DB.
- **Fix:** Cache problem list trong `ProblemService`, invalidate sau create/update/delete.
- **Files:** `ProblemService.java:34-41`

## Verification
- Build: `mvn clean package -q`
- Test: `mvn exec:java -Dexec.mainClass=com.java.SystemTest`
- Manual: Chạy app, test C++ chấm, test AI analysis, test xóa đề với nhiều testcase.
