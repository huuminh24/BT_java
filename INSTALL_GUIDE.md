# Hướng dẫn cài đặt — AI-Powered CP Judge System

## Yêu cầu hệ thống

- **OS**: Windows 10/11, macOS, hoặc Linux
- **JDK**: 17 trở lên ([Oracle](https://www.oracle.com/java/technologies/downloads/) hoặc [Adoptium](https://adoptium.net/))
- **Maven**: 3.8+ ([Maven Download](https://maven.apache.org/download.cgi))
- **Docker & Docker Compose** (khuyến nghị) — để chạy MySQL
- **Trình biên dịch**:
  - `javac` / `java` (đi kèm JDK)
  - `g++` (MinGW-w64 trên Windows, hoặc GCC trên Linux/macOS)
  - `python3` (nếu cần chấm Python — optional)

## Bước 1: Clone / Giải nén dự án

```bash
cd BT_Java-master
```

## Bước 2: Khởi động MySQL bằng Docker

```bash
docker-compose up -d
```

- MySQL sẽ chạy ở `localhost:3306`
- Database `JudgeSystem` được tự động tạo từ file `init.sql`
- Tài khoản mặc định: `root` / `123`

Kiểm tra:
```bash
docker ps
# Nếu container `judge_mysql_db` đang chạy là OK
```

## Bước 3: Cấu hình API Key Gemini

Mở file `src/main/resources/config.properties` và điền API Key:

```properties
ai.api.key=AIzaSyxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

> Lấy API Key tại: [Google AI Studio](https://aistudio.google.com/app/apikey)

## Bước 4: Build project

```bash
mvn clean package
```

Nếu thành công, sẽ xuất hiện file `target/JudgeSystem-1.0-SNAPSHOT.jar`.

## Bước 5: Chạy ứng dụng

```bash
java -jar target/JudgeSystem-1.0-SNAPSHOT.jar
```

Giao diện Swing sẽ hiện lên.

## Bước 6: Kiểm tra kết nối CSDL

Trong console sẽ hiển thị:
```
Da tao thu muc: ./JudgeSystemData/...
Ket noi CSDL da cau hinh: jdbc:mysql://localhost:3306/JudgeSystem
Ket noi CSDL thanh cong!
```

Nếu thấy "Ket noi CSDL that bai", kiểm tra:
- Docker container MySQL đã chạy chưa (`docker ps`)
- Port 3306 có bị chiếm không
- Firewall/Antivirus có chặn không

## Troubleshooting

| Lỗi | Nguyên nhân | Cách fix |
|---|---|---|
| `Cannot find driver` | MySQL Connector chưa vào classpath | `mvn clean package` lại |
| `Access denied for user` | Sai password/root | Sửa `config.properties` hoặc `docker-compose.yml` |
| `javac not found` | Chưa cài JDK hoặc chưa set PATH | Cài JDK 17 và thêm `JAVA_HOME/bin` vào PATH |
| `g++ not found` | Chưa cài MinGW/GCC | Cài MinGW-w64, thêm `bin` vào PATH |
| `AI response error` | Sai API Key hoặc hết quota | Kiểm tra key, đổi key mới từ Google AI Studio |

## Cấu trúc thư mục dữ liệu

Sau khi chạy, dữ liệu file cục bộ được lưu tại `./JudgeSystemData/`:

```
JudgeSystemData/
├── Problems/          # Ảnh đề bài
├── Testcases/         # File input/output .txt
├── Submissions/       # Output chấm bài
└── Samples/           # Code mẫu AC/WA/TLE
```
