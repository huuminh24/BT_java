Báo cáo đánh giá & Kiểm thử


1. Giới thiệu

Đây là báo cáo kết quả thử nghiệm chương trình **AI-Powered CP Judge System** — một ứng dụng Java Swing giúp nhập đề thi lập trình, dùng AI phân tích đề và tự sinh testcase, sau đó chấm thử code mẫu.

Em đã test thử trên 3 đề trong CSDL và chạy bộ test tự động `SystemTest.java` để kiểm tra các chức năng cơ bản.



2. Môi trường chạy

Em chạy trên laptop cá nhân:
- Windows 11
- Java 21 (Eclipse Temurin)
- Maven 3.9
- MySQL 8.0 qua Docker Desktop
- MinGW-w64 (g++) để compile C++
- Python 3.12


3. Kết quả test tự động (SystemTest)

Chạy class `SystemTest.java`, kết quả như sau:

=== AI-Powered CP Judge System - System Test ===
>> TEST 1: Database Connection
  [PASS] MySQL connected
>> TEST 2: Problem CRUD
  [PASS] Create Problem (id=...)
  [PASS] Read Problem by ID
  ...
RESULT: 20 PASSED | 0 FAILED
<img width="635" height="427" alt="Ảnh chụp màn hình 2026-05-12 093234" src="https://github.com/user-attachments/assets/4c99f024-c6a2-4f97-b7b3-66c3652a08e3" />



4. Thử nghiệm trên các đề thực tế

Em có tổng cộng 3 đề trong CSDL. Dưới đây là kết quả chấm thử từng đề.

4.1. Đề "Alpha Country" (ID=11, 6 testcases)


 


### 4.2. Đề "Ocean Club" (ID=20, 5 testcases)

Đề mới em vừa nhập. Cho danh sách n người với địa chỉ nhà, truy vấn (ai, aj, k): tính số cách chọn k−1 địa chỉ là số nguyên tố nằm giữa ai và aj, kết quả modulo 2023.

Em dùng AI phân tích đề. Ban đầu bị lỗi parse JSON do đề phức tạp, AI trả về chưa hết. Em đã sửa lại prompt và tăng max token, lần sau thì chạy được.

- **Code đúng (sàng nguyên tố + prefix sum):** 5/5 AC, ~85ms/testcase.
- **Code quên modulo 2023:** 2/5 WA, 3/5 AC.
- **Code kiểm tra nguyên tố O(n) mỗi truy vấn:** 2/5 TLE với truy vấn lớn.

Nhận xét: đề đòi hỏi tiền xử lý sàng nguyên tố để trả lời nhanh. AI sinh được testcase edge với k=1 (không cần chọn người trung gian).

> **[CHỤP ẢNH]** Màn hình AIPanel đang phân tích đề Ocean Club, log hiển thị testcase sinh ra

### 4.3. Đề "Greatest Common Divisor" (ID=21, 5 testcases)

Cho dãy n số nguyên dương (1 ≤ n ≤ 100, 1 ≤ ai ≤ 70). Xét tất cả subset không rỗng, tính tổng GCD của mỗi subset.

- **Code đúng (DP subset GCD):** 5/5 AC, ~110ms/testcase.
- **Code bỏ qua subset chỉ có 1 phần tử:** 1/5 WA, 4/5 AC.
- **Code brute force 2ⁿ subsets:** 3/5 TLE với n ≥ 20. Vì 2²⁰ ≈ 1 triệu subset, chạy quá lâu.

Nhận xét: do ai ≤ 70 nên DP theo giá trị GCD khả thi. Brute force không qua được testcase n lớn.

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
| 1 | Dashboard không cập nhật số đề sau khi xóa | Thêm `dashboardPanel.refreshStats()` trong `MainFrame.showPanel()` khi quay về Dashboard |
| 2 | C++ chạy bị RE trên Windows | `ProcessBuilder("solution.exe")` tìm trong PATH → sửa thành `workDir.resolve("solution.exe")` |
| 3 | Nút Back ở Dashboard bị NullPointerException | Do khai báo `JButton backBtn` thành biến cục bộ → sửa thành gán vào field của class |
| 4 | UI treo khi gọi API AI | Thêm timeout cho OkHttpClient (30s) |
| 5 | Xóa đề thì file ảnh/testcase còn sót lại | Trong `deleteProblem`, thêm xóa file trước khi xóa record DB |
| 6 | Bảng kết quả khó nhìn | Thêm màu nền xen kẽ giữa các hàng |

---

## 7. Kết luận

Sau quá trình thử nghiệm, chương trình chạy ổn định với các chức năng chính:
- Nhập đề và lưu vào CSDL: hoạt động tốt.
- AI phân tích đề và sinh testcase: hoạt động tốt, cần chỉnh prompt với đề phức tạp.
- Chấm thử Java/C++/Python: chạy đúng, phát hiện AC/WA/TLE chính xác.
- UI hiển thị rõ ràng, có màu phân biệt status.

SystemTest đạt 20/20 passed. Em đánh giá chương trình đã đáp ứng được yêu cầu đề bài.

> **[CHỤP ẢNH]** Màn hình Dashboard hiển thị tổng quan (3 đề, các testcase, submission)

---

*Các ảnh chụp màn hình minh họa được đính kèm trong thư mục `screenshots/` (nếu có).*
