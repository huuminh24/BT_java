# Hướng dẫn sử dụng — AI-Powered CP Judge System

## 1. Màn hình Dashboard

Khi khởi động, bạn sẽ thấy màn hình chính với 5 nút chức năng:

- **Nhập đề thi mới** — Tạo bài toán mới
- **AI Phân tích & Sinh Testcase** — Dùng AI đọc đề và sinh testcase
- **Nộp code mẫu / Chấm thử** — Nhập code AC/WA/TLE để kiểm tra
- **Xem kết quả chấm** — Bảng submissions với màu sắc trực quan
- **Thoát** — Đóng ứng dụng

## 2. Nhập đề thi mới (Problem Entry)

### 2.1. Nhập thông tin cơ bản
- **Tiêu đề**: Tên bài toán (ví dụ: "Tổng 2 số")
- **Nội dung đề**: Mô tả chi tiết đề bài, input/output format, ràng buộc
- **Loại kỳ thi**: ICPC / IOI / Codeforces / AtCoder / Other
- **Time limit**: Mặc định 2000ms
- **Memory limit**: Mặc định 256MB

### 2.2. Upload ảnh đề bài (tùy chọn)
- Bấm **Chọn ảnh...** → chọn file `.jpg`, `.png`, `.gif`
- Ảnh sẽ được lưu vào `JudgeSystemData/Problems/problem_X/`
- AI có thể đọc ảnh này qua OCR nếu bạn không nhập text

### 2.3. Lưu đề thi
- Bấm **💾 Lưu đề mới** → Tạo đề mới, ID hiển thị trong log
- Bấm **🔄 Cập nhật** → Sửa đề đã chọn trong danh sách bên trái (title, description, limits, ảnh)
- Bấm **📄 Mới** → Xóa trắng form để nhập đề khác
- **Chọn đề trong danh sách bên trái** để xem thông tin và chỉnh sửa

## 3. AI Phân tích & Sinh Testcase (AI Panel)

### 3.1. Chọn đề thi
- Từ dropdown, chọn đề vừa nhập
- Bấm **Tải lại danh sách** nếu đề chưa xuất hiện

### 3.2. Chọn tùy chọn
- ☑ **Tự động sinh code AC**: AI sẽ viết solution mẫu và lưu vào hệ thống
- ☑ **Sinh checker script**: AI sẽ viết script Python checker nếu đề cần chấm tùy biến

### 3.3. Phân tích đề
- Bấm **Phân tích đề & Sinh testcase**
- Hệ thống gọi Gemini API, log hiển thị tiến trình
- **Nhấn lại nút** trong khi đang chạy để **hủy** tác vụ AI, tránh UI treo nếu API chậm
- **Kết quả mong đợi**:
  - Mô tả ngắn gọn bài toán
  - 5+ testcase đa dạng (small, large, edge, normal)
  - Code AC (nếu chọn) được lưu với `expected_type = AC` và `is_ai_generated = true`

> **Lưu ý**: Cần kết nối Internet và API Key hợp lệ trong `config.properties`.

## 4. Nộp code mẫu / Chấm thử (Code Submit)

### 4.1. Nhập code
- Chọn đề thi, ngôn ngữ (`java` hoặc `cpp`), và kết quả mong đợi (`AC`, `WA`, `TLE`)
- Dán code vào textarea

### 4.2. Lưu code mẫu
- Bấm **Lưu code mẫu** để lưu vào CSDL mà không chấm
- Dùng khi bạn muốn lưu nhiều bộ code để thử nghiệm sau

### 4.3. Chấm thử
- Bấm **Chấm thử** để hệ thống:
  1. Compile code
  2. Chạy với tất cả testcase của đề đó
  3. So sánh output với expected output
  4. Ghi kết quả vào bảng Submissions

Log hiển thị:
```
Đang chấm bài...
Testcase 1: AC | Time: 45ms
Testcase 2: WA | Time: 50ms
...
Hoàn tất chấm bài.
```

## 5. Xem kết quả chấm (Result Panel)

Bảng hiển thị tất cả submissions với màu sắc:

| Màu | Ý nghĩa |
|---|---|
| 🟢 **Xanh lá** | **AC** — Accepted (đúng hoàn toàn) |
| 🔴 **Đỏ** | **WA** — Wrong Answer (output sai) |
| 🟡 **Vàng** | **TLE** — Time Limit Exceeded (quá thời gian) |
| ⚪ **Xám** | **RE/CE/MLE** — Runtime/Compile/Memory Error |

Các cột: ID, Problem ID, Sample Code ID, Testcase ID, Status, Time (ms), Memory (KB), Error message.

Bấm **Tải kết quả** để refresh bảng.
Bấm **Xóa kết quả** để xóa toàn bộ submission trong CSDL (có xác nhận).

> **Giao diện**: Bảng có màu nền xen kẽ giữa các hàng (alternating rows) giúp dễ đọc hơn trên nền tối.

## 6. Luồng thao tác đề xuất

```
Bước 1: Nhập đề thi (Problem Entry)
    ↓
Bước 2: Dùng AI sinh testcase + solution (AI Panel)
    ↓
Bước 3: Nhập code WA/TLE để kiểm tra độ mạnh testcase (Code Submit)
    ↓
Bước 4: Xem kết quả, đánh giá testcase có đủ mạnh không (Result Panel)
    ↓
Bước 5: Nếu testcase yếu → quay lại AI Panel yêu cầu sinh thêm edge case
```

## 7. Mẹo sử dụng

- **Testcase đủ mạnh?** Nhập một đoạn code cố tình sai (ví dụ: thiếu modulo, overflow int) để xem AI testcase có bắt được không.
- **Không có mạng?** Bạn vẫn có thể nhập đề + testcase + code thủ công, chỉ không dùng được AI.
- **Backup dữ liệu**: Thư mục `./JudgeSystemData/` chứa toàn bộ file vật lý. Database có thể backup bằng `mysqldump`.
