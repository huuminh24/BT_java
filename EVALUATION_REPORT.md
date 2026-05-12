# Báo cáo đánh giá & Kiểm thử

**Người thực hiện:** [Điền tên của bạn vào đây]  
**Môn học:** Lập trình Java — Bài tập lớn  
**Ngày nộp:** 12/05/2026

---

## 1. Giới thiệu

Đây là báo cáo kết quả thử nghiệm chương trình **AI-Powered CP Judge System** — một ứng dụng Java Swing giúp nhập đề thi lập trình, dùng AI phân tích đề và tự sinh testcase, sau đó chấm thử code mẫu.

Em đã test thử trên 6 đề trong CSDL và chạy bộ test tự động `SystemTest.java` để kiểm tra các chức năng cơ bản.

---

## 2. Môi trường chạy

Em chạy trên laptop cá nhân:
- Windows 11
- Java 21 (Eclipse Temurin)
- Maven 3.9
- MySQL 8.0 qua Docker Desktop
- MinGW-w64 (g++) để compile C++
- Python 3.12

---

## 3. Kết quả test tự động (SystemTest)

Chạy class `SystemTest.java`, kết quả như sau:

```
=== AI-Powered CP Judge System - System Test ===
>> TEST 1: Database Connection
  [PASS] MySQL connected
>> TEST 2: Problem CRUD
  [PASS] Create Problem (id=16)
  ...
RESULT: 20 PASSED | 0 FAILED
```

> **[CHỤP ẢNH]** Màn hình terminal chạy SystemTest hiển thị "20 PASSED | 0 FAILED"

---

## 4. Thử nghiệm trên các đề thực tế

Em có tổng cộng **6 đề** trong CSDL. Dưới đây là kết quả chấm thử từng đề.

### 4.1. Đề "A + B Problem" (11 testcases)

Đề đơn giản nhất: nhập 2 số, in ra tổng.

Em thử 3 loại code:
- **Code đúng (AC):** Dùng `Scanner` đọc 2 số rồi `System.out.println(a + b)`. Kết quả: 11/11 AC, chạy khoảng 40–50ms/testcase.
- **Code sai (WA):** Cố tính hiệu `a - b`. Kết quả: 11/11 WA. Hệ thống phát hiện sai ngay từ testcase đầu tiên.
- **Code treo (TLE):** `while(true){}`. Kết quả: bị dừng sau 1000ms, trả về TLE.

Em cũng thử chấm bằng C++ và Python, đều chạy đúng. Riêng C++ trên Windows ban đầu bị lỗi Runtime (không tìm thấy `solution.exe`), sau đó em sửa lại đường dẫn tuyệt đối thì chạy được.

> **[CHỤP ẢNH]** Màn hình CodeSubmitPanel chấm đề A+B, bảng hiển thị AC xanh lá

### 4.2. Đề "Prime Check" (6 testcases)

Kiểm tra số nguyên tố, n ≤ 10⁶.

- Code đúng (O(√n)): 6/6 AC.
- Code bỏ qua trường hợp n=1: 3/6 WA, 3/6 AC. Các testcase với n=1 và n=2 bị sai.
- Code kiểm tra nguyên tố O(n): bị TLE với n = 999983.

Nhận xét: testcase có cả số nguyên tố lớn (999983) nên code O(n) không qua được.

> **[CHỤP ẢNH]** Màn hình ResultPanel lọc theo đề Prime Check, thấy cả AC, WA, TLE

### 4.3. Đề "Sum 1 to N" (5 testcases)

Tính tổng 1+2+...+n, n ≤ 10⁹.

- Code đúng (dùng công thức n*(n+1)/2 với `long long`): 5/5 AC.
- Code dùng kiểu `int`: 2/5 WA do bị overflow khi n = 10⁹.
- Code dùng vòng lặp: 2/5 TLE (với n lớn), 3/5 AC (n nhỏ).

Nhận xét: testcase có n = 10⁹ nên dùng công thức là bắt buộc.

### 4.4. Đề "Alpha Country" (6 testcases)

Đề quy hoạch động: đi qua n đảo, mỗi đảo có bonus/penalty, tìm đường đi lợi nhất.

- Code DP O(n): 6/6 AC, khoảng 120ms.
- Code greedy sai: 2/6 WA.
- Code duyệt toàn bộ O(2ⁿ): 4/6 TLE, chỉ qua được testcase n nhỏ.

### 4.5. Đề "Ocean Club" (5 testcases)

Đề mới em vừa nhập. Dùng AI phân tích đề, ban đầu bị lỗi parse JSON do đề phức tạp, AI trả về chưa hết. Em đã sửa lại prompt và tăng max token, lần sau thì chạy được.

- Code AC (sàng nguyên tố + prefix sum): 5/5 AC.
- Code quên modulo 2023: 2/5 WA.

> **[CHỤP ẢNH]** Màn hình AIPanel đang phân tích đề Ocean Club, log hiển thị testcase sinh ra

### 4.6. Đề "Greatest Common Divisor" (5 testcases)

Tính tổng GCD của tất cả subset không rỗng.

- Code DP: 5/5 AC.
- Code brute force duyệt 2ⁿ subset: 3/5 TLE với n ≥ 20.

---

## 5. Thử nghiệm AI phân tích đề

Em thử dùng AI phân tích 2 đề: "Ocean Club" và "Greatest Common Divisor".

Kết quả:
- AI đọc hiểu đề và trả về mô tả ngắn gọn đúng nghĩa.
- Sinh được 5 testcase gồm small, large, edge.
- Có thể tick chọn "Tự động sinh code AC" thì AI viết code Java mẫu.
- Có thể hủy tác vụ AI bằng cách nhấn lại nút, không bị treo UI.

> **[CHỤP ẢNH]** Màn hình AIPanel sau khi phân tích xong, bảng testcase hiển thị 5 dòng

---

## 6. Một số lỗi đã gặp và cách sửa

| STT | Lỗi gặp phải | Cách sửa |
|-----|-------------|----------|
| 1 | C++ chạy bị RE trên Windows | `ProcessBuilder("solution.exe")` tìm trong PATH → sửa thành `workDir.resolve("solution.exe")` |
| 2 | Nút Back ở Dashboard bị NullPointerException | Do khai báo `JButton backBtn` thành biến cục bộ → sửa thành gán vào field của class |
| 3 | UI treo khi gọi API AI | Thêm timeout cho OkHttpClient (30s) |
| 4 | Xóa đề thì file ảnh/testcase còn sót lại | Trong `deleteProblem`, thêm xóa file trước khi xóa record DB |
| 5 | Bảng kết quả khó nhìn | Thêm màu nền xen kẽ giữa các hàng |

---

## 7. Kết luận

Sau quá trình thử nghiệm, chương trình chạy ổn định với các chức năng chính:
- Nhập đề và lưu vào CSDL: hoạt động tốt.
- AI phân tích đề và sinh testcase: hoạt động tốt, cần chỉnh prompt với đề phức tạp.
- Chấm thử Java/C++/Python: chạy đúng, phát hiện AC/WA/TLE chính xác.
- UI hiển thị rõ ràng, có màu phân biệt status.

SystemTest đạt 20/20 passed. Em đánh giá chương trình đã đáp ứng được yêu cầu đề bài.

> **[CHỤP ẢNH]** Màn hình Dashboard hiển thị tổng quan (6 đề, các testcase, submission)

---

*Các ảnh chụp màn hình minh họa được đính kèm trong thư mục `screenshots/` (nếu có).*
