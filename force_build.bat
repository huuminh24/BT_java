@echo off
cd /d c:\Users\ADMIN\Downloads\BT_Java-master\BT_Java-master
mvn compile -Dmaven.compiler.useIncrementalCompilation=false 2>&1
if %ERRORLEVEL% == 0 (
    echo COMPILE_OK
    mvn package -DskipTests -Dmaven.compiler.useIncrementalCompilation=false 2>&1 | findstr /i "BUILD error jar"
) else (
    echo COMPILE_FAILED
)
