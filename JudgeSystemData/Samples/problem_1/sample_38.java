import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo đối tượng Scanner để đọc dữ liệu từ bàn phím
        Scanner scanner = new Scanner(System.in);
        
        // Đọc 2 số a và b. 
        // Dùng kiểu long để tránh lỗi tràn bộ nhớ nếu số quá lớn
        long a = scanner.nextLong();
        long b = scanner.nextLong();
        
        // In ra tổng
        
        
        // Đóng scanner để giải phóng tài nguyên (thói quen tốt khi code Java)
        scanner.close();
    }
}