# AI-Powered CP Judge System

> Bài tập lớn Java — Khoa Công nghệ thông tin

Ứng dụng desktop Java Swing kết nối MySQL, tích hợp Google Gemini API để phân tích đề thi lập trình thi đấu, tự động sinh testcase và chấm thử code.

## Yêu cầu đề bài

1. **Nhập đề thi** (ICPC/IOI/Codeforces...) bằng text hoặc ảnh → lưu vào CSDL.
2. **Phân tích đề bằng AI** — Dùng Gemini API sinh testcase, checker script (nếu cần).
3. **Chấm thử code mẫu** — Nhập code AC/WA/TLE để kiểm tra testcase có đủ mạnh không. Nếu chưa có code mẫu, AI có thể tự sinh.
4. **Hướng dẫn cài đặt & sử dụng** chi tiết.
5. **Báo cáo đánh giá** kết quả thử nghiệm trên một số đề.

## Công nghệ

- Java 21 + Swing (FlatLaf Dark)
- MySQL 8.0 (Docker)
- Google Gemini 2.5 Flash API
- Maven build

## Chạy nhanh

```bash
# 1. Khởi động MySQL
docker-compose up -d

# 2. Build & chạy
mvn clean package
java -jar target/JudgeSystem-1.0-SNAPSHOT.jar
```

> Cần thêm API Key Gemini vào `src/main/resources/config.properties` trước khi build.

## Tài liệu

| File | Nội dung |
|------|----------|
| [INSTALL.md](INSTALL.md) | Hướng dẫn cài đặt chi tiết (JDK, Maven, Docker, g++, Python) |
| [USER_GUIDE.md](USER_GUIDE.md) | Hướng dẫn sử dụng từng chức năng |
| [EVALUATION_REPORT.md](EVALUATION_REPORT.md) | Báo cáo đánh giá 3 đề trong quá trình thử nghiệm |

## Giao diện

Dashboard hiển thị thống kê và điều hướng giữa các chức năng:
- **Nhập đề thi** — Form + danh sách đề + upload ảnh
- **AI Phân tích** — Chọn đề → Gemini sinh testcase + code mẫu
- **Nộp code / Chấm thử** — Chọn đề + ngôn ngữ (Java/C++/Python) → chạy với timeout
- **Kết quả chấm** — Bảng submissions với màu AC (xanh) / WA (đỏ) / TLE (vàng)
