@echo off
cd /d "c:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master"
echo Deleting old build...
rmdir /s /q target 2>nul
echo Building...
mvn clean package -q -B -DskipTests=true
if errorlevel 1 (
    echo BUILD FAILED
    pause
    exit /b 1
)
echo Running app...
java -jar target\JudgeSystem-1.0-SNAPSHOT.jar
pause
