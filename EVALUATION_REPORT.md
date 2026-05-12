Báo cáo đánh giá & Kiểm thử

1. Giới thiệu

Đây là báo cáo kết quả thử nghiệm chương trình AI-Powered CP Judge System — một ứng dụng Java Swing giúp nhập đề thi lập trình, dùng AI phân tích đề và tự sinh testcase, sau đó chấm thử code mẫu.

Em đã test thử trên 2 đề trong CSDL và chạy bộ test tự động `SystemTest.java` để kiểm tra các chức năng cơ bản.

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

Em có tổng cộng 2 đề trong CSDL. Dưới đây là kết quả chấm thử từng đề.

4.1. Đề "Alpha Country" 

- Code đúng  <img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/494792db-379d-46ff-96fa-eeb4c0f5cabd" />
- Code sai <img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/cba3ab7a-0441-4155-af80-7442c22a4197" />

4.2. Đề "Greatest Common Divisor" 
- Code đúng :<img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/32c60281-0835-43c0-8681-b475e616d1d5" />

-Code WA : <img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/675854fa-92b5-4393-89ee-5b7bd3811b5a" />

-Code TLE : <img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/e9360103-354c-4168-94e0-a0e0f64f6ff0" />

5. Thử nghiệm AI phân tích đề

Em thử dùng AI phân tích 2 đề: "Alpha country" và "Greatest Common Divisor".

Kết quả:
- AI đọc hiểu đề và trả về mô tả ngắn gọn đúng nghĩa.
- Sinh được 5 testcase gồm small, normal , edge.
- Có thể tick chọn "Tự động sinh code AC" thì AI viết code Java mẫu.
- Có thể hủy tác vụ AI bằng cách nhấn lại nút, không bị treo UI.

<img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/a481f0fc-685d-4b2e-8bb2-4eb365845416" />

6. Kết luận

Sau quá trình thử nghiệm, chương trình chạy ổn định với các chức năng chính:
- Nhập đề và lưu vào CSDL: hoạt động tốt.
- AI phân tích đề và sinh testcase: hoạt động chưa được tốt lắm , test case do AI sinh ra dễ bị sai  , cần chỉnh prompt với đề phức tạp.
- Chấm thử Java/C++/Python: chạy đúng, phát hiện AC/WA/TLE chính xác.
- UI hiển thị rõ ràng, có màu phân biệt status.

SystemTest đạt 20/20 passed. Em đánh giá chương trình đã đáp ứng được yêu cầu đề bài.
 Màn hình Dashboard hiển thị tổng quan (3 đề, các testcase, submission)

<img width="1733" height="1117" alt="image" src="https://github.com/user-attachments/assets/1096621a-ae06-4e40-b4cc-3986021e6c04" />

