content = """\
# Database Configuration
db.url=jdbc:mysql://localhost:3306/JudgeSystem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
db.username=root
db.password=123
db.driver=com.mysql.cj.jdbc.Driver

# AI API Configuration (Google Gemini)
ai.format=gemini
ai.api.key=AIzaSyBtyo3AejtCA5YBT-ZBETAAP9lsepOkp2U
ai.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-lite-latest:generateContent?key=
ai.model=gemini-flash-lite-latest

# File Storage
file.root.dir=./JudgeSystemData
file.problems.dir=./JudgeSystemData/Problems
file.testcases.dir=./JudgeSystemData/Testcases
file.submissions.dir=./JudgeSystemData/Submissions
file.samples.dir=./JudgeSystemData/Samples

# Judge Configuration
judge.default.timelimit=2000
judge.default.memorylimit=256
judge.compiler.java=javac
judge.compiler.cpp=g++
judge.runner.java=java
"""

path = r"c:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master\src\main\resources\config.properties"
with open(path, "w", encoding="utf-8") as f:
    f.write(content)
print("OK - da cau hinh Gemini 2.5 Flash Preview")
