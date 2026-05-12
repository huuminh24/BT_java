#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

const long long INF = 1e18; // Số âm vô cùng lớn để khởi tạo

int main() {
    // Tối ưu hóa I/O để chạy nhanh hơn trong C++
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    int n;
    if (!(cin >> n)) return 0;

    vector<long long> m(n);
    vector<long long> unique_vals;
    
    for (int i = 0; i < n; ++i) {
        cin >> m[i];
        unique_vals.push_back(m[i]);
    }

    // Lọc các giá trị xuất hiện trong mảng để làm mốc kiểm tra (X)
    sort(unique_vals.begin(), unique_vals.end());
    unique_vals.erase(unique(unique_vals.begin(), unique_vals.end()), unique_vals.end());

    long long max_profit = 0;

    // Duyệt qua từng giá trị có thể là phần tử lớn nhất trong dãy con
    for (long long X : unique_vals) {
        long long max_sum_for_X = -INF;
        long long current_sum = -INF;
        
        for (int i = 0; i < n; ++i) {
            // Nếu phần tử lớn hơn mức X cho phép, dãy con buộc phải đứt đoạn
            if (m[i] > X) {
                current_sum = -INF;
            } else {
                // Thuật toán Kadane (áp dụng cho dãy con bắt buộc phải có ít nhất 1 phần tử)
                if (current_sum < 0) {
                    current_sum = m[i]; // Bắt đầu dãy mới nếu tổng cũ đang âm
                } else {
                    current_sum += m[i]; // Cầm nối tiếp nếu tổng cũ mang lại lợi ích
                }
                
                if (current_sum > max_sum_for_X) {
                    max_sum_for_X = current_sum;
                }
            }
        }
        
        // Cập nhật lợi nhuận toàn cục
        // Nếu max_sum_for_X != -INF nghĩa là có ít nhất 1 dãy con hợp lệ
        if (max_sum_for_X != -INF) {
            long long profit = max_sum_for_X - X;
            if (profit > max_profit) {
                max_profit = profit;
            }
        }
    }

    cout << max_profit << "\n";
    return 0;
}