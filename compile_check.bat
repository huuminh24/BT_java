@echo off
cd /d c:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master
mvn clean compile 2>&1 | findstr /i "error"
