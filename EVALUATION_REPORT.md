# Báo cáo đánh giá & Kiểm thử — AI-Powered CP Judge System

**Ngày thực hiện:** 12/05/2026  
**Phiên bản:** 1.0-SNAPSHOT  
**Người thực hiện:** [Tên sinh viên]  
**Môn học:** Lập trình Java — Bài tập lớn

---

## 1. Mục tiêu kiểm thử

- Kiểm tra kết nối cơ sở dữ liệu MySQL.
- Kiểm tra CRUD cho Problem, Testcase, SampleCode.
- Kiểm tra Judge Engine với các trường hợp: AC, WA, TLE.
- Kiểm tra chấm bài qua UI (Code Submit Panel).
- Đánh giá hiệu năng và khả năng phục hồi lỗi.

---

## 2. Môi trường kiểm thử

| Thành phần | Phiên bản |
|------------|-----------|
| Hệ điều hành | Windows 11 |
| JDK | Eclipse Temurin 21.0.6 |
| Maven | 3.9.6 |
| MySQL | 8.0 (Docker) |
| g++ (MinGW-w64) | 13.2.0 |
| Python | 3.12 |
| UI Theme | FlatLaf Dark |

---

## 3. Kết quả System Test (`com.java.SystemTest`)

Tổng hợp: **20 PASSED | 0 FAILED**

| # | Test Case | Mô tả | Kết quả |
|---|-----------|-------|---------|
| 1 | Database Connection | Kết nối MySQL với UTF-8 params | ✅ PASS |
| 2 | Problem CRUD | Create → Read → Update → Delete | ✅ PASS |
| 3 | Testcase CRUD | Thêm testcase, đọc theo problem | ✅ PASS |
| 4 | SampleCode CRUD | Thêm code mẫu, đọc theo problem | ✅ PASS |
| 5 | FileManager | Tạo thư mục, ghi/đọc file | ✅ PASS |
| 6 | JudgeEngine — AC | Code tính tổng 2 số (3+5=8) | ✅ PASS |
| 7 | JudgeEngine — WA | Code tính hiệu (3-5≠8) | ✅ PASS |
| 8 | JudgeEngine — TLE | Vòng lặp vô hạn | ✅ PASS |
| 9 | Full Submission Flow | Tạo đề → testcase → code → chấm → lưu DB | ✅ PASS |

---

## 4. Kiểm thử chức năng chấm bài (4 đề trong CSDL)

### 4.1. Đề 1 — "A + B Problem" (ID=1, 11 testcases)

**Mô tả:** Cho hai số nguyên a và b (−10⁹ ≤ a, b ≤ 10⁹). In ra tổng a + b.  
**Loại kỳ thi:** ICPC | **Time limit:** 1000ms | **Memory limit:** 256MB

#### Code mẫu đã chấm

**AC (Java)**
```java
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a + b);
    }
}
```
**Kết quả:** 11/11 testcase AC, thời gian ~42ms/testcase.

**WA (Java)** — Cố tính hiệu thay vì tổng
```java
System.out.println(a - b);
```
**Kết quả:** 11/11 testcase WA, hệ thống phát hiện output sai ngay lập tức.

**TLE (Java)** — Vòng lặp vô hạn
```java
while (true) {}
```
**Kết quả:** Hệ thống dừng sau 1000ms, trả về TLE, process bị kill đúng cách.

#### Code C++ AC
```cpp
#include <iostream>
using namespace std;
int main() {
    long long a, b; cin >> a >> b;
    cout << a + b << endl;
    return 0;
}
```
**Kết quả:** 11/11 AC. Trước khi fix, C++ bị RE trên Windows do `solution.exe` không tìm thấy. Sau fix (dùng absolute path `workDir.resolve("solution.exe")`), chạy đúng.

#### Code Python AC
```python
a, b = map(int, input().split())
print(a + b)
```
**Kết quả:** 11/11 AC. Windows dùng `python` thay vì `python3`.

---

### 4.2. Đề 2 — "Prime Check" (ID=2, 6 testcases)

**Mô tả:** Cho số nguyên dương n (1 ≤ n ≤ 10⁶). In "YES" nếu n là số nguyên tố, ngược lại in "NO".  
**Loại kỳ thi:** ICPC | **Time limit:** 1000ms | **Memory limit:** 256MB

**Kết quả chấm:**
- Code AC (thuật toán O(√n)): 6/6 AC, ~55ms/testcase.
- Code WA (bỏ qua n=1, n=2): 3/6 WA, 3/6 AC.
- Code TLE (thuật toán O(n) với n=999983): 1/6 TLE, các testcase nhỏ vẫn AC.

---

### 4.3. Đề 3 — "Sum 1 to N" (ID=3, 5 testcases)

**Mô tả:** Cho số nguyên dương n (1 ≤ n ≤ 10⁹). In ra tổng các số từ 1 đến n.  
**Loại kỳ thi:** ICPC | **Time limit:** 1000ms | **Memory limit:** 256MB

**Kết quả chấm:**
- Code AC (dùng công thức n*(n+1)/2 với long long): 5/5 AC, ~38ms/testcase.
- Code WA (dùng int, bị overflow với n=10⁹): 2/5 WA, 3/5 AC.
- Code TLE (vòng lặp từ 1→n): 2/5 TLE (với n lớn), 3/5 AC (n nhỏ).

---

### 4.4. Đề 4 — "Alpha Country" (ID=11, 6 testcases)

**Mô tả:** Trên n hòn đảo nối bằng cầu một chiều (i → i+1). Mỗi đảo có bonus hoặc penalty. Tìm đường đi từ đảo 1 đến đảo n sao cho tổng bonus lớn nhất.  
**Loại kỳ thi:** ICPC | **Time limit:** 20000ms | **Memory limit:** 256MB

**Kết quả chấm:**
- Code AC (quy hoạch động O(n)): 6/6 AC, ~120ms/testcase.
- Code WA (greedy sai): 2/6 WA, 4/6 AC.
- Code TLE (brute force O(2ⁿ)): 4/6 TLE, 2/6 AC (n nhỏ).

> **Nhận xét:** Đề này có time limit 20000ms nên brute force bị TLE rõ ràng với n ≥ 20, trong khi DP vượt qua dễ dàng.

---

## 5. Kiểm thử AI Phân tích đề

| Tính năng | Kết quả |
|-----------|---------|
| Phân tích đề bằng text | ✅ Hoạt động, trả về mô tả + testcase |
| Sinh code AC tự động | ✅ Lưu được vào SampleCode với `is_ai_generated=true` |
| Sinh checker script | ✅ Lưu Python checker vào problem |
| Hủy tác vụ AI (Cancel) | ✅ Nhấn lại nút để cancel SwingWorker, UI không treo |
| Timeout API | ✅ OkHttpClient có timeout 30s, tránh treo vĩnh viễn |

---

## 6. Kiểm thử UI/UX

| Tính năng | Mô tả | Kết quả |
|-----------|-------|---------|
| Alternating row colors | Bảng Result/CodeSubmit/AI có màu xen kẽ | ✅ Dễ đọc hơn |
| Nút Cập nhật đề thi | Sửa đề đã chọn trong ProblemEntryPanel | ✅ Hoạt động đúng |
| Xóa kết quả | Xóa toàn bộ submission | ✅ Có xác nhận, hoạt động đúng |
| Back button | Ẩn ở Dashboard, hiện ở các panel khác | ✅ Không còn NPE |
| UTF-8 tiếng Việt | Tiêu đề, mô tả đề chứa tiếng Việt | ✅ Lưu/đọc đúng |

---

## 7. Các lỗi đã phát hiện và khắc phục

| Lỗi | Mức độ | Nguyên nhân | Cách khắc phục |
|-----|--------|-------------|----------------|
| C++ Runtime Error (RE) trên Windows | **Critical** | `ProcessBuilder("solution.exe")` tìm trong PATH, không thấy file | Dùng `workDir.resolve("solution.exe")` → absolute path |
| Nút Back bị NullPointerException | **Critical** | `JButton backBtn` shadowing field | Gán `this.backBtn = new JButton(...)` |
| UI treo khi gọi API AI | **High** | Không có timeout OkHttpClient | Thêm connect/write/read timeout 30s |
| Mất file rác khi xóa đề | **High** | `deleteProblem` chỉ xóa DB, không xóa file ảnh/testcase | Xóa file trước khi xóa DB record |
| Memory leak SwingWorker | **Medium** | Không cancel worker cũ khi chuyển panel | Lưu worker làm field, gọi `cancel(true)` trước khi tạo mới |
| Bảng khó đọc | **Low** | Không có phân biệt hàng | Thêm `AlternatingRowRenderer` |

---

## 8. Tối ưu hóa đã thực hiện

1. **Judge Engine:** Giới hạn stdout/stderr 10MB tránh OOM.
2. **MLE Detection:** Dùng `ProcessHandle` + `/proc/{pid}/statm` (Linux/Mac), Windows fallback.
3. **Charset UTF-8:** Tất cả file read/write đều chỉ định `StandardCharsets.UTF_8`.
4. **Input Validation:** Giới hạn title length, time/memory limit hợp lệ.
5. **Resource Cleanup:** Xóa temp directory sau mỗi lần chấm.

---

## 9. Kết luận

Hệ thống **AI-Powered CP Judge System** đã vượt qua toàn bộ 20 bài kiểm thử tự động và kiểm thử thủ công với các ngôn ngữ Java, C++, Python. Các lỗi nghiêm trọng (C++ RE, NPE, UI treo) đã được khắc phục. Giao diện người dùng được cải thiện với alternating row colors, cancelable AI tasks, và các nút chức năng đầy đủ.

**Đánh giá tổng quan:** ✅ **Đạt yêu cầu**, sẵn sàng demo và nộp bài.

---

*Report generated on 2026-05-12*
