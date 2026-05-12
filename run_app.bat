@echo off
cd /d "c:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master"
echo Checking Java...
java -version 2>&1
echo.
echo Running app...
java -jar target\JudgeSystem-1.0-SNAPSHOT.jar 2>&1
echo.
echo App finished with code: %ERRORLEVEL%
pause
