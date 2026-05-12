@echo off
cd /d c:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master
mvn clean package -q
if %ERRORLEVEL% == 0 (
    echo BUILD_OK
) else (
    echo BUILD_FAILED
    mvn clean compile
)
