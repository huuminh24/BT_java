# Báo cáo kết quả thử nghiệm — AI-Powered CP Judge System

## 1. Mục tiêu thử nghiệm

Đánh giá khả năng của AI (Gemini 2.0 Flash) trong việc:
1. Đọc hiểu đề thi ICPC/IOI từ text
2. Sinh testcase đa dạng (đủ mạnh để phát hiện lỗi)
3. Sinh code AC chạy đúng
4. Phát hiện code WA/TLE khi chấm với testcase AI sinh ra

## 2. Phương pháp thử nghiệm

| Bước | Mô tả |
|---|---|
| 1 | Nhập đề thi thật vào hệ thống |
| 2 | Dùng AI Panel phân tích & sinh testcase |
| 3 | Lưu testcase vào CSDL |
| 4 | Nhập code AC (do AI sinh hoặc tự viết) → chấm → đạt AC tất cả |
| 5 | Nhập code WA (cố tình sai) → chấm → kiểm tra xem WA có bị phát hiện không |
| 6 | Nhập code TLE (vòng lặp vô hạn / độ phức tạp cao) → chấm → kiểm tra TLE |

## 3. Đề thi thử nghiệm

### Đề 1: "A + B Problem" (ICPC cơ bản)
- **Input**: 2 số nguyên a, b
- **Output**: Tổng a + b
- **Kết quả AI**:
  - Sinh 5 testcase: 1 small, 1 large (10^9), 1 negative, 1 zero, 1 edge (overflow int64 nếu có)
  - Code AC Java sinh ra chạy đúng tất cả testcase
  - Code WA (trả về a - b) → bị phát hiện WA ở tất cả testcase
  - **Đánh giá**: ✅ Testcase đủ mạnh, bao phủ edge cases cơ bản

### Đề 2: "Số nguyên tố" (IOI/HSG cấp tỉnh)
- **Input**: N số, kiểm tra mỗi số có phải số nguyên tố không
- **Output**: Yes/No cho mỗi số
- **Kết quả AI**:
  - Sinh 6 testcase: số nhỏ (<100), số lớn (10^12), số chẵn, số 1, số nguyên tố lớn, số composite
  - Code AC sinh ra dùng Miller-Rabin cho số lớn
  - Code WA (chỉ kiểm tra đến sqrt mà không xử lý số lớn) → WA ở testcase số lớn
  - Code TLE (vòng lặp kiểm tra từ 2 đến n-1) → TLE ở testcase lớn
  - **Đánh giá**: ✅ Testcase rất mạnh, phân biệt được thuật toán tối ưu và thuật toán ngây thơ

### Đề 3: "Dãy con tăng dài nhất" (LIS — ICPC khu vực)
- **Input**: Dãy N số, tìm độ dài LIS
- **Output**: Số nguyên duy nhất
- **Kết quả AI**:
  - Sinh 5 testcase: N=5 (nhỏ), N=1000 (trung bình), N=10^5 (lớn), dãy giảm hoàn toàn, dãy tăng hoàn toàn
  - Code AC sinh ra dùng Binary Search O(N log N)
  - Code WA (trả về N luôn) → WA ở dãy giảm
  - Code TLE (DP O(N^2)) → TLE ở N=10^5
  - **Đánh giá**: ✅ Phân biệt rõ ràng độ phức tạp thuật toán. Testcase lớn đủ để bắt TLE.

## 4. Tổng kết đánh giá

| Tiêu chí | Điểm | Nhận xét |
|---|---|---|
| Đọc hiểu đề | 9/10 | AI hiểu đúng format input/output, ràng buộc |
| Số lượng testcase | 8/10 | Trung bình 5-6 testcase/đề, đủ đa dạng |
| Độ mạnh testcase | 8/10 | Edge cases và large test có hiệu quả bắt WA/TLE |
| Code AC chất lượng | 7/10 | Code đúng nhưng đôi khi thiếu xử lý ngoại lệ (IOException, scanner close) |
| Checker script | 6/10 | AI sinh checker Python nhưng đôi khi không khớp format đề bài |
| Thời gian sinh | 9/10 | Mỗi lần gọi API ~3-8 giây, chấp nhận được |
| Tổng | **47/60** | **Khá** — Đủ dùng cho môi trường học tập, cần review testcase trước khi dùng production |

## 5. Hạn chế phát hiện được

1. **Testcase đôi khi thiếu randomization**: AI sinh testcase deterministic, nếu người viết code hardcode theo pattern thì có thể qua mặt.
2. **Checker script chưa ổn định**: Cần người dùng review trước khi dùng.
3. **Code AC thiếu robust**: Đôi khi AI quên `close()` scanner hoặc không xử lý `NumberFormatException`.
4. **Không tự động validate độ mạnh**: Hệ thống chưa tự đánh giá testcase có đủ mạnh không; cần người dùng chạy code WA/TLE thủ công.

## 6. Đề xuất cải tiến

- Tích hợp **fuzzing** để sinh thêm testcase ngẫu nhiên sau khi AI sinh.
- Thêm **auto-validation loop**: chạy code AC/WA mẫu ngay sau khi AI sinh testcase để tự động đánh giá độ mạnh.
- Hỗ trợ **OCR ảnh đề bài** bằng Gemini Vision khi người dùng không nhập text.
- Cache kết quả AI để tránh gọi API lặp lại.

## 7. Kết luận

Hệ thống đã đạt được mục tiêu cơ bản: AI có thể phân tích đề thi ICPC/IOI, sinh testcase đa dạng, và phát hiện được code WA/TLE trong hầu hết trường hợp thử nghiệm. Testcase AI **đủ mạnh** để bắt lỗi các đoạn code cố tình viết sai trong quá trình thử nghiệm. Tuy nhiên, người dùng nên review và bổ sung testcase thủ công trước khi dùng cho kỳ thi thật.
