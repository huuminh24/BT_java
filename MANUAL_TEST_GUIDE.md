# Hướng dẫn Test thủ công (End-to-End)

## Bước 1: Import dữ liệu mẫu

1. Đảm bảo MySQL đang chạy:
   ```powershell
   docker-compose up -d
   ```
2. Import dữ liệu mẫu:
   ```powershell
   Get-Content sample_data.sql | docker exec -i bt_java-master-mysql-1 mysql -uroot -p123 JudgeSystem
   ```
   *(Nếu tên container khác, kiểm tra bằng `docker ps`)*

> Sau khi import, bạn sẽ có 3 bài toán trong CSDL: **A + B**, **Prime Check**, **Sum 1 to N**. Mỗi bài có 5 testcase và 3 code mẫu (AC, WA, TLE).

---

## Bước 2: Khởi động ứng dụng

```powershell
mvn clean package -DskipTests
java -jar target/JudgeSystem-1.0-SNAPSHOT.jar
```

---

## Bước 3: Test từng chức năng trên GUI

### 3.1. Xem danh sách đề bài (Problem Entry)
- Vào tab **Nhập đề / Quản lý**
- Xem danh sách: `A + B Problem`, `Prime Check`, `Sum 1 to N`
- Click vào 1 đề → thông tin hiện ra form → chứng tỏ **CRUD + DB** OK

### 3.2. Test AI Phân tích đề + Sinh testcase (AI Analysis)
- Vào tab **AI Phân tích**
- Chọn đề **A + B Problem**
- Click **Phân tích đề bằng AI**
  - Chờ kết quả: phân tích đề, gợi ý thuật toán
- Click **Sinh testcase tự động**
  - Chờ bảng testcase hiện ra → lưu vào DB
  - Chứng tỏ **AI API + Testcase generation** OK

### 3.3. Test Chấm code mẫu (Code Submission)
- Vào tab **Nộp code / Chấm thử**
- Chọn đề **A + B Problem**
- Chọn ngôn ngữ **Java**
- Chọn loại code **AC** (từ dropdown / paste code AC)
- Click **Chạy thử & Chấm**
- Kết quả mong đợi:
  - Status: **AC** (xanh)
  - Passed: 5/5
- Thử lại với code **WA** và **TLE**:
  - WA → Status: **WA** (đỏ), passed < 5
  - TLE → Status: **TLE** (cam), chạy quá 1s bị kill

- Lặp lại với đề **Prime Check** và **Sum 1 to N**
- Chứng tỏ **JudgeEngine + FileManager + Submission DB** OK

### 3.4. Test Sinh code mẫu bằng AI
- Vào tab **AI Phân tích**
- Chọn đề **Sum 1 to N**
- Check **Tự động sinh code AC**
- Click **Sinh testcase tự động**
- Sau khi AI sinh testcase, code AC cũng sẽ được tạo → lưu vào mục **Code mẫu**
- Chứng tỏ **AI sinh code mẫu** OK

### 3.5. Xem kết quả chấm (Result Panel)
- Vào tab **Kết quả**
- Xem bảng các submission vừa chạm
- Có thể lọc theo đề bài
- Chứng tỏ **Submission history + Status filter** OK

---

## Bước 4: Kiểm tra dữ liệu trên đĩa

Mở thư mục `JudgeSystemData` trong project root:
- `Problems/problem_<id>/` → chứa file mô tả
- `Testcases/problem_<id>/` → chứa `1.in`, `1.out`, ...
- `Samples/problem_<id>/` → chứa code mẫu `.java`
- `Submissions/` → chứa log chấm

Nếu thấy file đầy đủ → **FileManager 100% OK**.

---

## Tiêu chí PASS/FAIL

| Chức năng | PASS nếu |
|---|---|
| DB Connection | 3 đề bài hiển thị đúng trong danh sách |
| AI Phân tích | Trả về phân tích có nội dung liên quan đến đề |
| AI Testcase | Bảng testcase hiện >= 3 dòng, lưu DB thành công |
| AI Code | Code AC được sinh ra và lưu vào Samples |
| Judge AC | Code AC cho đúng 5/5 testcase, thời gian < 1s |
| Judge WA | Code WA cho sai ít nhất 1 testcase |
| Judge TLE | Code TLE bị kill sau ~1s, status TLE |
| File lưu đĩa | Thư mục `JudgeSystemData` có đủ file `.in`, `.out`, `.java` |

Nếu tất cả đều PASS → chương trình **ổn định 100%**, sẵn sàng nộp bài.
