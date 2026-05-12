# Hướng dẫn Demo cho Giảng Viên

## Tổng quan

Hệ thống **AI-Powered CP Judge** là ứng dụng desktop Java tích hợp AI (Google Gemini) để:
- Nhập đề thi lập trình (IOI/ICPC/Codeforces) dạng text hoặc ảnh
- Phân tích đề bằng AI để tự động sinh testcase, checker script, code mẫu
- Chấm bài đa ngôn ngữ (Java/C++/Python) với judge engine
- Đánh giá chất lượng testcase thông qua code mẫu AC/WA/TLE

---

## Chuẩn bị trước Demo

### 1. Kiểm tra môi trường

Đảm bảo các thành phần sau đang hoạt động:

| Thành phần | Kiểm tra | Cách kiểm tra |
|-----------|---------|--------------|
| **JDK 17+** | `java -version` | Cần Java 17+ |
| **MySQL** | Docker container đang chạy | `docker ps` |
| **g++** | `g++ --version` | MinGW trên Windows |
| **Python** | `python --version` | Python 3.8+ |
| **API Key** | config.properties có key hợp lệ | Không hiển thị lỗi "API key not configured" |

### 2. Khởi động MySQL

```bash
docker-compose up -d
```

Kiểm tra:
```bash
docker ps
```

Phải thấy container `judge-mysql` đang chạy.

### 3. Chạy ứng dụng

```bash
# Cách 1: Dùng Maven
mvn exec:java -Dexec.mainClass="com.java.Main"

# Cách 2: Chạy JAR
java -jar target/JudgeSystem-1.0-SNAPSHOT.jar

# Cách 3: Dùng script
run.bat
```

---

## Quy trình Demo (Gợi ý)

### Phần 1: Tổng quan hệ thống (2 phút)

**Màn hình Dashboard**

Khi ứng dụng khởi động, hiển thị:
- **4 thẻ thống kê**: Đề thi, Testcases, Code mẫu, Submissions
- **Thanh thống kê**: Số lượng AC/WA và tổng submissions
- **6 card điều hướng**: Nhập đề thi, AI Phân tích, Nộp code mẫu, Kết quả chấm, Hướng dẫn, Thoát

**Điểm nhấn:**
- Giao diện Cyberpunk Dark theme
- Thống kê real-time
- Điều hướng dễ dàng

---

### Phần 2: Nhập đề thi (3 phút)

**Bước 1: Chuyển sang màn hình "Nhập đề thi"**

Nhấn card **"Nhập đề thi"** từ Dashboard.

**Bước 2: Nhập đề mẫu**

Chọn một đề đơn giản để demo nhanh, ví dụ:

**Tiêu đề:** Tổng 2 số nguyên

**Nội dung đề:**
```
Cho hai số nguyên a và b. Tính tổng a + b.

Input:
- Dòng 1: Hai số nguyên a, b (-10^9 ≤ a, b ≤ 10^9)

Output:
- Một dòng duy nhất chứa tổng a + b
```

**Loại kỳ thi:** ICPC

**Time limit:** 2000ms

**Memory limit:** 256MB

**Bước 3: Lưu đề**

Nhấn **"Lưu đề thi"**

**Kết quả:**
- Đề được lưu vào CSDL
- Ảnh (nếu có) được copy vào `JudgeSystemData/Problems/problem_X/`
- Đề xuất hiện trong danh sách bên trái

**Bước 4: Quay lại Dashboard**

Nhấn **"Quay lại"** để xem thống kê cập nhật (Đề thi tăng +1).

---

### Phần 3: AI Phân tích đề & Sinh testcase (5 phút)

**Bước 1: Chuyển sang màn hình "AI Phân tích"**

Nhấn card **"AI Phân tích"** từ Dashboard.

**Bước 2: Chọn đề vừa tạo**

Dropdown hiển thị danh sách đề, chọn đề "Tổng 2 số nguyên".

**Bước 3: Cấu hình AI**

Tick vào:
- ☑ **Tự động sinh code AC** — AI sẽ tạo code giải đúng
- ☑ **Sinh checker script** — AI sẽ tạo checker Python (nếu cần)

**Bước 4: Gọi AI phân tích**

Nhấn **"Phân tích đề & Sinh testcase"**

**Quá trình thực hiện (hiển thị trong Log):**
- "Đang gọi Gemini API..."
- "Đang phân tích đề: Tổng 2 số nguyên"
- "Nhận response từ AI, đang parse JSON..."
- "Đã sinh 5 testcase"
- "Đã lưu testcase vào CSDL"
- "Đã lưu code AC vào SampleCodes"

**Bước 5: Xem testcase đã sinh**

Bảng testcase hiển thị:
- STT, Loại (small/large/edge/normal), Input, Expected Output, AI?

**Điểm nhấn:**
- AI sinh testcase đa dạng: small (input nhỏ), large (input lớn), edge (biên), normal (thông thường)
- Tất cả testcase đều có input/output khớp nhau (đúng đắn)
- AI sinh code AC tự động

---

### Phần 4: Chấm bài với code AC (3 phút)

**Bước 1: Chuyển sang màn hình "Nộp code mẫu"**

Nhấn card **"Nộp code mẫu"** từ Dashboard.

**Bước 2: Chọn đề và ngôn ngữ**

- Chọn đề: "Tổng 2 số nguyên"
- Chọn ngôn ngữ: `java`
- Kết quả mong đợi: `AC`

**Bước 3: Nhập code AC**

Copy-paste code sau vào text area:

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

**Bước 4: Lưu code mẫu**

Nhấn **"Lưu code mẫu"**

**Kết quả:**
- Code được lưu vào CSDL
- File được lưu tại `JudgeSystemData/Samples/problem_X/`

**Bước 5: Chấm thử**

Nhấn **"Chấm thử"**

**Kết quả hiển thị trong bảng:**
- Testcase #1: AC — 45ms (xanh)
- Testcase #2: AC — 38ms (xanh)
- Testcase #3: AC — 42ms (xanh)
- Testcase #4: AC — 35ms (xanh)
- Testcase #5: AC — 187ms (xanh)

**Điểm nhấn:**
- Tất cả testcase AC
- Thời gian chạy nhanh
- Màu xanh dễ nhìn

---

### Phần 5: Chấm bài với code WA (3 phút)

**Bước 1: Nhập code WA**

Giữ nguyên đề "Tổng 2 số nguyên", nhưng nhập code sai:

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a - b);  // SAI: tính hiệu thay vì tổng
    }
}
```

**Bước 2: Lưu code mẫu**

- Kết quả mong đợi: `WA`
- Nhấn **"Lưu code mẫu"**

**Bước 3: Chấm thử**

Nhấn **"Chấm thử"**

**Kết quả hiển thị trong bảng:**
- Testcase #1: WA — 40ms (đỏ)
- Testcase #2: WA — 35ms (đỏ)
- Testcase #3: WA — 42ms (đỏ)
- Testcase #4: WA — 30ms (đỏ)
- Testcase #5: WA — 180ms (đỏ)

**Điểm nhấn:**
- Hệ thống phát hiện code sai ngay lập tức
- Màu đỏ dễ nhận diện
- Thời gian vẫn nhanh

**Giải thích:**
- Testcase AI sinh ra có tính đúng đắn, nên code WA bị bắt ngay

---

### Phần 6: Chấm bài với code TLE (2 phút)

**Bước 1: Nhập code TLE**

Giữ nguyên đề, nhập code có vòng lặp vô hạn:

```java
public class Main {
    public static void main(String[] args) {
        while (true) {
            // Vòng lặp vô hạn
        }
    }
}
```

**Bước 2: Lưu code mẫu**

- Kết quả mong đợi: `TLE`
- Nhấn **"Lưu code mẫu"**

**Bước 3: Chấm thử**

Nhấn **"Chấm thử"**

**Kết quả hiển thị trong bảng:**
- Testcase #1: TLE — 2000ms (vàng)
- Testcase #2: TLE — 2000ms (vàng)
- ...

**Điểm nhấn:**
- Hệ thống dừng sau 2000ms (time limit)
- Process bị kill
- Màu vàng dễ nhận diện

---

### Phần 7: Xem kết quả tổng hợp (2 phút)

**Bước 1: Chuyển sang màn hình "Kết quả chấm"**

Nhấn card **"Kết quả chấm"** từ Dashboard.

**Bước 2: Xem bảng submissions**

Bảng hiển thị tất cả submissions đã chấm:
- ID, Đề thi, Code mẫu, Testcase
- Status (màu pill), Time, Memory, Error

**Bước 3: Lọc theo đề**

Dropdown chọn "Tổng 2 số nguyên" để xem chỉ submissions của đề đó.

**Bước 4: Xem thống kê**

Thanh thống kê hiển thị:
- Tổng: 15 submissions
- AC: 5
- WA: 5
- TLE: 5
- Other: 0

**Điểm nhấn:**
- Lọc dễ dàng
- Thống kê rõ ràng
- Màu sắc phân biệt

---

### Phần 8: Xử lý testcase AI sai (nếu có) (2 phút)

**Tình huống:** Nếu testcase AI sinh ra bị sai

**Bước 1: Xem testcase hiện có**

Trong màn hình "AI Phân tích", nhấn **"Xem testcase hiện có"**

**Bước 2: Xóa testcase sai**

Nếu thấy testcase có input/output không khớp:
- Xóa testcase đó khỏi CSDL (dùng MySQL workbench hoặc script)
- Hoặc sửa trực tiếp trong file `JudgeSystemData/Testcases/problem_X/tc_Y_input.txt` và `tc_Y_output.txt`

**Bước 3: Re-analyze với AI**

Quay lại màn hình "AI Phân tích", nhấn lại **"Phân tích đề & Sinh testcase"** để AI sinh testcase mới.

**Điểm nhấn:**
- Hệ thống cho phép chỉnh sửa testcase thủ công
- AI có thể sinh lại testcase
- Người dùng có full control

---

### Phần 9: Demo với ngôn ngữ khác (tùy chọn, 3 phút)

**Bước 1: Chấm code C++**

- Ngôn ngữ: `cpp`
- Code AC:

```cpp
#include <iostream>
using namespace std;

int main() {
    int a, b;
    cin >> a >> b;
    cout << a + b << endl;
    return 0;
}
```

**Kết quả:** Tất cả testcase AC

**Bước 2: Chấm code Python**

- Ngôn ngữ: `python`
- Code AC:

```python
a, b = map(int, input().split())
print(a + b)
```

**Kết quả:** Tất cả testcase AC

**Điểm nhấn:**
- Hệ thống hỗ trợ đa ngôn ngữ
- Judge engine hoạt động đúng cho Java/C++/Python

---

## Kết thúc Demo

### Tóm tắt các tính năng đã demo

| Tính năng | Đã demo? | Điểm nhấn |
|-----------|----------|-----------|
| Nhập đề thi (text/ảnh) | ✅ | Lưu vào CSDL, upload ảnh |
| AI phân tích đề | ✅ | Gọi Gemini API, parse JSON |
| AI sinh testcase | ✅ | Đa dạng: small/large/edge/normal |
| AI sinh code AC | ✅ | Tự động lưu vào SampleCodes |
| AI sinh checker script | ✅ | Python checker cho bài đặc biệt |
| Chấm Java AC | ✅ | Tất cả testcase AC, thời gian nhanh |
| Chấm Java WA | ✅ | Phát hiện sai ngay lập tức |
| Chấm Java TLE | ✅ | Dừng sau time limit |
| Chấm C++ | ✅ | Hoạt động đúng |
| Chấm Python | ✅ | Hoạt động đúng |
| Xem kết quả tổng hợp | ✅ | Lọc, thống kê, màu sắc |
| Chỉnh sửa testcase thủ công | ✅ | Full control |

---

## Các câu hỏi có thể gặp và câu trả lời

### Q1: Testcase AI sinh ra có luôn đúng không?

**A:** Không phải lúc nào cũng 100%. AI có thể sinh testcase sai do:
- Hiểu sai đề bài
- Tính toán sai output
- JSON parse lỗi

**Giải pháp:**
- Hệ thống cho phép người dùng xem và chỉnh sửa testcase thủ công
- Có thể xóa testcase sai và gọi AI sinh lại
- Có thể thêm testcase thủ công để bổ sung

### Q2: Nếu testcase AI bị sai, code mẫu vẫn được chấm đúng không?

**A:** Nếu testcase sai (input/output không khớp), thì ngay cả code AC cũng sẽ bị WA. Do đó:
- Người dùng cần kiểm tra testcase AI trước khi dùng
- Hệ thống cho phép chỉnh sửa testcase thủ công
- Có thể chạy code AC để verify testcase

### Q3: Code mẫu AC/WA/TLE có bắt buộc không?

**A:** Không bắt buộc, nhưng khuyến nghị:
- **Code AC:** Để verify testcase AI có đúng không
- **Code WA:** Để kiểm tra testcase AI có đủ mạnh không (bắt được lỗi không)
- **Code TLE:** Để kiểm tra time limit có hợp lý không

Nếu không có code mẫu, AI có thể tự sinh code AC (nếu tick option).

### Q4: Hệ thống có offline được không?

**A:** Không hoàn toàn offline vì:
- AI cần gọi Gemini API (cần Internet)
- MySQL chạy local (có thể offline sau khi đã tạo đề)

Nếu không có Internet:
- Vẫn có thể nhập đề thủ công
- Vẫn có thể chấm bài với testcase đã có
- Không thể dùng AI phân tích đề

### Q5: Hệ thống có bảo mật không?

**A:** Có các biện pháp bảo mật:
- Database connection string và API key nằm trong `config.properties`
- File `config.properties` được exclude khỏi Git (để không leak lên GitHub)
- Có file `config.properties.example` làm mẫu (không có secret)

### Q6: Có thể dùng AI khác ngoài Gemini không?

**A:** Hiện tại hệ thống chỉ tích hợp Gemini, nhưng kiến trúc cho phép mở rộng:
- Interface `AIService` có thể implement cho Claude, GPT-4
- Chỉ cần thay đổi implementation, không cần sửa UI/Judge engine

---

## Tham khảo thêm

- **INSTALL.md** — Hướng dẫn cài đặt chi tiết
- **README.md** — Tổng quan dự án và cấu trúc
- **BAO_CAO.md** — Báo cáo đánh giá testcase AI
- **EVALUATION_REPORT.md** — Báo cáo kiểm thử hệ thống

---

*Hướng dẫn demo tạo ngày 12/05/2026*
