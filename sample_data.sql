USE JudgeSystem;

-- ============================================
-- BÀI 1: A + B Problem (Dễ)
-- ============================================
INSERT INTO Problems (title, description, time_limit, memory_limit, contest_type, checker_script)
VALUES (
    'A + B Problem',
    'Cho hai số nguyên a và b (-10^9 <= a, b <= 10^9). In ra tổng a + b.',
    1000, 256, 'ICPC',
    NULL
);
SET @p1 = LAST_INSERT_ID();

INSERT INTO Testcases (problem_id, input_data, expected_output, testcase_type, is_ai_generated) VALUES
(@p1, '3 5\n', '8\n', 'normal', FALSE),
(@p1, '10 20\n', '30\n', 'normal', FALSE),
(@p1, '0 0\n', '0\n', 'normal', FALSE),
(@p1, '-5 5\n', '0\n', 'normal', FALSE),
(@p1, '1000000 2000000\n', '3000000\n', 'normal', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p1,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long a = sc.nextLong();
        long b = sc.nextLong();
        System.out.println(a + b);
    }
}',
'java', 'AC', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p1,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long a = sc.nextLong();
        long b = sc.nextLong();
        System.out.println(a - b); // SAI: phải là a + b
    }
}',
'java', 'WA', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p1,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        while (true) {} // TLE: vòng lặp vô hạn
    }
}',
'java', 'TLE', FALSE);

-- ============================================
-- BÀI 2: Kiểm tra số nguyên tố (Trung bình)
-- ============================================
INSERT INTO Problems (title, description, time_limit, memory_limit, contest_type, checker_script)
VALUES (
    'Prime Check',
    'Cho số nguyên dương n (1 <= n <= 10^6). In "YES" nếu n là số nguyên tố, ngược lại in "NO".',
    1000, 256, 'ICPC',
    NULL
);
SET @p2 = LAST_INSERT_ID();

INSERT INTO Testcases (problem_id, input_data, expected_output, testcase_type, is_ai_generated) VALUES
(@p2, '7\n', 'YES\n', 'normal', FALSE),
(@p2, '10\n', 'NO\n', 'normal', FALSE),
(@p2, '2\n', 'YES\n', 'normal', FALSE),
(@p2, '1\n', 'NO\n', 'normal', FALSE),
(@p2, '999983\n', 'YES\n', 'normal', FALSE),
(@p2, '1000000\n', 'NO\n', 'normal', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p2,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        if (n < 2) { System.out.println("NO"); return; }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) { System.out.println("NO"); return; }
        }
        System.out.println("YES");
    }
}',
'java', 'AC', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p2,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        if (n % 2 == 0) System.out.println("YES"); // SAI: cho rằng số chẵn đều là nguyên tố
        else System.out.println("NO");
    }
}',
'java', 'WA', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p2,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        for (int i = 2; i < n; i++) { // TLE: không dùng sqrt, chậm với n lớn
            if (n % i == 0) { System.out.println("NO"); return; }
        }
        System.out.println("YES");
    }
}',
'java', 'TLE', FALSE);

-- ============================================
-- BÀI 3: Tổng dãy số từ 1 đến N (Dễ - test WA/TLE)
-- ============================================
INSERT INTO Problems (title, description, time_limit, memory_limit, contest_type, checker_script)
VALUES (
    'Sum 1 to N',
    'Cho số nguyên dương n (1 <= n <= 10^9). In ra tổng các số từ 1 đến n.',
    1000, 256, 'ICPC',
    NULL
);
SET @p3 = LAST_INSERT_ID();

INSERT INTO Testcases (problem_id, input_data, expected_output, testcase_type, is_ai_generated) VALUES
(@p3, '5\n', '15\n', 'normal', FALSE),
(@p3, '10\n', '55\n', 'normal', FALSE),
(@p3, '100\n', '5050\n', 'normal', FALSE),
(@p3, '1\n', '1\n', 'normal', FALSE),
(@p3, '1000000\n', '500000500000\n', 'normal', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p3,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long n = sc.nextLong();
        System.out.println(n * (n + 1) / 2);
    }
}',
'java', 'AC', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p3,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long n = sc.nextLong();
        System.out.println(n * (n + 1)); // SAI: thiếu chia 2
    }
}',
'java', 'WA', FALSE);

INSERT INTO SampleCodes (problem_id, code_content, language, expected_type, is_ai_generated) VALUES
(@p3,
'import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long n = sc.nextLong();
        long sum = 0;
        for (long i = 1; i <= n; i++) sum += i; // TLE: với n=10^9 sẽ chạy rất lâu
        System.out.println(sum);
    }
}',
'java', 'TLE', FALSE);
