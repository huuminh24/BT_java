# AI-Powered CP Judge System

Chương trình chấm bài lập trình thi đấu tích hợp AI, cho phép nhập đề thi, tự động sinh testcase bằng AI, và chấm thử code.

---

## Giới thiệu

Chương trình này giúp:
- **Nhập đề thi** (IOI, ICPC, Codeforces...) dạng text hoặc ảnh
- **Phân tích đề bằng AI** — Google Gemini tự động sinh testcase và checker
- **Chấm bài** — Java, C++, Python
- **Kiểm tra testcase** — Nhập code AC/WA/TLE để xem testcase có đúng không

---

## Công nghệ sử dụng

- **Java 17** — Ngôn ngữ chính
- **Maven** — Build project
- **MySQL 8.0** — Cơ sở dữ liệu
- **Google Gemini AI** — Phân tích đề và sinh testcase
- **Java Swing** — Giao diện người dùng

---

## Cài đặt

### 1. Cài đặt các chương trình cần thiết

- **JDK 17** — Tải từ Oracle hoặc Eclipse Temurin
- **Maven** — Tải từ maven.apache.org
- **MySQL 8.0** — Tải từ mysql.com hoặc dùng Docker
- **g++** — Dùng MinGW trên Windows
- **Python 3.8+** — Tải từ python.org

### 2. Clone hoặc giải nén source code

```bash
git clone <repository-url>
cd BT_Java-master
```

### 3. Khởi động MySQL

```bash
docker-compose up -d
```

Hoặc cài MySQL trực tiếp và tạo database `JudgeSystem`.

### 4. Cấu hình API Key

Mở file `src/main/resources/config.properties` và thay thế `YOUR_GEMINI_API_KEY_HERE` bằng API key của bạn (lấy từ https://aistudio.google.com/app/apikey).

### 5. Build và chạy

```bash
mvn clean package
java -jar target/JudgeSystem-1.0-SNAPSHOT.jar
```

Hoặc dùng script:
```bash
run.bat
```

---

## Cấu trúc dự án

```
BT_Java-master/
├── src/main/java/com/java/          # Source code
│   ├── Main.java                    # Chạy chương trình
│   ├── dao/                         # Truy cập CSDL
│   ├── model/                       # Class dữ liệu
│   ├── service/                     # Xử lý logic, AI, chấm bài
│   ├── ui/                          # Giao diện Swing
│   └── util/                        # Tiện ích (DB, File)
├── src/main/resources/
│   └── config.properties            # Cấu hình DB, API key
├── pom.xml                         # Maven config
├── docker-compose.yml              # MySQL
├── init.sql                        # Tạo bảng
└── JudgeSystemData/                # Dữ liệu (tự tạo)
```

---

## Hướng dẫn sử dụng

### 1. Nhập đề thi

- Nhấn "Nhập đề thi"
- Điền tiêu đề, nội dung đề
- Chọn loại kỳ thi (IOI/ICPC/Codeforces)
- Upload ảnh (nếu có)
- Nhấn "Lưu đề thi"

### 2. AI Phân tích đề

- Nhấn "AI Phân tích"
- Chọn đề vừa tạo
- Tick "Tự động sinh code AC" và "Sinh checker script"
- Nhấn "Phân tích đề & Sinh testcase"
- AI sẽ sinh testcase và code mẫu

### 3. Chấm bài

- Nhấn "Nộp code mẫu"
- Chọn đề và ngôn ngữ (Java/C++/Python)
- Nhập code AC/WA/TLE
- Nhấn "Chấm thử"
- Xem kết quả (AC/WA/TLE)

---

---

## Tài liệu tham khảo

- [INSTALL.md](INSTALL.md) — Hướng dẫn cài đặt chi tiết
- [DEMO_GUIDE.md](DEMO_GUIDE.md) — Hướng dẫn demo cho giảng viên
- [BAO_CAO.md](BAO_CAO.md) — Báo cáo đánh giá testcase AI
- [EVALUATION_REPORT.md](EVALUATION_REPORT.md) — Báo cáo kiểm thử hệ thống
