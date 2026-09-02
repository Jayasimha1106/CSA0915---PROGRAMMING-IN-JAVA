@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ============================================
echo Compiling Smart Bank Management System...
echo ============================================

if not exist bin mkdir bin

javac -d bin -cp src src\*.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation successful! Class files saved to bin/
) else (
    echo [ERROR] Compilation failed.
)
pause
